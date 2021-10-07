package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.DifferenceUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_synthea_1_patient_seed_3_all_medical_types.properties")
public class SimpleDataProviderTest {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleDataProviderTest.class);

    @Autowired
    private SimpleDataProviderBuilder simpleDataProviderBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.path}")
    private String syntheaDataPath;

    @Test
    public void testGetPathwayEventsLine() {
        PathwayEventsLine expectedPathwayEventsLine = new PathwayEventsLine();
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.START,
                        "Condition_3",
                        Instant.parse("2009-07-27" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.START,
                        "Condition_3",
                        Instant.parse("2009-07-27" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.START,
                        "Condition_3",
                        Instant.parse("2009-07-27T07:50:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.STOP,
                        "Condition_3",
                        Instant.parse("2009-07-27T08:05:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_3",
                        Instant.parse("2009-08-10" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.START,
                        "Medication_2",
                        Instant.parse("2009-11-18" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.PROCEDURES,
                        PathwayEventTemporalType.START,
                        "Medication_2",
                        Instant.parse("2009-11-18" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
            new PathwayEvent(
                    SyntheaMedicalTypes.PROCEDURES,
                    PathwayEventTemporalType.STOP,
                    "Medication_2",
                    Instant.parse("2009-11-18" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.START,
                        "Medication_2",
                        Instant.parse("2009-11-18T07:50:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.STOP,
                        "Medication_2",
                        Instant.parse("2009-11-18T08:23:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.IMMUNIZATIONS,
                        PathwayEventTemporalType.ISOLATED,
                        "Immunization_1",
                        Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.OBSERVATIONS,
                        PathwayEventTemporalType.ISOLATED,
                        "Immunization_1",
                        Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.START,
                        "Immunization_1",
                        Instant.parse("2010-03-31T07:50:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.ENCOUNTERS,
                        PathwayEventTemporalType.STOP,
                        "Immunization_1",
                        Instant.parse("2010-03-31T08:05:07Z").toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_3",
                        Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));

        PathwayEvent startPathwayEvent = new PathwayEvent(
                SyntheaMedicalTypes.CONDITIONS,
                PathwayEventTemporalType.START,
                "Condition_3",
                Instant.parse("2009-07-27" + Commons.INSTANT_START_OF_DAY).toEpochMilli());
        PathwayEvent stopPathwayEvent = new PathwayEvent(
                SyntheaMedicalTypes.CONDITIONS,
                PathwayEventTemporalType.STOP,
                "Condition_3",
                Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli());
        DataProvider dataProvider = simpleDataProviderBuilder.build(syntheaDataPath, new SyntheaCsvInputDataParser(Instant.now().toEpochMilli()));
        PathwayEventsLine actualPathwayEventsLine = dataProvider.getPathwayEventsLine(startPathwayEvent, stopPathwayEvent, "Patient_1");
        if (!actualPathwayEventsLine.equals(expectedPathwayEventsLine)) {
            String diff = DifferenceUtils.diff(
                   new ObjectArrayList<>(actualPathwayEventsLine), 
                   new ObjectArrayList<>(expectedPathwayEventsLine));
            LOG.info("diff:\n" + diff);
        }
        assertThat(actualPathwayEventsLine.size(), equalTo(expectedPathwayEventsLine.size()));
        assertThat(actualPathwayEventsLine, equalTo(expectedPathwayEventsLine));
    }

}
