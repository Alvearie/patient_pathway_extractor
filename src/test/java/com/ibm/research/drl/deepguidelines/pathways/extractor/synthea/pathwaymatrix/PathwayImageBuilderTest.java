package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
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

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwaysBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.ExpectedDataForSynthea1PatientSeed3;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_synthea_1_patient_seed_3.properties")
public class PathwayImageBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger(PathwayImageBuilderTest.class);

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Autowired
    private PathwaysBuilder pathwaysBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path}")
    private String syntheaDataPath;

    private PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();

    @Test
    public void testTrimAndConcatenateSlices() throws IOException {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        Pathway pathway = pathwaysBuilder.build(dataProvider).collect(Collectors.toList()).get(2);
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(pathway);
        PathwayImage actualPathwayImage = pathwayImageBuilder.trimAndConcatenateSlices(actualPathwayMatrix);
        PathwayImage expectedPathwayImage = ExpectedDataForSynthea1PatientSeed3
                .getPathwayImageForCondition3WithTrimmedAndConcatenatedSlices();
        assertThat(actualPathwayImage, equalTo(expectedPathwayImage));
        LOG.info("pathway events line: "
                + FileUtils.writeToTempFile(pathway.getPathwayEventsLine().asHTMLTable(), "pathway_events_line_", ".html"));
        LOG.info("pathway image with concatenated slices: "
                + FileUtils.writeToTempFile(expectedPathwayImage.asHTMLTable(), "pathway_image_", ".html"));
    }

    @Test
    public void testCollapsedSlices() throws IOException {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        Pathway pathway = pathwaysBuilder.build(dataProvider).collect(Collectors.toList()).get(2);
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(pathway);
        PathwayImage actualPathwayImage = pathwayImageBuilder.collapseSlices(actualPathwayMatrix);
        PathwayImage expectedPathwayImage = ExpectedDataForSynthea1PatientSeed3.getPathwayImageForCondition3WithCollapsedSlices();
        assertThat(actualPathwayImage, equalTo(expectedPathwayImage));
        LOG.info("pathway events line: "
                + FileUtils.writeToTempFile(pathway.getPathwayEventsLine().asHTMLTable(), "pathway_events_line_", ".html"));
        LOG.info("pathway image with concatenated slices: "
                + FileUtils.writeToTempFile(expectedPathwayImage.asHTMLTable(), "pathway_image_", ".html"));
    }

}
