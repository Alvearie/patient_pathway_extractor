package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ChunksDirectoriesTreeTest {

    @Test
    public void test() {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";
        int maxNumberOfPatientsPerChunk = 5;
        String outputDirectoryName = "output" + File.separator;
        InputDataParser inputDataParser = new SyntheaCsvInputDataParser(Instant.now().toEpochMilli());
        ChunksDirectoriesTree chunksDirectoriesTree = new ChunksDirectoriesTree(inputDirectoryName, maxNumberOfPatientsPerChunk, outputDirectoryName,
            inputDataParser);
        assertThat(chunksDirectoriesTree, notNullValue());
        List<String> chunkRootDirectoryNames = chunksDirectoriesTree.getChunkRootDirectoryNames();
        assertThat(chunkRootDirectoryNames, notNullValue());
        assertThat(chunkRootDirectoryNames.size(), equalTo(3));
        assertThat(
                chunkRootDirectoryNames,
                equalTo(Arrays.asList("output" + File.separator + "chunk_0" + File.separator + "csv" + File.separator,
                    "output" + File.separator + "chunk_1" + File.separator + "csv" + File.separator,
                    "output" + File.separator + "chunk_2" + File.separator + "csv" + File.separator)));
        List<String> patientIds = new ObjectArrayList<>();
        inputDataParser.readAsStreamOfRecords(inputDirectoryName, SyntheaMedicalTypes.PATIENTS)
            .forEach(record -> {
                String patientId = record.getString("ID");
                String chunkRootDirectoryName = chunksDirectoriesTree.getChunkRootDirectoryNameFor(patientId);
                assertThat(chunkRootDirectoryName, notNullValue());
                int chunk = patientIds.size() / maxNumberOfPatientsPerChunk;
                assertThat(chunkRootDirectoryName, equalTo(outputDirectoryName + "chunk_" + chunk + File.separator + "csv" + File.separator));
                patientIds.add(patientId);
            });
    }

}
