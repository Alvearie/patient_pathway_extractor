package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Service
public class PathwaysBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PathwaysBuilder.class);

    private final long now;

    private final Optional<Set<String>> includedConditionsCodes;

    @Autowired
    public PathwaysBuilder(
            @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.included.conditions.codes}") String[] includedConditionsCodes,
            @Qualifier("now") long now) {
        super();
        this.now = now;
        this.includedConditionsCodes = getIncludedConditionsCodes(includedConditionsCodes);
    }

    public Stream<Pathway> build(DataProvider dataProvider) {
        String conditionsFileName = dataProvider.getDataPathName() + Commons.SYNTHEA_FILE_NAMES.get(SyntheaMedicalTypes.CONDITIONS);
        FunctionFromRecordToOptionalPathway functionFromRecordToOptionalPathway = new FunctionFromRecordToOptionalPathway(dataProvider,
                includedConditionsCodes, now);
        return FileParsingUtils.readAsStreamOfRecords(conditionsFileName)
                .map(functionFromRecordToOptionalPathway)
                .filter(Optional::isPresent)
                .map(Optional::get);
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
