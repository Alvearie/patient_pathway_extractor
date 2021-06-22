package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;

public class ImagingStudiesPathwayMatrixCell extends AbstractTemporalPathwayMatrixCell {

    private final String modalityCode;
    private final String bodysiteCode;

    public ImagingStudiesPathwayMatrixCell(String bodysiteCode, String modalityCode, PathwayEventTemporalType temporalType) {
        super(temporalType);
        this.bodysiteCode = bodysiteCode;
        this.modalityCode = modalityCode;
    }

    public String getModalityCode() {
        return modalityCode;
    }

    public String getBodysiteCode() {
        return bodysiteCode;
    }
    
    @Override
    public String asStringValue() {
        return String.join(DELIMITER, modalityCode, bodysiteCode);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bodysiteCode == null) ? 0 : bodysiteCode.hashCode());
        result = prime * result + ((modalityCode == null) ? 0 : modalityCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ImagingStudiesPathwayMatrixCell other = (ImagingStudiesPathwayMatrixCell) obj;
        if (bodysiteCode == null) {
            if (other.bodysiteCode != null) return false;
        } else if (!bodysiteCode.equals(other.bodysiteCode)) return false;
        if (modalityCode == null) {
            if (other.modalityCode != null) return false;
        } else if (!modalityCode.equals(other.modalityCode)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ImagingStudiesPathwayMatrixCell [modalityCode=" + modalityCode + ", bodysiteCode=" + bodysiteCode + "]";
    }

}
