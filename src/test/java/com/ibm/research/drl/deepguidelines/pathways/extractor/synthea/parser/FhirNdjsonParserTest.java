package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;

public class FhirNdjsonParserTest {

    private static final String INPUT_DATA_PATH = "simple_dataset_to_test_fhir_ndjson_parser/";
    private static final String[] expectedAllergyIntoleranceRecords = {
            "10831, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, null, 1999-02-20T11:31:00Z"
    };
    private static final String[] expectedCarePlanRecords = {
            "386463000, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T08:31:00Z, null",
            "58332002, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, b4ce28e4-1cfe-4b7c-e2a3-1f7cb6c2f546, 1999-02-20T11:55:48Z, null",
            "304510005, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 4eade156-3e80-e115-06f6-978be4923bc2, 2014-02-01T03:49:16Z, 2014-02-08T03:49:16Z",
            "385949008, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, d118adbb-7169-66d3-44e3-cf2873b47be9, 2017-12-21T09:28:12Z, 2018-01-08T09:47:00Z",
            "444908001, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 91dea1bf-0660-bc60-e03b-e641b5efff7a, 2020-03-03T08:44:47Z, 2020-03-13T12:57:20Z"
    };
    private static final String[] expectedConditionRecords = {
            "59621000, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T08:31:00Z, null",
            "224355006, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T09:11:41Z, null",
            "105531004, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T09:11:41Z, null",
            "224299000, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T09:11:41Z, null",
            "160903007, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T09:11:41Z, 1998-02-11T09:13:38Z"
    };
    private static final String[] expectedEncounterRecords = {
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T08:31:00Z, 1997-02-05T08:46:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 00255dc4-8f27-7137-51c3-45513b2feda3, 1997-03-07T08:31:00Z, 1997-03-07T08:46:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 578f73a2-71ce-1e63-5829-8f3770e38345, 1997-05-06T08:31:00Z, 1997-05-06T08:46:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 08521078-6543-50ae-5b9e-a06005a251ca, 1998-02-11T08:31:00Z, 1998-02-11T08:46:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 8ddaf8de-d208-5b6f-46f1-841f693aa21c, 1999-02-06T20:31:00Z, 1999-02-06T20:46:00Z"
    };
    private static final String[] expectedImagingStudyRecords = {
            "72696002, DX, d0027c78-054c-d1ec-6b74-98015caca830, 93ba6e8d-2322-2844-762f-5faf055afb21, 2019-05-21T09:03:51Z",
            "8205005, DX, 6a0aa707-09bb-c1f2-18af-a35834f53aeb, ae73befd-a62c-9096-8204-fc2d77c40f23, 2018-03-25T14:57:14Z",
            "51299004, DX, 7690d961-1a81-3686-bafa-857fb3f955a9, 81735039-305d-b442-2cf3-df6725c095ff, 2015-04-21T12:03:37Z",
            "40983000, DX, 35c315d8-3175-bc0d-d6b4-d90aa14b177c, ec2fe80a-3b00-d106-51b9-36a735091485, 2012-02-14T16:46:22Z",
            "72696002, DX, 3065ee0f-8cc5-9593-3a69-b687722f56c9, 3790bd7a-870e-1759-e95f-7b3db1c1172e, 2021-04-28T02:08:47Z"
    };
    private static final String[] expectedImmunizationRecords = {
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 968c485c-6e5b-310c-e2ae-90959071917d, 2012-08-01T08:31:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 7615a10c-2909-a7a3-8e02-544c3ea70f84, 2014-02-05T08:31:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, 7e79af20-dca2-ddb3-4a91-14b63f8d8af4, 2015-05-20T08:31:00Z",
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65, ab2cc031-12cc-e057-4db1-d553bd714e27, 2016-05-25T08:31:00Z"
    };
    private static final String[] expectedMedicationRequestRecords = {
            "314076, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 5a28a2b8-839c-4fcd-afb1-414ab42cd8cf, 1997-02-05T08:31:00Z",
            "308136, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 00255dc4-8f27-7137-51c3-45513b2feda3, 1997-03-07T08:31:00Z",
            "310798, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 578f73a2-71ce-1e63-5829-8f3770e38345, 1997-05-06T08:31:00Z",
            "310798, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 08521078-6543-50ae-5b9e-a06005a251ca, 1998-02-11T08:31:00Z",
            "314076, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 08521078-6543-50ae-5b9e-a06005a251ca, 1998-02-11T08:31:00Z"
    };
    private static final String[] expectedObservationRecords = {
            "8302-2, 169.4, cm, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "72514-3, 0, {score}, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "29463-7, 85.1, kg, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "39156-5, 29.64, kg/m2, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "85354-9, null, null, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z"
    };
    private static final String[] expectedPatientRecords = {
            "6538eea4-ba8b-cb7c-a55e-ec9290821f65"
    };
    private static final String[] expectedProcedureRecords = {
            "710824005, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "430193006, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T08:31:00Z",
            "710841007, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T09:29:30Z",
            "428211000124100, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T09:52:38Z",
            "713106006, 6538eea4-ba8b-cb7c-a55e-ec9290821f65, 14e17aa8-2578-b283-e3ad-712cd9ad6644, 2012-05-02T10:06:50Z"
    };
    
    @Test
    public void testReadAsStreamOfRecords() {
        List<String> actualRecords = new ArrayList<>();
        InputDataParser parser = new FhirNdjsonInputDataParser(Instant.now().toEpochMilli());
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.ALLERGIES)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedAllergyIntoleranceRecords.length));
        for (String expectedRecord : expectedAllergyIntoleranceRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.CAREPLANS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedCarePlanRecords.length));
        for (String expectedRecord : expectedCarePlanRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.CONDITIONS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedConditionRecords.length));
        for (String expectedRecord : expectedConditionRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.ENCOUNTERS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedEncounterRecords.length));
        for (String expectedRecord : expectedEncounterRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.IMAGING_STUDIES)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedImagingStudyRecords.length));
        for (String expectedRecord : expectedImagingStudyRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.IMMUNIZATIONS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedImmunizationRecords.length));
        for (String expectedRecord : expectedImmunizationRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.MEDICATIONS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedMedicationRequestRecords.length));
        for (String expectedRecord : expectedMedicationRequestRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.OBSERVATIONS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedObservationRecords.length));
        for (String expectedRecord : expectedObservationRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.PATIENTS)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedPatientRecords.length));
        for (String expectedRecord : expectedPatientRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
        
        actualRecords.clear();
        parser.readAsStreamOfRecords(INPUT_DATA_PATH, SyntheaMedicalTypes.PROCEDURES)
            .forEach(record -> {
                actualRecords.add(record.toString());
            });
        assertThat(actualRecords.size(), equalTo(expectedProcedureRecords.length));
        for (String expectedRecord : expectedProcedureRecords) {
            assertTrue(actualRecords.contains(expectedRecord));
        }
    }

    @Test
    public void testGetPatients() {
        InputDataParser parser = new FhirNdjsonInputDataParser(Instant.now().toEpochMilli());
        Map<String, Patient> patientsMap = parser.getPatients("");
        assertThat(patientsMap, notNullValue());
        assertThat(patientsMap.size(), equalTo(107));
        parser.readAsStreamOfRecords("", SyntheaMedicalTypes.PATIENTS)
            .forEach(record -> {
                assertTrue(patientsMap.keySet().contains(record.getString("ID")));
            });
    }

}
