package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SyntheaConfiguration {

    private final long now = Instant.parse(LocalDate.now() + Commons.INSTANT_END_OF_DAY).toEpochMilli();
    
    @Bean
    public long now() {
        return now;
    }
    
}
