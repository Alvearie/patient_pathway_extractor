package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.util.Map;
import java.util.stream.Stream;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public interface InputDataParser {

    public PathwayEvent getStartPathwayEvent(Record record, SyntheaMedicalTypes medicalType);

    public PathwayEvent getStopPathwayEvent(Record record, SyntheaMedicalTypes medicalType);

    public PathwayEvent getIsolatedPathwayEvent(Record record, SyntheaMedicalTypes medicalType);

    public String getPatientId(Record record, SyntheaMedicalTypes medicalType);

    public PathwayEventFeature getPathwayEventFeature(Record record, SyntheaMedicalTypes medicalType);

    public Stream<Record> readAsStreamOfRecords(String inputDataPath, SyntheaMedicalTypes medicalType);

    public Map<String, Patient> getPatients(String inputDataPath);
    
    public String getFileName(SyntheaMedicalTypes medicalType);
}
