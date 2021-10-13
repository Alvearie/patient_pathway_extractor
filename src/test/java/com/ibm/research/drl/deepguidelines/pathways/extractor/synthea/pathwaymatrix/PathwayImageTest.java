package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.ibm.research.drl.deepguidelines.pathways.extractor.testutils.ExpectedDataForSynthea1PatientSeed3;

public class PathwayImageTest {

    @Test
    public void testAsCSVLne() {
        int maxColumns = 10;
        PathwayImage pathwayImage = ExpectedDataForSynthea1PatientSeed3.getPathwayImageForCondition3WithCollapsedSlices();
        String expectedCSVLine = getExpectedPatwayImageAsCSVLine(maxColumns);
        assertThat(countCommas(expectedCSVLine), equalTo((pathwayImage.rows() * maxColumns) - 1));
        String actualCSVLine = pathwayImage.asCSVLine(10);
        assertThat(actualCSVLine, notNullValue());
        assertThat(countCommas(actualCSVLine), equalTo((pathwayImage.rows() * maxColumns) - 1));
        assertThat(actualCSVLine, equalTo(expectedCSVLine));
    }

    private String getExpectedPatwayImageAsCSVLine(int maxColumn) {
        String[][] rows = new String[][] {
                new String[] { "DemF_M_white_F", "", "", "", "" },
                new String[] { "", "", "", "",
                        "8302-2_UNKNOWNBUCKET;72514-3_UNKNOWNBUCKET;29463-7_UNKNOWNBUCKET;39156-5_High;8462-4_Normal;8480-6_Normal;2093-3_Normal;2571-8_Normal;18262-6_Optimal;2085-9_High;72166-2_Never" },
                new String[] { "A_65363002", "", "", "", "O_65363002" },
                new String[] { "A_849574_MedA", "O_849574_MedA", "A_1000126_MedB", "", "",  },
                new String[] { "", "", "76601001", "76601001", "" },
                new String[] { "", "", "", "", "AliveE" }
        };
        String[] lines = new String[rows.length];
        for (int r = 0; r < rows.length; r++) {
            String[] fragment = new String[maxColumn];
            Arrays.fill(fragment, "");
            String[] row = rows[r];
            for (int c = 0; c < row.length; c++)
                fragment[c] = row[c];
            lines[r] = String.join(",", fragment);
        }
        return String.join(",", lines) + System.lineSeparator();
    }

    private int countCommas(String s) {
        int count = 0;
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == ',') count++;
        }
        return count;
    }

}
