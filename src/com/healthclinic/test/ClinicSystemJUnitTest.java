package com.healthclinic.test;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.PatientController;
import com.healthclinic.controller.ReportController;
import com.healthclinic.data.DataManager;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Doctor;
import com.healthclinic.model.Patient;
import com.healthclinic.model.Person;
import com.healthclinic.model.Treatment;
import com.healthclinic.util.SearchAlgorithms;
import com.healthclinic.util.SortAlgorithms;
import com.healthclinic.util.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Community Health Clinic System - JUnit 5 Test Suite")
public class ClinicSystemJUnitTest {

    @Nested
    @DisplayName("Task 4: OOP Domain Models & Encapsulation")
    class ModelLayerTests {

        @Test
        @DisplayName("Verify Patient inheritance from Person and encapsulation")
        void testPatientModel() {
            Patient patient = new Patient("P999", "John Doe", "555-1122", "john@test.com",
                    "1990-01-01", "Male", "O+", "555-9999", "No issues");
            assertInstanceOf(Person.class, patient, "Patient must inherit from Person");
            assertEquals("P999", patient.getId());
            assertEquals("John Doe", patient.getName());
            assertTrue(patient.getRoleDescription().contains("John Doe"));
        }

        @Test
        @DisplayName("Verify Doctor inheritance and consultation fee calculation")
        void testDoctorModel() {
            Doctor doctor = new Doctor("D999", "Alice Smith", "555-3344", "alice@test.com",
                    "Pediatrics", "LIC-777", 75.0, "Mon, Wed");
            assertInstanceOf(Person.class, doctor, "Doctor must inherit from Person");
            assertEquals(75.0, doctor.getConsultationFee(), 0.001);
            assertEquals("Pediatrics", doctor.getSpecialization());
        }

        @Test
        @DisplayName("Verify Appointment and Treatment domain entities")
        void testAppointmentAndTreatment() {
            Appointment appt = new Appointment("A999", "P999", "D999",
                    LocalDateTime.of(2026, 10, 1, 10, 0), "SCHEDULED", "Checkup");
            assertEquals("SCHEDULED", appt.getStatus());

            Treatment treat = new Treatment("T999", "A999", "P999", "D999",
                    LocalDate.of(2026, 10, 1), "Fever", "Paracetamol 500mg", 50.0, "Rest");
            assertEquals(50.0, treat.getCost(), 0.001);
        }

        @Test
        @DisplayName("Verify Clinic aggregation root maintains collections")
        void testClinicAggregation() {
            Clinic clinic = new Clinic("Test Clinic", "123 Main St", "555-0000");
            Patient p = new Patient("P999", "John", "123", "j@t.com", "1990-01-01", "M", "A+", "", "");
            Doctor d = new Doctor("D999", "Dr. Smith", "456", "d@t.com", "General", "L1", 50.0, "Daily");
            clinic.addPatient(p);
            clinic.addDoctor(d);

            assertEquals(1, clinic.getPatients().size());
            assertNotNull(clinic.findPatientById("P999"));
            assertNotNull(clinic.findDoctorById("D999"));
        }
    }

    @Nested
    @DisplayName("Task 8: Search Algorithms (Binary Search O(log n) & Linear Search O(n))")
    class SearchAlgorithmTests {

        private ArrayList<Patient> list;

        @BeforeEach
        void setUp() {
            list = new ArrayList<>();
            list.add(new Patient("P300", "Charlie", "555-3333", "c@test.com", "1992-03-03", "Male", "A+", "", ""));
            list.add(new Patient("P100", "Alice", "555-1111", "a@test.com", "1990-01-01", "Female", "O+", "", ""));
            list.add(new Patient("P400", "Diana", "555-4444", "d@test.com", "1993-04-04", "Female", "B+", "", ""));
            list.add(new Patient("P200", "Bob", "555-2222", "b@test.com", "1991-02-02", "Male", "AB+", "", ""));
        }

