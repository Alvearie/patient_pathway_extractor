package com.ibm.research.drl.deepguidelines.pathways.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.OutputFormatter;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayImagesWriter;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.InMemoryDataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.FhirNdjsonInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImageBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.splitter.DatasetSplitter;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization.IntervalTreeVisualizationBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization.PathwayImageVisualizationBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization.PathwayMatrixVisualizationBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization.PathwayVisualizationBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SpringBootApplication
public class PatientPathwayExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PatientPathwayExtractor.class);

    public static void main(String[] args) throws ParseException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(PatientPathwayExtractor.class, args);
        ConfigurableEnvironment contextEnvironment = context.getEnvironment();
        String inputDataPath = Paths.get(contextEnvironment.getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.path")).toString();
        if (!inputDataPath.endsWith(File.separator)) {
            inputDataPath = inputDataPath + File.separator;
        }
        String inputDataFormat = contextEnvironment.getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.format");
        InputDataParser inputDataParser;
        long now = Instant.now().toEpochMilli();
        if ("SYNTHEA_CSV".equals(inputDataFormat)) {
            inputDataParser = new SyntheaCsvInputDataParser(now);
        } else if ("FHIR_NDJSON".equals(inputDataFormat)) {
            inputDataParser = new FhirNdjsonInputDataParser(now);
        } else {
            throw new RuntimeException("specified input data format '" + inputDataFormat + "' is not valid");
        }
        List<String> rootDirectoryNames = getRootDirectoriesNames(inputDataPath, inputDataParser, contextEnvironment);
        if (rootDirectoryNames.size() > 1 || (rootDirectoryNames.size() == 1 && !inputDataPath.equals(rootDirectoryNames.get(0)))) {
            // we split the data, so it's now in csv format
            inputDataParser = new SyntheaCsvInputDataParser(now);
        }
        InMemoryDataProviderBuilder inMemoryDataProviderBuilder = context.getBean(InMemoryDataProviderBuilder.class);
        PathwayImagesWriter pathwayImagesWriter = context.getBean(PathwayImagesWriter.class);
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        PathwayVisualizationBuilder pathwayVisualizationBuilder = context.getBean(PathwayVisualizationBuilder.class);
        PathwayMatrixVisualizationBuilder pathwayMatrixVisualizationBuilder = context.getBean(PathwayMatrixVisualizationBuilder.class);
        PathwayImageVisualizationBuilder pathwayImageVisualizationBuilder = context.getBean(PathwayImageVisualizationBuilder.class);
        IntervalTreeVisualizationBuilder intervalTreeVisualizationBuilder = context.getBean(IntervalTreeVisualizationBuilder.class);
        boolean buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents = context.getEnvironment().getProperty(
                "com.ibm.research.drl.deepguidelines.pathways.extractor.output.pathway.image.without.pathway.event.features.from.start.and.stop.pathway.events",
                Boolean.class);
        for (String rootDirectoryName : rootDirectoryNames) {
            LOG.info("processing input data chunk in directory " + rootDirectoryName);
            final DataProvider dataProvider = inMemoryDataProviderBuilder.build(rootDirectoryName, inputDataParser);
            intervalTreeVisualizationBuilder.buildVisualization((InMemoryDataProvider) dataProvider);
            final PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
            final OutputFormatter outputFormatter = new OutputFormatter();
            Stream<Pathway> pathwaysStream = dataProvider.getPathways();
            pathwaysStream.forEach(pathway -> {
                PathwayMatrix pathwayMatrix = (buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents)
                        ? pathwayMatrixBuilder.buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents(pathway)
                        : pathwayMatrixBuilder.build(pathway);
                if (pathwayMatrix.slices() == 0) {
                    if (LOG.isDebugEnabled()) LOG.debug("skipping empty " + pathway.toString());
                } else {
                    PathwayImage pathwayImage = pathwayImageBuilder.trimAndConcatenateSlices(pathwayMatrix);
                    try {
                        pathwayImagesWriter.write(pathwayImage, pathway, outputFormatter);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pathwayVisualizationBuilder.buildVisualization(pathway, dataProvider);
                    pathwayMatrixVisualizationBuilder.buildVisualization(pathwayMatrix, pathway);
                    pathwayImageVisualizationBuilder.buildVisualization(pathwayImage, pathway);
                }
            });
            LOG.info("finished processing input data chunk in directory " + rootDirectoryName);
        }
        pathwayImagesWriter.close();
        context.close();
    }

    private static List<String> getRootDirectoriesNames(String inputDataPath, InputDataParser inputDataParser,
            ConfigurableEnvironment contextEnvironment) throws IOException {
        List<String> rootDirectoriesNames;
        String splitToPath = contextEnvironment.getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.split.to.path");
        Integer maxPatientsPerChunk = null;
        if (splitToPath != null && !splitToPath.isEmpty()) {
            String maxPatientsPerChunkString = contextEnvironment
                .getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.split.max.number.of.patients.per.chunk");
            if (maxPatientsPerChunkString != null && !maxPatientsPerChunkString.isEmpty()) {
                maxPatientsPerChunk = Integer.valueOf(maxPatientsPerChunkString);
            }
        }
        if (splitToPath == null || splitToPath.isEmpty() || maxPatientsPerChunk == null || maxPatientsPerChunk < 1) {
            rootDirectoriesNames = new ObjectArrayList<>();
            rootDirectoriesNames.add(inputDataPath);
        } else {
            DatasetSplitter datasetSplitter = new DatasetSplitter(inputDataPath, inputDataParser);
            rootDirectoriesNames = datasetSplitter.split(maxPatientsPerChunk, splitToPath);
        }
        LOG.info("root directories names: " + rootDirectoriesNames);
        return rootDirectoriesNames;
    }

    @SuppressWarnings("unused")
    private static void computeStats(Stream<Pathway> pathwaysStream, PathwayMatrixBuilder pathwayMatrixBuilder,
            PathwayImageBuilder pathwayImageBuilder)
            throws IOException {
        LOG.info("computing statistics on pathway images length (using trimmed-and-concatenatd-slices) ...");
        Stream<PathwayImage> pathwayImagesStream = pathwaysStream
                .map(pathway -> pathwayMatrixBuilder.build(pathway))
                .map(pathwayMatrix -> pathwayImageBuilder.collapseSlices(pathwayMatrix));
        DescriptiveStatistics statsOnPathwayImagesLength = new DescriptiveStatistics();
        pathwayImagesStream.forEach(pathwayImage -> {
            statsOnPathwayImagesLength.addValue(pathwayImage.columns());
        });
        String valuesFile = writeStatsValuesToFile("stats_pathway_images_collapsed_slices_", statsOnPathwayImagesLength);
        LOG.info("... stats:" + System.lineSeparator() + statsOnPathwayImagesLength + System.lineSeparator() + " values in file: " + valuesFile);
    }

    private static String writeStatsValuesToFile(String prefix, DescriptiveStatistics stats) throws IOException {
        Path path = Files.createTempFile(prefix, ".txt");
        BufferedWriter bw = Files.newBufferedWriter(path);
        String values = Arrays.toString(stats.getValues());
        bw.write(values);
        bw.close();
        return path.toString();
    }

}
