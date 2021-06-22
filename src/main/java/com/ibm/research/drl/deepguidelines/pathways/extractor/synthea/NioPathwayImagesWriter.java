package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * An implementation of {@link AbstractPathwayImageWriter} using Java NIO {@link FileChannel} and mapped {@link ByteBuffer}.
 */
@Service
@ConditionalOnProperty(name = "com.ibm.research.drl.deepguidelines.pathways.extractor.output.PathwayImagesWriter", havingValue = "NIO")
public class NioPathwayImagesWriter extends AbstractPathwayImageWriter {

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;
    private ByteBuffer byteBuffer;

    @Autowired
    public NioPathwayImagesWriter(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.path}") String outpuDirectory,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.max.output.file.size.in.bytes}") Integer maxOutputFileSizeInBytes,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.pathway.image.max.columns}") int maxColumns)
            throws IOException {
        super(outpuDirectory, maxOutputFileSizeInBytes, maxColumns);
    }

    @Override
    protected void prepare() throws IOException {
        if (randomAccessFile != null) {
            close();
            numberOfFilesWrittenSoFar++;
            bytesWrittenSoFar = 0;
        }
        String filePath = getFilePath();
        randomAccessFile = new RandomAccessFile(filePath, "rw");
        fileChannel = randomAccessFile.getChannel();
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, maxOutputFileSizeInBytes);
    }

    @Override
    protected void doWrite(byte[] bytes) {
        byteBuffer.put(bytes);
    }

    @Override
    protected void doClose() throws IOException {
        fileChannel.truncate(bytesWrittenSoFar);
        fileChannel.close();
        randomAccessFile.close();
    }

}