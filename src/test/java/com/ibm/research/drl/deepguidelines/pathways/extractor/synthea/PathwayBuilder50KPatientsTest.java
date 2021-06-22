package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.DifferenceUtils;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/*
 * WARNING: Tests in this class uses 50000 Synthea patients, and therefore they are
 * _VERY_MUCH_ time and resources consuming! Run with care!
 *
 * You must generate a Synthea dataset with 50000 patients using seed 3:
 *
 * ./run_synthea -p 50000 -s 3
 *
 * (we Synthea code with commit id 5a44709c3a12eee32a3b38cb8d502b944580a276)
 *
 * You must specify the correct path of the 50000 Synthea dataset in the properties file
 * application_synthea_50K_patients_seed_3.properties, for example:
 *
 * com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path=/Users/marco/a/50k_patients_seed_3/csv/
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_synthea_50K_patients_seed_3.properties")
public class PathwayBuilder50KPatientsTest {

    private static final Logger LOG = LoggerFactory.getLogger(PathwayBuilder50KPatientsTest.class);

    private final int MAX_PATHWAYS = 10;

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;

    @Autowired
    private SimpleDataProviderBuilder simpleDataProviderBuilder;

    @Autowired
    private PathwaysBuilder pathwaysBuilder;

    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path}")
    private String syntheaDataPath;

    //    This file "synthea_50k_patients_seed_3_expected_pathways_ids.csv" contains the id of he expected pathways 
    //    for the 50K-patients-seed-3 Synthea dataset.
    //    I've generated this file from PostgreSQL using the following SQL
    //
    //    copy(
    //            select 
    //                concat(start, 'T00:00:00Z') as text_start,
    //                case when stop is null then 'yyyy-MM-ddT23:59:59Z' else concat(stop, 'T23:59:59Z') end as text_stop,
    //                patient, 
    //                encounter
    //            from conditions
    //            group by text_start, text_stop, patient, encounter
    //    ) to '/Users/marco/a/a.csv' with csv delimiter ','
    //
    //    When 'stop' is null, I write in the output file 'yyyy-MM-ddT23:59:59Z', and replace 'yyyy-MM-dd' with the
    //    current date at run-time (see function getExpectedPathwaysIds), so that dates match with what are generated 
    //    in the code.
    @Ignore
    public void testThatAllPathwaysAreBuilt() throws IOException {
        String fileNameForExpectedPathwayIds = "synthea_50k_patients_seed_3_expected_pathways_ids_with_included_conditions_codes.csv";
        List<String> expectedPathwaysIds = getExpectedPathwaysIds(fileNameForExpectedPathwayIds);
        Set<String> uniqueExpectedPathwaysIds = new ObjectOpenHashSet<>(expectedPathwaysIds);
        assertThat(uniqueExpectedPathwaysIds.size(), equalTo(expectedPathwaysIds.size()));
        LOG.info("***** the list of expected pathways ids have no duplicates");
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath);
        List<String> actualPathwaysIds = pathwaysBuilder
                .build(dataProvider)
                .map(Pathway::getId)
                .collect(Collectors.toList());
        Set<String> uniqueActualPathwaysIds = new ObjectOpenHashSet<>(actualPathwaysIds);
        assertThat(uniqueActualPathwaysIds.size(), equalTo(actualPathwaysIds.size()));
        LOG.info("***** the list of actual pathways ids have no duplicates");
        assertThat(uniqueActualPathwaysIds.size(), equalTo(uniqueExpectedPathwaysIds.size()));
        LOG.info("***** actual and expected sizes are equal");
        LOG.info("***** testing for set equality ...");
        assertThat(uniqueActualPathwaysIds.equals(uniqueExpectedPathwaysIds), is(true));
        LOG.info("***** actual and expected pathways ids are equal");
    }

    private List<String> getExpectedPathwaysIds(String fileName) throws IOException {
        List<String> expectedPathwaysIds = new ObjectArrayList<>();
        LocalDate today = LocalDate.now();
        BufferedReader br = new BufferedReader(new FileReader(FileUtils.getFile(fileName)));
        String line = br.readLine();
        while (line != null) {
            int i = line.indexOf("yyyy-MM-dd");
            if (i == -1)
                expectedPathwaysIds.add(line);
            else {
                StringBuilder sb = new StringBuilder();
                sb.append(line.substring(0, i))
                        .append(today)
                        .append(line.substring(i + 10)); // 10 is the length of the string yyyy-MM-dd
                expectedPathwaysIds.add(sb.toString());
            }
            line = br.readLine();
        }
        br.close();
        return expectedPathwaysIds;
    }

    @Ignore
    public void testThatPathwaysAreEqual() {
        Iterator<Pathway> actualPathways = pathwaysBuilder
                .build(inMemoryDataProviderBuilder.build(syntheaDataPath))
                .limit(MAX_PATHWAYS)
                .collect(Collectors.toList())
                .iterator();
        Stream<Pathway> expectedPathways = pathwaysBuilder
                .build(simpleDataProviderBuilder.build(syntheaDataPath));
        assertThat(expectedPathways, notNullValue());
        expectedPathways.forEach(expectedPathway -> {
            Pathway actualPathway = actualPathways.next();
            String actualId = actualPathway.getId();
            String expectedId = expectedPathway.getId();
            assertThat("pathways with different ids", actualId, equalTo(expectedId));
            if (!actualPathway.equals(expectedPathway)) {
                String diff = DifferenceUtils.diff(
                        new ObjectArrayList<>(actualPathway.getPathwayEventsLine()), 
                        new ObjectArrayList<>(expectedPathway.getPathwayEventsLine()));
                LOG.error("pathways with id " + actualId + " are different:" + System.lineSeparator() + diff);
            }
            assertThat(actualPathway, equalTo(expectedPathway));
            LOG.info("***** pathways with id " + actualId + " are equal");
        });
    }

}
