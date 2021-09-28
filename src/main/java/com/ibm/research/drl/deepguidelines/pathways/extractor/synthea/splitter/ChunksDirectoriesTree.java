package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ibm.fhir.model.type.code.FHIRResourceType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ChunksDirectoriesTree {

    private final Map<String, String> patientId2chunkRootDirectoryName;

    private final List<String> chunkRootDirectoryNames;

    public ChunksDirectoriesTree(String inputDirectoryName, int maxNumberOfPatientsPerChunk, String outputDirectoryName,
            InputDataParser inputDataParser) {
        if (!inputDirectoryName.endsWith(File.separator))
            inputDirectoryName = inputDirectoryName + File.separator;
        if (!outputDirectoryName.endsWith(File.separator))
            outputDirectoryName = outputDirectoryName + File.separator;

        final String CHUNK_DIRECTORY_PREFIX = outputDirectoryName + "chunk_";
        final String CHUNK_DIRECTORY_SUFFIX = File.separator + "csv" + File.separator;

        String inputPatientsFile = inputDirectoryName +
                        (inputDataParser instanceof SyntheaCsvInputDataParser ?
                        Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS) :
                        Commons.FHIR_FILE_NAMES.get(FHIRResourceType.Value.PATIENT));
        long totalNumberOfPatients = FileUtils
                .computeNumberOfLines(inputPatientsFile)
                .orElseThrow(() -> new IllegalStateException("error while counting lines in file " + inputPatientsFile));
        if (totalNumberOfPatients > Integer.MAX_VALUE)
            throw new IllegalStateException("well, that's a little bit too much work for today :)");
        patientId2chunkRootDirectoryName = new Object2ObjectOpenHashMap<>((int) totalNumberOfPatients);
        chunkRootDirectoryNames = new ObjectArrayList<>();

        String currentChunkRootDirectoryName = CHUNK_DIRECTORY_PREFIX + "0" + CHUNK_DIRECTORY_SUFFIX;
        chunkRootDirectoryNames.add(currentChunkRootDirectoryName);
        List<String> patientIds = new ObjectArrayList<>();
        inputDataParser.readAsStreamOfRecords(inputDirectoryName, SyntheaMedicalTypes.PATIENTS)
            .forEach(record -> {
                String patientId = inputDataParser.getPatientId(record, SyntheaMedicalTypes.PATIENTS);
                if (patientIds.size() + 1 > maxNumberOfPatientsPerChunk) {
                    patientIds.clear();
                    String chunkRootDirectoryName = CHUNK_DIRECTORY_PREFIX + String.valueOf(chunkRootDirectoryNames.size()) +
                            CHUNK_DIRECTORY_SUFFIX;
                    chunkRootDirectoryNames.add(chunkRootDirectoryName);
                }
                patientId2chunkRootDirectoryName.put(patientId, chunkRootDirectoryNames.get(chunkRootDirectoryNames.size()-1));
                patientIds.add(patientId);
            });
    }

    public String getChunkRootDirectoryNameFor(String patientId) {
        return patientId2chunkRootDirectoryName.get(patientId);
    }

    public List<String> getChunkRootDirectoryNames() {
        return Collections.unmodifiableList(chunkRootDirectoryNames);
    }

}
