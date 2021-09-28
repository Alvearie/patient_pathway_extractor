package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeatures;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.ExpectedDataForSynthea1PatientSeed3;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_synthea_1_patient_seed_3.properties")
public class PathwayMatrixBuilderTest {

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.path}")
    private String syntheaDataPath;

    @Test
    public void test() {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath, new SyntheaCsvInputDataParser(Instant.now().toEpochMilli()));
        List<Pathway> pathways = dataProvider.getPathways().collect(Collectors.toList());
        Pathway pathway = pathways.get(2);
        assertThat(pathway.getId(), equalTo("2009-07-27T00:00:00Z,2010-03-31T23:59:59Z,Patient_1,Condition_3"));
        assertThat(pathway.getPathwayEventsLine(), equalTo(getExpectedPathwayEventsLine()));
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(pathway);
        assertThat(actualPathwayMatrix, notNullValue());
        PathwayMatrix expectedPathwayMatrix = ExpectedDataForSynthea1PatientSeed3.buildPathwayMatrixForCondition3();
        assertThat(actualPathwayMatrix, equalTo(expectedPathwayMatrix));
    }

    private PathwayEventsLine getExpectedPathwayEventsLine() {
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
                        PathwayEventTemporalType.ISOLATED,
                        "Medication_2",
                        Instant.parse("2009-11-18" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.OBSERVATIONS,
                        PathwayEventTemporalType.ISOLATED,
                        "Immunization_1",
                        Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        expectedPathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_3",
                        Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        return expectedPathwayEventsLine;
    }

    @Test
    public void testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents_Case0() throws IOException {
        // Case 0: originating condition appears only at the beginning and end of the pathway, and it doesn't occur with other events

        Patient thePatient = new Patient("Patient_1", "1951-02-01", null, "U", "white", "M");
        PathwayEvent theStartPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_1", 1);
        PathwayEvent theStopPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_1", 6);
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        pathwayEventsLine.addAll(Arrays.asList(
                theStartPathwayEvent,
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.START, "Medication_1", 2),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_2", 2),
                new PathwayEvent(SyntheaMedicalTypes.OBSERVATIONS, PathwayEventTemporalType.ISOLATED, "Observation_1", 3),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_2", 4),
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.STOP, "Medication_1", 5),
                theStopPathwayEvent));
        DataProvider dataProvider = new TestDataProvider(thePatient, theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);
        String pathwayId = Pathway.buildId(thePatient.getId(), theStartPathwayEvent, theStopPathwayEvent);
        Pathway thePathway = new Pathway(pathwayId, thePatient, "162864005", theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);

        testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(thePathway, dataProvider, 4, 5);
    }
    
    @Test
    public void testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents_Case1() throws IOException {
        // Case 1: originating condition appears only at the beginning and end of the pathway, and it occurs with other events

        Patient thePatient = new Patient("Patient_1", "1951-02-01", null, "U", "white", "M");
        PathwayEvent theStartPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_1", 1);
        PathwayEvent theStopPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_1", 6);
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        pathwayEventsLine.addAll(Arrays.asList(
                theStartPathwayEvent,
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_2", 1),
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.START, "Medication_1", 2),
                new PathwayEvent(SyntheaMedicalTypes.OBSERVATIONS, PathwayEventTemporalType.ISOLATED, "Observation_1", 3),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_2", 4),
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.STOP, "Medication_1", 5),
                theStopPathwayEvent));
        DataProvider dataProvider = new TestDataProvider(thePatient, theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);
        String pathwayId = Pathway.buildId(thePatient.getId(), theStartPathwayEvent, theStopPathwayEvent);
        Pathway thePathway = new Pathway(pathwayId, thePatient, "162864005", theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);

        testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(thePathway, dataProvider, 5, 5);
    }
    
    @Test
    public void testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents_Case2() throws IOException {
        // Case 2: originating condition appears also within the pathway, and it may occur with other events

        Patient thePatient = new Patient("Patient_1", "1951-02-01", null, "U", "white", "M");
        PathwayEvent theStartPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_1", 1);
        PathwayEvent theStopPathwayEvent = new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_1", 7);
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        pathwayEventsLine.addAll(Arrays.asList(
                theStartPathwayEvent,
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_2", 1),
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.START, "Medication_1", 2),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.START, "Condition_1", 2),
                new PathwayEvent(SyntheaMedicalTypes.OBSERVATIONS, PathwayEventTemporalType.ISOLATED, "Observation_1", 3),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_2", 4),
                new PathwayEvent(SyntheaMedicalTypes.CONDITIONS, PathwayEventTemporalType.STOP, "Condition_1", 5),
                new PathwayEvent(SyntheaMedicalTypes.MEDICATIONS, PathwayEventTemporalType.STOP, "Medication_1", 6),
                theStopPathwayEvent));
        DataProvider dataProvider = new TestDataProvider(thePatient, theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);
        String pathwayId = Pathway.buildId(thePatient.getId(), theStartPathwayEvent, theStopPathwayEvent);
        Pathway thePathway = new Pathway(pathwayId, thePatient, "162864005", theStartPathwayEvent, theStopPathwayEvent, pathwayEventsLine);

        testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(thePathway, dataProvider, 5, 5);
    }
    
    private void testBuildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(
            Pathway pathway,
            DataProvider dataProvider,
            int expectedNumberOfSlicesInPathwayMatrix,
            int expectedNumberOfColumnsInPathwayMatrix) throws IOException {
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(pathway);
        assertThat(actualPathwayMatrix.slices(), equalTo(expectedNumberOfSlicesInPathwayMatrix));
        assertThat(actualPathwayMatrix.columns(), equalTo(expectedNumberOfColumnsInPathwayMatrix));
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();       
        String pathwayImageAsCsv = pathwayImageBuilder.trimAndConcatenateSlices(actualPathwayMatrix).asCSVLine(100);
        String originatingCondtionCode = pathway.getOriginatingConditionCode();
        assertThat(pathwayImageAsCsv.indexOf(originatingCondtionCode), equalTo(-1)); 
    }

    private static final class TestDataProvider implements DataProvider {

        private final Patient thePatient;
        private final PathwayEvent theStartPathwayEvent;
        private final PathwayEvent theStopPathwayEvent;
        private final PathwayEventsLine thePathwayEventsLine;

        public TestDataProvider(Patient thePatient, PathwayEvent theStartPathwayEvent, PathwayEvent theStopPathwayEvent,
                PathwayEventsLine pathwayEventsLine) {
            super();
            this.thePatient = thePatient;
            this.theStartPathwayEvent = theStartPathwayEvent;
            this.theStopPathwayEvent = theStopPathwayEvent;
            this.thePathwayEventsLine = pathwayEventsLine;
        }

        @Override
        public PathwayEventsLine getPathwayEventsLine(PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent, String patientId) {
            if (startPathwayEvent.equals(theStartPathwayEvent)
                    && stopPathwayEvent.equals(theStopPathwayEvent)
                    && patientId.equals(thePatient.getId())) {
                return thePathwayEventsLine;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public Patient getPatient(String patientId) {
            if (patientId.equals(thePatient.getId())) {
                return thePatient;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId) {
            if (!patientId.equals(thePatient.getId())) {
                throw new IllegalArgumentException();
            }
            PathwayEventFeatures pathwayEventFeatures = new PathwayEventFeatures();
            if (pathwayEvent.getMedicalType().equals(SyntheaMedicalTypes.CONDITIONS)) {
                if (pathwayEvent.getEventId().equals(theStartPathwayEvent.getEventId())
                        || pathwayEvent.getEventId().equals(theStopPathwayEvent.getEventId()))  {
                    PathwayEventFeature pathwayEventFeature = new PathwayEventFeature();
                    pathwayEventFeature.add("53741008");
                    pathwayEventFeatures.add(pathwayEventFeature);
                } else {
                    PathwayEventFeature pathwayEventFeature = new PathwayEventFeature();
                    pathwayEventFeature.add("74400008");
                    pathwayEventFeatures.add(pathwayEventFeature);
                }
            } else if (pathwayEvent.getMedicalType().equals(SyntheaMedicalTypes.MEDICATIONS)) {
                PathwayEventFeature pathwayEventFeature = new PathwayEventFeature();
                pathwayEventFeature.add("309362");
                pathwayEventFeature.add("92");
                pathwayEventFeatures.add(pathwayEventFeature);
            } else if (pathwayEvent.getMedicalType().equals(SyntheaMedicalTypes.OBSERVATIONS)) {
                for (int i = 0; i < 5; i++) {
                    PathwayEventFeature pathwayEventFeature = new PathwayEventFeature();
                    pathwayEventFeature.add("6299-2");
                    pathwayEventFeature.add(String.valueOf(i + 10));
                    pathwayEventFeature.add("mg/dL");
                    pathwayEventFeatures.add(pathwayEventFeature);
                }
            } else {
                throw new IllegalArgumentException();
            }
            return pathwayEventFeatures;
        }

        @Override
        public String produceJavascriptDataForIntervalTreeVisualization(String patientId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<Pathway> getPathways() {
            throw new UnsupportedOperationException();
        }

    }

}
