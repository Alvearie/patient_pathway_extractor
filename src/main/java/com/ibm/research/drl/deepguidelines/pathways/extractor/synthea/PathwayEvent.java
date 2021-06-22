package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.time.Instant;
import java.util.Comparator;

public class PathwayEvent implements Comparable<PathwayEvent> {

    private static final Comparator<PathwayEvent> COMPARATOR = Comparator
            .comparingLong(PathwayEvent::getDate)
            .thenComparing(PathwayEvent::getMedicalType)
            .thenComparing(PathwayEvent::getTemporalType)
            .thenComparing(PathwayEvent::getEventId);

    private final SyntheaMedicalTypes medicalType;
    private final PathwayEventTemporalType temporalType;
    private final String eventId;
    private final long date;
    private final String id;

    public PathwayEvent(SyntheaMedicalTypes medicalType, PathwayEventTemporalType temporalType, String eventId, long date) {
        this.medicalType = medicalType;
        this.temporalType = temporalType;
        this.eventId = eventId;
        this.date = date;
        this.id = String.join("_", medicalType.toString(), temporalType.toString(), eventId, String.valueOf(date));
    }

    public SyntheaMedicalTypes getMedicalType() {
        return medicalType;
    }

    public PathwayEventTemporalType getTemporalType() {
        return temporalType;
    }

    public String getEventId() {
        return eventId;
    }

    public long getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PathwayEvent other = (PathwayEvent) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + String.join(",", medicalType.toString(), temporalType.toString(), eventId, Instant.ofEpochMilli(date).toString()) + "]";
    }

    @Override
    public int compareTo(PathwayEvent that) {
        return COMPARATOR.compare(this, that);
    }

}
