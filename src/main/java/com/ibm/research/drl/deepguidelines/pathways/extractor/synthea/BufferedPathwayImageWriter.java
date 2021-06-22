package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * An implementation of {@link AbstractPathwayImageWriter} using {@link BufferedOutputStream}.
 */
@Service
@ConditionalOnProperty(name="com.ibm.research.drl.deepguidelines.pathways.extractor.output.PathwayImagesWriter", havingValue="BOS")
public class BufferedPathwayImageWriter extends AbstractPathwayImageWriter {
        
    private BufferedOutputStream bufferedOutputStream;

    @Autowired
    public BufferedPathwayImageWriter(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.path}") String outpuDirectory,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.max.output.file.size.in.bytes}") Integer maxOutputFileSizeInBytes,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.pathway.image.max.columns}") int maxColumns)
            throws IOException {
        super(outpuDirectory, maxOutputFileSizeInBytes, maxColumns);
    }
    
    @Override
    protected void prepare() throws IOException {
        if (bufferedOutputStream != null) {
            close();
            numberOfFilesWrittenSoFar++;
            bytesWrittenSoFar = 0;
        }
        String filePath = getFilePath();
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
    }

    @Override
    protected void doWrite(byte[] bytes) throws IOException {
        bufferedOutputStream.write(bytes);
    }

    @Override
    protected void doClose() throws IOException {
        bufferedOutputStream.close();        
    }

}
