package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.time.Instant;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public class IsolatedPathwayEventParser extends AbstractPathwayEventParser {

    public PathwayEvent getIsolatedPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.ISOLATED,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getDate(record));
    }

    private long getDate(Record record) {
        String date = record.getString("DATE");
        if (date.endsWith("Z"))
            return Instant.parse(date).toEpochMilli();
        else
            return Instant.parse(date + Commons.INSTANT_END_OF_DAY).toEpochMilli();
    }

}
