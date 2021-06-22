package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea.pathwaymatrix;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DemographicsPathwayMatrixCell implements PathwayMatrixCell {

    private static final DateTimeFormatter YYYY_MM_DD_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String birthdate;
    private final String marital;
    private final String race;
    private final String gender;
    private final long age;

    private String ageBucket = UNKNOWN_BUCKET;

    public DemographicsPathwayMatrixCell(String birthdate, String marital, String race, String gender) {
        this.birthdate = birthdate;
        this.marital = marital;
        this.race = race;
        this.gender = gender;
        this.age = ChronoUnit.YEARS.between(
                LocalDate.parse(birthdate, YYYY_MM_DD_DATE_TIME_FORMATTER),
                LocalDate.now());
    }

    public String getAgeBucket() {
        return ageBucket;
    }

    public void setAgeBucket(String ageBucket) {
        this.ageBucket = ageBucket;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getMarital() {
        return marital;
    }

    public String getRace() {
        return race;
    }

    public String getGender() {
        return gender;
    }

    public long getAge() {
        return age;
    }

    @Override
    public String asStringValue() {
        return String.join(DELIMITER, ageBucket, marital, race, gender);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (age ^ (age >>> 32));
        result = prime * result + ((ageBucket == null) ? 0 : ageBucket.hashCode());
        result = prime * result + ((birthdate == null) ? 0 : birthdate.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((marital == null) ? 0 : marital.hashCode());
        result = prime * result + ((race == null) ? 0 : race.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DemographicsPathwayMatrixCell other = (DemographicsPathwayMatrixCell) obj;
        if (age != other.age) return false;
        if (ageBucket == null) {
            if (other.ageBucket != null) return false;
        } else if (!ageBucket.equals(other.ageBucket)) return false;
        if (birthdate == null) {
            if (other.birthdate != null) return false;
        } else if (!birthdate.equals(other.birthdate)) return false;
        if (gender == null) {
            if (other.gender != null) return false;
        } else if (!gender.equals(other.gender)) return false;
        if (marital == null) {
            if (other.marital != null) return false;
        } else if (!marital.equals(other.marital)) return false;
        if (race == null) {
            if (other.race != null) return false;
        } else if (!race.equals(other.race)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "DemographicsPathwayMatrixCell [birthdate=" + birthdate + ", marital=" + marital + ", race=" + race + ", gender=" + gender + ", age="
                + age + ", ageBucket=" + ageBucket + "]";
    }
    
    public static void main(String[] args) {
        System.out.println(Long.MAX_VALUE);
    }

}
