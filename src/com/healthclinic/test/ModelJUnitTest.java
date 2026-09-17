package com.healthclinic.test;

import com.healthclinic.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 4: Formal JUnit 5 Tests for Domain Models & OOP Principles.
 * Validates Inheritance, Encapsulation, Polymorphism, and Aggregation.
 */
@DisplayName("Task 4: Model Layer OOP & Encapsulation Tests")
public class ModelJUnitTest {

    @Test
    @DisplayName("Patient inherits Person and encapsulates attributes")
    void testPatientInheritanceAndEncapsulation() {
        Patient patient = new Patient("P101", "Emma Watson", "555-0101", "emma@example.com",
                "1990-04-15", "Female", "A+", "555-0109", "Mild Asthma");

        assertInstanceOf(Person.class, patient, "Patient must extend abstract Person class");
        assertEquals("P101", patient.getId());
        assertEquals("Emma Watson", patient.getName());
        assertEquals("555-0101", patient.getPhone());
        assertEquals("emma@example.com", patient.getEmail());
        assertEquals("Female", patient.getGender());
        assertEquals("A+", patient.getBloodGroup());
        assertTrue(patient.getRoleDescription().contains("Patient: Emma Watson"));
    }

    @Test
    @DisplayName("Doctor inherits Person and calculates consultation fees")
    void testDoctorInheritanceAndFee() {
        Doctor doctor = new Doctor("D201", "Dr. Sarah Jenkins", "555-0201", "jenkins@clinic.org",
                "Cardiology", "LIC-CARD-991", 120.0, "Mon, Wed, Fri 09:00-13:00");

        assertInstanceOf(Person.class, doctor, "Doctor must extend abstract Person class");
        assertEquals("D201", doctor.getId());
        assertEquals("Dr. Sarah Jenkins", doctor.getName());
        assertEquals("Cardiology", doctor.getSpecialization());
        assertEquals("LIC-CARD-991", doctor.getLicenseNumber());
        assertEquals(120.0, doctor.getConsultationFee(), 0.001);
        assertTrue(doctor.getRoleDescription().contains("Sarah Jenkins"));
        assertTrue(doctor.getRoleDescription().contains("Cardiology"));
    }

    @Test
    @DisplayName("Administrator inherits Person and defines administrative role")
    void testAdministratorRole() {
        Administrator admin = new Administrator("ADM-01", "Chief Admin", "555-9999", "admin@clinic.org",
                "admin_user", "SUPER_ADMIN");

        assertInstanceOf(Person.class, admin, "Administrator must extend abstract Person class");
        assertEquals("ADM-01", admin.getId());
        assertEquals("admin_user", admin.getUsername());
        assertEquals("SUPER_ADMIN", admin.getRole());
        assertTrue(admin.getRoleDescription().contains("admin_user"));
    }

    @Test
    @DisplayName("Appointment encapsulates scheduling invariants")
    void testAppointmentModel() {
        LocalDateTime apptTime = LocalDateTime.of(2026, 10, 15, 10, 30);
        Appointment appt = new Appointment("A301", "P101", "D201", apptTime, "SCHEDULED", "Routine checkup");

        assertEquals("A301", appt.getAppointmentId());
        assertEquals("P101", appt.getPatientId());
        assertEquals("D201", appt.getDoctorId());
        assertEquals(apptTime, appt.getAppointmentDateTime());
        assertEquals("SCHEDULED", appt.getStatus());
        assertEquals("Routine checkup", appt.getNotes());

        appt.setStatus("COMPLETED");
        assertEquals("COMPLETED", appt.getStatus());
    }

    @Test
    @DisplayName("Treatment encapsulates clinical diagnosis and billing charges")
    void testTreatmentModel() {
        LocalDate treatDate = LocalDate.of(2026, 10, 15);
        Treatment treatment = new Treatment("T401", "A301", "P101", "D201",
                treatDate, "Bronchitis", "Amoxicillin 500mg TDS", 85.50, "Review in 7 days");

        assertEquals("T401", treatment.getTreatmentId());
        assertEquals("A301", treatment.getAppointmentId());
        assertEquals("P101", treatment.getPatientId());
        assertEquals("D201", treatment.getDoctorId());
        assertEquals(treatDate, treatment.getTreatmentDate());
        assertEquals("Bronchitis", treatment.getDiagnosis());
        assertEquals(85.50, treatment.getCost(), 0.001);
    }

    @Test
    @DisplayName("Clinic aggregates collections of patients, doctors, and appointments")
    void testClinicAggregation() {
        Clinic clinic = new Clinic("Hope Community Clinic", "100 Medical Plaza", "555-8000");

        Patient p1 = new Patient("P1", "Alice", "111", "a@c.com", "1990-01-01", "F", "O+", "", "");
        Doctor d1 = new Doctor("D1", "Dr. Bob", "222", "b@c.com", "General", "L1", 50.0, "Daily");
        Appointment a1 = new Appointment("A1", "P1", "D1", LocalDateTime.now(), "SCHEDULED", "");

        clinic.addPatient(p1);
        clinic.addDoctor(d1);
        clinic.addAppointment(a1);

        assertEquals(1, clinic.getPatients().size());
        assertEquals(1, clinic.getDoctors().size());
        assertEquals(1, clinic.getAppointments().size());
        assertNotNull(clinic.findPatientById("P1"));
        assertNotNull(clinic.findDoctorById("D1"));
        assertNotNull(clinic.findAppointmentById("A1"));
        assertNull(clinic.findPatientById("NON_EXISTENT"));
    }
}
