package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

public interface DataProvider {

    public PathwayEventsLine getPathwayEventsLine(PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent, String patientId);

    public Patient getPatient(String patientId);
    
    public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId);
    
    public String getDataPathName();
        
}
