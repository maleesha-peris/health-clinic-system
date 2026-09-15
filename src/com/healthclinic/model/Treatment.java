package com.healthclinic.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a Treatment record created following an Appointment or Consultation.
 */
public class Treatment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate treatmentDate;
    private String diagnosis;
    private String prescription;
    private double cost;
    private String notes;

    public Treatment() {
    }

    public Treatment(String treatmentId, String appointmentId, String patientId,
                     String doctorId, LocalDate treatmentDate, String diagnosis,
                     String prescription, double cost, String notes) {
        this.treatmentId = treatmentId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.cost = cost;
        this.notes = notes;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(LocalDate treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Treatment #" + treatmentId + " [Date: " + treatmentDate + ", Diagnosis: " + diagnosis + ", Cost: $" + cost + "]";
    }
}
