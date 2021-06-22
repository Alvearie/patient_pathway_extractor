package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.List;
import java.util.Optional;

public interface SerializableAsHTMLTable {
    
    public static final String EMPTY_CELL = "";

    public default String asHTMLTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<style> table, th, td { border: 1px solid black; border-collapse:collapse; } </style>");
        sb.append("<body>");
        sb.append("<table style=\"width:100%\">");
        getTableHeaders().ifPresent(tableHeaders -> {
            sb.append("<tr>");
            for (String header : tableHeaders) {
                sb.append("<th>").append(header).append("</th>");
            }
            sb.append("</tr>");
        });
        for (List<String> rowValues : getTableValues()) {
            sb.append("<tr>");
            for (String value : rowValues) {
                sb.append("<td>").append(value).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    
    public int rows();
    
    public int columns();
    
    public Optional<List<String>> getTableHeaders();
        
    public List<List<String>> getTableValues();

}