        @Test
        @DisplayName("Binary Search finds existing patient by ID")
        void testBinarySearchFound() {
            Patient found = SearchAlgorithms.binarySearchPatientById(list, "P200");
            assertNotNull(found);
            assertEquals("Bob", found.getName());
        }

        @Test
        @DisplayName("Binary Search finds boundary element (lowest ID)")
        void testBinarySearchBoundary() {
            Patient found = SearchAlgorithms.binarySearchPatientById(list, "P100");
            assertNotNull(found);
            assertEquals("Alice", found.getName());
        }

        @Test
        @DisplayName("Binary Search returns null for non-existent ID")
        void testBinarySearchNotFound() {
            Patient found = SearchAlgorithms.binarySearchPatientById(list, "P999");
            assertNull(found);
        }

        @Test
        @DisplayName("Linear Search finds matching patients by substring")
        void testLinearSearch() {
            ArrayList<Patient> matches = SearchAlgorithms.linearSearchPatients(list, "Alice");
            assertEquals(1, matches.size());
            assertEquals("P100", matches.get(0).getId());

            ArrayList<Patient> phoneMatches = SearchAlgorithms.linearSearchPatients(list, "555-3333");
            assertEquals(1, phoneMatches.size());
            assertEquals("P300", phoneMatches.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Task 8: Sorting Algorithms (Quick Sort O(n log n) & Bubble Sort O(n^2))")
    class SortAlgorithmTests {

        @Test
        @DisplayName("Quick Sort correctly sorts appointments chronologically by date/time")
        void testQuickSortAppointments() {
            ArrayList<Appointment> appts = new ArrayList<>();
            LocalDateTime dt1 = LocalDateTime.of(2026, 12, 1, 9, 0);
            LocalDateTime dt2 = LocalDateTime.of(2026, 10, 15, 14, 0);
            LocalDateTime dt3 = LocalDateTime.of(2026, 9, 20, 11, 30);
            LocalDateTime dt4 = LocalDateTime.of(2026, 11, 5, 8, 45);

            appts.add(new Appointment("A1", "P1", "D1", dt1, "SCHEDULED", ""));
            appts.add(new Appointment("A2", "P1", "D1", dt2, "SCHEDULED", ""));
            appts.add(new Appointment("A3", "P1", "D1", dt3, "SCHEDULED", ""));
            appts.add(new Appointment("A4", "P1", "D1", dt4, "SCHEDULED", ""));

            SortAlgorithms.quickSortAppointmentsByDate(appts);

            for (int i = 0; i < appts.size() - 1; i++) {
                assertFalse(appts.get(i).getAppointmentDateTime().isAfter(appts.get(i + 1).getAppointmentDateTime()),
                        "Appointments must be strictly ordered chronologically");
            }
            assertEquals("A3", appts.get(0).getAppointmentId(), "Earliest appointment must be first");
            assertEquals("A1", appts.get(appts.size() - 1).getAppointmentId(), "Latest appointment must be last");
        }

        @Test
        @DisplayName("Bubble Sort sorts patients ascending by Patient ID")
        void testBubbleSortPatients() {
            ArrayList<Patient> patients = new ArrayList<>();
            patients.add(new Patient("Z99", "Zack", "111", "z@t.com", "1990-01-01", "M", "A+", "", ""));
            patients.add(new Patient("A01", "Adam", "222", "a@t.com", "1990-01-01", "M", "A+", "", ""));
            patients.add(new Patient("M50", "Mary", "333", "m@t.com", "1990-01-01", "F", "A+", "", ""));

            SortAlgorithms.bubbleSortPatientsById(patients);

            assertEquals("A01", patients.get(0).getId());
            assertEquals("M50", patients.get(1).getId());
            assertEquals("Z99", patients.get(2).getId());
        }
    }

    @Nested
    @DisplayName("Task 7: Data Management (CSV File Persistence & CRUD)")
    class DataManagementCRUDTests {

        @Test
        @DisplayName("DataManager successfully seeds, saves, and loads entities")
        void testPersistenceLifecycle() throws Exception {
            DataManager dm = new DataManager();
            Clinic clinic = new Clinic("CRUD Clinic", "Street 1", "123");

            dm.seedSampleData(clinic);
            dm.saveAll(clinic);

            Clinic loadedClinic = new Clinic();
            dm.loadAll(loadedClinic);

            assertTrue(loadedClinic.getPatients().size() >= 4);
            assertTrue(loadedClinic.getDoctors().size() >= 3);
            assertTrue(loadedClinic.getAppointments().size() >= 4);
            assertTrue(loadedClinic.getTreatments().size() >= 1);
        }

        @Test
        @DisplayName("DataManager updates persist correctly across reloads")
        void testUpdatePersistence() throws Exception {
            DataManager dm = new DataManager();
            Clinic loadedClinic = new Clinic();
            dm.loadAll(loadedClinic);

            Patient p = loadedClinic.findPatientById("P101");
            assertNotNull(p);
            String originalName = p.getName();

            p.setName(originalName + "-Verified");
            dm.savePatients(loadedClinic.getPatients());

            Clinic reloaded = new Clinic();
            reloaded.setPatients(dm.loadPatients());
            assertEquals(originalName + "-Verified", reloaded.findPatientById("P101").getName());

            // Revert back
            p.setName(originalName);
            dm.savePatients(loadedClinic.getPatients());
        }
    }

    @Nested
    @DisplayName("Task 6: Controller Layer, Input Validation & Exceptions")
    class ControllerAndValidationTests {

        @Test
        @DisplayName("ValidationUtil catches malformed email")
        void testEmailValidation() throws Exception {
            ClinicController cc = new ClinicController();
            PatientController pc = cc.getPatientController();

            assertThrows(ValidationException.class, () -> {
                pc.registerPatient("P9999", "Test", "1234567890", "invalid-email-format",
                        "1990-01-01", "Male", "A+", "", "");
            });
        }

        @Test
        @DisplayName("ValidationUtil catches malformed date")
        void testDateValidation() throws Exception {
            ClinicController cc = new ClinicController();
            PatientController pc = cc.getPatientController();

            assertThrows(ValidationException.class, () -> {
                pc.registerPatient("P9999", "Test", "1234567890", "valid@email.com",
                        "32-13-2020", "Male", "A+", "", "");
            });
        }

        @Test
        @DisplayName("Controller rejects duplicate primary key IDs")
        void testDuplicateIdRejection() throws Exception {
            ClinicController cc = new ClinicController();
            PatientController pc = cc.getPatientController();

            assertThrows(ValidationException.class, () -> {
                pc.registerPatient("P101", "Duplicate Test", "1234567890", "valid@email.com",
                        "1990-01-01", "Male", "A+", "", "");
            });
        }

        @Test
        @DisplayName("Controller registers and deletes patient cleanly")
        void testRegisterAndDelete() throws Exception {
            ClinicController cc = new ClinicController();
            PatientController pc = cc.getPatientController();

            pc.registerPatient("P8888", "Temp JUnit Patient", "555-8888", "junit@test.com",
                    "1995-05-05", "Female", "B+", "", "");
            assertNotNull(pc.searchPatientById("P8888"));

            pc.deletePatient("P8888");
            assertNull(pc.searchPatientById("P8888"));
        }
    }

    @Nested
    @DisplayName("Task 6: Report Generation & Business Logic")
    class ReportGenerationTests {

        @Test
        @DisplayName("ReportController generates valid appointment and doctor schedule reports")
        void testReports() throws Exception {
            ClinicController cc = new ClinicController();
            ReportController rc = cc.getReportController();

            String apptReport = rc.generateAppointmentReport(null, null, null);
            assertNotNull(apptReport);
            assertTrue(apptReport.contains("APPOINTMENT REPORT"));
            assertTrue(apptReport.contains("Total Appointments:"));

            String docReport = rc.generateDoctorScheduleReport(null);
            assertNotNull(docReport);
            assertTrue(docReport.contains("DOCTOR SCHEDULE"));

            String stats = rc.getClinicStatistics();
            assertNotNull(stats);
            assertTrue(stats.contains("Patients:"));
        }
    }
}
