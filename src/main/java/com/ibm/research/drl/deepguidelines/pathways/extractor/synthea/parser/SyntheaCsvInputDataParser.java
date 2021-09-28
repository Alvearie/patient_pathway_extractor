package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Commons;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class SyntheaCsvInputDataParser extends AbstractInputDataParser {

    private static final Logger LOG = LoggerFactory.getLogger(SyntheaCsvInputDataParser.class);

    public SyntheaCsvInputDataParser(long now) {
        super(now);
    }

    @Override
    public Stream<Record> readAsStreamOfRecords(String inputDataPath, SyntheaMedicalTypes medicalType) {
        IteratorOfRecords iteratorOfRecords = new IteratorOfRecords(inputDataPath + Commons.SYNTHEA_FILE_NAMES.get(medicalType));
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iteratorOfRecords,
                        iteratorOfRecords.totalNumberOfLines,
                        Spliterator.ORDERED),
                false);
    }

    @Override
    public Map<String, Patient> getPatients(String inputDataPath) {
        final BeanListProcessor<Patient> beanListProcessor = new BeanListProcessor<>(Patient.class);
        CsvParserSettings csvParserSettings = new CsvParserSettings();
        csvParserSettings.setProcessor(beanListProcessor);
        csvParserSettings.setHeaderExtractionEnabled(true);
        CsvParser csvParser = new CsvParser(csvParserSettings);
        csvParser.parse(FileUtils.getFile(inputDataPath + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.PATIENTS)));
        Map<String, Patient> patientsMap = new Object2ObjectOpenHashMap<>();
        for (Patient patient : beanListProcessor.getBeans()) {
            patientsMap.put(patient.getId(), patient);
        }
        return patientsMap;
    }

    @Override
    public String getFileName(SyntheaMedicalTypes medicalType) {
        return Commons.SYNTHEA_FILE_NAMES.get(medicalType);
    }
    
    private static final class IteratorOfRecords implements Iterator<Record> {

        private final String filePath;
        private final long totalNumberOfLines;
        private CsvParser csvParser;

        // state variables
        private double numberOfLinesParsed = 0;
        private double percentageThreshold = 10;
        private Record nextRecord;
        private boolean finished = false;
        // end of state variables

        public IteratorOfRecords(String filePath) {
            super();
            this.filePath = filePath;

            totalNumberOfLines = FileUtils.computeNumberOfLines(filePath).orElse(-1L);

            if (totalNumberOfLines > 0) {
                CsvParserSettings csvParserSettings = new CsvParserSettings();
                csvParserSettings.setHeaderExtractionEnabled(true);
                csvParser = new CsvParser(csvParserSettings);
                csvParser.beginParsing(FileUtils.getFile(filePath));

                LOG.info("started parsing " + filePath);

                updateState();
            } else {
                finished = true;
                LOG.info("finished parsing " + filePath);
            }
        }

        @Override
        public boolean hasNext() {
            return !finished;
        }

        @Override
        public Record next() {
            if (hasNext()) {
                Record record = nextRecord;
                updateState();
                return record;
            } else
                throw new UnsupportedOperationException();
        }

        private void updateState() {
            nextRecord = csvParser.parseNextRecord();
            if (nextRecord == null) {
                finished = true;
                csvParser.stopParsing();
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
