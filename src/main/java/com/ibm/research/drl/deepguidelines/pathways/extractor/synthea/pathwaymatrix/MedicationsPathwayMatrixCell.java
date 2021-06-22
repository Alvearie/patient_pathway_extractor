package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;

public class MedicationsPathwayMatrixCell extends AbstractTemporalPathwayMatrixCell {

    private final String code;
    private final int dispenses;

    private String dispensesBucket = UNKNOWN_BUCKET;

    public MedicationsPathwayMatrixCell(String code, int dispenses, PathwayEventTemporalType temporalType) {
        super(temporalType);
        this.code = code;
        this.dispenses = dispenses;
    }

    public String getCode() {
        return code;
    }

    public int getDispenses() {
        return dispenses;
    }

    public String getDispensesBucket() {
        return dispensesBucket;
    }

    public void setDispensesBucket(String dispensesBucket) {
        this.dispensesBucket = dispensesBucket;
    }

    @Override
    public String asStringValue() {
        return String.join(DELIMITER, temporalType.abbreviation(), code, dispensesBucket);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + dispenses;
        result = prime * result + ((dispensesBucket == null) ? 0 : dispensesBucket.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MedicationsPathwayMatrixCell other = (MedicationsPathwayMatrixCell) obj;
        if (code == null) {
            if (other.code != null) return false;
        } else if (!code.equals(other.code)) return false;
        if (dispenses != other.dispenses) return false;
        if (dispensesBucket == null) {
            if (other.dispensesBucket != null) return false;
        } else if (!dispensesBucket.equals(other.dispensesBucket)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "MedicationsPathwayMatrixCell [code=" + code + ", dispenses=" + dispenses + ", dispensesBucket=" + dispensesBucket + "]";
    }

}
