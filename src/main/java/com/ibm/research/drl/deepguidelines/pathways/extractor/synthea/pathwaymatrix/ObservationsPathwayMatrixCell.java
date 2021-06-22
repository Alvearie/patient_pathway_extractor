package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import org.apache.commons.lang3.math.NumberUtils;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;

public class ObservationsPathwayMatrixCell extends AbstractTemporalPathwayMatrixCell {

    private final String code;
    private final double numericValue;
    private final String stringValue;
    private final String units;

    private String valueAndUnitsBucket = UNKNOWN_BUCKET;

    public ObservationsPathwayMatrixCell(String code, String value, String units, PathwayEventTemporalType temporalType) {
        super(temporalType);
        this.code = code;
        if (NumberUtils.isCreatable(value)) {
            this.numericValue = NumberUtils.createDouble(value);
            this.stringValue = null;
        } else {
            this.numericValue = Double.NaN;
            this.stringValue = value;
        }
        this.units = units;
    }

    public String getCode() {
        return code;
    }

    public double getNumericValue() {
        return numericValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getUnits() {
        return units;
    }

    public String getValueAndUnitsBucket() {
        return valueAndUnitsBucket;
    }

    public void setValueAndUnitsBucket(String valueAndUnitsBucket) {
        this.valueAndUnitsBucket = valueAndUnitsBucket;
    }

    @Override
    public String asStringValue() {
        return String.join(DELIMITER, code, valueAndUnitsBucket);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        long temp;
        temp = Double.doubleToLongBits(numericValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        result = prime * result + ((units == null) ? 0 : units.hashCode());
        result = prime * result + ((valueAndUnitsBucket == null) ? 0 : valueAndUnitsBucket.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ObservationsPathwayMatrixCell other = (ObservationsPathwayMatrixCell) obj;
        if (code == null) {
            if (other.code != null) return false;
        } else if (!code.equals(other.code)) return false;
        if (Double.doubleToLongBits(numericValue) != Double.doubleToLongBits(other.numericValue)) return false;
        if (stringValue == null) {
            if (other.stringValue != null) return false;
        } else if (!stringValue.equals(other.stringValue)) return false;
        if (units == null) {
            if (other.units != null) return false;
        } else if (!units.equals(other.units)) return false;
        if (valueAndUnitsBucket == null) {
            if (other.valueAndUnitsBucket != null) return false;
        } else if (!valueAndUnitsBucket.equals(other.valueAndUnitsBucket)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ObservationsPathwayMatrixCell [code=" + code + ", numericValue=" + numericValue + ", stringValue=" + stringValue + ", units=" + units
                + ", valueAndUnitsBucket=" + valueAndUnitsBucket + "]";
    }

}
