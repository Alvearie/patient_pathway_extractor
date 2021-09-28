package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import org.springframework.stereotype.Service;

@Service
public class AbstractDataProviderBuilder implements DataProviderBuilder {

    @Override
    public DataProvider build(String inputDataPath) {
        return new AbstractDataProvider(inputDataPath);
    }
    
}
