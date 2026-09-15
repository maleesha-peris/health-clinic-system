package com.healthclinic.controller;

import com.healthclinic.data.DataManager;
import com.healthclinic.data.DataPersistenceException;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Patient;
import com.healthclinic.util.SearchAlgorithms;
import com.healthclinic.util.ValidationException;
import com.healthclinic.util.ValidationUtil;

import java.util.ArrayList;

/**
 * Controller for Patient operations.
 * Handles input validation, coordination between View and Model, and exception handling.
 */
public class PatientController {

    private final Clinic clinic;
    private final DataManager dataManager;

    public PatientController(Clinic clinic, DataManager dataManager) {
        this.clinic = clinic;
        this.dataManager = dataManager;
    }

    public void registerPatient(String id, String name, String phone, String email,
                                String dateOfBirth, String gender, String bloodGroup,
                                String emergencyContact, String medicalHistory)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(id, "Patient ID");
        ValidationUtil.requireNonBlank(name, "Full Name");
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.parseAndValidateDate(dateOfBirth, "Date of Birth");
        ValidationUtil.requireNonBlank(gender, "Gender");
        ValidationUtil.requireNonBlank(bloodGroup, "Blood Group");

        // Check ID uniqueness
        if (clinic.findPatientById(id.trim()) != null) {
            throw new ValidationException("A patient with ID '" + id.trim() + "' already exists.");
        }

        Patient patient = new Patient(
                id.trim().toUpperCase(),
                name.trim(),
                phone.trim(),
                email.trim(),
                dateOfBirth.trim(),
                gender.trim(),
                bloodGroup.trim().toUpperCase(),
                emergencyContact != null ? emergencyContact.trim() : "",
                medicalHistory != null ? medicalHistory.trim() : ""
        );

        clinic.addPatient(patient);
        dataManager.savePatients(clinic.getPatients());
    }

    public void updatePatient(String id, String name, String phone, String email,
                              String dateOfBirth, String gender, String bloodGroup,
                              String emergencyContact, String medicalHistory)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(id, "Patient ID");
        Patient existing = clinic.findPatientById(id.trim());
        if (existing == null) {
            throw new ValidationException("Patient with ID '" + id.trim() + "' not found.");
        }

        ValidationUtil.requireNonBlank(name, "Full Name");
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.parseAndValidateDate(dateOfBirth, "Date of Birth");
        ValidationUtil.requireNonBlank(gender, "Gender");
        ValidationUtil.requireNonBlank(bloodGroup, "Blood Group");

        existing.setName(name.trim());
        existing.setPhone(phone.trim());
        existing.setEmail(email.trim());
        existing.setDateOfBirth(dateOfBirth.trim());
        existing.setGender(gender.trim());
        existing.setBloodGroup(bloodGroup.trim().toUpperCase());
        existing.setEmergencyContact(emergencyContact != null ? emergencyContact.trim() : "");
        existing.setMedicalHistory(medicalHistory != null ? medicalHistory.trim() : "");

        dataManager.savePatients(clinic.getPatients());
    }

    public void deletePatient(String id) throws ValidationException, DataPersistenceException {
        ValidationUtil.requireNonBlank(id, "Patient ID");
        boolean removed = clinic.removePatientById(id.trim());
        if (!removed) {
            throw new ValidationException("Patient with ID '" + id.trim() + "' not found.");
        }
        dataManager.savePatients(clinic.getPatients());
    }

    public Patient searchPatientById(String id) {
        // Binary Search implementation as specified in Task 8
        return SearchAlgorithms.binarySearchPatientById(clinic.getPatients(), id);
    }

    public ArrayList<Patient> searchPatients(String query) {
        // Linear Search implementation as specified in Task 8
        return SearchAlgorithms.linearSearchPatients(clinic.getPatients(), query);
    }

    public String getNextPatientId() {
        return clinic.generateNextPatientId();
    }

    public ArrayList<Patient> getAllPatients() {
        return clinic.getPatients();
    }
}