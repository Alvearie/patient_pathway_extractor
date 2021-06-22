package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.time.Instant;

public class Pathway {

    private final String id;
    private final Patient patient;
    private final String originatingConditionCode; // code of the condition that originates this pathway
    private final PathwayEvent startPathwayEvent;
    private final PathwayEvent stopPathwayEvent;
    private final PathwayEventsLine pathwayEventsLine;

    public Pathway(String pathwayId, Patient patient, String originatingConditionCode, PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent,
            PathwayEventsLine pathwayEventsLine) {
        super();
        this.id = pathwayId;
        this.patient = patient;
        this.originatingConditionCode = originatingConditionCode;
        this.startPathwayEvent = startPathwayEvent;
        this.stopPathwayEvent = stopPathwayEvent;
        this.pathwayEventsLine = pathwayEventsLine;
    }

    public static String buildId(String patientId, PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent) {
        return String.join(",",
                Instant.ofEpochMilli(startPathwayEvent.getDate()).toString(),
                Instant.ofEpochMilli(stopPathwayEvent.getDate()).toString(),
                patientId,
                startPathwayEvent.getEventId());
    }

    public String getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getOriginatingConditionCode() {
        return originatingConditionCode;
    }

    public PathwayEvent getStartPathwayEvent() {
        return startPathwayEvent;
    }

    public PathwayEvent getStopPathwayEvent() {
        return stopPathwayEvent;
    }

    public PathwayEventsLine getPathwayEventsLine() {
        return pathwayEventsLine;
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
        Pathway other = (Pathway) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Pathway [id=" + id + ", patient=" + patient + ", originatingConditionCode=" + originatingConditionCode + ", startPathwayEvent="
                + startPathwayEvent + ", stopPathwayEvent=" + stopPathwayEvent + ", pathwayEventsLine=" + pathwayEventsLine + "]";
    }

}
