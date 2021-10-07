package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import java.util.stream.Stream;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeatures;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;

public interface DataProvider {

    public PathwayEventsLine getPathwayEventsLine(PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent, String patientId);

    public Patient getPatient(String patientId);
    
    public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId);
    
    /*
     * I don't want to expose IntervalTree instances outside of this class, because they are mutable.
     */
    public String produceJavascriptDataForIntervalTreeVisualization(String patientId);
    
    public Stream<Pathway> getPathways();
}
