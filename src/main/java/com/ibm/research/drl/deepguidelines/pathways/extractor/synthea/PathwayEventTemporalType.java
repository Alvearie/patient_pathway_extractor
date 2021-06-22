package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

public enum PathwayEventTemporalType {

    // @formatter:off
    
    /* 
     *                                event B
     *                                |  
     *         |--- event A ---|      v
     * -------------------------------------------------------> time
     *         ^               ^      ^ 
     *         |               |      |
     *         START           STOP   ISOLATED
     */
    
    START {    // marks the starting of an event that spans an interval of time 
        @Override
        public String abbreviation() {
            return START_ABBREVIATION;
        }
    },
    STOP {    // marks the stopping of an event that spans an interval of time
        @Override
        public String abbreviation() {
            return STOP_ABBREVIATION;
        }
    },    
    ISOLATED { // marks an event happening at a given point in time (no interval)
        @Override
        public String abbreviation() {
            return ISOLATED_ABBREVIATION;
        }
    }; 
    
    public abstract String abbreviation();
    
    private static final String START_ABBREVIATION = "A";
    private static final String STOP_ABBREVIATION = "O";
    private static final String ISOLATED_ABBREVIATION = "I";
    
    // @formatter:on

}
