package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;

public class PatientsParserTest {

    private static final String SYNTHEA_DATA_PATH = "";
    
    @Test
    public void test() {
        PatientsParser patientsParser = new PatientsParser(SYNTHEA_DATA_PATH);
        Map<String, Patient> patientsMap = patientsParser.parse();
        assertThat(patientsMap, notNullValue());
        assertThat(patientsMap.size(), equalTo(55421));
        for (Patient patient : patientsMap.values()) {
            assertThat(patient.getMarital(), notNullValue());
        }
    }

}
