package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

public class PathwayMatrixCellWithCoordinates {

    private final int slice;
    private final int row;
    private final int column;
    private final PathwayMatrixCell pathwayMatrixCell;

    public PathwayMatrixCellWithCoordinates(int slice, int row, int column, PathwayMatrixCell pathwayMatrixCell) {
        this.slice = slice;
        this.row = row;
        this.column = column;
        this.pathwayMatrixCell = pathwayMatrixCell;
    }

    public int getSlice() {
        return slice;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public PathwayMatrixCell getPathwayMatrixCell() {
        return pathwayMatrixCell;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + ((pathwayMatrixCell == null) ? 0 : pathwayMatrixCell.hashCode());
        result = prime * result + row;
        result = prime * result + slice;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PathwayMatrixCellWithCoordinates other = (PathwayMatrixCellWithCoordinates) obj;
        if (column != other.column) return false;
        if (pathwayMatrixCell == null) {
            if (other.pathwayMatrixCell != null) return false;
        } else if (!pathwayMatrixCell.equals(other.pathwayMatrixCell)) return false;
        if (row != other.row) return false;
        if (slice != other.slice) return false;
        return true;
    }

    @Override
    public String toString() {
        return "PathwayMatrixCellWithCoordinates [slice=" + slice + ", row=" + row + ", column=" + column + ", pathwayMatrixCell=" + pathwayMatrixCell
                + "]";
    }

}
