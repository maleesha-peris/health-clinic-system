package com.healthclinic.model;

/**
 * Represents a Patient in the clinic.
 * Extends Person and encapsulates health-specific information.
 */
public class Patient extends Person {
    private static final long serialVersionUID = 1L;

    private String dateOfBirth; // YYYY-MM-DD
    private String gender;      // Male, Female, Other
    private String bloodGroup;  // e.g. A+, O-, B+
    private String emergencyContact;
    private String medicalHistory;

    public Patient() {
        super();
    }

    public Patient(String id, String name, String phone, String email,
                   String dateOfBirth, String gender, String bloodGroup,
                   String emergencyContact, String medicalHistory) {
        super(id, name, phone, email);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.medicalHistory = medicalHistory;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String getRoleDescription() {
        return "Patient: " + name + " [Blood: " + bloodGroup + ", Phone: " + phone + "]";
    }
}
