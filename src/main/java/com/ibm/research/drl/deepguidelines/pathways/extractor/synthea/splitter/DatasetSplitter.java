package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;

public class DatasetSplitter {

    private final static Logger LOG = LoggerFactory.getLogger(DatasetSplitter.class);

    private final String inputDirectoryName;
    private final InputDataParser inputDataParser;

    public DatasetSplitter(String inputDirectoryName, InputDataParser inputDataParser) {
        this.inputDirectoryName = (inputDirectoryName.endsWith(File.separator))
                ? inputDirectoryName
                : inputDirectoryName + File.separator;
        this.inputDataParser = inputDataParser;
    }

    public List<String> split(int maxNumberOfPatientsPerChunk, String outputDirectoryName) throws IOException {
        LOG.info("splitting dataset from " + inputDirectoryName + " to " + outputDirectoryName);
        Path outputDirectoryPath = Paths.get(outputDirectoryName);
        if (Files.exists(outputDirectoryPath))
            throw new IllegalArgumentException(outputDirectoryName + "already exists");
        Files.createDirectories(outputDirectoryPath);

        ChunksDirectoriesTree chunksDirectoriesTree = new ChunksDirectoriesTree(inputDirectoryName, maxNumberOfPatientsPerChunk,
                outputDirectoryName, inputDataParser);
        buildChunkRootDirectoryPaths(chunksDirectoriesTree.getChunkRootDirectoryNames());
        split(chunksDirectoriesTree);
        LOG.info("finished splitting dataset from " + inputDirectoryName + " to " + outputDirectoryName);
        return chunksDirectoriesTree.getChunkRootDirectoryNames();
    }

    private void buildChunkRootDirectoryPaths(List<String> chunkRootDirectoryNames) throws IOException {
        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            Path chunkRootDirectoryPath = Paths.get(chunkRootDirectoryName);
            Files.createDirectories(chunkRootDirectoryPath);
        }
    }

    private void split(ChunksDirectoriesTree chunksDirectoriesTree) throws IOException {
        FileSplitter fileSplitter = new FileSplitter(inputDirectoryName, inputDataParser);
        for (SyntheaMedicalTypes syntheaType : SyntheaMedicalTypes.values()) {
            fileSplitter.split(chunksDirectoriesTree, syntheaType);
        }
    }

}