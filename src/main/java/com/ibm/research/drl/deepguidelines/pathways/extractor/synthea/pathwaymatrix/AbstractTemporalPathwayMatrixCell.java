package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;

public abstract class AbstractTemporalPathwayMatrixCell implements PathwayMatrixCell {

    protected final PathwayEventTemporalType temporalType;

    public AbstractTemporalPathwayMatrixCell(PathwayEventTemporalType temporalType) {
        super();
        this.temporalType = temporalType;
    }

}
