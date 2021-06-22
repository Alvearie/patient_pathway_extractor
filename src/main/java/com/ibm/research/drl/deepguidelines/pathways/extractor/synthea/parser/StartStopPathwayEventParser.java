package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.time.Instant;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public class StartStopPathwayEventParser extends AbstractPathwayEventParser {

    private final long now;

    public StartStopPathwayEventParser(long now) {
        super();
        this.now = now;
    }

    public PathwayEvent getStartPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.START,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getStart(record));
    }

    public PathwayEvent getStopPathwayEvent(Record record, SyntheaMedicalTypes medicalType) {
        return new PathwayEvent(
                medicalType,
                PathwayEventTemporalType.STOP,
                record.getString(Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType)),
                getStop(record));
    }

    private long getStart(Record record) {
        String start = record.getString("START");
        if (start.endsWith("Z"))
            return Instant.parse(start).toEpochMilli();
        else
            return Instant.parse(start + Commons.INSTANT_START_OF_DAY).toEpochMilli();
    }

    private long getStop(Record record) {
        String stop = record.getString("STOP");
        if (stop == null)
            return now;
        else if (stop.endsWith("Z"))
            return Instant.parse(stop).toEpochMilli();
        else
            return Instant.parse(stop + Commons.INSTANT_END_OF_DAY).toEpochMilli();
    }

}
