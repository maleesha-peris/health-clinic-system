package com.healthclinic.util;

import com.healthclinic.model.Appointment;
import com.healthclinic.model.Patient;

import java.util.ArrayList;

/**
 * Task 8 - Sorting Algorithms.
 * Implements Quick Sort and Bubble Sort explicitly (plus Insertion Sort).
 */
public final class SortAlgorithms {

    private SortAlgorithms() {}

    /**
     * Sorts Appointments chronologically by appointmentDateTime using Quick Sort (O(n log n)).
     * Required by Scenario: "sort appointments by date".
     */
    public static void quickSortAppointmentsByDate(ArrayList<Appointment> list) {
        if (list == null || list.size() <= 1) {
            return;
        }
        quickSort(list, 0, list.size() - 1);
    }

    private static void quickSort(ArrayList<Appointment> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static int partition(ArrayList<Appointment> list, int low, int high) {
        Appointment pivot = list.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            // Compare dates
            Appointment current = list.get(j);
            if (current.compareTo(pivot) <= 0) {
                i++;
                swapAppointments(list, i, j);
            }
        }
        swapAppointments(list, i + 1, high);
        return i + 1;
    }

    private static void swapAppointments(ArrayList<Appointment> list, int i, int j) {
        Appointment temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Sorts Patients alphabetically/numerically by ID using Bubble Sort (O(n^2)).
     * Used to prepare lists for Binary Search by Patient ID.
     */
    public static void bubbleSortPatientsById(ArrayList<Patient> list) {
        if (list == null || list.size() <= 1) {
            return;
        }
        int n = list.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                String id1 = list.get(j).getId();
                String id2 = list.get(j + 1).getId();
                if (id1.compareToIgnoreCase(id2) > 0) {
                    Patient temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // Optimization: early exit if already sorted
            }
        }
    }

    /**
     * Alternative sorting algorithm: Insertion Sort for Appointments.
     */
    public static void insertionSortAppointmentsByDate(ArrayList<Appointment> list) {
        if (list == null || list.size() <= 1) {
            return;
        }
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            Appointment key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }
}