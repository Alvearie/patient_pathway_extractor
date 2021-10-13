package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.DifferenceUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_synthea_1_patient_seed_3_all_medical_types.properties")
public class PathwaysBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger(PathwaysBuilderTest.class);

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Autowired
    private SimpleDataProviderBuilder simpleDataProviderBuilder;

    @Test
    public void testBuild() {
        long now = Instant.now().toEpochMilli();
        String syntheaDataPath = "synthea_1_patient_seed_3/csv/";
        List<Pathway> expectedPathways = simpleDataProviderBuilder.build(syntheaDataPath, new SyntheaCsvInputDataParser(now))
                .getPathways()
                .collect(Collectors.toList());
        List<Pathway> actualPathways = inMemoryDataProviderBuilder.build(syntheaDataPath, new SyntheaCsvInputDataParser(now))
                .getPathways()
                .collect(Collectors.toList());
        assertThat(actualPathways, notNullValue());
        assertThat(actualPathways.size(), equalTo(expectedPathways.size()));
        if (!actualPathways.equals(expectedPathways)) comparePathways(actualPathways, expectedPathways);
        assertThat(actualPathways, equalTo(expectedPathways));
    }
    
    @Test
    public void testIssue93() {
        
    }

    private void comparePathways(List<Pathway> actualPathways, List<Pathway> expectedPathways) {
        Iterator<Pathway> iteratorActualPathways = actualPathways.iterator();
        Iterator<Pathway> iteratorExpectedPathways = expectedPathways.iterator();
        int n = 0;
        while (iteratorActualPathways.hasNext()) {
            LOG.info("-----------------------------");
            LOG.info("comparing pathway at position " + n);
            PathwayEventsLine actualPathwayEventsLine = iteratorActualPathways.next().getPathwayEventsLine();
            PathwayEventsLine expectedPathwayEventsLine = iteratorExpectedPathways.next().getPathwayEventsLine();
            if (actualPathwayEventsLine.equals(expectedPathwayEventsLine)) {
                LOG.info("pathway events line are the same");
            } else {
                String diff = DifferenceUtils.diff(
                        new ObjectArrayList<>(actualPathwayEventsLine), 
                        new ObjectArrayList<>(expectedPathwayEventsLine));
                LOG.info("diff:\n" + diff);
            }
            n++;
        }
    }

}
