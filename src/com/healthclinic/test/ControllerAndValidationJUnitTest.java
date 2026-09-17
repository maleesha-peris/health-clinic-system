package com.healthclinic.test;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.PatientController;
import com.healthclinic.controller.ReportController;
import com.healthclinic.util.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 6: Formal JUnit 5 Tests for Controller Layer, Input Validation & Exceptions.
 */
@DisplayName("Task 6: Controller & Validation Exception Tests")
public class ControllerAndValidationJUnitTest {

    @Test
    @DisplayName("ValidationUtil throws ValidationException when email is malformed")
    void testInvalidEmailValidationException() throws Exception {
        ClinicController cc = new ClinicController();
        PatientController pc = cc.getPatientController();

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            pc.registerPatient("P9999", "Bad Email Patient", "555-1234", "not-an-email",
                    "1990-01-01", "Male", "A+", "", "");
        }, "Controller must throw ValidationException for invalid email");

        assertTrue(ex.getMessage().toLowerCase().contains("email"));
    }

    @Test
    @DisplayName("ValidationUtil throws ValidationException when date format is invalid")
    void testInvalidDateFormatValidationException() throws Exception {
        ClinicController cc = new ClinicController();
        PatientController pc = cc.getPatientController();

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            pc.registerPatient("P9999", "Bad Date Patient", "555-1234", "valid@clinic.org",
                    "99-99-9999", "Male", "A+", "", "");
        }, "Controller must throw ValidationException for invalid date format");

        assertTrue(ex.getMessage().toLowerCase().contains("date"));
    }

    @Test
    @DisplayName("Controller rejects duplicate primary key IDs with ValidationException")
    void testDuplicatePrimaryKeyRejection() throws Exception {
        ClinicController cc = new ClinicController();
        PatientController pc = cc.getPatientController();

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            pc.registerPatient("P101", "Duplicate Emma", "555-1234", "emma.dup@clinic.org",
                    "1990-04-15", "Female", "A+", "", "");
        }, "Controller must reject duplicate Patient ID P101");

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Controller registers, finds, and deletes patient entity cleanly")
    void testPatientRegistrationAndDeletion() throws Exception {
        ClinicController cc = new ClinicController();
        PatientController pc = cc.getPatientController();

        pc.registerPatient("P7777", "JUnit Temp Patient", "555-7777", "temp.junit@clinic.org",
                "1998-08-08", "Female", "O+", "", "");
        assertNotNull(pc.searchPatientById("P7777"), "Patient P7777 must be found after registration");

        pc.deletePatient("P7777");
        assertNull(pc.searchPatientById("P7777"), "Patient P7777 must be null after deletion");
    }

    @Test
    @DisplayName("ReportController generates non-empty formatted reports and summaries")
    void testReportGenerationOutput() throws Exception {
        ClinicController cc = new ClinicController();
        ReportController rc = cc.getReportController();

        String apptReport = rc.generateAppointmentReport(null, null, null);
        assertNotNull(apptReport);
        assertTrue(apptReport.contains("APPOINTMENT REPORT"));
        assertTrue(apptReport.contains("Total Appointments:"));

        String stats = rc.getClinicStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Patients:"));
    }
}
