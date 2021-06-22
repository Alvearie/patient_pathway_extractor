package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.utils.FileUtils;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class FileParsingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileParsingUtils.class);

    public static Stream<Record> readAsStreamOfRecords(String filePath) {
        IteratorOfRecords iteratorOfRecords = new IteratorOfRecords(filePath);
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iteratorOfRecords,
                        iteratorOfRecords.totalNumberOfLines,
                        Spliterator.ORDERED),
                false);
    }

    public static Iterator<Record> getAsIteratorOfRecords(String filePath) {
        return new IteratorOfRecords(filePath);
    }

    private static final class IteratorOfRecords implements Iterator<Record> {

        private final String filePath;
        private final long totalNumberOfLines;
        private final CsvParser csvParser;

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

            CsvParserSettings csvParserSettings = new CsvParserSettings();
            csvParserSettings.setHeaderExtractionEnabled(true);
            csvParser = new CsvParser(csvParserSettings);
            csvParser.beginParsing(FileUtils.getFile(filePath));

            LOG.info("started parsing " + filePath);

            updateState();
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
