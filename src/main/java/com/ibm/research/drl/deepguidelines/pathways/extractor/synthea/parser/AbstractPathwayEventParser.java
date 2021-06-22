package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public abstract class AbstractPathwayEventParser {

    private static final String EMPTY_STRING = "";

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

    public String getPatientId(Record record, SyntheaMedicalTypes medicalType) {
        return record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
    }

}
