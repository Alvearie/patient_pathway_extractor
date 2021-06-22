package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import java.util.Arrays;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class PathwayImageBuilder {

    /**
     * Flatten a {@link PathwayMatrix} by concatenating each slice one after the other after trimming empty columns
     * from the previous slice.
     * 
     * Example:
     * 
     * ┌───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬───────────────┬──────────────┬──────────────┐
     * │UNKNOWNBUCKET_M│               │               │               │               │               │               │               │               │               │               │               │               │              │              │
     * │_white_F       │               │               │               │               │               │               │               │               │               │               │               │               │              │              │
     * ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼──────────────┼──────────────┤
     * │               │               │               │               │8302-2_UNKNOWNB│72514-3_UNKNOWN│29463-7_UNKNOWN│39156-5_UNKNOWN│8462-4_UNKNOWNB│8480-6_UNKNOWNB│2093-3_UNKNOWNB│2571-8_UNKNOWNB│18262-6_UNKNOWN│2085-9_UNKNOWN│72166-2_UNKNOW│
     * │               │               │               │               │UCKET          │BUCKET         │BUCKET         │BUCKET         │UCKET          │UCKET          │UCKET          │UCKET          │BUCKET         │BUCKET        │NBUCKET       │
     * ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼──────────────┼──────────────┤
     * │65363002       │               │               │               │65363002       │               │               │               │               │               │               │               │               │              │              │
     * ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼──────────────┼──────────────┤
     * │849574_UNKNOWNB│849574_UNKNOWNB│1000126_UNKNOWN│               │               │               │               │               │               │               │               │               │               │              │              │
     * │UCKET          │UCKET          │BUCKET         │               │               │               │               │               │               │               │               │               │               │              │              │
     * ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼──────────────┼──────────────┤
     * │               │               │               │76601001       │               │               │               │               │               │               │               │               │               │              │              │
     * ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼───────────────┼──────────────┼──────────────┤
     * │               │               │               │               │ALIVE_247      │               │               │               │               │               │               │               │               │              │              │
     * └───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴───────────────┴──────────────┴──────────────┘
     */
    public PathwayImage trimAndConcatenateSlices(PathwayMatrix pathwayMatrix) {
        List<Integer> sliceIndexes = new IntArrayList();
        List<Integer> rowIndexes = new IntArrayList();
        List<Integer> colIndexes = new IntArrayList();
        List<PathwayMatrixCell> pathwayMatrixCells = new ObjectArrayList<>();
        pathwayMatrix.getNonNullValues(sliceIndexes, rowIndexes, colIndexes, pathwayMatrixCells);
        int numberOfSlices = pathwayMatrix.slices();
        int[] highestColumnPerSlice = new int[numberOfSlices];
        Arrays.fill(highestColumnPerSlice, 0);
        for (int i = 0; i < sliceIndexes.size(); i++) {
            int slice = sliceIndexes.get(i);
            int col = colIndexes.get(i);
            highestColumnPerSlice[slice] = Math.max(highestColumnPerSlice[slice], col);
        }
        int[] sizeUpToSlice = new int[numberOfSlices];
        sizeUpToSlice[0] = 1 + highestColumnPerSlice[0];
        for (int slice = 1; slice < numberOfSlices; slice++)
            sizeUpToSlice[slice] = sizeUpToSlice[slice - 1] + highestColumnPerSlice[slice] + 1;
        int rows = pathwayMatrix.rows();
        int cols = sizeUpToSlice[numberOfSlices - 1];
        PathwayImage pathwayImage = new PathwayImage(rows, cols);
        for (int i = 0; i < sliceIndexes.size(); i++) {
            int slice = sliceIndexes.get(i);
            int row = rowIndexes.get(i);
            int col = colIndexes.get(i);
            PathwayMatrixCell pathwayMatrixCell = pathwayMatrixCells.get(i);
            int destCol = (slice == 0) ? col : sizeUpToSlice[slice - 1] + col;
            pathwayImage.setQuick(row, destCol, pathwayMatrixCell.asStringValue());
        }
        return pathwayImage;
    }
    
    
    /**
     * Flatten a {@link PathwayMatrix} by collapsing each slice into a single column, and concatenating values for 
     * those dimensions that have more than one value in the slice.
     * 
     * Example:
     * 
     * ┌───────────────────────────────────────────────┬───────────────────────────────────────────────┬───────────────────────────────────────────────┬──────────────────────────────────────────────┬──────────────────────────────────────────────┐
     * │UNKNOWNBUCKET_M_white_F                        │                                               │                                               │                                              │                                              │
     * ├───────────────────────────────────────────────┼───────────────────────────────────────────────┼───────────────────────────────────────────────┼──────────────────────────────────────────────┼──────────────────────────────────────────────┤
     * │                                               │                                               │                                               │                                              │8302-2_UNKNOWNBUCKET;72514-3_UNKNOWNBUCKET;294│
     * │                                               │                                               │                                               │                                              │63-7_UNKNOWNBUCKET;39156-5_UNKNOWNBUCKET;8462-│
     * │                                               │                                               │                                               │                                              │4_UNKNOWNBUCKET;8480-6_UNKNOWNBUCKET;2093-3_UN│
     * │                                               │                                               │                                               │                                              │KNOWNBUCKET;2571-8_UNKNOWNBUCKET;18262-6_UNKNO│
     * │                                               │                                               │                                               │                                              │WNBUCKET;2085-9_UNKNOWNBUCKET;72166-2_UNKNOWNB│
     * │                                               │                                               │                                               │                                              │UCKET                                         │
     * ├───────────────────────────────────────────────┼───────────────────────────────────────────────┼───────────────────────────────────────────────┼──────────────────────────────────────────────┼──────────────────────────────────────────────┤
     * │65363002                                       │                                               │                                               │                                              │65363002                                      │
     * ├───────────────────────────────────────────────┼───────────────────────────────────────────────┼───────────────────────────────────────────────┼──────────────────────────────────────────────┼──────────────────────────────────────────────┤
     * │849574_UNKNOWNBUCKET                           │849574_UNKNOWNBUCKET                           │1000126_UNKNOWNBUCKET                          │                                              │                                              │
     * ├───────────────────────────────────────────────┼───────────────────────────────────────────────┼───────────────────────────────────────────────┼──────────────────────────────────────────────┼──────────────────────────────────────────────┤
     * │                                               │                                               │                                               │76601001                                      │                                              │
     * ├───────────────────────────────────────────────┼───────────────────────────────────────────────┼───────────────────────────────────────────────┼──────────────────────────────────────────────┼──────────────────────────────────────────────┤
     * │                                               │                                               │                                               │                                              │ALIVE_247                                     │
     * └───────────────────────────────────────────────┴───────────────────────────────────────────────┴───────────────────────────────────────────────┴──────────────────────────────────────────────┴──────────────────────────────────────────────┘
     */
    public PathwayImage collapseSlices(PathwayMatrix pathwayMatrix) {
        int rows = pathwayMatrix.rows();
        int cols = pathwayMatrix.slices();
        PathwayImage pathwayImage = new PathwayImage(rows, cols);
        List<Integer> sliceIndexes = new IntArrayList();
        List<Integer> rowIndexes = new IntArrayList();
        List<Integer> colIndexes = new IntArrayList();
        List<PathwayMatrixCell> pathwayMatrixCells = new ObjectArrayList<>();
        pathwayMatrix.getNonNullValues(sliceIndexes, rowIndexes, colIndexes, pathwayMatrixCells);
        for (int i = 0; i < pathwayMatrixCells.size(); i++) {
            int slice = sliceIndexes.get(i);
            int row = rowIndexes.get(i);
            PathwayMatrixCell pathwayMatrixCell = pathwayMatrixCells.get(i);
            pathwayImage.appendQuick(row, slice, pathwayMatrixCell.asStringValue());
        }
        return pathwayImage;
    }

}
