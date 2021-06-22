package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.IsolatedPathwayEventParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.PatientsParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.StartStopPathwayEventParser;

public class SimpleDataProvider implements DataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleDataProvider.class);
    
    // Map[PatientId, Patient]
    private final Map<String, Patient> patientsIndex;

    private final String dataPathName;
    private final long now;
    private final Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes;

    public SimpleDataProvider(String dataPathName, long now, Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes) {
        super();
        this.dataPathName = dataPathName;
        this.now = now;
        this.includedSyntheaMedicalTypes = includedSyntheaMedicalTypes;
        this.patientsIndex = new PatientsParser(dataPathName).parse();
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
    public String getDataPathName() {
        return dataPathName;
    }

    private PathwayEventsLine buildPathwayEventsLine(long pathwayStartDate, long pathwayStopDate, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        StartStopPathwayEventParser startStopPathwayEventParser = new StartStopPathwayEventParser(now);
        IsolatedPathwayEventParser isolatedPathwayEventParser = new IsolatedPathwayEventParser();
        for (SyntheaMedicalTypes medicalType : Commons.SYNTHEA_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS) {
            pathwayEventsLine.addAll(buildPathwayEventsLine(pathwayStartDate, pathwayStopDate, startStopPathwayEventParser, medicalType, patientId));
        }
        for (SyntheaMedicalTypes medicalType : Commons.SYNTHEA_MEDICAL_TYPES_YIELDING_ISOLATED_PATHWAY_EVENTS) {
            pathwayEventsLine.addAll(buildPathwayEventsLine(pathwayStartDate, pathwayStopDate, isolatedPathwayEventParser, medicalType, patientId));
        }
        return pathwayEventsLine;
    }

    private PathwayEventsLine buildPathwayEventsLine(long pathwayStartDate, long pathwayStopDate,
            StartStopPathwayEventParser startStopPathwayEventParser, SyntheaMedicalTypes medicalType, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        Interval pathwayInterval = new Interval(pathwayStartDate, pathwayStopDate);
        if (includedSyntheaMedicalTypes.contains(medicalType)) {
            FileParsingUtils.readAsStreamOfRecords(dataPathName + Commons.SYNTHEA_FILE_NAMES.get(medicalType))
                    .forEach(record -> {
                        String patientIdFromRecord = record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
                        if (patientId.equals(patientIdFromRecord)) {
                            PathwayEvent startPathwayEvent = startStopPathwayEventParser.getStartPathwayEvent(record, medicalType);
                            PathwayEvent stopPathwayEvent = startStopPathwayEventParser.getStopPathwayEvent(record, medicalType);
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

    private PathwayEventsLine buildPathwayEventsLine(long pathwayStartDate, long pathwayStopDate,
            IsolatedPathwayEventParser isolatedPathwayEventParser, SyntheaMedicalTypes medicalType, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        if (includedSyntheaMedicalTypes.contains(medicalType)) {
            FileParsingUtils.readAsStreamOfRecords(dataPathName + Commons.SYNTHEA_FILE_NAMES.get(medicalType))
                    .forEach(record -> {
                        String patientIdFromRecord = record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
                        if (patientId.equals(patientIdFromRecord)) {
                            PathwayEvent pathwayEvent = isolatedPathwayEventParser.getIsolatedPathwayEvent(record, medicalType);
                            if (pathwayEvent.getDate() >= pathwayStartDate && pathwayEvent.getDate() <= pathwayStopDate)
                                pathwayEventsLine.add(pathwayEvent);
                        }
                    });
        }
        return pathwayEventsLine;
    }

}
