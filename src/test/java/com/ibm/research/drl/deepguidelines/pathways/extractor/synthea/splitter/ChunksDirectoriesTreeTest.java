package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.FileParsingUtils;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.univocity.parsers.common.record.Record;

public class ChunksDirectoriesTreeTest {

    @Test
    public void test() {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";
        int maxNumberOfPatientsPerChunk = 5;
        String outputDirectoryName = "output/";
        ChunksDirectoriesTree chunksDirectoriesTree = new ChunksDirectoriesTree(inputDirectoryName, maxNumberOfPatientsPerChunk, outputDirectoryName);
        assertThat(chunksDirectoriesTree, notNullValue());
        List<String> chunkRootDirectoryNames = chunksDirectoriesTree.getChunkRootDirectoryNames();
        assertThat(chunkRootDirectoryNames, notNullValue());
        assertThat(chunkRootDirectoryNames.size(), equalTo(3));
        assertThat(
                chunkRootDirectoryNames,
                equalTo(Arrays.asList("output/chunk_0/csv/", "output/chunk_1/csv/", "output/chunk_2/csv/")));
        Iterator<Record> records = FileParsingUtils
                .getAsIteratorOfRecords(inputDirectoryName + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS));
        int i = 0;
        while (records.hasNext()) {
            Record record = records.next();
            String patientId = record.getString("ID");
            String chunkRootDirectoryName = chunksDirectoriesTree.getChunkRootDirectoryNameFor(patientId);
            assertThat(chunkRootDirectoryName, notNullValue());
            int chunk = i / maxNumberOfPatientsPerChunk;
            assertThat(chunkRootDirectoryName, equalTo(outputDirectoryName + "chunk_" + chunk + File.separator + "csv" + File.separator));
            i++;
        }
    }

}
