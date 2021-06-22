package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SerializableAsHTMLTable;

import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.impl.SparseObjectMatrix2D;

/**
 * A flattened representation (using a sparse 2D matrix) of a {@link PathwayMatrix}.
 * 
 * - The cell values are String.
 * 
 * - The rows are the dimensions:
 * -- demographics
 * -- observation & imaging studies
 * -- conditions
 * -- medications
 * -- procedures
 * -- outcomes
 * 
 * - The number of columns depend on the format.
 */
public class PathwayImage implements SerializableAsHTMLTable {

    private static final String DELIMITER_FOR_CONCATENATING_CELL_VALUES = ";";

    private final SparseObjectMatrix2D matrix;

    public PathwayImage(int rows, int cols) {
        this.matrix = new SparseObjectMatrix2D(rows, cols);
    }

    public void checkShape(PathwayImage other) {
        matrix.checkShape(other.matrix);
    }

    @Override
    public int rows() {
        return matrix.rows();
    }

    @Override
    public int columns() {
        return matrix.columns();
    }

    public String getQuick(int row, int col) {
        return (String) matrix.getQuick(row, col);
    }

    public void setQuick(int row, int col, String value) {
        matrix.setQuick(row, col, value);
    }

    public void appendQuick(int row, int col, String value) {
        String previous = (String) matrix.getQuick(row, col);
        if (previous == null)
            matrix.setQuick(row, col, value);
        else
            matrix.setQuick(row, col, String.join(DELIMITER_FOR_CONCATENATING_CELL_VALUES, previous, value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PathwayImage other = (PathwayImage) obj;
        if (matrix == null) {
            if (other.matrix != null) return false;
        } else if (!matrix.equals(other.matrix)) return false;
        return true;
    }

    public String asCSVLine(int maxColumns) {
        if (maxColumns < matrix.columns()) throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder();
        int rows = matrix.rows();
        for (int row = 0; row < rows; row++) {
            IntArrayList columnIndexes = new IntArrayList();
            ObjectArrayList values = new ObjectArrayList();
            matrix.viewRow(row).getNonZeros(columnIndexes, values);
            String[] fragment = new String[maxColumns];
            Arrays.fill(fragment, "");
            for (int i = 0; i < columnIndexes.size(); i++) {
                int col = columnIndexes.get(i);
                String value = (String) values.get(i);
                fragment[col] = value;
            }
            sb.append(String.join(",", fragment));
            if (row + 1 < rows) sb.append(",");
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    @Override
    public Optional<List<String>> getTableHeaders() {
        return Optional.empty();
    }
    
    @Override
    public List<List<String>> getTableValues() {
        int rows = matrix.rows();
        int cols = matrix.columns();
        List<List<String>> tableValues = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(rows);
        for (int row = 0; row < rows; row++) {
            List<String> rowValues = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(cols);
            tableValues.add(rowValues);
            for (int col = 0; col < cols; col++) {
                String value = getQuick(row, col);
                if (value == null) rowValues.add(EMPTY_CELL);
                else rowValues.add(value);
            }
        }
        return tableValues;
    }

}
