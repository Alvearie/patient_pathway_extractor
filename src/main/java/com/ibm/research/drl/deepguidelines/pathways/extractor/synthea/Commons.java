package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class Commons {

    public static final String INSTANT_START_OF_DAY = "T00:00:00Z";
    public static final String INSTANT_END_OF_DAY = "T23:59:59Z";

    public static final EnumMap<SyntheaMedicalTypes, String> SYNTHEA_FILE_NAMES = new EnumMap<>(SyntheaMedicalTypes.class);

    public static final EnumMap<SyntheaMedicalTypes, String[]> SYNTHEA_COLUMN_NAMES = new EnumMap<>(SyntheaMedicalTypes.class);

    public static final EnumMap<SyntheaMedicalTypes, String> SYNTHEA_PATIENT_COLUMN_NAME = new EnumMap<>(SyntheaMedicalTypes.class);

    public static final EnumMap<SyntheaMedicalTypes, String> SYNTHEA_EVENT_COLUMN_NAME = new EnumMap<>(SyntheaMedicalTypes.class);

    public static final EnumMap<SyntheaMedicalTypes, String[]> SYNTHEA_FEATURE_COLUMN_NAMES = new EnumMap<>(SyntheaMedicalTypes.class);

    public static final Set<SyntheaMedicalTypes> SYNTHEA_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS = new ObjectOpenHashSet<>(Arrays.asList(
            SyntheaMedicalTypes.ALLERGIES,
            SyntheaMedicalTypes.CAREPLANS,
            SyntheaMedicalTypes.CONDITIONS,
            SyntheaMedicalTypes.ENCOUNTERS,
            SyntheaMedicalTypes.MEDICATIONS));

    public static final Set<SyntheaMedicalTypes> SYNTHEA_MEDICAL_TYPES_YIELDING_ISOLATED_PATHWAY_EVENTS = new ObjectOpenHashSet<>(Arrays.asList(
            SyntheaMedicalTypes.IMAGING_STUDIES,
            SyntheaMedicalTypes.IMMUNIZATIONS,
            SyntheaMedicalTypes.OBSERVATIONS,
            SyntheaMedicalTypes.PROCEDURES));

    static {
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.ALLERGIES, "allergies.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.CAREPLANS, "careplans.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.CONDITIONS, "conditions.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.ENCOUNTERS, "encounters.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.IMAGING_STUDIES, "imaging_studies.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.IMMUNIZATIONS, "immunizations.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.MEDICATIONS, "medications.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.OBSERVATIONS, "observations.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.PATIENTS, "patients.csv");
        SYNTHEA_FILE_NAMES.put(SyntheaMedicalTypes.PROCEDURES, "procedures.csv");

        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.ALLERGIES,
                new String[] { "START", "STOP", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.CAREPLANS,
                new String[] { "ID", "START", "STOP", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION", "REASONCODE", "REASONDESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.CONDITIONS,
                new String[] { "START", "STOP", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.ENCOUNTERS,
                new String[] { "ID", "START", "STOP", "PATIENT", "ENCOUNTERCLASS", "CODE", "DESCRIPTION", "COST", "REASONCODE",
                        "REASONDESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.IMAGING_STUDIES,
                new String[] { "ID", "DATE", "PATIENT", "ENCOUNTER", "BODYSITE_CODE", "BODYSITE_DESCRIPTION", "MODALITY_CODE", "MODALITY_DESCRIPTION",
                        "SOP_CODE", "SOP_DESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.IMMUNIZATIONS,
                new String[] { "DATE", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION", "COST" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.MEDICATIONS,
                new String[] { "START", "STOP", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION", "COST", "DISPENSES", "TOTALCOST", "REASONCODE",
                        "REASONDESCRIPTION" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.OBSERVATIONS,
                new String[] { "DATE", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION", "VALUE", "UNITS", "TYPE" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.PATIENTS,
                new String[] { "ID", "BIRTHDATE", "DEATHDATE", "SSN", "DRIVERS", "PASSPORT", "PREFIX", "FIRST", "LAST", "SUFFIX", "MAIDEN", "MARITAL",
                        "RACE", "ETHNICITY", "GENDER", "BIRTHPLACE", "ADDRESS", "CITY", "STATE", "ZIP" });
        SYNTHEA_COLUMN_NAMES.put(SyntheaMedicalTypes.PROCEDURES,
                new String[] { "DATE", "PATIENT", "ENCOUNTER", "CODE", "DESCRIPTION", "COST", "REASONCODE", "REASONDESCRIPTION" });

        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.ALLERGIES, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.CAREPLANS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.CONDITIONS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.ENCOUNTERS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.IMAGING_STUDIES, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.IMMUNIZATIONS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.MEDICATIONS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.OBSERVATIONS, "PATIENT");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.PATIENTS, "ID");
        SYNTHEA_PATIENT_COLUMN_NAME.put(SyntheaMedicalTypes.PROCEDURES, "PATIENT");

        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.ALLERGIES, "ENCOUNTER");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.CAREPLANS, "ID");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.CONDITIONS, "ENCOUNTER");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.ENCOUNTERS, "ID");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.IMAGING_STUDIES, "ID");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.IMMUNIZATIONS, "ENCOUNTER");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.MEDICATIONS, "ENCOUNTER");
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.OBSERVATIONS, "ENCOUNTER");
        // patients have no event column
        SYNTHEA_EVENT_COLUMN_NAME.put(SyntheaMedicalTypes.PROCEDURES, "ENCOUNTER");

        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.ALLERGIES, new String[] { "CODE" });
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.CAREPLANS, new String[] { "CODE" });
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.CONDITIONS, new String[] { "CODE" });
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.ENCOUNTERS, new String[] {});
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.IMAGING_STUDIES, new String[] { "BODYSITE_CODE", "MODALITY_CODE" });
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.IMMUNIZATIONS, new String[] {});
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.MEDICATIONS, new String[] { "CODE", "DISPENSES" });
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.OBSERVATIONS, new String[] { "CODE", "VALUE", "UNITS" });
        // patients have no feature columns
        SYNTHEA_FEATURE_COLUMN_NAMES.put(SyntheaMedicalTypes.PROCEDURES, new String[] { "CODE" });
    }

}