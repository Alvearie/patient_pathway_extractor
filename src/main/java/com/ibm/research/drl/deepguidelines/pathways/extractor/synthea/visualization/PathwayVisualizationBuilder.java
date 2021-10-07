package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeature;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventFeatures;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Service
public class PathwayVisualizationBuilder extends AbstractVisualizationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PathwayVisualizationBuilder.class);

    private static final String HTML_OUTPUT_PREFIX = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Pathway</title>\n" +
            "<style>\n" +
            "table, th, td {\n" +
            "    border: 1px solid black;\n" +
            "    border-collapse: collapse;\n" +
            "}\n" +
            "</style>\n" +
            "<script type=\"text/javascript\" src=\"vis.min.js\"></script>\n" +
            "<link href=\"vis.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "    <div id=\"timeline\"></div>\n" +
            "\n" +
            "    <script type=\"text/javascript\">\n" +
            "        var container = document.getElementById('timeline');\n" +
            "        var options = {\n" +
            "            tooltip : {\n" +
            "                followMouse : true\n" +
            "            }\n" +
            "        };\n";

    private static final String HTML_OUTPUT_SUFFIX = "        var timeline = new vis.Timeline(container, items, options);\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n";

    public PathwayVisualizationBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.produce.javascript.data.for.visualizations.for.patient.with.ids}") String[] patientIdsForWhichWeWantVisualizations) {
        super(patientIdsForWhichWeWantVisualizations);
    }

    public void buildVisualization(Pathway pathway, DataProvider dataProvider) {
        if (patientIdsForWhichWeWantVisualizations.isPresent()
                && patientIdsForWhichWeWantVisualizations.get().contains(pathway.getPatient().getId())) {
            StringBuilder sb = new StringBuilder();
            sb.append(HTML_OUTPUT_PREFIX);
            sb.append(buildVisualizationDataAsJavascript(pathway.getPathwayEventsLine(), pathway.getPatient().getId(), dataProvider)).append(System.lineSeparator());
            sb.append(HTML_OUTPUT_SUFFIX);
            try {
                Path path = FileUtils.writeToTempFile(
                        sb.toString(),
                        String.join("_", "vis_pathway_events_line", pathway.getId(), "_"),
                        ".html");
                LOG.info("visualization data saved to file " + path.toString());
            } catch (IOException e) {
                LOG.error("error while writing visualization data", e);
            }
        }
    }

    private final class StartStopPathwayEventPair {
        private PathwayEvent start;
        private PathwayEvent stop;
    }

    private Collection<StartStopPathwayEventPair> pairStartStopPathwayEvents(PathwayEventsLine pathwayEventsLine) {
        Map<String, StartStopPathwayEventPair> index = new Object2ObjectOpenHashMap<>();
        for (PathwayEvent pathwayEvent : pathwayEventsLine) {
            String key = String.join("_", pathwayEvent.getMedicalType().toString(), pathwayEvent.getEventId());
            switch (pathwayEvent.getTemporalType()) {
            case START:
                index.computeIfAbsent(key, k -> new StartStopPathwayEventPair()).start = pathwayEvent;
                break;

            case STOP:
                index.computeIfAbsent(key, k -> new StartStopPathwayEventPair()).stop = pathwayEvent;
                break;

            default:
                break;
            }
        }
        long pathwayStartDate = pathwayEventsLine.first().getDate();
        long pathwayStopDate = pathwayEventsLine.last().getDate();
        for (StartStopPathwayEventPair startStopPathwayEventPair : index.values()) {
            if (startStopPathwayEventPair.start == null) {
                startStopPathwayEventPair.start = new PathwayEvent(
                        startStopPathwayEventPair.stop.getMedicalType(),
                        startStopPathwayEventPair.stop.getTemporalType(),
                        startStopPathwayEventPair.stop.getEventId(),
                        pathwayStartDate);
            } else if (startStopPathwayEventPair.stop == null) {
                startStopPathwayEventPair.stop = new PathwayEvent(
                        startStopPathwayEventPair.start.getMedicalType(),
                        startStopPathwayEventPair.start.getTemporalType(),
                        startStopPathwayEventPair.start.getEventId(),
                        pathwayStopDate);
            }
        }
        return index.values();
    }

    private String buildVisualizationDataAsJavascript(PathwayEventsLine pathwayEventsLine, String patientId, DataProvider dataProvider) {
        StringBuilder sb = new StringBuilder();
        sb.append("var items = new vis.DataSet([");
        Collection<StartStopPathwayEventPair> startStopPathwayEventPairs = pairStartStopPathwayEvents(pathwayEventsLine);
        int itemCounter = 0;
        for (StartStopPathwayEventPair startStopPathwayEventPair : startStopPathwayEventPairs) {
            PathwayEventFeatures pathwayEventFeatures = dataProvider.getPathwayEventFeatures(startStopPathwayEventPair.start, patientId);
            if (pathwayEventFeatures == null) {
                // we might have generated a fake start event in the pair for the purpose of the visualization, and so
                // we need to use the stop event for retrieving the actual PathwayEventFeatures.
                pathwayEventFeatures = dataProvider.getPathwayEventFeatures(startStopPathwayEventPair.stop, patientId);
            }
            sb.append("{");
            // id
            sb.append("id : 'item_").append(String.valueOf(itemCounter)).append("',");
            itemCounter++;
            // content
            sb.append("content : '").append(startStopPathwayEventPair.start.getMedicalType().toString()).append("',");
            // title
            sb.append("title : '").append(buildTitle(startStopPathwayEventPair.start, pathwayEventFeatures)).append("',");
            // start
            sb.append("start : '").append(Instant.ofEpochMilli(startStopPathwayEventPair.start.getDate()).toString()).append("',");
            // end
            sb.append("end : '").append(Instant.ofEpochMilli(startStopPathwayEventPair.stop.getDate()).toString()).append("'");
            sb.append("}");
            sb.append(",");
        }
        for (PathwayEvent pathwayEvent : pathwayEventsLine) {
            if (pathwayEvent.getTemporalType().equals(PathwayEventTemporalType.ISOLATED)) {
                sb.append("{");
                // id
                sb.append("id : 'item_").append(String.valueOf(itemCounter)).append("',");
                itemCounter++;
                // content
                sb.append("content : '").append(pathwayEvent.getMedicalType().toString()).append("',");
                // title
                sb.append("title : '").append(buildTitle(pathwayEvent, dataProvider.getPathwayEventFeatures(pathwayEvent, patientId))).append("',");
                // start
                sb.append("start : '").append(Instant.ofEpochMilli(pathwayEvent.getDate()).toString()).append("',");
                // type point
                sb.append("type: 'point'");
                sb.append("}");
                sb.append(",");
            }
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1); // removing the last ,
        sb.append("]);");
        return sb.toString();
    }

    private String buildTitle(PathwayEvent pathwayEvent, PathwayEventFeatures pathwayEventFeatures) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(Instant.ofEpochMilli(pathwayEvent.getDate()).toString()).append("</p>");
        sb.append("<table>");
        for (PathwayEventFeature pathwayEventFeature : pathwayEventFeatures) {
            sb.append("<tr>");
            for (String featureValue : pathwayEventFeature) {
                sb.append("<td>").append(featureValue).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
