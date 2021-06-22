package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

public class DeadOutcomePathwayMatrixCell extends OutcomePathwayMatrixCell {

    public DeadOutcomePathwayMatrixCell(long days) {
        super(false, days);
    }

    @Override
    public String toString() {
        return "DeadOutcomePathwayMatrixCell [alive=" + alive + ", days=" + days + ", outcomeBucket=" + outcomeBucket + "]";
    }

}
