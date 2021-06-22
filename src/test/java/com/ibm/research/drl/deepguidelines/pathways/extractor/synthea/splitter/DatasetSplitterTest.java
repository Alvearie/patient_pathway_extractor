package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.FileParsingUtils;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwaysBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.PatientsParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImageBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.record.Record;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class DatasetSplitterTest {

    @Test
    public void testThatPathwaysOnSplittedDataAreTheSameAsPathwayOnOriginalData() throws IOException {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";

        long now = Instant.parse(LocalDate.now() + Commons.INSTANT_END_OF_DAY).toEpochMilli();
        String[] includedPathwayEventMedicalTypes = new String[] {
                SyntheaMedicalTypes.ALLERGIES.toString(),
                SyntheaMedicalTypes.CAREPLANS.toString(),
                SyntheaMedicalTypes.CONDITIONS.toString(),
                SyntheaMedicalTypes.ENCOUNTERS.toString(),
                SyntheaMedicalTypes.IMAGING_STUDIES.toString(),
                SyntheaMedicalTypes.IMMUNIZATIONS.toString(),
                SyntheaMedicalTypes.MEDICATIONS.toString(),
                SyntheaMedicalTypes.OBSERVATIONS.toString(),
                SyntheaMedicalTypes.PROCEDURES.toString()
        };
        int maxNumberOfPatientsPerChunk = 5;

        List<Pathway> pathwaysOnOriginalData = getPathwaysOnOriginalData(inputDirectoryName, now, includedPathwayEventMedicalTypes);
        assertThat(pathwaysOnOriginalData, notNullValue());

        List<Pathway> pathwaysOnSplittedData = getPathwaysOnSplittedData(inputDirectoryName, now, includedPathwayEventMedicalTypes,
                maxNumberOfPatientsPerChunk);
        assertThat(pathwaysOnSplittedData, notNullValue());

        assertThat(pathwaysOnSplittedData, equalTo(pathwaysOnOriginalData));
    }

    private List<Pathway> getPathwaysOnSplittedData(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes,
            int maxNumberOfPatientsPerChunk)
            throws IOException {
        DatasetSplitter datasetSplitter = new DatasetSplitter(inputDirectoryName);
        String outputDirectoryName = Files.createTempDirectory("DatasetSplitter_test_").toString() + File.separator + UUID.randomUUID().toString();
        List<String> chunkRootDirectoryNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, outputDirectoryName);

        List<Pathway> pathways = new ObjectArrayList<>();
        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            pathways.addAll(getPathwaysFromDirectory(chunkRootDirectoryName, now, includedPathwayEventMedicalTypes));
        }
        return pathways;
    }

    private List<Pathway> getPathwaysOnOriginalData(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes) {
        return getPathwaysFromDirectory(inputDirectoryName, now, includedPathwayEventMedicalTypes);
    }

    private List<Pathway> getPathwaysFromDirectory(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes) {
        InMemoryDataProviderBuilder inMemoryDataProviderBuilder = new InMemoryDataProviderBuilder(now, includedPathwayEventMedicalTypes);
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(inputDirectoryName);
        PathwaysBuilder pathwaysBuilder = new PathwaysBuilder(new String[] {}, now);
        return pathwaysBuilder.build(dataProvider).collect(Collectors.toList());
    }

    @Test
    public void testThatPathwayImagesOnSplittedDataAreTheSameAsPathwayOnOriginalData() throws IOException {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";

        long now = Instant.parse(LocalDate.now() + Commons.INSTANT_END_OF_DAY).toEpochMilli();
        String[] includedPathwayEventMedicalTypes = new String[] {
                SyntheaMedicalTypes.OBSERVATIONS.toString(),
                SyntheaMedicalTypes.IMAGING_STUDIES.toString(),
                SyntheaMedicalTypes.ALLERGIES.toString(),
                SyntheaMedicalTypes.CONDITIONS.toString(),
                SyntheaMedicalTypes.MEDICATIONS.toString(),
                SyntheaMedicalTypes.PROCEDURES.toString(),
                SyntheaMedicalTypes.CAREPLANS.toString()
        };
        int maxNumberOfPatientsPerChunk = 5;
        int maxColumns = 400;

        List<String> pathwayImagesOnOriginalData = getPathwaysImagesOnOriginalData(inputDirectoryName, now, includedPathwayEventMedicalTypes,
                maxColumns);
        assertThat(pathwayImagesOnOriginalData, notNullValue());

        List<String> pathwayImagesOnSplittedData = getPathwayImagesOnSplittedData(inputDirectoryName, now, includedPathwayEventMedicalTypes,
                maxNumberOfPatientsPerChunk, maxColumns);
        assertThat(pathwayImagesOnSplittedData, notNullValue());

        assertThat(pathwayImagesOnSplittedData, equalTo(pathwayImagesOnOriginalData));
    }

    private List<String> getPathwaysImagesOnOriginalData(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes,
            int maxColumns) {
        return getPathwayImagesFromDirectory(inputDirectoryName, now, includedPathwayEventMedicalTypes, maxColumns);
    }

    private List<String> getPathwayImagesOnSplittedData(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes,
            int maxNumberOfPatientsPerChunk, int maxColumns)
            throws IOException {
        DatasetSplitter datasetSplitter = new DatasetSplitter(inputDirectoryName);
        String outputDirectoryName = Files.createTempDirectory("DatasetSplitter_test_").toString() + File.separator + UUID.randomUUID().toString();
        List<String> chunkRootDirectoryNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, outputDirectoryName);

        List<String> pathwayImages = new ObjectArrayList<>();
        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            pathwayImages.addAll(getPathwayImagesFromDirectory(chunkRootDirectoryName, now, includedPathwayEventMedicalTypes, maxColumns));
        }
        return pathwayImages;
    }

    private List<String> getPathwayImagesFromDirectory(String inputDirectoryName, long now, String[] includedPathwayEventMedicalTypes,
            int maxColumns) {
        InMemoryDataProviderBuilder inMemoryDataProviderBuilder = new InMemoryDataProviderBuilder(now, includedPathwayEventMedicalTypes);
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(inputDirectoryName);
        PathwaysBuilder pathwaysBuilder = new PathwaysBuilder(new String[] {}, now);
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        return pathwaysBuilder
                .build(dataProvider)
                .map(pathway -> pathwayMatrixBuilder.build(pathway))
                .map(pathwayMatrix -> pathwayImageBuilder.trimAndConcatenateSlices(pathwayMatrix))
                .filter(pathwayImage -> pathwayImage.columns() <= maxColumns)
                .map(pathwayImage -> pathwayImage.asCSVLine(maxColumns))
                .collect(Collectors.toList());
    }

    /*
     * we test the following conditions for each chunk directory:
     * 1. each line in each file contains a patient id that is contained in the "patients.csv" file in this chunk
     * 2. the union of all the patient ids seen across all the files in this chunk is equal to the set of patients ids from the "patients.csv" file in this chunk
     */
    @Test
    public void testThatEachChunkIsConsistent() throws IOException {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";
        DatasetSplitter datasetSplitter = new DatasetSplitter(inputDirectoryName);
        int maxNumberOfPatientsPerChunk = 5;
        String outputDirectoryName = Files.createTempDirectory("DatasetSplitter_test_").toString() + File.separator + UUID.randomUUID().toString();
        List<String> chunkRootDirectoryNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, outputDirectoryName);

        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            Map<String, Patient> patientsInChunk = new PatientsParser(chunkRootDirectoryName).parse();
            Set<String> seenPatients = new ObjectOpenHashSet<>();
            for (SyntheaMedicalTypes syntheaType : SyntheaMedicalTypes.values()) {
                String fileName = Commons.SYNTHEA_FILE_NAMES.get(syntheaType);
                Iterator<Record> records = FileParsingUtils.getAsIteratorOfRecords(chunkRootDirectoryName + fileName);
                while (records.hasNext()) {
                    Record record = records.next();
                    String patientId = record.getString(Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(syntheaType));
                    assertThat(patientId, notNullValue());
                    assertThat(patientsInChunk.containsKey(patientId), is(true));
                    seenPatients.add(patientId);
                }
            }
            assertThat(seenPatients, equalTo(patientsInChunk.keySet()));
        }
    }

    @Test
    public void testThatTheUnionOfChunkedFilesIsEqualToTheInputFiles() throws IOException {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";
        DatasetSplitter datasetSplitter = new DatasetSplitter(inputDirectoryName);
        int maxNumberOfPatientsPerChunk = 5;
        String outputDirectoryName = Files.createTempDirectory("DatasetSplitter_test_").toString() + File.separator + UUID.randomUUID().toString();
        List<String> chunkRootDirectoryNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, outputDirectoryName);

        for (String fileName : Commons.SYNTHEA_FILE_NAMES.values()) {
            List<String> allLinesInInputFile = Files.readAllLines(FileUtils.getFile(inputDirectoryName + fileName).toPath());
            List<String> allLinesInChunkedFiles = new ObjectArrayList<>();
            for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
                List<String> linesFromChunkedFile = Files.readAllLines(FileUtils.getFile(chunkRootDirectoryName + fileName).toPath());
                if (linesFromChunkedFile.size() > 0) {
                    if (allLinesInChunkedFiles.isEmpty())
                        allLinesInChunkedFiles.addAll(linesFromChunkedFile);
                    else
                        // we don't want to add header line multiple times
                        allLinesInChunkedFiles.addAll(linesFromChunkedFile.subList(1, linesFromChunkedFile.size()));
                }
            }
            assertThat("file " + fileName + " has not been split correctly", allLinesInChunkedFiles, equalTo(allLinesInInputFile));
        }
    }

    @Test
    public void testThatThereAreMaxNumberOfPatientsPerChunk() throws IOException {
        String inputDirectoryName = "synthea_10_patients_seed_3/csv/";
        DatasetSplitter datasetSplitter = new DatasetSplitter(inputDirectoryName);
        int maxNumberOfPatientsPerChunk = 5;
        String outputDirectoryName = Files.createTempDirectory("DatasetSplitter_test_").toString() + File.separator + UUID.randomUUID().toString();
        List<String> chunkRootDirectoryNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, outputDirectoryName);

        long totalNumberOfPatients = FileUtils.computeNumberOfLines(inputDirectoryName + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS))
                .orElseThrow(() -> new IllegalStateException());
        for (String chunkRootDirectoryName : chunkRootDirectoryNames) {
            long numberOfPatientsInChunk = FileUtils
                    .computeNumberOfLines(chunkRootDirectoryName + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS))
                    .orElseThrow(() -> new IllegalStateException());
            assertThat(numberOfPatientsInChunk,
                    anyOf(is(maxNumberOfPatientsPerChunk + 1L), is(totalNumberOfPatients % maxNumberOfPatientsPerChunk))); // +1 because of the header
        }
    }

}
