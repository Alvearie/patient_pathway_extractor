package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.time.Instant;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public abstract class AbstractInputDataParser implements InputDataParser {

    private static final String EMPTY_STRING = "";
    private final long now;

    public AbstractInputDataParser(long now) {
        super();
        this.now = now;
    }

    @Override
    public PathwayEventFeature getPathwayEventFeature(Record record, SyntheaMedicalTypes medicalType) {
        PathwayEventFeature pathwayEventFeature = new PathwayEventFeature();
        for (String featureColumnName : Commons.SYNTHEA_FEATURE_COLUMN_NAMES.get(medicalType)) {
            String featureValue = record.getString(featureColumnName);
            if (featureValue == null)
                pathwayEventFeature.add(EMPTY_STRING);
            else
                pathwayEventFeature.add(featureValue);
        }
        return pathwayEventFeature;
    }

    @Override
    public PathwayEvent getStartPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.START,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getStart(record));
    }

    @Override
    public PathwayEvent getStopPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.STOP,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getStop(record));
    }

    @Override
    public PathwayEvent getIsolatedPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.ISOLATED,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getDate(record));
    }

    @Override
    public String getPatientId(Record record, SyntheaMedicalTypes medicalType) {
        return record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
    }

    private long getStart(Record record) {
        String start = record.getString("START");
        if (start == null) {
            return -1;
        } else if (start.endsWith("Z")) {
            return Instant.parse(start).toEpochMilli();
        } else {
            return Instant.parse(start + Commons.INSTANT_START_OF_DAY).toEpochMilli();
        }
    }

    private long getStop(Record record) {
        String stop = record.getString("STOP");
        if (stop == null) {
            return now;
        } else if (stop.endsWith("Z")) {
            return Instant.parse(stop).toEpochMilli();
        } else {
            return Instant.parse(stop + Commons.INSTANT_END_OF_DAY).toEpochMilli();
        }
    }

    private long getDate(Record record) {
        String date = record.getString("DATE");
        if (date == null) {
            return -1;
        } else if (date.endsWith("Z")) {
            return Instant.parse(date).toEpochMilli();
        } else {
            return Instant.parse(date + Commons.INSTANT_END_OF_DAY).toEpochMilli();
        }
    }

}
