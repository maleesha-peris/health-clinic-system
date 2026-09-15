package com.healthclinic.controller;

import com.healthclinic.model.*;
import com.healthclinic.util.SortAlgorithms;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Controller for generating clinical reports and doctor schedules.
 */
public class ReportController {

    private final Clinic clinic;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter D_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportController(Clinic clinic) {
        this.clinic = clinic;
    }

    /**
     * Generates a comprehensive Appointment Report filtered optionally by date range and doctor.
     */
    public String generateAppointmentReport(LocalDate fromDate, LocalDate toDate, String doctorIdFilter) {
        ArrayList<Appointment> list = clinic.getAppointments();
        SortAlgorithms.quickSortAppointmentsByDate(list);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                         COMMUNITY HEALTH CLINIC - APPOINTMENT REPORT                   \n");
        sb.append("========================================================================================\n");
        sb.append("Generated on: ").append(LocalDate.now().format(D_FMT)).append("\n");
        if (fromDate != null && toDate != null) {
            sb.append("Period: ").append(fromDate.format(D_FMT)).append(" to ").append(toDate.format(D_FMT)).append("\n");
        }
        if (doctorIdFilter != null && !doctorIdFilter.isEmpty()) {
            Doctor doc = clinic.findDoctorById(doctorIdFilter);
            sb.append("Doctor Filter: ").append(doc != null ? doc.getName() : doctorIdFilter).append("\n");
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-18s %-16s %-18s %-12s %s\n",
                "Appt ID", "Date & Time", "Patient Name", "Doctor Name", "Status", "Notes"));
        sb.append("----------------------------------------------------------------------------------------\n");

        int count = 0;
        int scheduled = 0;
        int completed = 0;
        int cancelled = 0;

        for (Appointment a : list) {
            if (a.getAppointmentDateTime() != null) {
                LocalDate aDate = a.getAppointmentDateTime().toLocalDate();
                if (fromDate != null && aDate.isBefore(fromDate)) continue;
                if (toDate != null && aDate.isAfter(toDate)) continue;
            }
            if (doctorIdFilter != null && !doctorIdFilter.isEmpty() &&
                !a.getDoctorId().equalsIgnoreCase(doctorIdFilter)) {
                continue;
            }

            count++;
            if ("SCHEDULED".equalsIgnoreCase(a.getStatus())) scheduled++;
            else if ("COMPLETED".equalsIgnoreCase(a.getStatus())) completed++;
            else if ("CANCELLED".equalsIgnoreCase(a.getStatus())) cancelled++;

            Patient p = clinic.findPatientById(a.getPatientId());
            Doctor d = clinic.findDoctorById(a.getDoctorId());

            String pName = p != null ? p.getName() : a.getPatientId();
            String dName = d != null ? "Dr. " + d.getName() : a.getDoctorId();
            String dtStr = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(DT_FMT) : "N/A";

            sb.append(String.format("%-10s %-18s %-16s %-18s %-12s %s\n",
                    a.getAppointmentId(), dtStr, truncate(pName, 15), truncate(dName, 17), a.getStatus(), a.getNotes()));
        }

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("Total Appointments: %d | Scheduled: %d | Completed: %d | Cancelled: %d\n",
                count, scheduled, completed, cancelled));
        sb.append("========================================================================================\n");

        return sb.toString();
    }

    /**
     * Generates a Doctor Schedule Report.
     */
    public String generateDoctorScheduleReport(String doctorId) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                         COMMUNITY HEALTH CLINIC - DOCTOR SCHEDULE                      \n");
        sb.append("========================================================================================\n");

        ArrayList<Doctor> doctorsToReport = new ArrayList<>();
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            Doctor d = clinic.findDoctorById(doctorId.trim());
            if (d != null) {
                doctorsToReport.add(d);
            }
        } else {
            doctorsToReport.addAll(clinic.getDoctors());
        }

        if (doctorsToReport.isEmpty()) {
            sb.append("No doctor records found.\n");
            return sb.toString();
        }

        ArrayList<Appointment> allAppts = clinic.getAppointments();
        SortAlgorithms.quickSortAppointmentsByDate(allAppts);

        for (Doctor doc : doctorsToReport) {
            sb.append("DOCTOR: Dr. ").append(doc.getName()).append(" (ID: ").append(doc.getId()).append(")\n");
            sb.append("Specialization: ").append(doc.getSpecialization()).append(" | License: ").append(doc.getLicenseNumber()).append("\n");
            sb.append("Available Days: ").append(doc.getAvailableDays()).append(" | Consultation Fee: $").append(doc.getConsultationFee()).append("\n");
            sb.append("Contact: ").append(doc.getPhone()).append(" | Email: ").append(doc.getEmail()).append("\n");
            sb.append("Scheduled Appointments:\n");

            int docApptCount = 0;
            for (Appointment a : allAppts) {
                if (a.getDoctorId().equalsIgnoreCase(doc.getId())) {
                    docApptCount++;
                    Patient p = clinic.findPatientById(a.getPatientId());
                    String dtStr = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(DT_FMT) : "N/A";
                    String pInfo = p != null ? p.getName() + " (Phone: " + p.getPhone() + ")" : a.getPatientId();

                    sb.append(String.format("   [%s] Appt #%s - Patient: %s | Status: %s | Notes: %s\n",
                            dtStr, a.getAppointmentId(), pInfo, a.getStatus(), a.getNotes()));
                }
            }

            if (docApptCount == 0) {
                sb.append("   (No appointments currently scheduled for this doctor)\n");
            }
            sb.append("----------------------------------------------------------------------------------------\n");
        }

        return sb.toString();
    }

    /**
     * Computes high-level clinic metrics.
     */
    public String getClinicStatistics() {
        int totalPatients = clinic.getPatients().size();
        int totalDoctors = clinic.getDoctors().size();
        int totalAppointments = clinic.getAppointments().size();
        int scheduledAppts = 0;
        int completedAppts = 0;
        for (Appointment a : clinic.getAppointments()) {
            if ("SCHEDULED".equalsIgnoreCase(a.getStatus())) scheduledAppts++;
            else if ("COMPLETED".equalsIgnoreCase(a.getStatus())) completedAppts++;
        }
        int totalTreatments = clinic.getTreatments().size();
        double totalRevenue = 0.0;
        for (Treatment t : clinic.getTreatments()) {
            totalRevenue += t.getCost();
        }

        return String.format(
                "Patients: %d | Doctors: %d | Total Appointments: %d (Active: %d, Completed: %d) | Treatments: %d | Total Billed: $%.2f",
                totalPatients, totalDoctors, totalAppointments, scheduledAppts, completedAppts, totalTreatments, totalRevenue
        );
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 2) + "..";
    }
}