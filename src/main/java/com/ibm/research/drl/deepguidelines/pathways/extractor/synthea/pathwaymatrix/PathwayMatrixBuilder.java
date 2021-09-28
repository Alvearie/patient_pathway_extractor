package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class PathwayMatrixBuilder {

    public static final int NUMBER_OF_ROWS = 6;

    public static final EnumMap<SyntheaMedicalTypes, Integer> SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX = new EnumMap<>(
            SyntheaMedicalTypes.class);

    public static final int DEMOGRAPHICS_ROW_INDEX = 0;

    public static final int OUTCOMES_ROW_INDEX = 5;

    static {
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.OBSERVATIONS, 1);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.IMAGING_STUDIES, 1);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.ALLERGIES, 2);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.CONDITIONS, 2);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.MEDICATIONS, 3);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.PROCEDURES, 4);
        SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.put(SyntheaMedicalTypes.CAREPLANS, 4);
    }

    private static final String[] DROOLS_DECISION_TABLE_FILE_PATHS = new String[] {
            "rules_for_computing_buckets_of_values/DecisionTable_drools_Demographics.xls",
            "rules_for_computing_buckets_of_values/DecisionTable_drools_Medications.xls",
            "rules_for_computing_buckets_of_values/DecisionTable_drools_Observations.xls",
            "rules_for_computing_buckets_of_values/DecisionTable_drools_OutcomesS.xls"
    };

    private final DataProvider dataProvider;

    private final KieContainer kieContainer;

    public PathwayMatrixBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.kieContainer = setUpDrools();
    }

    private KieContainer setUpDrools() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        for (String droolsDecisionTableFilePath : DROOLS_DECISION_TABLE_FILE_PATHS) {
            Resource resource = ResourceFactory.newClassPathResource(droolsDecisionTableFilePath);
            kieFileSystem.write(resource);
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieRepository kieRepository = kieServices.getRepository();
        ReleaseId releaseId = kieRepository.getDefaultReleaseId();
        return kieServices.newKieContainer(releaseId);
    }

    public PathwayMatrix build(Pathway pathway) {
        return build(pathway, Collections.emptySet());
    }

    public PathwayMatrix buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(Pathway pathway) {
        Set<PathwayEventFeature> unwantedPathwayEventFeatures = new ObjectOpenHashSet<>();
        String patientId = pathway.getPatient().getId();
        unwantedPathwayEventFeatures.addAll(dataProvider.getPathwayEventFeatures(pathway.getStartPathwayEvent(), patientId));
        unwantedPathwayEventFeatures.addAll(dataProvider.getPathwayEventFeatures(pathway.getStopPathwayEvent(), patientId));
        return build(pathway, unwantedPathwayEventFeatures);
    }

    private PathwayMatrix build(Pathway pathway, Set<PathwayEventFeature> unwantedPathwayEventFeatures) {
        // group PathwayEvents by time; each instant of time corresponds to a slice of the resulting PathwayMatrix.
        // If all PathwayEvents at that instant yield PathwayEventFeatures that belong to the set 
        // unwantedPathwayEventFeatures, then the slice is empty, and it will not be included in the PathwayMatrix.
        SortedMap<Long, List<PathwayEvent>> pathwayEventsGroupedByInstant = new Long2ObjectRBTreeMap<>();
        for (PathwayEvent pathwayEvent : pathway.getPathwayEventsLine()) {
            pathwayEventsGroupedByInstant.computeIfAbsent(pathwayEvent.getDate(), date -> new ObjectArrayList<>()).add(pathwayEvent);
        }

        // build PathwayMatrixCellWithCoordinates and compute numberOfColumns and numberOfSlices
        int numberOfColumns = 0;
        int numberOfSlices = 0;
        List<PathwayMatrixCellWithCoordinates> pathwayMatrixCellsWithCoordinates = new ObjectArrayList<>();
        for (List<PathwayEvent> pathwayEventsOccurringAtTheSameInstantOfTime : pathwayEventsGroupedByInstant.values()) {
            // build slice of all PathwayEvents occurring at the same instant of time
            int[] columnIndexAtRow = new int[NUMBER_OF_ROWS];
            Arrays.fill(columnIndexAtRow, 0);
            boolean sliceIsEmpty = true; // a slice is empty if all PathwayEvents at this instant yield PathwayEventFeatures that belong to the set unwantedPathwayEventFeatures
            for (PathwayEvent pathwayEvent : pathwayEventsOccurringAtTheSameInstantOfTime) {
                int row = SYNTHEA_MEDICAL_TYPE_2_ROW_INDEX.get(pathwayEvent.getMedicalType());
                for (PathwayEventFeature pathwayEventFeature : dataProvider.getPathwayEventFeatures(pathwayEvent, pathway.getPatient().getId())) {
                    if (!unwantedPathwayEventFeatures.contains(pathwayEventFeature)) {
                        sliceIsEmpty = false;
                        pathwayMatrixCellsWithCoordinates.add(
                                new PathwayMatrixCellWithCoordinates(
                                        numberOfSlices,
                                        row,
                                        columnIndexAtRow[row],
                                        buildPathwayMatrixCell(pathwayEvent, pathwayEventFeature)));
                        columnIndexAtRow[row]++;
                        numberOfColumns = Math.max(numberOfColumns, columnIndexAtRow[row]);
                    }
                }
            }
            if (!sliceIsEmpty) numberOfSlices++;
        }
        // add demographics
        pathwayMatrixCellsWithCoordinates.add(
                new PathwayMatrixCellWithCoordinates(
                        0,
                        DEMOGRAPHICS_ROW_INDEX,
                        0,
                        computeDemographics(pathway)));
        // add outcome
        pathwayMatrixCellsWithCoordinates.add(
                new PathwayMatrixCellWithCoordinates(
                        numberOfSlices - 1,
                        OUTCOMES_ROW_INDEX,
                        0,
                        computeOutcome(pathway)));

        // build the actual PathwayMatrix using the computed PathwayMatrixCellWithCoordinates
        PathwayMatrix pathwayMatrix = new PathwayMatrix(numberOfSlices, NUMBER_OF_ROWS, numberOfColumns);
        for (PathwayMatrixCellWithCoordinates p : pathwayMatrixCellsWithCoordinates) {
            pathwayMatrix.setQuick(p.getSlice(), p.getRow(), p.getColumn(), p.getPathwayMatrixCell());
        }

        // apply rules to compute buckets
        applyRules(pathwayMatrix);

        // done       
        return pathwayMatrix;
    }

    private void applyRules(PathwayMatrix pathwayMatrix) {
        KieSession kieSession = kieContainer.newKieSession();
        List<PathwayMatrixCell> pathwayMatrixCells = new ObjectArrayList<>();
        pathwayMatrix.getNonNullValues(new IntArrayList(), new IntArrayList(), new IntArrayList(), pathwayMatrixCells);
        for (PathwayMatrixCell pathwayMatrixCell : pathwayMatrixCells) {
            kieSession.insert(pathwayMatrixCell);
        }
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    private DemographicsPathwayMatrixCell computeDemographics(Pathway pathway) {
        Patient patient = pathway.getPatient();
        return new DemographicsPathwayMatrixCell(patient.getBirthdate(), patient.getMarital(), patient.getRace(), patient.getGender());
    }

    private OutcomePathwayMatrixCell computeOutcome(Pathway pathway) {
        OutcomePathwayMatrixCell outcomePathwayMatrixCell;
        if (pathway.getPatient().getDeathdate() == null) {
            Instant start = Instant.ofEpochMilli(pathway.getStartPathwayEvent().getDate());
            Instant stop = Instant.ofEpochMilli(pathway.getStopPathwayEvent().getDate());
            Duration duration = Duration.between(start, stop);
            outcomePathwayMatrixCell = new AliveOutcomePathwayMatrixCell(duration.toDays());
        } else {
            Instant start = Instant.parse(pathway.getPatient().getBirthdate() + Commons.INSTANT_START_OF_DAY);
            Instant stop = Instant.parse(pathway.getPatient().getDeathdate() + Commons.INSTANT_END_OF_DAY);
            Duration duration = Duration.between(start, stop);
            outcomePathwayMatrixCell = new DeadOutcomePathwayMatrixCell(duration.toDays());
        }
        return outcomePathwayMatrixCell;
    }

    private PathwayMatrixCell buildPathwayMatrixCell(PathwayEvent pathwayEvent, PathwayEventFeature pathwayEventFeature) {
        PathwayMatrixCell pathwayMatrixCell = null;
        switch (pathwayEvent.getMedicalType()) {
        case ALLERGIES:
            pathwayMatrixCell = new AllergiesPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEvent.getTemporalType());
            break;

        case CAREPLANS:
            pathwayMatrixCell = new CareplansPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEvent.getTemporalType());
            break;

        case CONDITIONS:
            pathwayMatrixCell = new ConditionsPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEvent.getTemporalType());
            break;

        case IMAGING_STUDIES:
            pathwayMatrixCell = new ImagingStudiesPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEventFeature.get(1),
                    pathwayEvent.getTemporalType());
            break;

        case MEDICATIONS:
            pathwayMatrixCell = new MedicationsPathwayMatrixCell(pathwayEventFeature.get(0), Integer.valueOf(pathwayEventFeature.get(1)),
                    pathwayEvent.getTemporalType());
            break;

        case OBSERVATIONS:
            pathwayMatrixCell = new ObservationsPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEventFeature.get(1), pathwayEventFeature.get(2),
                    pathwayEvent.getTemporalType());
            break;

        case PROCEDURES:
            pathwayMatrixCell = new ProceduresPathwayMatrixCell(pathwayEventFeature.get(0), pathwayEvent.getTemporalType());
            break;

        default:
            throw new IllegalArgumentException("cannot build a PathwayMatrixCell for medical type " + pathwayEvent.getMedicalType());
        }
        return pathwayMatrixCell;
    }

}
