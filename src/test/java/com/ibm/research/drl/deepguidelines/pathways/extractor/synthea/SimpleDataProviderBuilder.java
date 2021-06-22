package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Service
public class SimpleDataProviderBuilder implements DataProviderBuilder {

    private final long now;
    private final Set<SyntheaMedicalTypes> includedPathwayEventMedicalTypes;

    public SimpleDataProviderBuilder(
            @Qualifier("now") long now,
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.included.medical.types}") String[] includedMedicalTypes) {
        super();
        this.now = now;
        this.includedPathwayEventMedicalTypes = new ObjectOpenHashSet<>(includedMedicalTypes.length);
        for (String includedMedicalType : includedMedicalTypes)
            includedPathwayEventMedicalTypes.add(SyntheaMedicalTypes.valueOf(includedMedicalType));
    }

    @Override
    public DataProvider build(String syntheaDataPath) {
        return new SimpleDataProvider(syntheaDataPath, now, includedPathwayEventMedicalTypes);
    }

}
