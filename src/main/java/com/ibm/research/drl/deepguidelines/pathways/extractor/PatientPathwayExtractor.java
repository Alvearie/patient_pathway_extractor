package com.ibm.research.drl.deepguidelines.pathways.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.InMemoryDataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.OutputFormatter;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayImagesWriter;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwaysBuilder;
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
        List<String> rootDirectoryNames = getRootDirectoriesNames(context);
        InMemoryDataProviderBuilder inMemoryDataProviderBuilder = context.getBean(InMemoryDataProviderBuilder.class);
        PathwayImagesWriter pathwayImagesWriter = context.getBean(PathwayImagesWriter.class);
        PathwaysBuilder pathwaysBuilder = context.getBean(PathwaysBuilder.class);
        PathwayImageBuilder pathwayImageBuilder = new PathwayImageBuilder();
        PathwayVisualizationBuilder pathwayVisualizationBuilder = context.getBean(PathwayVisualizationBuilder.class);
        PathwayMatrixVisualizationBuilder pathwayMatrixVisualizationBuilder = context.getBean(PathwayMatrixVisualizationBuilder.class);
        PathwayImageVisualizationBuilder pathwayImageVisualizationBuilder = context.getBean(PathwayImageVisualizationBuilder.class);
        IntervalTreeVisualizationBuilder intervalTreeVisualizationBuilder = context.getBean(IntervalTreeVisualizationBuilder.class);
        boolean buildWithoutPathwayEventFeaturesFromStartAndStopPathwayEvents = context.getEnvironment().getProperty(
                "com.ibm.research.drl.deepguidelines.pathways.extractor.output.pathway.image.without.pathway.event.features.from.start.and.stop.pathway.events",
                Boolean.class);
        for (String rootDirectoryName : rootDirectoryNames) {
            LOG.info("processing Synthea data chunk in direcotry " + rootDirectoryName);
            final DataProvider dataProvider = inMemoryDataProviderBuilder.build(rootDirectoryName);
            intervalTreeVisualizationBuilder.buildVisualization((InMemoryDataProvider) dataProvider);
            final PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
            final OutputFormatter outputFormatter = new OutputFormatter();
            Stream<Pathway> pathwaysStream = pathwaysBuilder.build(dataProvider);
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
            LOG.info("finished processing Synthea data chunk in direcotry " + rootDirectoryName);
        }
        pathwayImagesWriter.close();
        context.close();
    }

    private static List<String> getRootDirectoriesNames(ConfigurableApplicationContext context) throws IOException {
        String syntheaDataPath = context.getEnvironment().getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.path");
        if (!syntheaDataPath.endsWith(File.separator))
            syntheaDataPath = syntheaDataPath + File.separator;
        String syntheaSplitToPath = context.getEnvironment()
                .getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.split.to.path");
        List<String> rootDirectoriesNames = new ObjectArrayList<>();
        if (syntheaSplitToPath == null || "".equals(syntheaSplitToPath)) {
            rootDirectoriesNames.add(syntheaDataPath);
        } else {
            DatasetSplitter datasetSplitter = new DatasetSplitter(syntheaDataPath);
            int maxNumberOfPatientsPerChunk = Integer.valueOf(context.getEnvironment()
                    .getProperty("com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.data.split.max.number.of.patients.per.chunk"));
            rootDirectoriesNames = datasetSplitter.split(maxNumberOfPatientsPerChunk, syntheaSplitToPath);
        }
        LOG.info("root directories names: " + rootDirectoriesNames);
        return rootDirectoriesNames;
    }

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
