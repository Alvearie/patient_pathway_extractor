package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

public class AliveOutcomePathwayMatrixCell extends OutcomePathwayMatrixCell {

    public AliveOutcomePathwayMatrixCell(long days) {
        super(true, days);
    }

    @Override
    public String toString() {
        return "AliveOutcomePathwayMatrixCell [alive=" + alive + ", days=" + days + ", outcomeBucket=" + outcomeBucket + "]";
    }

}
