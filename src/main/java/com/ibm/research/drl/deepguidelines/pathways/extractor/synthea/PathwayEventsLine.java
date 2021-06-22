package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;

public class PathwayEventsLine extends ObjectRBTreeSet<PathwayEvent> implements SerializableAsHTMLTable {

    private static final long serialVersionUID = 2194631567740869905L;
    
    private static final List<String> TABLE_HEADERS = Arrays.asList("MEDICAL TYPE", "TEMPORAL TYPE", "EVENT ID", "DATE");


    @Override
    public int rows() {
        return this.size();
    }

    @Override
    public int columns() {
        return 4;
    }

    @Override
    public Optional<List<String>> getTableHeaders() {
        return Optional.of(TABLE_HEADERS);
    }
    
    @Override
    public List<List<String>> getTableValues() {
        List<List<String>> tableValues = new ObjectArrayList<>();
        for (PathwayEvent pathwayEvent : this) {
            List<String> rowValues = new ObjectArrayList<>(4);
            tableValues.add(rowValues);
            rowValues.add(pathwayEvent.getMedicalType().toString());
            rowValues.add(pathwayEvent.getTemporalType().toString());
            rowValues.add(pathwayEvent.getEventId());
            rowValues.add(Instant.ofEpochMilli(pathwayEvent.getDate()).toString());
        }
        return tableValues;
    }

}
