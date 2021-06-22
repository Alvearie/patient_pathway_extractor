package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;

/**
 * An abstract implementation of {@link PathwayImagesWriter}.
 * 
 * Features:
 * - it delegates writing {@link PathwayImage} objects as CSV to its subclasses
 * - it writes output to a configurable output directory; if the output directory is unspecified, it will create 
 *   and use a temporary directory;
 * - if splits output across files, each having a maximum configurable size in bytes.
 */
public abstract class AbstractPathwayImageWriter implements PathwayImagesWriter {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractPathwayImageWriter.class);

    protected static final String FILE_NAME_PREFIX = "pathway_images_";
    protected static final String FILE_NAME_SUFFIX = ".csv";

    protected final String outputDirectory;
    protected final int maxOutputFileSizeInBytes;
    protected final int maxColumns;
    
    protected int bytesWrittenSoFar = 0;
    protected int numberOfFilesWrittenSoFar = 0;
    protected long oversizedPathwayImages = 0;

    public AbstractPathwayImageWriter(String outpuDirectory, Integer maxOutputFileSizeInBytes, int maxColumns) throws IOException {
        if (outpuDirectory == null || "".equals(outpuDirectory))
            this.outputDirectory = Files.createTempDirectory("pathway_images_").toString();
        else {
            if (outpuDirectory.endsWith(File.separator))
                this.outputDirectory = outpuDirectory + UUID.randomUUID() + File.separator;
            else
                this.outputDirectory = outpuDirectory + File.separator + UUID.randomUUID() + File.separator;
        }
        LOG.info("pathway images will be written to " + this.outputDirectory);
        this.maxOutputFileSizeInBytes = (maxOutputFileSizeInBytes == null) ? Integer.MAX_VALUE : maxOutputFileSizeInBytes;
        this.maxColumns = maxColumns;
        Path outputPath = Paths.get(this.outputDirectory);
        Files.createDirectories(outputPath);
        prepare();
    }

    protected abstract void prepare() throws IOException;
    
    protected String getFilePath() {
        return String.join("", outputDirectory, FILE_NAME_PREFIX, String.valueOf(numberOfFilesWrittenSoFar), FILE_NAME_SUFFIX);
    }
    
    @Override
    public void write(PathwayImage pathwayImage, Pathway pathway, OutputFormatter outputFormatter) throws IOException {
        if (pathwayImage.columns() > maxColumns) {
            oversizedPathwayImages++;
        } else {
            String line = outputFormatter.format(pathway, pathwayImage, maxColumns);
            byte[] bytes = line.getBytes();    
            long bytesAfterNextWrite = (long) bytesWrittenSoFar + (long) bytes.length; // this is a long, because adding bytes.lenght may eventually go beyond int capacity
            if (bytesAfterNextWrite >= maxOutputFileSizeInBytes) {
                prepare();
            }
            doWrite(bytes);
            bytesWrittenSoFar += bytes.length;
        }
    }

    protected abstract void doWrite(byte[] bytes) throws IOException;
    
    @Override
    public void close() throws IOException {
        doClose();
        LOG.info("number of oversized pathway images: " + oversizedPathwayImages);
    }
    
    protected abstract void doClose() throws IOException;
    
}
