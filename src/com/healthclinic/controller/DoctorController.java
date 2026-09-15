package com.healthclinic.controller;

import com.healthclinic.data.DataManager;
import com.healthclinic.data.DataPersistenceException;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Doctor;
import com.healthclinic.util.ValidationException;
import com.healthclinic.util.ValidationUtil;

import java.util.ArrayList;

/**
 * Controller for Doctor registration and management.
 */
public class DoctorController {

    private final Clinic clinic;
    private final DataManager dataManager;

    public DoctorController(Clinic clinic, DataManager dataManager) {
        this.clinic = clinic;
        this.dataManager = dataManager;
    }

    public void registerDoctor(String id, String name, String phone, String email,
                               String specialization, String licenseNumber, String consultationFeeStr,
                               String availableDays)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(id, "Doctor ID");
        ValidationUtil.requireNonBlank(name, "Doctor Name");
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.requireNonBlank(specialization, "Specialization");
        ValidationUtil.requireNonBlank(licenseNumber, "Medical License Number");
        double fee = ValidationUtil.parseAndValidatePositiveDouble(consultationFeeStr, "Consultation Fee");
        ValidationUtil.requireNonBlank(availableDays, "Available Days");

        if (clinic.findDoctorById(id.trim()) != null) {
            throw new ValidationException("A doctor with ID '" + id.trim() + "' already exists.");
        }

        Doctor doctor = new Doctor(
                id.trim().toUpperCase(),
                name.trim(),
                phone.trim(),
                email.trim(),
                specialization.trim(),
                licenseNumber.trim(),
                fee,
                availableDays.trim()
        );

        clinic.addDoctor(doctor);
        dataManager.saveDoctors(clinic.getDoctors());
    }

    public void updateDoctor(String id, String name, String phone, String email,
                             String specialization, String licenseNumber, String consultationFeeStr,
                             String availableDays)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(id, "Doctor ID");
        Doctor existing = clinic.findDoctorById(id.trim());
        if (existing == null) {
            throw new ValidationException("Doctor with ID '" + id.trim() + "' not found.");
        }

        ValidationUtil.requireNonBlank(name, "Doctor Name");
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.requireNonBlank(specialization, "Specialization");
        ValidationUtil.requireNonBlank(licenseNumber, "Medical License Number");
        double fee = ValidationUtil.parseAndValidatePositiveDouble(consultationFeeStr, "Consultation Fee");
        ValidationUtil.requireNonBlank(availableDays, "Available Days");

        existing.setName(name.trim());
        existing.setPhone(phone.trim());
        existing.setEmail(email.trim());
        existing.setSpecialization(specialization.trim());
        existing.setLicenseNumber(licenseNumber.trim());
        existing.setConsultationFee(fee);
        existing.setAvailableDays(availableDays.trim());

        dataManager.saveDoctors(clinic.getDoctors());
    }

    public void deleteDoctor(String id) throws ValidationException, DataPersistenceException {
        ValidationUtil.requireNonBlank(id, "Doctor ID");
        boolean removed = clinic.removeDoctorById(id.trim());
        if (!removed) {
            throw new ValidationException("Doctor with ID '" + id.trim() + "' not found.");
        }
        dataManager.saveDoctors(clinic.getDoctors());
    }

    public Doctor findDoctorById(String id) {
        return clinic.findDoctorById(id);
    }

    public String getNextDoctorId() {
        return clinic.generateNextDoctorId();
    }

    public ArrayList<Doctor> getAllDoctors() {
        return clinic.getDoctors();
    }
}