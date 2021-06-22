package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.FileParsingUtils;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.record.Record;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ChunksDirectoriesTree {

    private static final String CHUNK_DIRECTORY_PREFIX = "chunk_";
    private static final String CHUNK_DIRECTORY_SUFFIX = File.separator + "csv" + File.separator;

    private final Map<String, String> patientId2chunkRootDirectoryName;

    private final List<String> chunkRootDirectoryNames;

    public ChunksDirectoriesTree(String inputDirectoryName, int maxNumberOfPatientsPerChunk, String outputDirectoryName) {
        if (!inputDirectoryName.endsWith(File.separator))
            inputDirectoryName = inputDirectoryName + File.separator;
        if (!outputDirectoryName.endsWith(File.separator))
            outputDirectoryName = outputDirectoryName + File.separator;

        String inputPatientsFile = inputDirectoryName + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS);
        long totalNumberOfPatients = FileUtils
                .computeNumberOfLines(inputPatientsFile)
                .orElseThrow(() -> new IllegalStateException("error while counting lines in file " + inputPatientsFile));
        if (totalNumberOfPatients > Integer.MAX_VALUE)
            throw new IllegalStateException("well, that's a little bit too much work for today :)");
        patientId2chunkRootDirectoryName = new Object2ObjectOpenHashMap<>((int) totalNumberOfPatients);
        chunkRootDirectoryNames = new ObjectArrayList<>();

        Iterator<Record> iteratorOfPatientRecords = FileParsingUtils.getAsIteratorOfRecords(inputPatientsFile);
        int currentChunk = 0;
        int numberOfPatientsInCurrentChunk = 0;
        String currentChunkRootDirectoryName = String.join(
                "",
                outputDirectoryName, CHUNK_DIRECTORY_PREFIX + String.valueOf(currentChunk), CHUNK_DIRECTORY_SUFFIX);
        chunkRootDirectoryNames.add(currentChunkRootDirectoryName);
        while (iteratorOfPatientRecords.hasNext()) {
            Record patientRecord = iteratorOfPatientRecords.next();
            String patientId = patientRecord.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(SyntheaMedicalTypes.PATIENTS));
            if (numberOfPatientsInCurrentChunk + 1 > maxNumberOfPatientsPerChunk) {
                currentChunk++;
                numberOfPatientsInCurrentChunk = 0;
                currentChunkRootDirectoryName = String.join(
                        "",
                        outputDirectoryName, CHUNK_DIRECTORY_PREFIX + String.valueOf(currentChunk), CHUNK_DIRECTORY_SUFFIX);
                chunkRootDirectoryNames.add(currentChunkRootDirectoryName);
            }
            patientId2chunkRootDirectoryName.put(patientId, currentChunkRootDirectoryName);
            numberOfPatientsInCurrentChunk++;
        }
    }

    public String getChunkRootDirectoryNameFor(String patientId) {
        return patientId2chunkRootDirectoryName.get(patientId);
    }

    public List<String> getChunkRootDirectoryNames() {
        return Collections.unmodifiableList(chunkRootDirectoryNames);
    }

}
