package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

@Service
public class PathwayImageVisualizationBuilder extends AbstractVisualizationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PathwayImageVisualizationBuilder.class);

    private static final String HTML_HEAD = "<head> <style> table {table-layout: fixed; width: 100px;} td {overflow: hidden; width: 8px; height: 8px;} table, th, td { border: 1px solid blue; border-collapse:collapse; } </style> </head>";

    private static final String WHITE = "rgb(255,255,255)";

    public PathwayImageVisualizationBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.produce.javascript.data.for.visualizations.for.patient.with.ids}") String[] patientIdsForWhichWeWantVisualizations) {
        super(patientIdsForWhichWeWantVisualizations);
    }

    public void buildVisualization(PathwayImage pathwayImage, Pathway pathway) {
        if (patientIdsForWhichWeWantVisualizations.isPresent()
                && patientIdsForWhichWeWantVisualizations.get().contains(pathway.getPatient().getId())) {
            try {
                Path path = FileUtils.writeToTempFile(
                        buildVisualizationAsHTMLTable(pathwayImage),
                        String.join("_", "vis_pathway_image", pathway.getId(), "_"),
                        ".html");
                LOG.info("visualization data saved to file " + path.toString());
            } catch (IOException e) {
                LOG.error("error while writing visualization data", e);
            }
        }
    }

    private String buildVisualizationAsHTMLTable(PathwayImage pathwayImage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(HTML_HEAD);
        sb.append("<table>");
        sb.append("<tbody>");
        int row = 0;
        for (List<String> rowValues : pathwayImage.getTableValues()) {
            sb.append("<tr>");
            for (String value : rowValues) {
                String color = ("".equals(value)) ? WHITE : PathwayMatrixVisualizationBuilder.COLORS[row];
                sb.append("<td style='background-color:").append(color).append("'");
                if (!"".equals(value)) sb.append(" title = '").append(value).append("'");
                sb.append("></td>");
            }
            sb.append("</tr>");
            row++;
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</html>");
        return sb.toString();
    }

}
