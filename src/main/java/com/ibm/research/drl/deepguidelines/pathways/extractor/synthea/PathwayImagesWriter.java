package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.io.IOException;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;

public interface PathwayImagesWriter {
    
    public void write(PathwayImage pathwayImage, Pathway pathway, OutputFormatter outputFormatter) throws IOException;
    
    public void close() throws IOException;

}
