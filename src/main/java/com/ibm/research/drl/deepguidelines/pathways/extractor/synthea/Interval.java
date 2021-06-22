package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

public class Interval implements Comparable<Interval> {

    private final long min;
    private final long max;

    public Interval(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (max ^ (max >>> 32));
        result = prime * result + (int) (min ^ (min >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Interval other = (Interval) obj;
        if (max != other.max) return false;
        if (min != other.min) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Interval [min=" + min + ", max=" + max + "]";
    }

    // @formatter:off
    public boolean intersects(Interval that) {
        if (that.max < this.min) return false;
        if (this.max < that.min) return false;
        return true;
    }
    
    @Override
    public int compareTo(Interval that) {
        if      (this.min < that.min) return -1;
        else if (this.min > that.min) return +1;
        else if (this.max < that.max) return -1;
        else if (this.max > that.max) return +1;
        else                          return  0;
    }
    // @formatter:on

}
