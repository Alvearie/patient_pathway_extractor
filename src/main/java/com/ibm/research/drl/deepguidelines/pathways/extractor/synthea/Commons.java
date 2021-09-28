package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;

import com.ibm.fhir.model.type.code.FHIRResourceType;

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

    public static final EnumMap<FHIRResourceType.Value, String> FHIR_FILE_NAMES = new EnumMap<>(FHIRResourceType.Value.class);

    public static final EnumMap<FHIRResourceType.Value, String> FHIR_START_DATE_ELEMENT_FHIRPATH = new EnumMap<>(FHIRResourceType.Value.class);

    public static final EnumMap<FHIRResourceType.Value, String> FHIR_STOP_DATE_ELEMENT_FHIRPATH = new EnumMap<>(FHIRResourceType.Value.class);

    public static final EnumMap<FHIRResourceType.Value, String> FHIR_PATIENT_ELEMENT_FHIRPATH = new EnumMap<>(FHIRResourceType.Value.class);

    public static final EnumMap<FHIRResourceType.Value, String> FHIR_EVENT_ELEMENT_FHIRPATH = new EnumMap<>(FHIRResourceType.Value.class);

    public static final EnumMap<FHIRResourceType.Value, String[]> FHIR_FEATURE_ELEMENT_FHIRPATHS = new EnumMap<>(FHIRResourceType.Value.class);
    
    public static final EnumMap<FHIRResourceType.Value, SyntheaMedicalTypes> FHIR_MEDICAL_TYPE_MAP = new EnumMap<>(FHIRResourceType.Value.class);

    public static final Set<FHIRResourceType.Value> FHIR_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS = new ObjectOpenHashSet<>(Arrays.asList(
        FHIRResourceType.Value.ALLERGY_INTOLERANCE,
        FHIRResourceType.Value.CARE_PLAN,
        FHIRResourceType.Value.CONDITION,
        FHIRResourceType.Value.ENCOUNTER));

    public static final Set<FHIRResourceType.Value> FHIR_MEDICAL_TYPES_YIELDING_ISOLATED_PATHWAY_EVENTS = new ObjectOpenHashSet<>(Arrays.asList(
        FHIRResourceType.Value.IMAGING_STUDY,
        FHIRResourceType.Value.IMMUNIZATION,
        FHIRResourceType.Value.MEDICATION_REQUEST,
        FHIRResourceType.Value.OBSERVATION,
        FHIRResourceType.Value.PROCEDURE));

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

        
        
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, "AllergyIntolerance.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.CARE_PLAN, "CarePlan.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.CONDITION, "Condition.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.ENCOUNTER, "Encounter.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.IMAGING_STUDY, "ImagingStudy.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.IMMUNIZATION, "Immunization.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.MEDICATION_REQUEST, "MedicationRequest.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.OBSERVATION, "Observation.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.PATIENT, "Patient.ndjson");
        FHIR_FILE_NAMES.put(FHIRResourceType.Value.PROCEDURE, "Procedure.ndjson");
        
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, "AllergyIntolerance.onsetDateTime");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CARE_PLAN, "CarePlan.period.start");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CONDITION, "Condition.onsetDateTime");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ENCOUNTER, "Encounter.period.start");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMAGING_STUDY, "ImagingStudy.series.started");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMMUNIZATION, "Immunization.occurrenceDateTime");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.MEDICATION_REQUEST, "MedicationRequest.authoredOn");
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.OBSERVATION, "Observation.effectiveDateTime");
        // patients have no start date
        FHIR_START_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.PROCEDURE, "Procedure.performedDateTime");

        FHIR_STOP_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, "AllergyIntolerance.lastOccurrence");
        FHIR_STOP_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CARE_PLAN, "CarePlan.period.end");
        FHIR_STOP_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CONDITION, "Condition.abatementDateTime");
        FHIR_STOP_DATE_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ENCOUNTER, "Encounter.period.end");

        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, "AllergyIntolerance.patient");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CARE_PLAN, "CarePlan.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CONDITION, "Condition.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ENCOUNTER, "Encounter.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMAGING_STUDY, "ImagingStudy.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMMUNIZATION, "Immunization.patient");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.MEDICATION_REQUEST, "MedicationRequest.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.OBSERVATION, "Observation.subject");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.PATIENT, "Patient.id");
        FHIR_PATIENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.PROCEDURE, "Procedure.subject");

        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, "AllergyIntolerance.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CARE_PLAN, "CarePlan.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.CONDITION, "Condition.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.ENCOUNTER, "Encounter.id");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMAGING_STUDY, "ImagingStudy.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.IMMUNIZATION, "Immunization.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.MEDICATION_REQUEST, "MedicationRequest.encounter");
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.OBSERVATION, "Observation.encounter");
        // patients have no event id
        FHIR_EVENT_ELEMENT_FHIRPATH.put(FHIRResourceType.Value.PROCEDURE, "Procedure.encounter");

        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, new String[] { "AllergyIntolerance.code.coding.code" });
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.CARE_PLAN, new String[] { "CarePlan.activity.detail.code.coding.code" });
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.CONDITION, new String[] { "Condition.code.coding.code" });
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.ENCOUNTER, new String[] {});
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.IMAGING_STUDY, new String[] { "ImagingStudy.series.bodySite.code", "ImagingStudy.series.modality.code" });
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.IMMUNIZATION, new String[] {});
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.MEDICATION_REQUEST, new String[] { "MedicationRequest.medicationCodeableConcept.coding.code" });
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.OBSERVATION, new String[] { "Observation.code.coding.code", "Observation.valueQuantity.value", "Observation.valueQuantity.unit" });
        // patients have no features
        FHIR_FEATURE_ELEMENT_FHIRPATHS.put(FHIRResourceType.Value.PROCEDURE, new String[] { "Procedure.code.coding.code" });
        
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.ALLERGY_INTOLERANCE, SyntheaMedicalTypes.ALLERGIES);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.CARE_PLAN, SyntheaMedicalTypes.CAREPLANS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.CONDITION, SyntheaMedicalTypes.CONDITIONS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.ENCOUNTER, SyntheaMedicalTypes.ENCOUNTERS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.IMAGING_STUDY, SyntheaMedicalTypes.IMAGING_STUDIES);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.IMMUNIZATION, SyntheaMedicalTypes.IMMUNIZATIONS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.MEDICATION_REQUEST, SyntheaMedicalTypes.MEDICATIONS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.OBSERVATION, SyntheaMedicalTypes.OBSERVATIONS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.PATIENT, SyntheaMedicalTypes.PATIENTS);
        FHIR_MEDICAL_TYPE_MAP.put(FHIRResourceType.Value.PROCEDURE, SyntheaMedicalTypes.PROCEDURES);
    }

}