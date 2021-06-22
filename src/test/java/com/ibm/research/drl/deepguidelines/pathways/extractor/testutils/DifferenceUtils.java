package com.ibm.research.drl.deepguidelines.pathways.extractor.testutils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixCellWithCoordinates;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class DifferenceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DifferenceUtils.class);

    public static <T> String diff(List<T> original, List<T> revised) {
        StringBuilder sb = new StringBuilder();
        try {
            Patch<T> patch = DiffUtils.diff(original, revised);
            for (AbstractDelta<T> delta : patch.getDeltas()) {
                sb.append(delta.toString()).append(System.lineSeparator());
            }
        } catch (DiffException e) {
            String msg = "error while computing diff";
            sb.append(msg);
            LOG.error(msg, e);
        }
        return sb.toString();
    }

    public static String diff(PathwayMatrix original, PathwayMatrix revised) {
        return diff(toMatrixCells(original), toMatrixCells(revised));
    }

    private static List<PathwayMatrixCellWithCoordinates> toMatrixCells(PathwayMatrix pathwayMatrix) {
        List<PathwayMatrixCellWithCoordinates> pathwayMatrixCellsWithCoordinates = new ObjectArrayList<>();
        List<Integer> sliceIndexes = new ObjectArrayList<>();
        List<Integer> rowIndexes = new ObjectArrayList<>();
        List<Integer> columnIndexes = new ObjectArrayList<>();
        List<PathwayMatrixCell> pathwayMatrixCells = new ObjectArrayList<>();
        pathwayMatrix.getNonNullValues(sliceIndexes, rowIndexes, columnIndexes, pathwayMatrixCells);
        for (int i = 0; i < sliceIndexes.size(); i++) {
            pathwayMatrixCellsWithCoordinates.add(new PathwayMatrixCellWithCoordinates(sliceIndexes.get(i), rowIndexes.get(i), columnIndexes.get(i), pathwayMatrixCells.get(i)));
        }
        return pathwayMatrixCellsWithCoordinates;
    }

    public static String diff(PathwayImage original, PathwayImage revised) {
        return diff(original.getTableValues(), revised.getTableValues());
    }

}
