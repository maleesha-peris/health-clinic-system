package com.healthclinic.model;

/**
 * Represents a Doctor in the clinic.
 * Extends Person with specialization, license, and consultation details.
 */
public class Doctor extends Person {
    private static final long serialVersionUID = 1L;

    private String specialization;
    private String licenseNumber;
    private double consultationFee;
    private String availableDays; // e.g. "Mon, Wed, Fri"

    public Doctor() {
        super();
    }

    public Doctor(String id, String name, String phone, String email,
                  String specialization, String licenseNumber, double consultationFee,
                  String availableDays) {
        super(id, name, phone, email);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    @Override
    public String getRoleDescription() {
        return "Doctor: Dr. " + name + " (" + specialization + ", Fee: $" + consultationFee + ")";
    }
}
