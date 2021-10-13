package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Interval;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.IntervalTree;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeatures;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeaturesIndex;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * This {@link DataProvider} reads input files and builds in-memory data structures to hold the minimum amount of data that we need to produce a {@link Pathway}, 
 * and corresponding {@link PathwayMatrix} and {@link PathwayImage}.
 * 
 *  More specifically it builds:
 *  - a forest of {@link IntervalTree}; each IntervalTree in the forest belongs to one patient, and it contains {@link PathwayEvent} objects having start/stop dates 
 *  for that patient;
 *  - a bundle of {@link PathwayEventsLine}; each PathwayEventsLine in the bundle belongs to one patient, and it contains {@link PathwayEvent} objects happening 
 *  at an isolated date for that patient
 *  - a Map from patient-ID to {@link PathwayEventFeaturesIndex}, where we store all the data ({@link PathwayEventFeatures}) for that patient.
 *  - a Map from patient-ID to {@link Patient}, where we store Patient objects.
 *  
 *  Notes:
 *  - an instance of this class may consume a lot of memory, depending on the data set in input
 *  - this class is NOT thread safe, and the underlying data structures are mutable, and therefore they should not be exposed
 */
public class AbstractDataProvider implements DataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDataProvider.class);

    // Map[PatientId, IntervalTree[IntervalItemId]]
    protected final Map<String, IntervalTree<PathwayEvent>> intervalTreesForest = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, EventLine]
    protected final Map<String, PathwayEventsLine> eventLinesBundle = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, PathwayEventFeaturesIndex]
    private final Map<String, PathwayEventFeaturesIndex> eventsFeatures = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, Patient]
    protected Map<String, Patient> patientsIndex = null;

    protected Stream<Pathway> pathways = null;
    
    protected final String inputDataPath;

    public AbstractDataProvider(String inputDataPath) {
        super();
        this.inputDataPath = inputDataPath;
    }

    @Override
    public PathwayEventsLine getPathwayEventsLine(PathwayEvent startPathwayEvent, PathwayEvent stopPathwayEvent, String patientId) {
        PathwayEventsLine pathwayEventsLine = new PathwayEventsLine();
        long pathwayStartDate = startPathwayEvent.getDate();
        long pathwayStopDate = stopPathwayEvent.getDate();
        IntervalTree<PathwayEvent> patientIntervalTree = intervalTreesForest.get(patientId);
        if (patientIntervalTree != null) {
            List<PathwayEvent> pathwayEventsFromIntersectingIntervals = patientIntervalTree
                    .searchAll(new Interval(pathwayStartDate, pathwayStopDate));
            for (PathwayEvent pathwayEvent : pathwayEventsFromIntersectingIntervals) {
                long pathwayEventDate = pathwayEvent.getDate();
                if (pathwayEventDate >= pathwayStartDate && pathwayEventDate <= pathwayStopDate) {
                    pathwayEventsLine.add(pathwayEvent);
                }
            }
        }
        PathwayEventsLine patientEventsLine = eventLinesBundle.get(patientId);
        if (patientEventsLine != null) {
            for (PathwayEvent event : patientEventsLine.subSet(startPathwayEvent, stopPathwayEvent)) {
                pathwayEventsLine.add(event);
            }
            // ObjectSortedSet.subSet, according to JavaDoc "returns a view of the portion of this sorted set whose elements range 
            // from fromElement, inclusive, to toElement, exclusive", but we want to include also toElements, and therefore we use
            // the following loop on the tailSet
            for (PathwayEvent event : patientEventsLine.tailSet(stopPathwayEvent)) {
                if (event.getDate() > pathwayStopDate) break;
                pathwayEventsLine.add(event);
            }
        }
        return pathwayEventsLine;
    }

    @Override
    public Patient getPatient(String patientId) {
        return patientsIndex.get(patientId);
    }

    @Override
    public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId) {
        return eventsFeatures
                .get(patientId)
                .get(pathwayEvent.getId());
    }

    @Override
    public String produceJavascriptDataForIntervalTreeVisualization(String patientId) {
        return intervalTreesForest.get(patientId).buildVisualizationDataAsJavascript();
    }

    @Override
    public Stream<Pathway> getPathways() {
        return pathways;
    }

    public void logStatisticsOfIntervalTreesForest() {
        SummaryStatistics treeSizeStats = new SummaryStatistics();
        SummaryStatistics treeHeightStats = new SummaryStatistics();
        SummaryStatistics treesNumberOfValues = new SummaryStatistics();
        for (String patientId : intervalTreesForest.keySet()) {
            IntervalTree<PathwayEvent> intervalTree = intervalTreesForest.get(patientId);
            treeSizeStats.addValue(intervalTree.size());
            treeHeightStats.addValue(intervalTree.height());
            treesNumberOfValues.addValue(intervalTree.numberOfValues());
        }
        LOG.info("interval forest stats:");
        LOG.info("number of trees: " + intervalTreesForest.size());
        LOG.info("trees size:" + treeSizeStats.toString());
        LOG.info("trees heights:" + treeHeightStats.toString());
        LOG.info("number of values:" + treesNumberOfValues.toString());
    }
    
    public void logStatisticsOfEventsLinesBundle() {
        SummaryStatistics lineSizeStats = new SummaryStatistics();
        for (String patientId : eventLinesBundle.keySet()) {
            PathwayEventsLine eventLine = eventLinesBundle.get(patientId);
            lineSizeStats.addValue(eventLine.size());
        }
        LOG.info("event bundle statst:");
        LOG.info("number of lines: " + eventLinesBundle.size());
        LOG.info("line size stats:\n" + lineSizeStats.toString());
    }

    protected void updateEventsFeatures(String patientId, PathwayEvent pathwayEvent, PathwayEventFeature pathwayEventFeature) {
        eventsFeatures
                .computeIfAbsent(patientId, p -> new PathwayEventFeaturesIndex())
                .computeIfAbsent(pathwayEvent.getId(), i -> new PathwayEventFeatures())
                .add(pathwayEventFeature);
    }

}
