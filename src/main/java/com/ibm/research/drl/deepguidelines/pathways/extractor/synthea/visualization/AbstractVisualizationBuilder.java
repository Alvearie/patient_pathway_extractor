package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.visualization;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public abstract class AbstractVisualizationBuilder {

    protected final Optional<Set<String>> patientIdsForWhichWeWantVisualizations;

    public AbstractVisualizationBuilder(String[] patientIdsForWhichWeWantVisualizations) {
        this.patientIdsForWhichWeWantVisualizations = getPatientIdsForWhichWeWantVisualizations(patientIdsForWhichWeWantVisualizations);
    }

    private Optional<Set<String>> getPatientIdsForWhichWeWantVisualizations(String[] patientIdsForWhichWeWantVisualizations) {
        if (patientIdsForWhichWeWantVisualizations == null || patientIdsForWhichWeWantVisualizations.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new ObjectOpenHashSet<>(Arrays.asList(patientIdsForWhichWeWantVisualizations)));
        }
    }

}
