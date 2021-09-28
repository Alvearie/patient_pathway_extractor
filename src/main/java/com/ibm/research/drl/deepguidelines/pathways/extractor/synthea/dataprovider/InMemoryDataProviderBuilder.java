package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Service
public class InMemoryDataProviderBuilder extends AbstractDataProviderBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryDataProviderBuilder.class);

    private final Set<SyntheaMedicalTypes> includedPathwayEventMedicalTypes;
    private final Optional<Set<String>> includedConditionsCodes;

    @Autowired
    public InMemoryDataProviderBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.included.medical.types}") String[] includedMedicalTypes,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.included.conditions.codes}") String[] includedConditionsCodes) {
        this.includedPathwayEventMedicalTypes = new ObjectOpenHashSet<>(includedMedicalTypes.length);
        for (String includedMedicalType : includedMedicalTypes) {
            includedPathwayEventMedicalTypes.add(SyntheaMedicalTypes.valueOf(includedMedicalType));
        }
        this.includedConditionsCodes = getIncludedConditionsCodes(includedConditionsCodes);
    }

    public DataProvider build(String inputDataPath, InputDataParser inputDataParser) {
        return new InMemoryDataProvider(inputDataPath, includedPathwayEventMedicalTypes, includedConditionsCodes, inputDataParser);
    }

    private Optional<Set<String>> getIncludedConditionsCodes(String[] includedConditionsCodes) {
        if (includedConditionsCodes == null || includedConditionsCodes.length == 0) {
            LOG.info("included conditions codes: empty");
            return Optional.empty();
        } else {
            LOG.info("included conditions codes: " + Arrays.toString(includedConditionsCodes));
            return Optional.of(new ObjectOpenHashSet<>(Arrays.asList(includedConditionsCodes)));
        }
    }

}
