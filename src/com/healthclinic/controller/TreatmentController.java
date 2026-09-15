package com.healthclinic.controller;

import com.healthclinic.data.DataManager;
import com.healthclinic.data.DataPersistenceException;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Treatment;
import com.healthclinic.util.ValidationException;
import com.healthclinic.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller for Treatment records entry and management.
 */
public class TreatmentController {

    private final Clinic clinic;
    private final DataManager dataManager;

    public TreatmentController(Clinic clinic, DataManager dataManager) {
        this.clinic = clinic;
        this.dataManager = dataManager;
    }

    public void recordTreatment(String treatmentId, String appointmentId, String patientId,
                                String doctorId, String dateStr, String diagnosis,
                                String prescription, String costStr, String notes)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(treatmentId, "Treatment ID");
        ValidationUtil.requireNonBlank(patientId, "Patient ID");
        ValidationUtil.requireNonBlank(doctorId, "Doctor ID");
        ValidationUtil.requireNonBlank(diagnosis, "Diagnosis");
        ValidationUtil.requireNonBlank(prescription, "Prescription");
        LocalDate date = ValidationUtil.parseAndValidateDate(dateStr, "Treatment Date");
        double cost = ValidationUtil.parseAndValidatePositiveDouble(costStr, "Treatment Cost");

        if (clinic.findTreatmentById(treatmentId.trim()) != null) {
            throw new ValidationException("Treatment ID '" + treatmentId.trim() + "' already exists.");
        }
        if (clinic.findPatientById(patientId.trim()) == null) {
            throw new ValidationException("Patient with ID '" + patientId.trim() + "' does not exist.");
        }
        if (clinic.findDoctorById(doctorId.trim()) == null) {
            throw new ValidationException("Doctor with ID '" + doctorId.trim() + "' does not exist.");
        }

        // If appointment provided, verify it and mark COMPLETED
        if (appointmentId != null && !appointmentId.trim().isEmpty()) {
            Appointment a = clinic.findAppointmentById(appointmentId.trim());
            if (a != null) {
                a.setStatus("COMPLETED");
                dataManager.saveAppointments(clinic.getAppointments());
            }
        }

        Treatment treatment = new Treatment(
                treatmentId.trim().toUpperCase(),
                appointmentId != null ? appointmentId.trim().toUpperCase() : "",
                patientId.trim().toUpperCase(),
                doctorId.trim().toUpperCase(),
                date,
                diagnosis.trim(),
                prescription.trim(),
                cost,
                notes != null ? notes.trim() : ""
        );

        clinic.addTreatment(treatment);
        dataManager.saveTreatments(clinic.getTreatments());
    }

    public void deleteTreatment(String treatmentId) throws ValidationException, DataPersistenceException {
        ValidationUtil.requireNonBlank(treatmentId, "Treatment ID");
        boolean removed = clinic.removeTreatmentById(treatmentId.trim());
        if (!removed) {
            throw new ValidationException("Treatment with ID '" + treatmentId.trim() + "' not found.");
        }
        dataManager.saveTreatments(clinic.getTreatments());
    }

    public String getNextTreatmentId() {
        return clinic.generateNextTreatmentId();
    }

    public ArrayList<Treatment> getAllTreatments() {
        return clinic.getTreatments();
    }

    public ArrayList<Treatment> getTreatmentsByPatientId(String patientId) {
        ArrayList<Treatment> results = new ArrayList<>();
        if (patientId == null || patientId.trim().isEmpty()) return results;
        for (Treatment t : clinic.getTreatments()) {
            if (t.getPatientId().equalsIgnoreCase(patientId.trim())) {
                results.add(t);
            }
        }
        return results;
    }
}