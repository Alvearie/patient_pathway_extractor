package com.ibm.research.drl.deepguidelines.pathways.extractor.testutils;

import java.util.List;
import java.util.Map;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventTemporalType;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.AliveOutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.ConditionsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.DemographicsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.MedicationsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.ObservationsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.OutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayImage;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.ProceduresPathwayMatrixCell;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/*
 * Procduces expected data for Synthea dataset with 1 patient generated with seed 3
 */
public class ExpectedDataForSynthea1PatientSeed3 {

    private static final DemographicsPathwayMatrixCell DEMOGRAPHICS_PATHWAY_MATRIX_CELL = new DemographicsPathwayMatrixCell("1965-12-08", "M",
            "white", "F");

    private static final OutcomePathwayMatrixCell OUTCOME_PATHWAY_MATRIX_CELL = new AliveOutcomePathwayMatrixCell(247);

    private static final Map<Integer, List<PathwayMatrixCell>> PATHWAY_MATRIX_CELLS_BY_SLICE = new Int2ObjectOpenHashMap<>();

    static {
        DEMOGRAPHICS_PATHWAY_MATRIX_CELL.setAgeBucket("DemF");
        OUTCOME_PATHWAY_MATRIX_CELL.setOutcomeBucket("AliveE");

        // pathway:
        //        
        //        ┌───────────────────┬───────────────────┬───────────────────┬──────────────────┐
        //        │medical type       │temporal type      │event id           │date              │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │CONDITION          │START              │Condition_3        │2009-07-27T00:00:0│ slice 0
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │START              │Condition_3        │2009-07-27T00:00:0│
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │STOP               │Condition_3        │2009-08-10T23:59:5│ slice 1
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │START              │Medication_2       │2009-11-18T00:00:0│ slice 2
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │PROCEDURE          │ISOLATED           │Medication_2       │2009-11-18T23:59:5│ slice 2
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │CONDITION          │STOP               │Condition_3        │2010-03-31T23:59:5│ slice 4
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │OBSERVATION        │ISOLATED           │Immunization_1     │2010-03-31T23:59:5│
        //        │                   │                   │                   │9Z                │
        //        └───────────────────┴───────────────────┴───────────────────┴──────────────────┘

        // slice 0
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(0, new ObjectArrayList<>());
        // CONDITION, Condition_3, START
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice0Cell0 = new ConditionsPathwayMatrixCell("65363002", PathwayEventTemporalType.START);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).add(pathwayMatrixCellSlice0Cell0);
        // MEDICATION, Condition_3, START
        MedicationsPathwayMatrixCell pathwayMatrixCellSlice0Cell1 = new MedicationsPathwayMatrixCell("849574", 1, PathwayEventTemporalType.START);
        pathwayMatrixCellSlice0Cell1.setDispensesBucket("MedA");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).add(pathwayMatrixCellSlice0Cell1);

        // slice 1
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(1, new ObjectArrayList<>());
        // MEDICATION, Condition_3, STOP
        MedicationsPathwayMatrixCell pathwayMatrixCellSlice1Cell0 = new MedicationsPathwayMatrixCell("849574", 1, PathwayEventTemporalType.STOP);
        pathwayMatrixCellSlice1Cell0.setDispensesBucket("MedA");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).add(pathwayMatrixCellSlice1Cell0);

        // slice 2
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(2, new ObjectArrayList<>());
        // MEDICATION, Medication_2, START
        MedicationsPathwayMatrixCell pathwayMatrixCellSlice2Cell0 = new MedicationsPathwayMatrixCell("1000126", 10, PathwayEventTemporalType.START);
        pathwayMatrixCellSlice2Cell0.setDispensesBucket("MedB");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).add(pathwayMatrixCellSlice2Cell0);

        // slice 3
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(3, new ObjectArrayList<>());
        // PROCEDURE, Medication_2, ISOLATED
        ProceduresPathwayMatrixCell pathwayMatrixCellSlice3Cell0 = new ProceduresPathwayMatrixCell("76601001", PathwayEventTemporalType.ISOLATED);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).add(pathwayMatrixCellSlice3Cell0);

        // slice 4
        PATHWAY_MATRIX_CELLS_BY_SLICE.put(4, new ObjectArrayList<>());
        // OBSERVATION, Immunization_1, ISOLATED
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell0 = new ObservationsPathwayMatrixCell("8302-2", "161.5", "cm", PathwayEventTemporalType.ISOLATED);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell0);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell1 = new ObservationsPathwayMatrixCell("72514-3", "0.6", "{score}", PathwayEventTemporalType.ISOLATED);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell1);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell2 = new ObservationsPathwayMatrixCell("29463-7", "121.2", "kg", PathwayEventTemporalType.ISOLATED);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell2);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell3 = new ObservationsPathwayMatrixCell("39156-5", "46.5", "kg/m2", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell3.setValueAndUnitsBucket("High");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell3);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell4 = new ObservationsPathwayMatrixCell("8462-4", "77.3", "mmHg", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell4.setValueAndUnitsBucket("Normal");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell4);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell5 = new ObservationsPathwayMatrixCell("8480-6", "117.3", "mmHg", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell5.setValueAndUnitsBucket("Normal");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell5);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell6 = new ObservationsPathwayMatrixCell("2093-3", "169.6", "mg/dL", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell6.setValueAndUnitsBucket("Normal");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell6);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell7 = new ObservationsPathwayMatrixCell("2571-8", "137.1", "mg/dL", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell7.setValueAndUnitsBucket("Normal");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell7);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell8 = new ObservationsPathwayMatrixCell("18262-6", "66.1", "mg/dL", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell8.setValueAndUnitsBucket("Optimal");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell8);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell9 = new ObservationsPathwayMatrixCell("2085-9", "76.0", "mg/dL", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell9.setValueAndUnitsBucket("High");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell9);
        ObservationsPathwayMatrixCell pathwayMatrixCellSlice4Cell10 = new ObservationsPathwayMatrixCell("72166-2", "Never smoker", "", PathwayEventTemporalType.ISOLATED);
        pathwayMatrixCellSlice4Cell10.setValueAndUnitsBucket("Never");
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell10);
        // CONDITION, Condition_3, STOP
        ConditionsPathwayMatrixCell pathwayMatrixCellSlice4Cell11 = new ConditionsPathwayMatrixCell("65363002", PathwayEventTemporalType.STOP);
        PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).add(pathwayMatrixCellSlice4Cell11);
    }

    public static PathwayMatrix buildPathwayMatrixForCondition3() {
        PathwayMatrix pathwayMatrix = new PathwayMatrix(5, 6, 11);
        // demographics
        pathwayMatrix.setQuick(0, 0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL);
        // slice 0
        pathwayMatrix.setQuick(0, 2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0)); // CONDITION, Condition_3, START
        pathwayMatrix.setQuick(0, 3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1)); // MEDICATION, Condition_3, START
        // slice 1
        pathwayMatrix.setQuick(1, 3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0)); // MEDICATION, Condition_3, STOP
        // slice 2
        pathwayMatrix.setQuick(2, 3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0)); // MEDICATION, Medication_2, START
        // slice 3
        pathwayMatrix.setQuick(3, 4, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0)); // PROCEDURE, Medication_2, ISOLATED
        // slice 4
        pathwayMatrix.setQuick(4, 1, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(0)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(1)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(2)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(3)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(4)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 5, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(5)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 6, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(6)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 7, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(7)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 8, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(8)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 9, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(9)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 1, 10, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(10)); // OBSERVATION, Immunization_1, ISOLATED
        pathwayMatrix.setQuick(4, 2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(11)); // CONDITION, Condition_3, STOP
        // outcomes
        pathwayMatrix.setQuick(4, 5, 0, OUTCOME_PATHWAY_MATRIX_CELL);
        // done
        return pathwayMatrix;
    }

    public static PathwayImage getPathwayImageForCondition3WithTrimmedAndConcatenatedSlices() {
        PathwayImage pathwayImage = new PathwayImage(6, 15);
        // demographics
        pathwayImage.setQuick(0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL.asStringValue());
        // slice 0
        pathwayImage.setQuick(2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0).asStringValue()); // CONDITION, Condition_3, START
        pathwayImage.setQuick(3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1).asStringValue()); // MEDICATION, Condition_3, START
        // slice 1
        pathwayImage.setQuick(3, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0).asStringValue()); // MEDICATION, Condition_3, STOP
        // slice 2
        pathwayImage.setQuick(3, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0).asStringValue()); // MEDICATION, Medication_2, START
        // slice 3
        pathwayImage.setQuick(4, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0).asStringValue()); // PROCEDURE, Medication_2, ISOLATED
        // slice 4
        pathwayImage.setQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(0).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 5, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(1).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 6, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(2).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 7, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(3).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 8, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(4).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 9, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(5).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 10, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(6).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 11, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(7).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 12, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(8).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 13, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(9).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(1, 14, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(10).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.setQuick(2, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(11).asStringValue()); // CONDITION, Condition_3, STOP
        // outcomes
        pathwayImage.setQuick(5, 4, OUTCOME_PATHWAY_MATRIX_CELL.asStringValue());
        // done
        return pathwayImage;
    }

    public static PathwayImage getPathwayImageForCondition3WithCollapsedSlices() {
        PathwayImage pathwayImage = new PathwayImage(6, 5);
        // demographics
        pathwayImage.appendQuick(0, 0, DEMOGRAPHICS_PATHWAY_MATRIX_CELL.asStringValue());
        // slicee 0
        pathwayImage.appendQuick(2, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(0).asStringValue()); // CONDITION, Condition_3, START
        pathwayImage.setQuick(3, 0, PATHWAY_MATRIX_CELLS_BY_SLICE.get(0).get(1).asStringValue()); // MEDICATION, Condition_3, START
        // slice 1

        pathwayImage.setQuick(3, 1, PATHWAY_MATRIX_CELLS_BY_SLICE.get(1).get(0).asStringValue()); // MEDICATION, Condition_3, STOP
        // slice 2
        pathwayImage.setQuick(3, 2, PATHWAY_MATRIX_CELLS_BY_SLICE.get(2).get(0).asStringValue()); // MEDICATION, Medication_2, START
        // slice 3
        pathwayImage.appendQuick(4, 3, PATHWAY_MATRIX_CELLS_BY_SLICE.get(3).get(0).asStringValue()); // PROCEDURE, Medication_2, ISOLATED
        // slice 4
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(0).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(1).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(2).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(3).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(4).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(5).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(6).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(7).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(8).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(9).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(1, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(10).asStringValue()); // OBSERVATION, Immunization_1, ISOLATED
        pathwayImage.appendQuick(2, 4, PATHWAY_MATRIX_CELLS_BY_SLICE.get(4).get(11).asStringValue()); // CONDITION, Condition_3, STOP
        // outcomes
        pathwayImage.setQuick(5, 4, OUTCOME_PATHWAY_MATRIX_CELL.asStringValue());
        // done
        return pathwayImage;
    }
}
