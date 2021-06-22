package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

public abstract class OutcomePathwayMatrixCell implements PathwayMatrixCell {

    protected final boolean alive;
    protected final long days;

    protected String outcomeBucket = UNKNOWN_BUCKET;

    public OutcomePathwayMatrixCell(boolean alive, long days) {
        super();
        this.alive = alive;
        this.days = days;
    }

    public OutcomePathwayMatrixCell(boolean alive, long days, String outcomeBucket) {
        super();
        this.alive = alive;
        this.days = days;
        this.outcomeBucket = outcomeBucket;
    }

    public String getOutcomeBucket() {
        return outcomeBucket;
    }

    public void setOutcomeBucket(String outcomeBucket) {
        this.outcomeBucket = outcomeBucket;
    }

    public boolean isAlive() {
        return alive;
    }

    public long getDays() {
        return days;
    }

    @Override
    public String asStringValue() {
        return outcomeBucket;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (alive ? 1231 : 1237);
        result = prime * result + (int) (days ^ (days >>> 32));
        result = prime * result + ((outcomeBucket == null) ? 0 : outcomeBucket.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OutcomePathwayMatrixCell other = (OutcomePathwayMatrixCell) obj;
        if (alive != other.alive) return false;
        if (days != other.days) return false;
        if (outcomeBucket == null) {
            if (other.outcomeBucket != null) return false;
        } else if (!outcomeBucket.equals(other.outcomeBucket)) return false;
        return true;
    }

}
