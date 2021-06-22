package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;

public class OutputFormatter {
    
    public String format(Pathway pathway, PathwayImage pathwayImage, int maxColumns) {
        String pathwayId = pathway.getId();
        String originatingConditionCode = pathway.getOriginatingConditionCode();
        String pathwayImageAsCsvLine = pathwayImage.asCSVLine(maxColumns);
        return String.join(",", pathwayId, originatingConditionCode, pathwayImageAsCsvLine);
    }

}
