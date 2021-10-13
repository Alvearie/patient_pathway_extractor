package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Map;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;

public class PatientsParserTest {

    private static final String SYNTHEA_DATA_PATH = "";
    
    @Test
    public void test() {
        InputDataParser patientsParser = new SyntheaCsvInputDataParser(Instant.now().toEpochMilli());
        Map<String, Patient> patientsMap = patientsParser.getPatients(SYNTHEA_DATA_PATH);
        assertThat(patientsMap, notNullValue());
        assertThat(patientsMap.size(), equalTo(55421));
        for (Patient patient : patientsMap.values()) {
            assertThat(patient.getMarital(), notNullValue());
        }
    }

}
