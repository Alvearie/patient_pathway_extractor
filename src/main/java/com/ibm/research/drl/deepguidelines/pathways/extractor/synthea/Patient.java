package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import com.univocity.parsers.annotations.Parsed;

public class Patient {

    @Parsed
    private String id;

    @Parsed
    private String birthdate;

    private String deathdate;

    @Parsed(defaultNullRead = "U")
    private String marital;

    @Parsed
    private String race;

    @Parsed
    private String gender;

    public Patient() {}

    public Patient(String id, String birthdate, String deathdate, String marital, String race, String gender) {
        super();
        this.id = id;
        this.birthdate = birthdate;
        this.deathdate = deathdate;
        this.marital = marital;
        this.race = race;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(String deathdate) {
        this.deathdate = deathdate;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Patient other = (Patient) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Patient [id=" + id + ", birthdate=" + birthdate + ", deathdate=" + deathdate + ", marital=" + marital + ", race=" + race + ", gender="
                + gender + "]";
    };

}
