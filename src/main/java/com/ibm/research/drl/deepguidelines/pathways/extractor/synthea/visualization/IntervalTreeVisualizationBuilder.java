package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

@Service
public class IntervalTreeVisualizationBuilder extends AbstractVisualizationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(IntervalTreeVisualizationBuilder.class);

    private static final String HTML_OUTPUT_PREFIX = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <title>Interval Tree</title>\n" +
            "\n" +
            "  <script type=\"text/javascript\" src=\"vis.min.js\"></script>\n" +
            "  <link href=\"vis.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
            "\n" +
            "  <style type=\"text/css\">\n" +
            "    #mynetwork {\n" +
            "      width: 2048px;\n" +
            "      height: 1024px;\n" +
            "      border: 1px solid lightgray;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<div id=\"mynetwork\"></div>\n" +
            "\n" +
            "<script type=\"text/javascript\">\n";

    private static final String HTML_OUTPUT_SUFFIX = "  var container = document.getElementById('mynetwork');\n" +
            "  var data = {\n" +
            "    nodes: nodes,\n" +
            "    edges: edges\n" +
            "  };\n" +
            "  var options = {\n" +
            "      layout: {\n" +
            "          hierarchical: {\n" +
            "              direction: 'UD',\n" +
            "          sortMethod: 'directed',\n" +
            "          nodeSpacing: 200\n" +
            "          }\n" +
            "      },\n" +
            "      physics: {\n" +
            "          enabled: false\n" +
            "      }\n" +
            "  };\n" +
            "  var network = new vis.Network(container, data, options);\n" +
            "</script>\n" +
            "\n" +
            "\n" +
            "</body>\n" +
            "</html>\n" +
            "";

    public IntervalTreeVisualizationBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.output.produce.javascript.data.for.visualizations.for.patient.with.ids}") String[] patientIdsForWhichWeWantVisualizations) {
        super(patientIdsForWhichWeWantVisualizations);
    }

    public void buildVisualization(DataProvider dataProvider) {
        if (patientIdsForWhichWeWantVisualizations.isPresent()) {
            for (String patientId : patientIdsForWhichWeWantVisualizations.get()) {

                StringBuilder sb = new StringBuilder();
                sb.append(HTML_OUTPUT_PREFIX);
                sb.append(dataProvider.produceJavascriptDataForIntervalTreeVisualization(patientId));
                sb.append(HTML_OUTPUT_SUFFIX);
                try {
                    Path path = FileUtils.writeToTempFile(
                            sb.toString(),
                            String.join("_", "vis_interval_tree_", patientId, "_"),
                            ".html");
                    LOG.info("visualization data saved to file " + path.toString());
                } catch (IOException e) {
                    LOG.error("error while writing visualization data", e);
                }
            }
        }
    }

}
