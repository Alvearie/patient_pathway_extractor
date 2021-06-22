package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Service
public class PathwayMatrixVisualizationBuilder extends AbstractVisualizationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PathwayMatrixVisualizationBuilder.class);

    private static final String HTML_OUTPUT_PREFIX = "<head>\n" +
            "<!-- Plotly.js -->\n" +
            "<script src=\"plotly-latest.min.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <!-- Plotly chart will be drawn inside this DIV -->\n" +
            "    <div id=\"myDiv\" style=\"width: 100%; height: 100%\"></div>\n" +
            "    <script>\n";

    private static final String HTML_OUTPUT_SUFFIX = "        var layout = {\n" +
            "            scene : {\n" +
            "                xaxis : {\n" +
            "                    title : 'time'\n" +
            "                },\n" +
            "                yaxis : {\n" +
            "                    title : 'multiple events'\n" +
            "                },\n" +
            "                zaxis: {\n" +
            "                    title : ''\n" +
            "                }\n" +
            "            },\n" +
            "            dragmode : false,\n" +
            "            margin : {\n" +
            "                l : 0,\n" +
            "                r : 0,\n" +
            "                b : 0,\n" +
            "                t : 0\n" +
            "            }\n" +
            "        };\n" +
            "        Plotly.newPlot('myDiv', data, layout);\n" +
            "    </script>\n" +
            "</body>";

    public static final String[] COLORS = new String[] {
            "rgb(110,51,156)",
            "rgb(203,88,32)",
            "rgb(124,124,124)",
            "rgb(255,190,53)",
            "rgb(255,0,18)",
            "rgb(83,130,61)" };

    private static final Map<Integer, String> Z_VALUE_2_Z_LABEL = new Int2ObjectOpenHashMap<>();

    static {
        Z_VALUE_2_Z_LABEL.put(0, "Demographics");
        Z_VALUE_2_Z_LABEL.put(1, "Obeservations");
        Z_VALUE_2_Z_LABEL.put(2, "Conditions");
        Z_VALUE_2_Z_LABEL.put(3, "Medications");
        Z_VALUE_2_Z_LABEL.put(4, "Procedures");
        Z_VALUE_2_Z_LABEL.put(5, "Outcomes");
    }

    public PathwayMatrixVisualizationBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.produce.javascript.data.for.visualizations.for.patient.with.ids}") String[] patientIdsForWhichWeWantVisualizations) {
        super(patientIdsForWhichWeWantVisualizations);
    }

    public void buildVisualization(PathwayMatrix pathwayMatrix, Pathway pathway) {
        if (patientIdsForWhichWeWantVisualizations.isPresent()
                && patientIdsForWhichWeWantVisualizations.get().contains(pathway.getPatient().getId())) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(HTML_OUTPUT_PREFIX);
                sb.append(buildVisualizationDataAsJavascript(pathwayMatrix)).append(System.lineSeparator());
                sb.append(HTML_OUTPUT_SUFFIX);
                Path path = FileUtils.writeToTempFile(
                        sb.toString(),
                        String.join("_", "vis_pathway_matrix", pathway.getId(), "_"),
                        ".html");
                LOG.info("visualization data saved to file " + path.toString());
            } catch (IOException e) {
                LOG.error("error while writing visualization data", e);
            }
        }
    }

    private String buildVisualizationDataAsJavascript(PathwayMatrix pathwayMatrix) {
        List<Integer> sliceIndexes = new IntArrayList(); // x dimension
        List<Integer> rowIndexes = new IntArrayList(); // z dimension
        List<Integer> columnIndexes = new IntArrayList(); // y dimension
        List<PathwayMatrixCell> pathwayMatrixCells = new ObjectArrayList<>();
        pathwayMatrix.getNonNullValues(sliceIndexes, rowIndexes, columnIndexes, pathwayMatrixCells);
        SortedMap<Integer, TraceData> traceDataByZ = new Int2ObjectRBTreeMap<>();
        for (int i = 0; i < sliceIndexes.size(); i++) {
            int x = sliceIndexes.get(i);
            int y = columnIndexes.get(i);
            int z = rowIndexes.get(i);
            String text = pathwayMatrixCells.get(i).toString();
            TraceData traceData = traceDataByZ.computeIfAbsent(z, newZ -> new TraceData(newZ));
            traceData.xValues.add(x);
            traceData.yValues.add(y);
            traceData.textValues.add(text);
        }
        if (traceDataByZ.size() > COLORS.length) {
            LOG.warn("too few colors for this PathwayMatrix: re-using colors");
        }
        StringBuilder sb = new StringBuilder();
        List<String> traceVariables = new ObjectArrayList<>();
        for (int z : traceDataByZ.keySet()) {
            TraceData traceData = traceDataByZ.get(z);
            String traceVariable = String.format("trace%d", traceData.z);
            traceVariables.add(traceVariable);
            sb.append("var ").append(traceVariable).append(" = {");
            sb.append("  name : '").append(Z_VALUE_2_Z_LABEL.get(traceData.z)).append("',");
            sb.append("  x : ").append(traceData.getXValuesAsJavascriptArray()).append(",");
            sb.append("  y : ").append(traceData.getYValuesAsJavascriptArray()).append(",");
            sb.append("  z : ").append(traceData.getZValuesAsJavascriptArray()).append(",");
            sb.append("  text : ").append(traceData.getTextValuesAsJavascriptArray()).append(",");
            sb.append("  mode : 'markers',");
            sb.append("  type : 'scatter3d',");
            sb.append("  marker : {");
            sb.append("    color : '").append(COLORS[traceData.z % COLORS.length]).append("',");
            sb.append("    size : 10,");
            sb.append("    line : {");
            sb.append("      color : 'rgb(255,255,255)',");
            sb.append("      width : 0.5");
            sb.append("    }");
            sb.append("  }");
            sb.append("};");
        }
        sb.append("var data = [ ").append(String.join(",", traceVariables)).append("];");
        return sb.toString();
    }

    private final class TraceData {
        private final int z;
        private final List<Integer> xValues = new IntArrayList();
        private final List<Integer> yValues = new IntArrayList();
        private final List<String> textValues = new ObjectArrayList<>();

        public TraceData(int z) {
            this.z = z;
        }

        public String getXValuesAsJavascriptArray() {
            return getAsJavascriptArray(xValues);
        }

        public String getYValuesAsJavascriptArray() {
            return getAsJavascriptArray(yValues);
        }

        public String getZValuesAsJavascriptArray() {
            return getAsJavascriptArray(Collections.nCopies(xValues.size(), Z_VALUE_2_Z_LABEL.get(z)));
        }

        public String getTextValuesAsJavascriptArray() {
            return getAsJavascriptArray(textValues);
        }

        private <T> String getAsJavascriptArray(Iterable<T> ts) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            for (T t : ts) {
                sb.append("'").append(t).append("',");
            }
            if (sb.length() > 2) sb.setLength(sb.length() - 1); // removing the last ,
            sb.append(" ]");
            return sb.toString();
        }

    }

}
