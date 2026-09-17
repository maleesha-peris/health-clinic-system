package com.healthclinic.test;

import com.healthclinic.model.Appointment;
import com.healthclinic.model.Patient;
import com.healthclinic.util.SearchAlgorithms;
import com.healthclinic.util.SortAlgorithms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 8: Formal JUnit 5 Tests for Custom Searching & Sorting Algorithms.
 * Validates Binary Search O(log n), Linear Search O(n), Quick Sort O(n log n), and Bubble Sort O(n^2).
 */
@DisplayName("Task 8: Search & Sort Algorithm Tests")
public class AlgorithmsJUnitTest {

    private ArrayList<Patient> patientList;

    @BeforeEach
    void setUp() {
        patientList = new ArrayList<>();
        patientList.add(new Patient("P300", "Charlie", "555-3333", "charlie@clinic.org", "1992-03-03", "Male", "A+", "", ""));
        patientList.add(new Patient("P100", "Alice", "555-1111", "alice@clinic.org", "1990-01-01", "Female", "O+", "", ""));
        patientList.add(new Patient("P400", "Diana", "555-4444", "diana@clinic.org", "1993-04-04", "Female", "B+", "", ""));
        patientList.add(new Patient("P200", "Bob", "555-2222", "bob@clinic.org", "1991-02-02", "Male", "AB+", "", ""));
    }

    @Test
    @DisplayName("Binary Search finds existing patient by ID in logarithmic time O(log n)")
    void testBinarySearchFound() {
        Patient found = SearchAlgorithms.binarySearchPatientById(patientList, "P200");
        assertNotNull(found, "Binary search must return the matching Patient object");
        assertEquals("Bob", found.getName());
        assertEquals("P200", found.getId());
    }

    @Test
    @DisplayName("Binary Search handles boundary elements (first sorted element)")
    void testBinarySearchBoundary() {
        Patient found = SearchAlgorithms.binarySearchPatientById(patientList, "P100");
        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    @DisplayName("Binary Search returns null for non-existent ID")
    void testBinarySearchNotFound() {
        Patient found = SearchAlgorithms.binarySearchPatientById(patientList, "P999");
        assertNull(found, "Binary search must return null when patient ID does not exist");
    }

    @Test
    @DisplayName("Linear Search filters patients by name substring (case-insensitive)")
    void testLinearSearchByName() {
        ArrayList<Patient> matches = SearchAlgorithms.linearSearchPatients(patientList, "ali");
        assertEquals(1, matches.size());
        assertEquals("P100", matches.get(0).getId());
    }

    @Test
    @DisplayName("Linear Search filters patients by phone number")
    void testLinearSearchByPhone() {
        ArrayList<Patient> matches = SearchAlgorithms.linearSearchPatients(patientList, "555-3333");
        assertEquals(1, matches.size());
        assertEquals("P300", matches.get(0).getId());
    }

    @Test
    @DisplayName("Quick Sort arranges appointments strictly in chronological order O(n log n)")
    void testQuickSortAppointmentsChronological() {
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
                    "Appointment at index " + i + " must occur on or before appointment at index " + (i + 1));
        }

        assertEquals("A3", appts.get(0).getAppointmentId(), "Earliest appointment (Sept 20) must be at index 0");
        assertEquals("A1", appts.get(appts.size() - 1).getAppointmentId(), "Latest appointment (Dec 1) must be last");
    }

    @Test
    @DisplayName("Bubble Sort arranges patients in ascending ID order O(n^2)")
    void testBubbleSortPatientsById() {
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
