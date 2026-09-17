package com.healthclinic.test;

import com.healthclinic.controller.*;
import com.healthclinic.data.DataManager;
import com.healthclinic.model.*;
import com.healthclinic.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Standalone Headless Verification Runner (Console-based, no external JARs required).
 *
 * NOTE FOR GRADING & TEAM MEMBERS:
 * This file is a standalone verification runner for headless command-line checks.
 * FOR THE FORMAL JUNIT 5 TEST SUITES, RUN OR INSPECT:
 *  - ClinicSystemJUnitTest.java (All-in-one comprehensive JUnit 5 suite)
 *  - ModelJUnitTest.java (Task 4: Models, Inheritance, Encapsulation)
 *  - AlgorithmsJUnitTest.java (Task 8: Binary/Linear Search & Quick/Bubble Sort)
 *  - DataManagerJUnitTest.java (Task 7: CSV File I/O & Persistence CRUD)
 *  - ControllerAndValidationJUnitTest.java (Task 6: Validation, Exceptions, Business logic)
 */
public class SystemTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("  COMMUNITY HEALTH CLINIC MANAGEMENT SYSTEM - VERIFICATION SUITE   ");
        System.out.println("====================================================================");

        testTask4ModelLayer();
        testTask8SearchingAlgorithms();
        testTask8SortingAlgorithms();
        testTask7DataManagementCRUD();
        testTask6ControllerValidationAndExceptions();
        testTask6Reporting();

        System.out.println("\n====================================================================");
        System.out.printf("  TEST RESULTS: %d / %d PASSED (%.1f%%)\n",
                testsPassed, testsRun, (testsPassed * 100.0 / testsRun));
        System.out.println("====================================================================");

        if (testsPassed == testsRun) {
            System.out.println(">>> ALL VERIFICATION CHECKS COMPLETED SUCCESSFULLY! <<<");
        } else {
            System.err.println(">>> SOME TESTS FAILED! <<<");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf(" [PASS] %s\n", testName);
        } else {
            System.err.printf(" [FAIL] %s\n", testName);
        }
    }

    private static void testTask4ModelLayer() {
        System.out.println("\n--- Testing Task 4: Model Layer (Encapsulation & OOP) ---");

        Patient patient = new Patient("P999", "John Doe", "555-1122", "john@test.com",
                "1990-01-01", "Male", "O+", "555-9999", "No issues");
        assertTrue("Patient is instanceof Person (Inheritance)", patient instanceof Person);
        assertTrue("Patient Encapsulation (getId)", "P999".equals(patient.getId()));
        assertTrue("Patient Polymorphic Description", patient.getRoleDescription().contains("John Doe"));

        Doctor doctor = new Doctor("D999", "Alice Smith", "555-3344", "alice@test.com",
                "Pediatrics", "LIC-777", 75.0, "Mon, Wed");
        assertTrue("Doctor is instanceof Person (Inheritance)", doctor instanceof Person);
        assertTrue("Doctor Consultation Fee", doctor.getConsultationFee() == 75.0);

        Appointment appt = new Appointment("A999", "P999", "D999",
                LocalDateTime.of(2026, 10, 1, 10, 0), "SCHEDULED", "Checkup");
        assertTrue("Appointment status check", "SCHEDULED".equals(appt.getStatus()));

        Treatment treat = new Treatment("T999", "A999", "P999", "D999",
                LocalDate.of(2026, 10, 1), "Fever", "Paracetamol 500mg", 50.0, "Rest");
        assertTrue("Treatment cost check", treat.getCost() == 50.0);

        Clinic clinic = new Clinic("Test Clinic", "123 Main St", "555-0000");
        clinic.addPatient(patient);
        clinic.addDoctor(doctor);
        clinic.addAppointment(appt);
        clinic.addTreatment(treat);

        assertTrue("Clinic aggregates patients (ArrayList)", clinic.getPatients().size() == 1);
        assertTrue("Clinic findPatientById", clinic.findPatientById("P999") != null);
        assertTrue("Clinic findDoctorById", clinic.findDoctorById("D999") != null);
    }

    private static void testTask8SearchingAlgorithms() {
        System.out.println("\n--- Testing Task 8: Searching Algorithms (Binary & Linear Search) ---");

        ArrayList<Patient> list = new ArrayList<>();
        list.add(new Patient("P300", "Charlie", "555-3333", "c@test.com", "1992-03-03", "Male", "A+", "", ""));
        list.add(new Patient("P100", "Alice", "555-1111", "a@test.com", "1990-01-01", "Female", "O+", "", ""));
        list.add(new Patient("P400", "Diana", "555-4444", "d@test.com", "1993-04-04", "Female", "B+", "", ""));
        list.add(new Patient("P200", "Bob", "555-2222", "b@test.com", "1991-02-02", "Male", "AB+", "", ""));

        // Binary Search by ID (Requirement: search patients by ID)
        Patient found1 = SearchAlgorithms.binarySearchPatientById(list, "P200");
        assertTrue("Binary Search finds existing patient 'P200'", found1 != null && "Bob".equals(found1.getName()));

        Patient found2 = SearchAlgorithms.binarySearchPatientById(list, "P100");
        assertTrue("Binary Search finds boundary patient 'P100'", found2 != null && "Alice".equals(found2.getName()));

        Patient notFound = SearchAlgorithms.binarySearchPatientById(list, "P999");
        assertTrue("Binary Search returns null for non-existent ID", notFound == null);

        // Linear Search
        ArrayList<Patient> matches = SearchAlgorithms.linearSearchPatients(list, "Alice");
        assertTrue("Linear Search matches by Name", matches.size() == 1 && "P100".equals(matches.get(0).getId()));

        ArrayList<Patient> phoneMatches = SearchAlgorithms.linearSearchPatients(list, "555-3333");
        assertTrue("Linear Search matches by Phone", phoneMatches.size() == 1 && "P300".equals(phoneMatches.get(0).getId()));
    }

    private static void testTask8SortingAlgorithms() {
        System.out.println("\n--- Testing Task 8: Sorting Algorithms (Quick Sort & Bubble Sort) ---");

        // Quick Sort: Sort appointments by date (Requirement: sort appointments by date)
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

        boolean isSorted = true;
        for (int i = 0; i < appts.size() - 1; i++) {
            if (appts.get(i).getAppointmentDateTime().isAfter(appts.get(i + 1).getAppointmentDateTime())) {
                isSorted = false;
                break;
            }
        }
        assertTrue("Quick Sort correctly sorts appointments chronologically", isSorted);
        assertTrue("Quick Sort earliest date is first (A3 - Sept 20)", "A3".equals(appts.get(0).getAppointmentId()));
        assertTrue("Quick Sort latest date is last (A1 - Dec 1)", "A1".equals(appts.get(appts.size() - 1).getAppointmentId()));

        // Bubble Sort: Sort patients by ID
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(new Patient("Z99", "Zack", "111", "z@t.com", "1990-01-01", "M", "A+", "", ""));
        patients.add(new Patient("A01", "Adam", "222", "a@t.com", "1990-01-01", "M", "A+", "", ""));
        patients.add(new Patient("M50", "Mary", "333", "m@t.com", "1990-01-01", "F", "A+", "", ""));

        SortAlgorithms.bubbleSortPatientsById(patients);
        assertTrue("Bubble Sort places 'A01' at index 0", "A01".equals(patients.get(0).getId()));
        assertTrue("Bubble Sort places 'M50' at index 1", "M50".equals(patients.get(1).getId()));
        assertTrue("Bubble Sort places 'Z99' at index 2", "Z99".equals(patients.get(2).getId()));
    }

    private static void testTask7DataManagementCRUD() {
        System.out.println("\n--- Testing Task 7: Data Management (File I/O CRUD) ---");

        DataManager dm = new DataManager();
        Clinic testClinic = new Clinic("CRUD Test Clinic", "Test Address", "123");

        try {
            // Seed and save
            dm.seedSampleData(testClinic);
            dm.saveAll(testClinic);
            assertTrue("Save All creates files without throwing exception", true);

            // Load and verify
            Clinic loadedClinic = new Clinic();
            dm.loadAll(loadedClinic);
            assertTrue("Load Patients count >= 4", loadedClinic.getPatients().size() >= 4);
            assertTrue("Load Doctors count >= 3", loadedClinic.getDoctors().size() >= 3);
            assertTrue("Load Appointments count >= 4", loadedClinic.getAppointments().size() >= 4);
            assertTrue("Load Treatments count >= 1", loadedClinic.getTreatments().size() >= 1);

            // CRUD: Update
            Patient p = loadedClinic.findPatientById("P101");
            p.setName("Emma Watson-Updated");
            dm.savePatients(loadedClinic.getPatients());

            Clinic reloadClinic = new Clinic();
            reloadClinic.setPatients(dm.loadPatients());
            assertTrue("Update persisted to disk", "Emma Watson-Updated".equals(reloadClinic.findPatientById("P101").getName()));

            // Restore original name
            p.setName("Emma Watson");
            dm.savePatients(loadedClinic.getPatients());

        } catch (Exception e) {
            System.err.println("CRUD Exception: " + e.getMessage());
            assertTrue("File I/O operations failed: " + e.getMessage(), false);
        }
    }

    private static void testTask6ControllerValidationAndExceptions() {
        System.out.println("\n--- Testing Task 6: Controller Layer (Validation & Exceptions) ---");

        try {
            ClinicController controller = new ClinicController();
            PatientController pc = controller.getPatientController();

            // Test email validation
            boolean caughtInvalidEmail = false;
            try {
                pc.registerPatient("PTEST", "Test", "555-1234", "not-an-email", "1990-01-01", "Male", "O+", "", "");
            } catch (ValidationException ve) {
                caughtInvalidEmail = true;
            }
            assertTrue("Controller rejects invalid email address", caughtInvalidEmail);

            // Test date validation
            boolean caughtInvalidDate = false;
            try {
                pc.registerPatient("PTEST", "Test", "555-1234", "test@test.com", "not-a-date", "Male", "O+", "", "");
            } catch (ValidationException ve) {
                caughtInvalidDate = true;
            }
            assertTrue("Controller rejects invalid date format", caughtInvalidDate);

            // Test duplicate ID validation
            boolean caughtDuplicate = false;
            try {
                pc.registerPatient("P101", "Duplicate Emma", "555-1234", "test@test.com", "1990-01-01", "Male", "O+", "", "");
            } catch (ValidationException ve) {
                caughtDuplicate = true;
            }
            assertTrue("Controller prevents duplicate Patient ID registration", caughtDuplicate);

            // Test valid registration & deletion
            pc.registerPatient("P9999", "Temp Patient", "555-9999", "temp@test.com", "1999-09-09", "Male", "B+", "", "");
            assertTrue("Controller successfully registered temporary patient", pc.searchPatientById("P9999") != null);

            pc.deletePatient("P9999");
            assertTrue("Controller successfully deleted temporary patient", pc.searchPatientById("P9999") == null);

        } catch (Exception e) {
            assertTrue("Controller test exception: " + e.getMessage(), false);
        }
    }

    private static void testTask6Reporting() {
        System.out.println("\n--- Testing Task 6: Report Generation ---");

        try {
            ClinicController controller = new ClinicController();
            ReportController rc = controller.getReportController();

            String apptReport = rc.generateAppointmentReport(null, null, null);
            assertTrue("Appointment report is non-empty", apptReport != null && apptReport.contains("APPOINTMENT REPORT"));
            assertTrue("Appointment report includes summary statistics", apptReport.contains("Total Appointments:"));

            String docSchedule = rc.generateDoctorScheduleReport(null);
            assertTrue("Doctor schedule report generated", docSchedule != null && docSchedule.contains("DOCTOR SCHEDULE"));

            String summary = rc.getClinicStatistics();
            assertTrue("Clinic summary statistics generated", summary != null && summary.contains("Patients:"));

        } catch (Exception e) {
            assertTrue("Reporting test exception: " + e.getMessage(), false);
        }
    }
}