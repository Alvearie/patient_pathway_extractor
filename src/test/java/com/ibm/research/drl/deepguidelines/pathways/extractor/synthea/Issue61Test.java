package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.DataProvider;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.dataprovider.InMemoryDataProviderBuilder;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.parser.SyntheaCsvInputDataParser;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.AliveOutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.ConditionsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.DemographicsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.MedicationsPathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.OutcomePathwayMatrixCell;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrix;
import com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix.PathwayMatrixBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application_issue61.properties")
public class Issue61Test {

    @Autowired
    private InMemoryDataProviderBuilder inMemoryDataProviderBuilder;
    
    @Value("${com.ibm.research.drl.deepguidelines.pathways.extractor.input.data.path}")
    private String syntheaDataPath;
    
    @Test
    public void test() {
        DataProvider dataProvider = inMemoryDataProviderBuilder.build(syntheaDataPath, new SyntheaCsvInputDataParser(Instant.now().toEpochMilli()));
        List<Pathway> actualPathways = dataProvider.getPathways().collect(Collectors.toList());
        assertThat(actualPathways, notNullValue());
        assertThat(actualPathways.size(), equalTo(1));
        PathwayMatrixBuilder pathwayMatrixBuilder = new PathwayMatrixBuilder(dataProvider);
        PathwayMatrix actualPathwayMatrix = pathwayMatrixBuilder.build(actualPathways.get(0));
        assertThat(actualPathwayMatrix, notNullValue());
        PathwayMatrix expectedPathwayMatrix = getExpectedPathwayMatrix();
        assertThat(actualPathwayMatrix, equalTo(expectedPathwayMatrix));
    }
    
    private static PathwayMatrix getExpectedPathwayMatrix() {
        // pathway:
        //        ┌───────────────────┬───────────────────┬───────────────────┬──────────────────┐
        //        │medical type       │temporal type      │event id           │date              │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │CONDITION          │START              │Condition_1        │1981-11-11T00:00:0│ slice 0
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │START              │Medication_1       │1981-11-12T00:00:0│ slice 1
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │STOP               │Medication_1       │1981-11-13T23:59:5│ slice 2
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │START              │Medication_2       │1981-12-12T00:00:0│ slice 3
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │STOP               │Medication_2       │1981-12-13T23:59:5│ slice 4
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │START              │Medication_3       │1982-01-12T00:00:0│ slice 5
        //        │                   │                   │                   │0Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │MEDICATION         │STOP               │Medication_3       │1982-02-13T23:59:5│ slice 6
        //        │                   │                   │                   │9Z                │
        //        ├───────────────────┼───────────────────┼───────────────────┼──────────────────┤
        //        │CONDITION          │STOP               │Condition_1        │1991-11-11T23:59:5│ slice 7
        //        │                   │                   │                   │9Z                │
        //        └───────────────────┴───────────────────┴───────────────────┴──────────────────┘
        PathwayMatrix pathwayMatrix = new PathwayMatrix(8, 6, 1);
        // demographics
        DemographicsPathwayMatrixCell demographics = new DemographicsPathwayMatrixCell("1965-12-08", "M", "white", "F");
        demographics.setAgeBucket("DemF");
        pathwayMatrix.setQuick(0, 0, 0, demographics);
        // slice 0
        pathwayMatrix.setQuick(0, 2, 0, new ConditionsPathwayMatrixCell("162864005", PathwayEventTemporalType.START)); // CONDITION, Condition_1, START
        // slice 1
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell130 = new MedicationsPathwayMatrixCell("389221", 2, PathwayEventTemporalType.START);
        medicationsPathwayMatrixCell130.setDispensesBucket("MedB");
        pathwayMatrix.setQuick(1, 3, 0, medicationsPathwayMatrixCell130); // MEDICATION, Medication_1, START
        // slice 2
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell230 = new MedicationsPathwayMatrixCell("389221", 2, PathwayEventTemporalType.STOP);
        medicationsPathwayMatrixCell230.setDispensesBucket("MedB");
        pathwayMatrix.setQuick(2, 3, 0, medicationsPathwayMatrixCell230); // MEDICATION, Medication_1, STOP        
        // slice 3
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell330 = new MedicationsPathwayMatrixCell("389221", 12, PathwayEventTemporalType.START);
        medicationsPathwayMatrixCell330.setDispensesBucket("MedC");
        pathwayMatrix.setQuick(3, 3, 0, medicationsPathwayMatrixCell330); // MEDICATION, Medication_2, START
        // slice 4
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell430 = new MedicationsPathwayMatrixCell("389221", 12, PathwayEventTemporalType.STOP);
        medicationsPathwayMatrixCell430.setDispensesBucket("MedC");
        pathwayMatrix.setQuick(4, 3, 0, medicationsPathwayMatrixCell430); // MEDICATION, Medication_2, STOP        
        // slice 5
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell530 = new MedicationsPathwayMatrixCell("389221", 999999999, PathwayEventTemporalType.START);
        pathwayMatrix.setQuick(5, 3, 0, medicationsPathwayMatrixCell530); // MEDICATION, Medication_3, START
        // slice 6
        MedicationsPathwayMatrixCell medicationsPathwayMatrixCell630 = new MedicationsPathwayMatrixCell("389221", 999999999, PathwayEventTemporalType.STOP);
        pathwayMatrix.setQuick(6, 3, 0, medicationsPathwayMatrixCell630); // MEDICATION, Medication_3, STOP        
        // slice 7
        pathwayMatrix.setQuick(7, 2, 0, new ConditionsPathwayMatrixCell("162864005", PathwayEventTemporalType.STOP)); // CONDITION, Condition_1, STOP
        // outcomes
        OutcomePathwayMatrixCell outcomePathwayMatrixCell = new AliveOutcomePathwayMatrixCell(3652);
        outcomePathwayMatrixCell.setOutcomeBucket("AliveG");
        pathwayMatrix.setQuick(7, 5, 0, outcomePathwayMatrixCell);
        // done
        return pathwayMatrix;
    }
    
}
