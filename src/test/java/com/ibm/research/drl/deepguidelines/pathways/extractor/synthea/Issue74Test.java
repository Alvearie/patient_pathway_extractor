package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.AliveOutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.ConditionsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.DemographicsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.MedicationsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.OutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImageBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_issue74.properties")
public class Issue74Test {

    private static final Logger LOG = LoggerFactory.getLogger(Issue74Test.class);

    private static final DemographicsPathwayMatrixCell DEMOGRAPHICS_PATHWAY_MATRIX_CELL = new DemographicsPathwayMatrixCell("1965-12-08", "M",
            "white", "F");

    private static final OutcomePathwayMatrixCell OUTCOME_PATHWAY_MATRIX_CELL = new AliveOutcomePathwayMatrixCell(3652);

    private static final Map<Integer, List<PathwayMatrixCell>> PATHWAY_MATRIX_CELLS_BY_SLICE = new Int2ObjectOpenHashMap<>();

    static {
        DEMOGRAPHICS_PATHWAY_MATRIX_CELL.setAgeBucket("DemF");
        OUTCOME_PATHWAY_MATRIX_CELL.setOutcomeBucket("AliveG");

        // slice 0
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(0, new ObjectArrayList<>());
        // Condition_1 START
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice0Cell0 = new ConditionsPathwayMatrixCell("74400008", PathwayEventTemporalType.START);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).add(pathwayMatrixCellSlice0Cell0);
        // Condition_2 START
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice0Cell1 = new ConditionsPathwayMatrixCell("428251008", PathwayEventTemporalType.START);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).add(pathwayMatrixCellSlice0Cell1);

        // slice 1
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(1, new ObjectArrayList<>());
        // Medication_1 START
        MedicationsPathwayMatrixCell pathwayMatrixCellSlice1Cell0 = new MedicationsPathwayMatrixCell("389221", 12, PathwayEventTemporalType.START);
        pathwayMatrixCellSlice1Cell0.setDispensesBucket("MedC");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).add(pathwayMatrixCellSlice1Cell0);

        // slice 2
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(2, new ObjectArrayList<>());
        // Condition_2 STOP
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice2Cell0 = new ConditionsPathwayMatrixCell("428251008", PathwayEventTemporalType.STOP);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).add(pathwayMatrixCellSlice2Cell0);
        // Medication_1 STOP
        MedicationsPathwayMatrixCell pathwayMatrixCellSlice2Cell1 = new MedicationsPathwayMatrixCell("389221", 12, PathwayEventTemporalType.STOP);
        pathwayMatrixCellSlice2Cell1.setDispensesBucket("MedC");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).add(pathwayMatrixCellSlice2Cell1);

        // slice 3
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(3, new ObjectArrayList<>());
        // Condition_1 START
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice3Cell0 = new ConditionsPathwayMatrixCell("74400008", PathwayEventTemporalType.STOP);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).add(pathwayMatrixCellSlice3Cell0);
    }

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Autowired
    private PathwaysBuilder pathwaysBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path}")
    private String syntheaDataPath;

    @Test
    public void testThatPathwayEventsLineAreEquals() throws IOException {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
        assertThat(actualPathways, notNullValue());
        assertThat(actualPathways.size(), equalTo(1));
        PathwayEventsLine actualPathwayEventsLine = actualPathways.get(0).getPathwayEventsLine();
        PathwayEventsLine expectedPathwayEventsLine = getExpectedPathwayEventsLine();
        assertThat(actualPathwayEventsLine, equalTo(expectedPathwayEventsLine));
        LOG.info("pathway events line:" + FileUtils.writeToTempFile(actualPathwayEventsLine.asHTMLTable(), "pathway_events_line_", ".html"));
    }

    @Test
    public void testThatPathwayMatricesAreEqual() {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(actualPathways.get(0));
        PathwayMatrix expectedPathwayMatrix = getExpectedPathwayMatrix();
        assertThat(actualPathwayMatrix, equalTo(expectedPathwayMatrix));
    }
    
    @Test
    public void testThatPahtwayImagesWithTrimmedAndConcatenateSlicesAreEqual() {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(actualPathways.get(0));
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        PathwayImage actualPathwayImage = pathwayImageBuilder.trimAndConcatenateSlices(actualPathwayMatrix);
        PathwayImage expectedPathwayImage = getExpectedPathwayImageWithTrimmedAndConcatenateSlices();
        assertThat(actualPathwayImage, equalTo(expectedPathwayImage));
    }
    
    @Test
    public void testThatPahtwayImagesWithCollapsedSlicesAreEqual() {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(actualPathways.get(0));
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        PathwayImage actualPathwayImage = pathwayImageBuilder.collapseSlices(actualPathwayMatrix);
        PathwayImage expectedPathwayImage = getExpectedPathwayImageWithCollapsedSlices();
        assertThat(actualPathwayImage, equalTo(expectedPathwayImage));
    }

    private PathwayEventsLine getExpectedPathwayEventsLine() {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        // slice 0
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.START,
                        "Condition_1",
                        Instant.parse("1981-11-11" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.START,
                        "Condition_2",
                        Instant.parse("1981-11-11" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        // slice 1
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.START,
                        "Medication_1",
                        Instant.parse("1982-11-11" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        // slice 2
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_2",
                        Instant.parse("1985-11-11" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.STOP,
                        "Medication_1",
                        Instant.parse("1985-11-11" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        // slice 3
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_1",
                        Instant.parse("1991-11-11" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        return pathwayEventsLine;
    }

    private PathwayMatrix getExpectedPathwayMatrix() {
        PathwayMatrix pathwayMatrix = new PathwayMatrix(4, 6, 2);
        // demographics
        pathwayMatrix.setQuick(0, 0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL);
        // slice 0
        pathwayMatrix.setQuick(0, 2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0));
        pathwayMatrix.setQuick(0, 2, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1));
        // slice 1
        pathwayMatrix.setQuick(1, 3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0));
        // slice 2
        pathwayMatrix.setQuick(2, 2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0));
        pathwayMatrix.setQuick(2, 3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(1));
        // slice 3
        pathwayMatrix.setQuick(3, 2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0));
        // outcomes
        pathwayMatrix.setQuick(3, 5, 0, OUTCOME_PATHWAY_MATRIX_CELL);
        // done
        return pathwayMatrix;
    }
    
    private PathwayImage getExpectedPathwayImageWithTrimmedAndConcatenateSlices() {
        PathwayImage pathwayImage = new PathwayImage(6, 5);
        // demographics
        pathwayImage.setQuick(0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL.asStringValue());
        // slice 0
        pathwayImage.setQuick(2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0).asStringValue());
        pathwayImage.setQuick(2, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1).asStringValue());
        // slice 1
        pathwayImage.setQuick(3, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0).asStringValue());
        // slice 2
        pathwayImage.setQuick(2, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0).asStringValue());
        pathwayImage.setQuick(3, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(1).asStringValue());
        // slice 3
        pathwayImage.setQuick(2, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0).asStringValue());
        // outcomes
        pathwayImage.setQuick(5, 4, OUTCOME_PATHWAY_MATRIX_CELL.asStringValue());
        return pathwayImage;
    }
    
    private PathwayImage getExpectedPathwayImageWithCollapsedSlices() {
        PathwayImage pathwayImage = new PathwayImage(6, 4);
        // demographics
        pathwayImage.appendQuick(0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL.asStringValue());
        // slice 0
        pathwayImage.appendQuick(2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0).asStringValue());
        pathwayImage.appendQuick(2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1).asStringValue());
        // slice 1
        pathwayImage.appendQuick(3, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0).asStringValue());
        // slice 2
        pathwayImage.appendQuick(2, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0).asStringValue());
        pathwayImage.appendQuick(3, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(1).asStringValue());
        // slice 3
        pathwayImage.appendQuick(2, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0).asStringValue());
        // outcomes
        pathwayImage.appendQuick(5, 3, OUTCOME_PATHWAY_MATRIX_CELL.asStringValue());
        return pathwayImage;
    }

}
