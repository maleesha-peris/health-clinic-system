package com.healthclinic.controller;

import com.healthclinic.data.DataManager;
import com.healthclinic.data.DataPersistenceException;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;
import com.healthclinic.util.SearchAlgorithms;
import com.healthclinic.util.SortAlgorithms;
import com.healthclinic.util.ValidationException;
import com.healthclinic.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Controller for Appointment booking and scheduling.
 */
public class AppointmentController {

    private final Clinic clinic;
    private final DataManager dataManager;

    public AppointmentController(Clinic clinic, DataManager dataManager) {
        this.clinic = clinic;
        this.dataManager = dataManager;
    }

    public void bookAppointment(String appointmentId, String patientId, String doctorId,
                                String dateTimeStr, String notes)
            throws ValidationException, DataPersistenceException {

        ValidationUtil.requireNonBlank(appointmentId, "Appointment ID");
        ValidationUtil.requireNonBlank(patientId, "Patient ID");
        ValidationUtil.requireNonBlank(doctorId, "Doctor ID");
        LocalDateTime dt = ValidationUtil.parseAndValidateDateTime(dateTimeStr, "Appointment Date & Time");

        if (clinic.findAppointmentById(appointmentId.trim()) != null) {
            throw new ValidationException("Appointment ID '" + appointmentId.trim() + "' already exists.");
        }
        if (clinic.findPatientById(patientId.trim()) == null) {
            throw new ValidationException("Patient with ID '" + patientId.trim() + "' does not exist.");
        }
        if (clinic.findDoctorById(doctorId.trim()) == null) {
            throw new ValidationException("Doctor with ID '" + doctorId.trim() + "' does not exist.");
        }

        Appointment appointment = new Appointment(
                appointmentId.trim().toUpperCase(),
                patientId.trim().toUpperCase(),
                doctorId.trim().toUpperCase(),
                dt,
                "SCHEDULED",
                notes != null ? notes.trim() : ""
        );

        clinic.addAppointment(appointment);
        dataManager.saveAppointments(clinic.getAppointments());
    }

    public void updateAppointmentStatus(String appointmentId, String newStatus)
            throws ValidationException, DataPersistenceException {
        ValidationUtil.requireNonBlank(appointmentId, "Appointment ID");
        ValidationUtil.requireNonBlank(newStatus, "Status");

        Appointment a = clinic.findAppointmentById(appointmentId.trim());
        if (a == null) {
            throw new ValidationException("Appointment with ID '" + appointmentId.trim() + "' not found.");
        }

        a.setStatus(newStatus.trim().toUpperCase());
        dataManager.saveAppointments(clinic.getAppointments());
    }

    public void deleteAppointment(String appointmentId)
            throws ValidationException, DataPersistenceException {
        ValidationUtil.requireNonBlank(appointmentId, "Appointment ID");
        boolean removed = clinic.removeAppointmentById(appointmentId.trim());
        if (!removed) {
            throw new ValidationException("Appointment with ID '" + appointmentId.trim() + "' not found.");
        }
        dataManager.saveAppointments(clinic.getAppointments());
    }

    public String getNextAppointmentId() {
        return clinic.generateNextAppointmentId();
    }

    public ArrayList<Appointment> getAllAppointments() {
        return clinic.getAppointments();
    }

    /**
     * Sorts appointments chronologically by date using Quick Sort (Task 8 requirement).
     */
    public ArrayList<Appointment> getAppointmentsSortedByDate() {
        ArrayList<Appointment> list = clinic.getAppointments();
        SortAlgorithms.quickSortAppointmentsByDate(list);
        return list;
    }

    public ArrayList<Appointment> searchAppointments(String query) {
        return SearchAlgorithms.linearSearchAppointments(clinic.getAppointments(), query);
    }
}