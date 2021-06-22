package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Test;

public class IntervalTest {

    @Test
    public void test_intersect_1() {
        Interval eventInterval = new Interval(
                Instant.parse("2010-03-31T07:50:07Z").toEpochMilli(),
                Instant.parse("2010-03-31T08:05:07Z").toEpochMilli());
        Interval pathwayInterval = new Interval(
                Instant.parse("2009-07-27" + Commons.INSTANT_START_OF_DAY).toEpochMilli(),
                Instant.parse("2010-03-31" + Commons.INSTANT_END_OF_DAY).toEpochMilli());
        assertThat(pathwayInterval.intersects(eventInterval), is(true));
    }
    
    @Test
    public void test_intersect_2() {
        Interval eventInterval = new Interval(
                Instant.parse("2011-08-17T07:50:07Z").toEpochMilli(),
                Instant.parse("2011-08-17T08:35:07Z").toEpochMilli());
        Interval pathwayInterval = new Interval(
                Instant.parse("1981-11-11" + Commons.INSTANT_START_OF_DAY).toEpochMilli(),
                Instant.parse("2018-11-01T17:41:41.460Z").toEpochMilli());
        assertThat(pathwayInterval.intersects(eventInterval), is(true));
    }

}
