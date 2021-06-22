package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
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

import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_issue31.properties")
public class Issue31Test {

    private static final Logger LOG = LoggerFactory.getLogger(Issue31Test.class);

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Autowired
    private PathwaysBuilder pathwaysBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path}")
    private String syntheaDataPath;

    @Test
    public void test() throws IOException {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<Pathway> actualPathways = pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
        assertThat(actualPathways, notNullValue());
        assertThat(actualPathways.size(), equalTo(1));
        PathwayEventsLine actualPathwayEventsLine = actualPathways.get(0).getPathwayEventsLine();
        PathwayEventsLine expectedPathwayEventsLine = getExpectedPathwayEventsLine();
        assertThat(actualPathwayEventsLine, equalTo(expectedPathwayEventsLine));
        LOG.info("pathway events line:" + FileUtils.writeToTempFile(actualPathwayEventsLine.asHTMLTable(), "pathway_events_line_", ".html"));
    }

    private PathwayEventsLine getExpectedPathwayEventsLine() {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.START,
                        "Condition_1",
                        Instant.parse("1981-11-11" + Commons.INSTANT_START_OF_DAY).toEpochMilli()));
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.MEDICATIONS,
                        PathwayEventTemporalType.STOP,
                        "Medication_1",
                        Instant.parse("1985-11-11" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        pathwayEventsLine.add(
                new PathwayEvent(
                        SyntheaMedicalTypes.CONDITIONS,
                        PathwayEventTemporalType.STOP,
                        "Condition_1",
                        Instant.parse("1991-11-11" + Commons.INSTANT_END_OF_DAY).toEpochMilli()));
        return pathwayEventsLine;
    }

}
