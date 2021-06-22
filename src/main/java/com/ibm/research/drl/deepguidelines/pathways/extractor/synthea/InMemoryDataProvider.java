package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.IsolatedPathwayEventParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.PatientsParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.StartStopPathwayEventParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * This {@link DataProvider} reads all Synthea files, and builds in-memory data sturcture to hold the minimum amount of data that we need to produce a {@link Pathway}, 
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
 *  - an instance of this class may consume a lot of memory, depending on the Synthea data set in input
 *  - this class is NOT thread safe, and the underlying data structures are mutable, and therefore they should not be exposed
 */
public class InMemoryDataProvider implements DataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryDataProvider.class);

    // Map[PatientId, IntervalTree[IntervalItemId]]
    private final Map<String, IntervalTree<PathwayEvent>> intervalTreesForest = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, EventLine]
    private final Map<String, PathwayEventsLine> eventLinesBundle = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, PathwayEventFeaturesIndex]
    private final Map<String, PathwayEventFeaturesIndex> eventsFeatures = new Object2ObjectOpenHashMap<>();

    // Map[PatientId, Patient]
    private final Map<String, Patient> patientsIndex;

    private final String dataPathName;
    private final long now;
    private final Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes;

    public InMemoryDataProvider(String dataPathName, long now, Set<SyntheaMedicalTypes> includedSyntheaMedicalTypes) {
        this.dataPathName = dataPathName;
        this.now = now;
        this.includedSyntheaMedicalTypes = includedSyntheaMedicalTypes;
        LOG.info("building Data for path " + dataPathName);
        buildIntervalTreesForest();
        if (LOG.isInfoEnabled()) logStatisticsOfIntervalTreesForest();
        buildEventsLinesBundle();
        if (LOG.isInfoEnabled()) logStatisticsOfEventsLinesBundle();
        this.patientsIndex = new PatientsParser(dataPathName).parse();
        LOG.info("finished building Data for path " + dataPathName);
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
    public String getDataPathName() {
        return dataPathName;
    }

    @Override
    public PathwayEventFeatures getPathwayEventFeatures(PathwayEvent pathwayEvent, String patientId) {
        return eventsFeatures
                .get(patientId)
                .get(pathwayEvent.getId());
    }

    private void buildIntervalTreesForest() {
        if (LOG.isInfoEnabled()) LOG.info("building interval trees forest ...");
        StartStopPathwayEventParser startStopPathwayEventParser = new StartStopPathwayEventParser(now);
        for (SyntheaMedicalTypes medicalType : Commons.SYNTHEA_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS) {
            if (includedSyntheaMedicalTypes.contains(medicalType)) {
                FileParsingUtils.readAsStreamOfRecords(dataPathName + Commons.SYNTHEA_FILE_NAMES.get(medicalType))
                        .forEach(record -> {
                            PathwayEvent startPathwayEvent = startStopPathwayEventParser.getStartPathwayEvent(record, medicalType);
                            PathwayEvent stopPathwayEvent = startStopPathwayEventParser.getStopPathwayEvent(record, medicalType);
                            long start = startPathwayEvent.getDate();
                            long stop = stopPathwayEvent.getDate();
                            if (stop < start) {
                                LOG.error("bad data: stop < start: " + record);
                            } else {
                                Interval interval = new Interval(start, stop);
                                String patientId = startStopPathwayEventParser.getPatientId(record, medicalType);
                                intervalTreesForest
                                        .computeIfAbsent(patientId, p -> new IntervalTree<>())
                                        .put(interval, startPathwayEvent);
                                intervalTreesForest
                                        .computeIfAbsent(patientId, p -> new IntervalTree<>())
                                        .put(interval, stopPathwayEvent);
                                PathwayEventFeature pathwayEventFeature = startStopPathwayEventParser.getPathwayEventFeature(record, medicalType);
                                updateEventsFeaures(patientId, startPathwayEvent, pathwayEventFeature);
                                updateEventsFeaures(patientId, stopPathwayEvent, pathwayEventFeature);
                            }
                        });
            }
        }
        if (LOG.isInfoEnabled()) LOG.info("... finished building interval trees forest");
    }

    private void logStatisticsOfIntervalTreesForest() {
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
    
    /*
     * I don't want to expose IntervalTree instances outside of this class, because they are mutable.
     */
    public String produceJavascriptDataForIntervalTreeVisualization(String patientId) {
        return intervalTreesForest.get(patientId).buildVisualizationDataAsJavascript();
    }

    private void buildEventsLinesBundle() {
        if (LOG.isInfoEnabled()) LOG.info("building event bundle ...");
        IsolatedPathwayEventParser isolatedPathwayEventParser = new IsolatedPathwayEventParser();
        for (SyntheaMedicalTypes medicalType : Commons.SYNTHEA_MEDICAL_TYPES_YIELDING_ISOLATED_PATHWAY_EVENTS) {
            if (includedSyntheaMedicalTypes.contains(medicalType)) {
                FileParsingUtils.readAsStreamOfRecords(dataPathName + Commons.SYNTHEA_FILE_NAMES.get(medicalType))
                        .forEach(record -> {
                            String patientId = isolatedPathwayEventParser.getPatientId(record, medicalType);
                            PathwayEvent pathwayEvent = isolatedPathwayEventParser.getIsolatedPathwayEvent(record, medicalType);
                            eventLinesBundle
                                    .computeIfAbsent(patientId, p -> new PathwayEventsLine())
                                    .add(pathwayEvent);
                            PathwayEventFeature pathwayEventFeature = isolatedPathwayEventParser.getPathwayEventFeature(record, medicalType);
                            updateEventsFeaures(patientId, pathwayEvent, pathwayEventFeature);
                        });
            }
        }
        if (LOG.isInfoEnabled()) LOG.info("... finished building event bundle");
    }

    private void logStatisticsOfEventsLinesBundle() {
        SummaryStatistics lineSizeStats = new SummaryStatistics();
        for (String patientId : eventLinesBundle.keySet()) {
            PathwayEventsLine eventLine = eventLinesBundle.get(patientId);
            lineSizeStats.addValue(eventLine.size());
        }
        LOG.info("event bundle statst:");
        LOG.info("number of lines: " + eventLinesBundle.size());
        LOG.info("line size stats:\n" + lineSizeStats.toString());
    }

    private void updateEventsFeaures(String patientId, PathwayEvent pathwayEvent, PathwayEventFeature pathwayEventFeature) {
        eventsFeatures
                .computeIfAbsent(patientId, p -> new PathwayEventFeaturesIndex())
                .computeIfAbsent(pathwayEvent.getId(), i -> new PathwayEventFeatures())
                .add(pathwayEventFeature);
    }

}
