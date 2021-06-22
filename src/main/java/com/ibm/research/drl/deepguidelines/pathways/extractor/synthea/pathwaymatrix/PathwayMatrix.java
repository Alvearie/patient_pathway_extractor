package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import java.util.List;

import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.impl.SparseObjectMatrix3D;

/**
 * A pathway full representation as a sparse 3D matrix:
 * 
 *         columns: values of the dimensions of pathway at a given time (slice) 
 *        ^
 *       /
 *      /
 *     /
 *    -------------------> slices: time along pathway
 *    |
 *    |
 *    |
 *    v 
 *    rows: dimensions of pathway (demographics, observations, etc.)
 *    
 * - The rows are the dimensions:
 * -- demographics
 * -- observation & imaging studies
 * -- conditions
 * -- medications
 * -- procedures
 * -- outcomes
 */
public class PathwayMatrix {

    private final SparseObjectMatrix3D matrix;

    public PathwayMatrix(int slices, int rows, int cols) {
        this.matrix = new SparseObjectMatrix3D(slices, rows, cols);
    }

    public void checkShape(PathwayMatrix other) {
        matrix.checkShape(other.matrix);
    }

    public int slices() {
        return matrix.slices();
    }

    public int rows() {
        return matrix.rows();
    }

    public int columns() {
        return matrix.columns();
    }

    public PathwayMatrixCell getQuick(int slice, int row, int col) {
        return (PathwayMatrixCell) matrix.getQuick(slice, row, col);
    }

    public void setQuick(int slice, int row, int col, PathwayMatrixCell value) {
        matrix.setQuick(slice, row, col, value);
    }
    
    public int cardinality() {
        return matrix.cardinality();
    }

    public void getNonNullValues(List<Integer> sliceIndexes, List<Integer> rowIndexes, List<Integer> columnIndexes,
            List<PathwayMatrixCell> pathwayMatrixCells) {
        IntArrayList sIndexes = new IntArrayList();
        IntArrayList rIndexes = new IntArrayList();
        IntArrayList cIndexes = new IntArrayList();
        ObjectArrayList objects = new ObjectArrayList();
        matrix.getNonZeros(sIndexes, rIndexes, cIndexes, objects);
        for (int i = 0; i < objects.size(); i++) {
            sliceIndexes.add(sIndexes.get(i));
            rowIndexes.add(rIndexes.get(i));
            columnIndexes.add(cIndexes.get(i));
            pathwayMatrixCells.add((PathwayMatrixCell) objects.get(i));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PathwayMatrix other = (PathwayMatrix) obj;
        if (matrix == null) {
            if (other.matrix != null) return false;
        } else if (!matrix.equals(other.matrix)) return false;
        return true;
    }

}
