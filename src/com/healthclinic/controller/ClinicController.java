package com.healthclinic.controller;

import com.healthclinic.data.DataManager;
import com.healthclinic.data.DataPersistenceException;
import com.healthclinic.model.Clinic;

/**
 * Central Controller coordinating sub-controllers, data persistence, and application flow.
 */
public class ClinicController {

    private final Clinic clinic;
    private final DataManager dataManager;

    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final TreatmentController treatmentController;
    private final ReportController reportController;

    public ClinicController() throws DataPersistenceException {
        this.clinic = new Clinic();
        this.dataManager = new DataManager();

        // Load all data from CSV files (or auto-seed sample data on first launch)
        this.dataManager.loadAll(this.clinic);

        // Initialize sub-controllers
        this.patientController = new PatientController(clinic, dataManager);
        this.doctorController = new DoctorController(clinic, dataManager);
        this.appointmentController = new AppointmentController(clinic, dataManager);
        this.treatmentController = new TreatmentController(clinic, dataManager);
        this.reportController = new ReportController(clinic);
    }

    public Clinic getClinic() {
        return clinic;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public DoctorController getDoctorController() {
        return doctorController;
    }

    public AppointmentController getAppointmentController() {
        return appointmentController;
    }

    public TreatmentController getTreatmentController() {
        return treatmentController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public void saveAllData() throws DataPersistenceException {
        dataManager.saveAll(clinic);
    }

    public void reloadAllData() throws DataPersistenceException {
        dataManager.loadAll(clinic);
    }
}