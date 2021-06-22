package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImageBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_issue93.properties")
public class Issue93Test {

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;
    
    @Autowired
    private PathwaysBuilder pathwaysBuilder;
    
    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.pathway.image.max.columns}") 
    private int maxColumns;
    
    @Test
    public void test() {
        String syntheaDataPath = "simple_dataset_to_test_issue_93/csv/";
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder
                .build(dataProvider)
                .collect(Collectors.toList());
        assertThat(actualPathways.size(), equalTo(1));
        Pathway pathway = actualPathways.get(0);
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        OutputFormatter outputFormatter = new OutputFormatter();
        PathwayMatrix pathwayMatrix = pathwayMatrixBuilder.build(pathway);
        PathwayImage pathwayImage = pathwayImageBuilder.trimAndConcatenateSlices(pathwayMatrix);
        String pathwayAsCsvLine = outputFormatter.format(pathway, pathwayImage, maxColumns);
        String[] fields = pathwayAsCsvLine.split(",");
        String codeOfTheConditionThatOriginatedThePathway = fields[4];
        assertThat(codeOfTheConditionThatOriginatedThePathway, equalTo("44054006"));
    }
    
}
