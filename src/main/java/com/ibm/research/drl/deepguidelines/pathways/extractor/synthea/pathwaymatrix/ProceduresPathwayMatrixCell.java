package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;

public class ProceduresPathwayMatrixCell extends AbstractTemporalPathwayMatrixCell {

    private final String code;

    public ProceduresPathwayMatrixCell(String code, PathwayEventTemporalType temporalType) {
        super(temporalType);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String asStringValue() {
        return code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ProceduresPathwayMatrixCell other = (ProceduresPathwayMatrixCell) obj;
        if (code == null) {
            if (other.code != null) return false;
        } else if (!code.equals(other.code)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProceduresPathwayMatrixCell [code=" + code + "]";
    }

}
