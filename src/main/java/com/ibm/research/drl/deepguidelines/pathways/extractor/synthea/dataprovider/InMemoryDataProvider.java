package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Interval;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.IntervalTree;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeatures;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeaturesIndex;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;

/**
 * This {@link DataProvider} reads all input data files, and builds in-memory data structures to hold the minimum amount of data that we need to produce a {@link Pathway}, 
 * and corresponding {@link PathwayMatrix} and {@link PathwayImage}.
 * 
 *  More specifically it builds:
 *  - a forest of {@link IntervalTree}; each IntervalTree in the forest belongs to one patient, and it contains {@link PathwayEvent} objects having start/stop dates 
 *  for that patient;
 *  - a bundle of {@link PathwayEventsLine}; each PathwayEventsLine in the bundle belongs to one patient, and it contains {@link PathwayEvent} objects happening 
 *  at an isolated date for that patient
 *  - a Map from patient-ID to {@link PathwayEventFeaturesIndex}, where we store all the data ({@link PathwayEventFeatures}) for that patient.
 *  - a Map from patient-ID to {@link Patient}, where we store Patient objects.
 *  
 *  Notes:
 *  - an instance of this class may consume a lot of memory, depending on the input data set
 *  - this class is NOT thread safe, and the underlying data structures are mutable, and therefore they should not be exposed
 */
public class InMemoryDataProvider extends AbstractDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryDataProvider.class);

    private final Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes;
    private final Optional<Set<String>> includedConditionsCodes;
    private final InputDataParser inputDataParser;
    
    public InMemoryDataProvider(String inputDataPath, Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes,
            Optional<Set<String>> includedConditionsCodes, InputDataParser inputDataParser) {
        super(inputDataPath);
        this.includedSyntheaMedicalTypes = includedSyntheaMedicalTypes;
        this.includedConditionsCodes = includedConditionsCodes;
        this.inputDataParser = inputDataParser;
        LOG.info("building Data for path " + inputDataPath);
        buildIntervalTreesForest();
        if (LOG.isInfoEnabled()) logStatisticsOfIntervalTreesForest();
        buildEventsLinesBundle();
        if (LOG.isInfoEnabled()) logStatisticsOfEventsLinesBundle();
        this.patientsIndex = inputDataParser.getPatients(inputDataPath);
        buildPathways();
        LOG.info("finished building Data for path " + inputDataPath);
    }

    private void buildIntervalTreesForest() {
        if (LOG.isInfoEnabled()) LOG.info("building interval trees forest ...");
        for (SyntheaMedicalTypes medicalType : inputDataParser.getMedicalTypesYieldingStartStopPathwayEvents()) {
            if (includedSyntheaMedicalTypes.contains(medicalType)) {
                inputDataParser.readAsStreamOfRecords(inputDataPath, medicalType)
                        .forEach(record -> {
                            PathwayEvent startPathwayEvent = inputDataParser.getStartPathwayEvent(record, medicalType);
                            PathwayEvent stopPathwayEvent = inputDataParser.getStopPathwayEvent(record, medicalType);
                            long start = startPathwayEvent.getDate();
                            long stop = stopPathwayEvent.getDate();
                            if (start == -1) {
                                LOG.error("bad start date: " + record);
                            }
                            if (stop < start) {
                                LOG.error("bad data: stop < start: " + record);
                            } else {
                                Interval interval = new Interval(start, stop);
                                String patientId = inputDataParser.getPatientId(record, medicalType);
                                intervalTreesForest
                                        .computeIfAbsent(patientId, p -> new IntervalTree<>())
                                        .put(interval, startPathwayEvent);
                                intervalTreesForest
                                        .computeIfAbsent(patientId, p -> new IntervalTree<>())
                                        .put(interval, stopPathwayEvent);
                                PathwayEventFeature pathwayEventFeature = inputDataParser.getPathwayEventFeature(record, medicalType);
                                updateEventsFeatures(patientId, startPathwayEvent, pathwayEventFeature);
                                updateEventsFeatures(patientId, stopPathwayEvent, pathwayEventFeature);
                            }
                        });
            }
        }
        if (LOG.isInfoEnabled()) LOG.info("... finished building interval trees forest");
    }

    private void buildEventsLinesBundle() {
        if (LOG.isInfoEnabled()) LOG.info("building event bundle ...");
        for (SyntheaMedicalTypes medicalType : inputDataParser.getMedicalTypesYieldingIsolatedPathwayEvents()) {
            if (includedSyntheaMedicalTypes.contains(medicalType)) {
                inputDataParser.readAsStreamOfRecords(inputDataPath, medicalType)
                        .forEach(record -> {
                            System.out.println(record.toString());
                            PathwayEvent pathwayEvent = inputDataParser.getIsolatedPathwayEvent(record, medicalType);
                            if (pathwayEvent.getDate() == -1 || pathwayEvent.getEventId() == null) {
                                LOG.error("bad date or eventId: " + record);
                            } else {
                                String patientId = inputDataParser.getPatientId(record, medicalType);
                                eventLinesBundle
                                        .computeIfAbsent(patientId, p -> new PathwayEventsLine())
                                        .add(pathwayEvent);
                                PathwayEventFeature pathwayEventFeature = inputDataParser.getPathwayEventFeature(record, medicalType);
                                updateEventsFeatures(patientId, pathwayEvent, pathwayEventFeature);
                            }
                        });
            }
        }
        if (LOG.isInfoEnabled()) LOG.info("... finished building event bundle");
    }

    private void buildPathways() {
        FunctionFromRecordToOptionalPathway functionFromRecordToOptionalPathway = new FunctionFromRecordToOptionalPathway(this,
                inputDataParser, includedConditionsCodes);
        pathways = inputDataParser.readAsStreamOfRecords(inputDataPath, SyntheaMedicalTypes.CONDITIONS)
                .map(functionFromRecordToOptionalPathway)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

}
