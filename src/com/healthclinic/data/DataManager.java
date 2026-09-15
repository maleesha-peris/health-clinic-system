package com.healthclinic.data;

import com.healthclinic.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Task 7 - Data Management.
 * Implements File I/O for saving and loading data from CSV files.
 * Supports Save, Load, Update, and Delete (CRUD) operations on all entities.
 */
public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String PATIENTS_FILE = DATA_DIR + File.separator + "patients.csv";
    private static final String DOCTORS_FILE = DATA_DIR + File.separator + "doctors.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + File.separator + "appointments.csv";
    private static final String TREATMENTS_FILE = DATA_DIR + File.separator + "treatments.csv";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DataManager() {
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // -------------------------------------------------------------
    // PATIENTS File I/O (CRUD)
    // -------------------------------------------------------------

    public ArrayList<Patient> loadPatients() throws DataPersistenceException {
        ArrayList<Patient> list = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = parseCsvLine(line);
                if (tokens.length >= 9) {
                    Patient p = new Patient(
                            tokens[0], // id
                            tokens[1], // name
                            tokens[2], // phone
                            tokens[3], // email
                            tokens[4], // dob
                            tokens[5], // gender
                            tokens[6], // bloodGroup
                            tokens[7], // emergencyContact
                            tokens[8]  // medicalHistory
                    );
                    list.add(p);
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to read patients file: " + e.getMessage(), e);
        }
        return list;
    }

    public void savePatients(ArrayList<Patient> patients) throws DataPersistenceException {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATIENTS_FILE), StandardCharsets.UTF_8))) {
            writer.write("id,name,phone,email,dateOfBirth,gender,bloodGroup,emergencyContact,medicalHistory");
            writer.newLine();
            for (Patient p : patients) {
                String line = String.join(",",
                        escapeCsv(p.getId()),
                        escapeCsv(p.getName()),
                        escapeCsv(p.getPhone()),
                        escapeCsv(p.getEmail()),
                        escapeCsv(p.getDateOfBirth()),
                        escapeCsv(p.getGender()),
                        escapeCsv(p.getBloodGroup()),
                        escapeCsv(p.getEmergencyContact()),
                        escapeCsv(p.getMedicalHistory())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save patients file: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------
    // DOCTORS File I/O (CRUD)
    // -------------------------------------------------------------

    public ArrayList<Doctor> loadDoctors() throws DataPersistenceException {
        ArrayList<Doctor> list = new ArrayList<>();
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = parseCsvLine(line);
                if (tokens.length >= 8) {
                    double fee = 0.0;
                    try {
                        fee = Double.parseDouble(tokens[6]);
                    } catch (NumberFormatException ignored) {}

                    Doctor d = new Doctor(
                            tokens[0], // id
                            tokens[1], // name
                            tokens[2], // phone
                            tokens[3], // email
                            tokens[4], // specialization
                            tokens[5], // licenseNumber
                            fee,       // consultationFee
                            tokens[7]  // availableDays
                    );
                    list.add(d);
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to read doctors file: " + e.getMessage(), e);
        }
        return list;
    }

    public void saveDoctors(ArrayList<Doctor> doctors) throws DataPersistenceException {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DOCTORS_FILE), StandardCharsets.UTF_8))) {
            writer.write("id,name,phone,email,specialization,licenseNumber,consultationFee,availableDays");
            writer.newLine();
            for (Doctor d : doctors) {
                String line = String.join(",",
                        escapeCsv(d.getId()),
                        escapeCsv(d.getName()),
                        escapeCsv(d.getPhone()),
                        escapeCsv(d.getEmail()),
                        escapeCsv(d.getSpecialization()),
                        escapeCsv(d.getLicenseNumber()),
                        String.valueOf(d.getConsultationFee()),
                        escapeCsv(d.getAvailableDays())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save doctors file: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------
    // APPOINTMENTS File I/O (CRUD)
    // -------------------------------------------------------------

    public ArrayList<Appointment> loadAppointments() throws DataPersistenceException {
        ArrayList<Appointment> list = new ArrayList<>();
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = parseCsvLine(line);
                if (tokens.length >= 6) {
                    LocalDateTime dt = null;
                    try {
                        dt = LocalDateTime.parse(tokens[3], DATE_TIME_FORMATTER);
                    } catch (Exception ignored) {}

                    Appointment a = new Appointment(
                            tokens[0], // appointmentId
                            tokens[1], // patientId
                            tokens[2], // doctorId
                            dt,        // appointmentDateTime
                            tokens[4], // status
                            tokens[5]  // notes
                    );
                    list.add(a);
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to read appointments file: " + e.getMessage(), e);
        }
        return list;
    }

    public void saveAppointments(ArrayList<Appointment> appointments) throws DataPersistenceException {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(APPOINTMENTS_FILE), StandardCharsets.UTF_8))) {
            writer.write("appointmentId,patientId,doctorId,appointmentDateTime,status,notes");
            writer.newLine();
            for (Appointment a : appointments) {
                String dtStr = a.getAppointmentDateTime() != null ?
                        a.getAppointmentDateTime().format(DATE_TIME_FORMATTER) : "";
                String line = String.join(",",
                        escapeCsv(a.getAppointmentId()),
                        escapeCsv(a.getPatientId()),
                        escapeCsv(a.getDoctorId()),
                        escapeCsv(dtStr),
                        escapeCsv(a.getStatus()),
                        escapeCsv(a.getNotes())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save appointments file: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------
    // TREATMENTS File I/O (CRUD)
    // -------------------------------------------------------------

    public ArrayList<Treatment> loadTreatments() throws DataPersistenceException {
        ArrayList<Treatment> list = new ArrayList<>();
        File file = new File(TREATMENTS_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = parseCsvLine(line);
                if (tokens.length >= 9) {
                    LocalDate date = null;
                    try {
                        date = LocalDate.parse(tokens[4], DATE_FORMATTER);
                    } catch (Exception ignored) {}

                    double cost = 0.0;
                    try {
                        cost = Double.parseDouble(tokens[7]);
                    } catch (Exception ignored) {}

                    Treatment t = new Treatment(
                            tokens[0], // treatmentId
                            tokens[1], // appointmentId
                            tokens[2], // patientId
                            tokens[3], // doctorId
                            date,      // treatmentDate
                            tokens[5], // diagnosis
                            tokens[6], // prescription
                            cost,      // cost
                            tokens[8]  // notes
                    );
                    list.add(t);
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to read treatments file: " + e.getMessage(), e);
        }
        return list;
    }

    public void saveTreatments(ArrayList<Treatment> treatments) throws DataPersistenceException {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TREATMENTS_FILE), StandardCharsets.UTF_8))) {
            writer.write("treatmentId,appointmentId,patientId,doctorId,treatmentDate,diagnosis,prescription,cost,notes");
            writer.newLine();
            for (Treatment t : treatments) {
                String dateStr = t.getTreatmentDate() != null ?
                        t.getTreatmentDate().format(DATE_FORMATTER) : "";
                String line = String.join(",",
                        escapeCsv(t.getTreatmentId()),
                        escapeCsv(t.getAppointmentId()),
                        escapeCsv(t.getPatientId()),
                        escapeCsv(t.getDoctorId()),
                        escapeCsv(dateStr),
                        escapeCsv(t.getDiagnosis()),
                        escapeCsv(t.getPrescription()),
                        String.valueOf(t.getCost()),
                        escapeCsv(t.getNotes())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Failed to save treatments file: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------
    // Full Clinic Save & Load
    // -------------------------------------------------------------

    public void loadAll(Clinic clinic) throws DataPersistenceException {
        clinic.setPatients(loadPatients());
        clinic.setDoctors(loadDoctors());
        clinic.setAppointments(loadAppointments());
        clinic.setTreatments(loadTreatments());

        // Auto-seed initial sample data if completely empty
        if (clinic.getPatients().isEmpty() && clinic.getDoctors().isEmpty()) {
            seedSampleData(clinic);
            saveAll(clinic);
        }
    }

    public void saveAll(Clinic clinic) throws DataPersistenceException {
        savePatients(clinic.getPatients());
        saveDoctors(clinic.getDoctors());
        saveAppointments(clinic.getAppointments());
        saveTreatments(clinic.getTreatments());
    }

    public void seedSampleData(Clinic clinic) {
        // Patients
        Patient p1 = new Patient("P101", "Emma Watson", "555-0101", "emma@example.com", "1990-04-15", "Female", "A+", "555-0109", "Mild Asthma");
        Patient p2 = new Patient("P102", "James Wilson", "555-0102", "james@example.com", "1985-11-20", "Male", "O+", "555-0108", "No chronic conditions");
        Patient p3 = new Patient("P103", "Sophia Taylor", "555-0103", "sophia@example.com", "1995-07-08", "Female", "B-", "555-0107", "Penicillin allergy");
        Patient p4 = new Patient("P104", "Michael Brown", "555-0104", "michael@example.com", "1978-02-28", "Male", "AB+", "555-0106", "Hypertension");
        clinic.addPatient(p1);
        clinic.addPatient(p2);
        clinic.addPatient(p3);
        clinic.addPatient(p4);

        // Doctors
        Doctor d1 = new Doctor("D201", "Sarah Connor", "555-0201", "s.connor@clinic.com", "General Practice", "LIC-8831", 60.0, "Mon, Tue, Wed, Thu, Fri");
        Doctor d2 = new Doctor("D202", "Gregory House", "555-0202", "g.house@clinic.com", "Diagnostic Medicine", "LIC-7412", 120.0, "Tue, Thu");
        Doctor d3 = new Doctor("D203", "Meredith Grey", "555-0203", "m.grey@clinic.com", "General Surgery", "LIC-9954", 95.0, "Mon, Wed, Fri");
        clinic.addDoctor(d1);
        clinic.addDoctor(d2);
        clinic.addDoctor(d3);

        // Appointments
        Appointment a1 = new Appointment("A301", "P101", "D201", LocalDateTime.of(2026, 9, 16, 9, 30), "SCHEDULED", "Routine checkup and lung auscultation");
        Appointment a2 = new Appointment("A302", "P102", "D202", LocalDateTime.of(2026, 9, 16, 11, 0), "SCHEDULED", "Unexplained joint pain consult");
        Appointment a3 = new Appointment("A303", "P103", "D201", LocalDateTime.of(2026, 9, 15, 14, 0), "COMPLETED", "Seasonal allergy evaluation");
        Appointment a4 = new Appointment("A304", "P104", "D203", LocalDateTime.of(2026, 9, 18, 10, 15), "SCHEDULED", "Pre-op surgical consultation");
        clinic.addAppointment(a1);
        clinic.addAppointment(a2);
        clinic.addAppointment(a3);
        clinic.addAppointment(a4);

        // Treatments
        Treatment t1 = new Treatment("T401", "A303", "P103", "D201", LocalDate.of(2026, 9, 15), "Seasonal Allergic Rhinitis", "Cetirizine 10mg daily x 14 days", 60.0, "Avoid exposure to fresh pollen.");
        clinic.addTreatment(t1);

        // Admin
        Administrator admin = new Administrator("ADM01", "System Administrator", "555-0000", "admin@clinic.com", "admin", "SUPER_ADMIN");
        clinic.addAdministrator(admin);
    }

    // -------------------------------------------------------------
    // CSV Utilities (Robust escaping & line parsing)
    // -------------------------------------------------------------

    private String escapeCsv(String val) {
        if (val == null) return "";
        String s = val.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s + "\"";
        }
        return s;
    }

    private String[] parseCsvLine(String line) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }
}