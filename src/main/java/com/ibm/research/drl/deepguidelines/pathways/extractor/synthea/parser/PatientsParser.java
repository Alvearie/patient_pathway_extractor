package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.util.Map;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class PatientsParser {

    private final BeanListProcessor<Patient> beanListProcessor = new BeanListProcessor<>(Patient.class);
    private final String syntheaDataPath;

    public PatientsParser(String syntheaDataPath) {
        super();
        this.syntheaDataPath = syntheaDataPath;
    }

    public Map<String, Patient> parse() {
        CsvParserSettings csvParserSettings = new CsvParserSettings();
        csvParserSettings.setProcessor(beanListProcessor);
        csvParserSettings.setHeaderExtractionEnabled(true);
        CsvParser csvParser = new CsvParser(csvParserSettings);
        csvParser.parse(FileUtils.getFile(syntheaDataPath + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS)));
        Map<String, Patient> patientsMap = new Object2ObjectOpenHashMap<>();
        for (Patient patient : beanListProcessor.getBeans()) {
            patientsMap.put(patient.getId(), patient);
        }
        return patientsMap;
    }

}
