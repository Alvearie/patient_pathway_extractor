package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.AbstractDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Service
public class SimpleDataProviderBuilder extends AbstractDataProviderBuilder {

    private final Set<SyntheaMedicalTypes> includedPathwayEventMedicalTypes;
    private final Optional<Set<String>> includedConditionsCodes;

    public SimpleDataProviderBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.included.medical.types}") String[] includedMedicalTypes,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.included.conditions.codes}") String[] includedConditionsCodes) {
        super();
        this.includedPathwayEventMedicalTypes = new ObjectOpenHashSet<>(includedMedicalTypes.length);
        for (String includedMedicalType : includedMedicalTypes) {
            includedPathwayEventMedicalTypes.add(SyntheaMedicalTypes.valueOf(includedMedicalType));
        }
        this.includedConditionsCodes = getIncludedConditionsCodes(includedConditionsCodes);
    }

    public DataProvider build(String syntheaDataPath, InputDataParser inputDataParser) {
        return new SimpleDataProvider(syntheaDataPath, includedPathwayEventMedicalTypes, includedConditionsCodes, inputDataParser);
    }

    private Optional<Set<String>> getIncludedConditionsCodes(String[] includedConditionsCodes) {
        if (includedConditionsCodes == null || includedConditionsCodes.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new ObjectOpenHashSet<>(Arrays.asList(includedConditionsCodes)));
        }
    }

}
