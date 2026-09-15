package com.healthclinic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root model representing the Clinic.
 * Encapsulates ArrayList collections for Patients, Doctors, Appointments,
 * Treatments, and Administrators. Demonstrates High Cohesion and Encapsulation.
 */
public class Clinic implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clinicName;
    private String address;
    private String contactNumber;

    // ArrayList collections as specified in Task 7
    private ArrayList<Patient> patients;
    private ArrayList<Doctor> doctors;
    private ArrayList<Appointment> appointments;
    private ArrayList<Treatment> treatments;
    private ArrayList<Administrator> administrators;

    public Clinic() {
        this("Community Health Clinic", "100 Healthcare Blvd, Metro City", "555-0199");
    }

    public Clinic(String clinicName, String address, String contactNumber) {
        this.clinicName = clinicName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.treatments = new ArrayList<>();
        this.administrators = new ArrayList<>();
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Defensive copy getters & direct mutators ensuring encapsulation

    public ArrayList<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients != null ? new ArrayList<>(patients) : new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        if (patient != null) {
            this.patients.add(patient);
        }
    }

    public boolean removePatientById(String patientId) {
        return this.patients.removeIf(p -> p.getId().equalsIgnoreCase(patientId));
    }

    public Patient findPatientById(String patientId) {
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Doctor> getDoctors() {
        return new ArrayList<>(doctors);
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors != null ? new ArrayList<>(doctors) : new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        if (doctor != null) {
            this.doctors.add(doctor);
        }
    }

    public boolean removeDoctorById(String doctorId) {
        return this.doctors.removeIf(d -> d.getId().equalsIgnoreCase(doctorId));
    }

    public Doctor findDoctorById(String doctorId) {
        for (Doctor d : doctors) {
            if (d.getId().equalsIgnoreCase(doctorId)) {
                return d;
            }
        }
        return null;
    }

    public ArrayList<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments != null ? new ArrayList<>(appointments) : new ArrayList<>();
    }

    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            this.appointments.add(appointment);
        }
    }

    public boolean removeAppointmentById(String appointmentId) {
        return this.appointments.removeIf(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId));
    }

    public Appointment findAppointmentById(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equalsIgnoreCase(appointmentId)) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<Treatment> getTreatments() {
        return new ArrayList<>(treatments);
    }

    public void setTreatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments != null ? new ArrayList<>(treatments) : new ArrayList<>();
    }

    public void addTreatment(Treatment treatment) {
        if (treatment != null) {
            this.treatments.add(treatment);
        }
    }

    public boolean removeTreatmentById(String treatmentId) {
        return this.treatments.removeIf(t -> t.getTreatmentId().equalsIgnoreCase(treatmentId));
    }

    public Treatment findTreatmentById(String treatmentId) {
        for (Treatment t : treatments) {
            if (t.getTreatmentId().equalsIgnoreCase(treatmentId)) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Administrator> getAdministrators() {
        return new ArrayList<>(administrators);
    }

    public void setAdministrators(ArrayList<Administrator> administrators) {
        this.administrators = administrators != null ? new ArrayList<>(administrators) : new ArrayList<>();
    }

    public void addAdministrator(Administrator admin) {
        if (admin != null) {
            this.administrators.add(admin);
        }
    }
}
