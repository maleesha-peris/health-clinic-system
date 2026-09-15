package com.healthclinic.util;

import com.healthclinic.model.Appointment;
import com.healthclinic.model.Patient;

import java.util.ArrayList;

/**
 * Task 8 - Searching Algorithms.
 * Implements Binary Search and Linear Search.
 */
public final class SearchAlgorithms {

    private SearchAlgorithms() {}

    /**
     * Searches for a Patient by ID using Binary Search (O(log n)).
     * Required by Scenario: "search patients by ID".
     * Note: List must be sorted by ID prior to search (or will be sorted on a copy).
     */
    public static Patient binarySearchPatientById(ArrayList<Patient> list, String targetId) {
        if (list == null || targetId == null || targetId.trim().isEmpty()) {
            return null;
        }

        // Ensure list is sorted by ID using Bubble Sort on a shallow copy
        ArrayList<Patient> sortedList = new ArrayList<>(list);
        SortAlgorithms.bubbleSortPatientsById(sortedList);

        int low = 0;
        int high = sortedList.size() - 1;
        String query = targetId.trim().toUpperCase();

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Patient midPatient = sortedList.get(mid);
            String midId = midPatient.getId().toUpperCase();

            int cmp = midId.compareTo(query);
            if (cmp == 0) {
                return midPatient; // Target found
            } else if (cmp < 0) {
                low = mid + 1; // Search right half
            } else {
                high = mid - 1; // Search left half
            }
        }
        return null; // Not found
    }

    /**
     * Searches for Patients using Linear Search (O(n)).
     * Matches against ID, Name, or Phone number (case-insensitive substring match).
     */
    public static ArrayList<Patient> linearSearchPatients(ArrayList<Patient> list, String query) {
        ArrayList<Patient> results = new ArrayList<>();
        if (list == null || query == null || query.trim().isEmpty()) {
            return results;
        }

        String lowerQuery = query.trim().toLowerCase();
        for (Patient p : list) {
            if (p.getId().toLowerCase().contains(lowerQuery) ||
                p.getName().toLowerCase().contains(lowerQuery) ||
                p.getPhone().toLowerCase().contains(lowerQuery)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Searches for Appointments by ID, Patient ID, or Doctor ID using Linear Search.
     */
    public static ArrayList<Appointment> linearSearchAppointments(ArrayList<Appointment> list, String query) {
        ArrayList<Appointment> results = new ArrayList<>();
        if (list == null || query == null || query.trim().isEmpty()) {
            return results;
        }

        String lowerQuery = query.trim().toLowerCase();
        for (Appointment a : list) {
            if (a.getAppointmentId().toLowerCase().contains(lowerQuery) ||
                a.getPatientId().toLowerCase().contains(lowerQuery) ||
                a.getDoctorId().toLowerCase().contains(lowerQuery) ||
                (a.getStatus() != null && a.getStatus().toLowerCase().contains(lowerQuery))) {
                results.add(a);
            }
        }
        return results;
    }
}