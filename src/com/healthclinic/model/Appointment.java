package com.healthclinic.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an Appointment scheduled between a Patient and a Doctor.
 */
public class Appointment implements Serializable, Comparable<Appointment> {
    private static final long serialVersionUID = 1L;

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDateTime;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String notes;

    public Appointment() {
    }

    public Appointment(String appointmentId, String patientId, String doctorId,
                       LocalDateTime appointmentDateTime, String status, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.notes = notes;
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

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int compareTo(Appointment other) {
        if (this.appointmentDateTime == null || other.appointmentDateTime == null) {
            return 0;
        }
        return this.appointmentDateTime.compareTo(other.appointmentDateTime);
    }

    @Override
    public String toString() {
        return "Appointment #" + appointmentId + " [" + appointmentDateTime + "] Patient: " + patientId + ", Doctor: " + doctorId + " (" + status + ")";
    }
}
