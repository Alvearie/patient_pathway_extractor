package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Pathway;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEvent;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.PathwayEventsLine;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.Patient;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.SyntheaMedicalTypes;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.InputDataParser;
import com.univocity.parsers.common.record.Record;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * This {@link Function} transforms a {@link Record} into an optional {@link Pathway}.
 * 
 * The returned {@code Optional<Pathway>} is empty if:
 * - the input {@code Record} contains inconsistent dates (stop date < start date)
 * - if we have a set of included condition codes, and the code of the condition form the input {@code Record} is not in the set
 * - if we have already built a {@code Pathway} with the same id - note: this means that this {@link Function} is stateful:
 *   we want to avoid building a {@code Pathway} twice, because building its {@code PathwayEventsLine} is a potentially costly 
 *   operation 
 */
public class FunctionFromRecordToOptionalPathway implements Function<Record, Optional<Pathway>> {
    
    private static final Logger LOG = LoggerFactory.getLogger(FunctionFromRecordToOptionalPathway.class);

    private final DataProvider dataProvider;
    private final Optional<Set<String>> includedConditionsCodes;
    private final InputDataParser inputDataParser;
    private final Set<String> alreadyBuiltPathwayIds = new ObjectOpenHashSet<>();

    public FunctionFromRecordToOptionalPathway(DataProvider dataProvider, InputDataParser inputDataParser,
            Optional<Set<String>> includedConditionsCodes) {
        super();
        this.dataProvider = dataProvider;
        this.includedConditionsCodes = includedConditionsCodes;
        this.inputDataParser = inputDataParser;
    }

    @Override
    public Optional<Pathway> apply(Record record) {
        Optional<Pathway> result = Optional.empty();
        PathwayEvent startPathwayEvent = inputDataParser.getStartPathwayEvent(record, SyntheaMedicalTypes.CONDITIONS);
        PathwayEvent stopPathwayEvent = inputDataParser.getStopPathwayEvent(record, SyntheaMedicalTypes.CONDITIONS);
        long pathwayStartDate = startPathwayEvent.getDate();
        long pathwayStopDate = stopPathwayEvent.getDate();
        String originatingConditionCode = record.getString("CODE"); // code of the condition that originates this pathway 
        if (pathwayStopDate < pathwayStartDate) {
            LOG.error("bad data: stop < start: " + record);
        } else if (includedConditionsCodes.isPresent() && !includedConditionsCodes.get().contains(originatingConditionCode)) {
            // we have a set of included conditions codes, and the current condition code is not in that set, so we do nothing
            ;
        } else {
            // either we haven't a set of included conditions codes, or we have one and the current condition code is in that set, so we produce a pathway
            String patientId = inputDataParser.getPatientId(record, SyntheaMedicalTypes.CONDITIONS);
            String pathwayId = Pathway.buildId(patientId, startPathwayEvent, stopPathwayEvent);
            if (alreadyBuiltPathwayIds.contains(pathwayId)) {
                if (LOG.isDebugEnabled()) LOG.debug("already built a pathway with id " + pathwayId);
            } else {
                alreadyBuiltPathwayIds.add(pathwayId);
                PathwayEventsLine pathwayEventsLine = dataProvider.getPathwayEventsLine(startPathwayEvent, stopPathwayEvent, patientId);
                Patient patient = dataProvider.getPatient(patientId);
                result = Optional.of(new Pathway(pathwayId, patient, originatingConditionCode, startPathwayEvent, stopPathwayEvent, pathwayEventsLine));
            }
        }
        return result;
    }

}
