package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.AbstractDataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.FunctionFromRecordToOptionalPathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;

public class SimpleDataProvider extends AbstractDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleDataProvider.class);
    
    private final Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes;
    private final Optional<Set<String>> includedConditionsCodes;
    private final InputDataParser inputDataParser;

    public SimpleDataProvider(String inputDataPath, Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes,
            Optional<Set<String>> includedConditionsCodes, InputDataParser inputDataParser) {
        super(inputDataPath);
        this.includedSyntheaMedicalTypes = includedSyntheaMedicalTypes;
        this.includedConditionsCodes = includedConditionsCodes;
        this.inputDataParser = inputDataParser;
        this.patientsIndex = inputDataParser.getPatients(inputDataPath);
    }

    @Override
    public PathwayEventsLine getPathwayEventsLine(PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent, String patientId) {
        return buildPathwayEventsLine(startPathwayEvent.getDate(), stopPathwayEvent.getDate(), patientId);
    }

    @Override
    public Patient getPatient(String patientId) {
        return patientsIndex.get(patientId);
    }

    @Override
    public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String produceJavascriptDataForIntervalTreeVisualization(String patientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<Pathway> getPathways() {
        return buildPathways();
    }

    private PathwayEventsLine buildPathwayEventsLine(long pathwayStartDate, long pathwayStopDate, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        for (SyntheaMedicalTypes medicalType : inputDataParser.getMedicalTypesYieldingStartStopPathwayEvents()) {
            pathwayEventsLine.addAll(buildStartStopPathwayEventsLine(pathwayStartDate, pathwayStopDate, medicalType, patientId));
        }
        for (SyntheaMedicalTypes medicalType : inputDataParser.getMedicalTypesYieldingIsolatedPathwayEvents()) {
            pathwayEventsLine.addAll(buildIsolatedPathwayEventsLine(pathwayStartDate, pathwayStopDate, medicalType, patientId));
        }
        return pathwayEventsLine;
    }

    private PathwayEventsLine buildStartStopPathwayEventsLine(long pathwayStartDate, long pathwayStopDate,
            SyntheaMedicalTypes medicalType, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        Interval pathwayInterval = new Interval(pathwayStartDate, pathwayStopDate);
        if (includedSyntheaMedicalTypes.contains(medicalType)) {
            inputDataParser.readAsStreamOfRecords(inputDataPath, medicalType)
                    .forEach(record -> {
                        String patientIdFromRecord = record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
                        if (patientId.equals(patientIdFromRecord)) {
                            PathwayEvent startPathwayEvent = inputDataParser.getStartPathwayEvent(record, medicalType);
                            PathwayEvent stopPathwayEvent = inputDataParser.getStopPathwayEvent(record, medicalType);
                            long start = startPathwayEvent.getDate();
                            long stop = stopPathwayEvent.getDate();
                            if (stop < start)
                                LOG.error("stop < start in " + record);
                            else {
                                Interval eventIterval = new Interval(start, stop);
                                if (pathwayInterval.intersects(eventIterval)) {
                                    if (start >= pathwayStartDate && start <= pathwayStopDate)
                                        pathwayEventsLine.add(startPathwayEvent);
                                    if (stop >= pathwayStartDate && stop <= pathwayStopDate)
                                        pathwayEventsLine.add(stopPathwayEvent);
                                }
                            }
                        }
                    });
        }
        return pathwayEventsLine;
    }

    private PathwayEventsLine buildIsolatedPathwayEventsLine(long pathwayStartDate, long pathwayStopDate,
            SyntheaMedicalTypes medicalType, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        if (includedSyntheaMedicalTypes.contains(medicalType)) {
            inputDataParser.readAsStreamOfRecords(inputDataPath, medicalType)
                    .forEach(record -> {
                        String patientIdFromRecord = record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
                        if (patientId.equals(patientIdFromRecord)) {
                            PathwayEvent pathwayEvent = inputDataParser.getIsolatedPathwayEvent(record, medicalType);
                            if (pathwayEvent.getDate() >= pathwayStartDate && pathwayEvent.getDate() <= pathwayStopDate)
                                pathwayEventsLine.add(pathwayEvent);
                        }
                    });
        }
        return pathwayEventsLine;
    }

    private Stream<Pathway> buildPathways() {
        FunctionFromRecordToOptionalPathway functionFromRecordToOptionalPathway = new FunctionFromRecordToOptionalPathway(this,
                inputDataParser, includedConditionsCodes);
        return inputDataParser.readAsStreamOfRecords(inputDataPath, SyntheaMedicalTypes.CONDITIONS)
                .map(functionFromRecordToOptionalPathway)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

}
