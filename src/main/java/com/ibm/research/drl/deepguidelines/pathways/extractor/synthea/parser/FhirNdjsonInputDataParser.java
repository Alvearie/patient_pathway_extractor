package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.resource.Resource;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Date;
import com.ibm.fhir.model.type.DateTime;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Element;
import com.ibm.fhir.model.type.Instant;
import com.ibm.fhir.model.type.Reference;
import com.ibm.fhir.model.type.code.FHIRResourceType;
import com.ibm.fhir.path.FHIRPathNode;
import com.ibm.fhir.path.evaluator.FHIRPathEvaluator;
import com.ibm.fhir.path.evaluator.FHIRPathEvaluator.EvaluationContext;
import com.ibm.fhir.path.exception.FHIRPathException;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class FhirNdjsonInputDataParser extends AbstractInputDataParser {

    private static final Logger LOG = LoggerFactory.getLogger(FhirNdjsonInputDataParser.class);

    private static final String EMPTY_STRING = "";
    private static final FHIRPathEvaluator fhirPathEvaluator = FHIRPathEvaluator.evaluator();

    public FhirNdjsonInputDataParser(long now) {
        super(now);
    }

    @Override
    public Stream<Record> readAsStreamOfRecords(String inputDataPath, SyntheaMedicalTypes medicalType) {
        IteratorOfRecords iteratorOfRecords = new IteratorOfRecords(inputDataPath, medicalType);
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iteratorOfRecords,
                        iteratorOfRecords.totalNumberOfLines,
                        Spliterator.ORDERED),
                false);
    }

    @Override
    public Map<String, Patient> getPatients(String inputDataPath) {
        Map<String, Patient> patientsMap = new Object2ObjectOpenHashMap<>();
        
        try {
            for (Resource resource : FileUtils.readFhirNdjsonFile(inputDataPath + Commons.FHIR_FILE_NAMES.get(FHIRResourceType.Value.PATIENT))) {
                com.ibm.fhir.model.resource.Patient fhirPatient = (com.ibm.fhir.model.resource.Patient) resource;
                String id = fhirPatient.getId();
                String birthDate = fhirPatient.getBirthDate().getValue() != null ? fhirPatient.getBirthDate().getValue().toString() : null;
                String deathDate = null;
                Element deceased = fhirPatient.getDeceased();
                if (deceased != null && deceased instanceof DateTime && deceased.hasValue()) {
                    deathDate = ((DateTime)deceased).getValue().toString();
                }
                String maritalStatus = null;
                if (fhirPatient.getMaritalStatus() != null) {
                    if (fhirPatient.getMaritalStatus().getCoding() != null) {
                        for (Coding coding : fhirPatient.getMaritalStatus().getCoding()) {
                            if (coding.getCode() != null) {
                                maritalStatus = coding.getCode().getValue();
                                break;
                            }
                        }
                    } else if (fhirPatient.getMaritalStatus().getText() != null) {
                        maritalStatus = fhirPatient.getMaritalStatus().getText().getValue();
                    }
                }
                String race = null; // FHIR Patient resource does not contain a race element
                String gender = fhirPatient.getGender() != null ? fhirPatient.getGender().getValue() : null;
                Patient patient = new Patient(id, birthDate, deathDate, maritalStatus, race, gender);
                patientsMap.put(patient.getId(), patient);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return patientsMap;
    }

    @Override
    public String getFileName(SyntheaMedicalTypes medicalType) {
        return Commons.FHIR_FILE_NAMES.get(getFhirMedicalType(medicalType));
    }
    
    private String getPatientId(Resource resource, FHIRResourceType.Value medicalType) throws FHIRPathException {
        return getNodeValueAsString(getElementNodes(resource, Commons.FHIR_PATIENT_ELEMENT_FHIRPATH.get(medicalType)));
    }

    private String getEventId(Resource resource, FHIRResourceType.Value medicalType) throws FHIRPathException {
        return getNodeValueAsString(getElementNodes(resource, Commons.FHIR_EVENT_ELEMENT_FHIRPATH.get(medicalType)));
    }

    private String getStartDate(Resource resource, FHIRResourceType.Value medicalType) throws FHIRPathException {
        return getNodeValueAsString(getElementNodes(resource, Commons.FHIR_START_DATE_ELEMENT_FHIRPATH.get(medicalType)));
    }

    private String getStopDate(Resource resource, FHIRResourceType.Value medicalType) throws FHIRPathException {
        return getNodeValueAsString(getElementNodes(resource, Commons.FHIR_STOP_DATE_ELEMENT_FHIRPATH.get(medicalType)));
    }

    private Collection<FHIRPathNode> getElementNodes(Resource resource, String fhirPathExpression) throws FHIRPathException {
        EvaluationContext evaluationContext = new EvaluationContext(resource);
        return fhirPathEvaluator.evaluate(evaluationContext, fhirPathExpression);
    }
    
    private String getNodeValueAsString(Collection<FHIRPathNode> nodes) {
        String nodeValue = null;
        
        for (FHIRPathNode node : nodes) {
            if (node.isSystemValue()) {
                nodeValue = node.asSystemValue().toString();
            } else {
                Element element = node.asElementNode().element();
                if (element instanceof com.ibm.fhir.model.type.String) {
                    nodeValue = element.as(com.ibm.fhir.model.type.String.class).getValue();
                }
                else if (element instanceof Code) {
                    nodeValue = element.as(Code.class).getValue();
                }
                else if (element instanceof Date) {
                    nodeValue = element.as(Date.class).getValue() != null ? DateTimeFormatter.ISO_INSTANT.format(element.as(Date.class).getValue()) : null;
                }
                else if (element instanceof DateTime) {
                    nodeValue = element.as(DateTime.class).getValue() != null ? DateTimeFormatter.ISO_INSTANT.format(element.as(DateTime.class).getValue()) : null;
                }
                else if (element instanceof Instant) {
                    nodeValue = element.as(Instant.class).getValue() != null ? DateTimeFormatter.ISO_INSTANT.format(element.as(Instant.class).getValue()) : null;
                }
                else if (element instanceof Decimal) {
                    nodeValue = element.as(Decimal.class).getValue() != null ? element.as(Decimal.class).getValue().toString() : null;
                }
                else if (element instanceof Reference) {
                    nodeValue = element.as(Reference.class).getReference() != null ? element.as(Reference.class).getReference().getValue() : null;
                }
            }
            if (nodeValue != null) {
                break;
            }
        }

        return nodeValue;
    }

    private FHIRResourceType.Value getFhirMedicalType(SyntheaMedicalTypes syntheaMedicalType) {
        for (FHIRResourceType.Value fhirMedicalType : Commons.FHIR_MEDICAL_TYPE_MAP.keySet()) {
            if (Commons.FHIR_MEDICAL_TYPE_MAP.get(fhirMedicalType).equals(syntheaMedicalType)) {
                return fhirMedicalType;
            }
        }
        return null;
    }
    
    private final class IteratorOfRecords implements Iterator<Record> {

        private final String filePath;
        private final long totalNumberOfLines;
        private CsvParser csvParser;
        private FHIRResourceType.Value fhirMedicalType;
        private BufferedReader reader;

        // state variables
        private double numberOfLinesParsed = 0;
        private double percentageThreshold = 10;
        private Record nextRecord;
        // end of state variables

        public IteratorOfRecords(String inputDataPath, SyntheaMedicalTypes medicalType) {
            super();
            this.fhirMedicalType = getFhirMedicalType(medicalType);
            this.filePath = inputDataPath + Commons.FHIR_FILE_NAMES.get(fhirMedicalType);

            try {
                this.totalNumberOfLines = FileUtils.computeNumberOfLines(filePath).orElse(-1L);
                if (this.totalNumberOfLines > 0) {
                    this.reader = new BufferedReader(new FileReader(filePath));

                    CsvParserSettings csvParserSettings = new CsvParserSettings();
                    csvParserSettings.setHeaderExtractionEnabled(true);
                    csvParser = new CsvParser(csvParserSettings);

                    // Add column headers
                    StringBuilder sb = new StringBuilder(String.join(",",
                        Commons.SYNTHEA_FEATURE_COLUMN_NAMES.get(medicalType)));
                    sb.append("," + Commons.SYNTHEA_PATIENT_COLUMN_NAME.get(medicalType));
                    if (!SyntheaMedicalTypes.PATIENTS.equals(medicalType)) {
                        sb.append("," + Commons.SYNTHEA_EVENT_COLUMN_NAME.get(medicalType));
                    }
                    if (Commons.FHIR_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS.contains(fhirMedicalType)) {
                        sb.append(",START,STOP");
                    } else {
                        sb.append(",DATE");
                    }
                    csvParser.parseRecord(sb.toString());

                    updateState();
                    
                    LOG.info("started parsing " + filePath);
                } else {
                    nextRecord = null;
                    LOG.info("finished parsing " + filePath);
                }
                
            } catch (Exception e) {
                try {
                    this.reader.close();
                } catch (IOException e1) {}
                LOG.error("unable to read file at: " + filePath);
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return nextRecord != null;
        }

        @Override
        public Record next() {
            if (hasNext()) {
                Record record = nextRecord;
                updateState();
                return record;
            }
            throw new UnsupportedOperationException();
        }

        private void updateState() {
            nextRecord = null;
            String line;
            boolean validStartDate = false;
            try {
                while ((line = reader.readLine()) != null && !validStartDate) {
                    try (StringReader resourceReader = new StringReader(line)) {
                        Resource resource = FHIRParser.parser(Format.JSON).parse(resourceReader);
                        // Parse the resource and generate a csv string. It's possible that FHIR
                        // resources do not contain start dates. If so, skip the resource and go
                        // on to the next one.
                        String startDate = getStartDate(resource, fhirMedicalType);
                        if (startDate != null && !startDate.isEmpty()) {
                            validStartDate = true;
                            List<String> columnValues = new ArrayList<>();
                            for (String featureFhirPathExpression : Commons.FHIR_FEATURE_ELEMENT_FHIRPATHS.get(fhirMedicalType)) {
                                String featureValue = getNodeValueAsString(getElementNodes(resource, featureFhirPathExpression));
                                columnValues.add(featureValue == null ? EMPTY_STRING : featureValue);
                            }
                            String patientId = getPatientId(resource, fhirMedicalType);
                            columnValues.add(patientId == null ? EMPTY_STRING : patientId);
                            if (!FHIRResourceType.Value.PATIENT.equals(fhirMedicalType)) {
                                String eventId = getEventId(resource, fhirMedicalType);
                                columnValues.add(eventId == null ? EMPTY_STRING : eventId);
                            }
                            columnValues.add(startDate == null ? EMPTY_STRING : startDate);
                            if (Commons.FHIR_MEDICAL_TYPES_YIELDING_START_STOP_PATHWAY_EVENTS.contains(fhirMedicalType)) {
                                String stopDate = getStopDate(resource, fhirMedicalType);
                                columnValues.add(stopDate == null ? EMPTY_STRING : stopDate);
                            }
                            nextRecord = csvParser.parseRecord(String.join(",", columnValues));
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("unable to parse resource");
                throw new RuntimeException(e);
            }
            if (nextRecord == null) {
                LOG.info("finished parsing " + filePath);
            } else {
                numberOfLinesParsed++;
                if (numberOfLinesParsed / totalNumberOfLines * 100 >= percentageThreshold) {
                    LOG.info("... parsed " + percentageThreshold + "%");
                    percentageThreshold += 10;
                }
            }
        }
        
    }

}
