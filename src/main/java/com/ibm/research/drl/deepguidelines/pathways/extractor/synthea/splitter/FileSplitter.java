package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.univocity.parsers.common.record.Record;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class FileSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

    private static final byte[] NEW_LINE_BYTES = System.lineSeparator().getBytes();

    private final String inputDirectoryName;
    private final InputDataParser inputDataParser;

    public FileSplitter(String inputDirectoryName, InputDataParser inputDataParser) {
        this.inputDirectoryName = (inputDirectoryName.endsWith(File.separator)) ? inputDirectoryName : inputDirectoryName + File.separator;
        this.inputDataParser = inputDataParser;
    }

    public void split(ChunksDirectoriesTree chunksDirectoriesTree, SyntheaMedicalTypes syntheaType) throws IOException {
        String fileName = inputDataParser.getFileName(syntheaType);
        LOG.info("splitting file " + fileName);
        List<String> chunkRootDirectoryNames = chunksDirectoriesTree.getChunkRootDirectoryNames();
        Set<RandomAccessFile> chunkRandomAccessFiles = new ObjectOpenHashSet<>(chunkRootDirectoryNames.size());
        Map<String, FileChannel> chunkRootDirectoryName2fileChannel = new Object2ObjectOpenHashMap<>(chunkRootDirectoryNames.size());
        // Map[ChunkRootDirectoryName, Boolean
        // mustPrintColumnNamesForFileInChunkRootDirectoryName.get(X) = true means that we must print column names for the file in chunk root directory X
        // mustPrintColumnNamesForFileInChunkRootDirectoryName.get(X) = false means that we have already printed column names for the file in chunk root directory X
        Map<String, Boolean> mustPrintColumnNamesForFileInChunkRootDirectoryName = new Object2BooleanOpenHashMap<>();
        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            String chunkFileName = chunkRootDirectoryName + fileName;
            RandomAccessFile chunkRandomAccessFile = new RandomAccessFile(chunkFileName, "rw");
            chunkRandomAccessFiles.add(chunkRandomAccessFile);
            chunkRootDirectoryName2fileChannel.put(chunkRootDirectoryName, chunkRandomAccessFile.getChannel());
            mustPrintColumnNamesForFileInChunkRootDirectoryName.put(chunkRootDirectoryName, true);
        }
        inputDataParser.readAsStreamOfRecords(inputDirectoryName, syntheaType)
            .forEach(record -> {
                try {
                    String patientId = inputDataParser.getPatientId(record, syntheaType);
                    String chunkRootDirectoryName = chunksDirectoriesTree.getChunkRootDirectoryNameFor(patientId);
                    FileChannel fileChannel = chunkRootDirectoryName2fileChannel.get(chunkRootDirectoryName);

                    if (chunkRootDirectoryName == null) {
                        LOG.error("chunkRootDirectoryName is null"
                                + "; patientId = " + patientId
                                + "; syntheaType = " + syntheaType
                                + "; fileName = " + fileName
                                + "; record = " + record);
                    }

                    if (mustPrintColumnNamesForFileInChunkRootDirectoryName.get(chunkRootDirectoryName) == null) {
                        LOG.error("mustPrintColumnNamesForFileInChunkRootDirectoryName.get(chunkRootDirectoryName) is null"
                                + "; chunkRootDirectoryName = " + chunkRootDirectoryName
                                + "; patientId = " + patientId
                                + "; syntheaType = " + syntheaType
                                + "; fileName = " + fileName
                                + "; record = " + record);
                    }

                    if (mustPrintColumnNamesForFileInChunkRootDirectoryName.get(chunkRootDirectoryName)) {
                        byte[] headerBytes = String.join(",", Commons.SYNTHEA_COLUMN_NAMES.get(syntheaType)).getBytes();
                        writeWithNewLine(headerBytes, fileChannel);
                        mustPrintColumnNamesForFileInChunkRootDirectoryName.put(chunkRootDirectoryName, false);
                    }
                    byte[] lineBytes = String.join(",", getAllValues(record, syntheaType)).getBytes();
                    writeWithNewLine(lineBytes, fileChannel);
                } catch (IOException e) {
                    LOG.error("failed to write split file output");
                    throw new RuntimeException(e);
                }
            });
        for (FileChannel fileChannel : chunkRootDirectoryName2fileChannel.values()) {
            fileChannel.close();
        }
        for (RandomAccessFile randomAccessFile : chunkRandomAccessFiles) {
            randomAccessFile.close();
        }
        LOG.info("finished splitting file " + fileName);
    }

    /*
     * this method ensure that record values are returned in the same order as the column-names
     */
    private String[] getAllValues(Record record, SyntheaMedicalTypes syntheaType) {
        String[] columnNames = Commons.SYNTHEA_COLUMN_NAMES.get(syntheaType);
        String[] allValues = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            allValues[i] = record.getString(columnNames[i]);
            if (allValues[i] == null) allValues[i] = "";
        }
        return allValues;
    }

    private void writeWithNewLine(byte[] bytes, FileChannel fileChannel) throws IOException {
        byte[] withNewLine = appendNewLine(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(withNewLine);
        fileChannel.write(byteBuffer);
    }

    private byte[] appendNewLine(byte[] bytes) {
        byte[] result = new byte[bytes.length + NEW_LINE_BYTES.length];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        System.arraycopy(NEW_LINE_BYTES, 0, result, bytes.length, NEW_LINE_BYTES.length);
        return result;
    }

}
