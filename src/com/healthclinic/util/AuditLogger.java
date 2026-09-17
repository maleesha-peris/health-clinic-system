package com.healthclinic.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Healthcare Regulatory Audit Logger.
 * Records clinical actions, patient registrations, appointment modifications,
 * and system security events with chronological timestamps for auditing.
 */
public class AuditLogger {

    private static final String AUDIT_LOG_PATH = "data/audit.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> sessionLogs = new ArrayList<>();

    /**
     * Records an audit log entry to memory and persists it to data/audit.log.
     *
     * @param category The functional category (e.g., PATIENT, APPOINTMENT, TREATMENT, SYSTEM)
     * @param action   Detailed description of the clinical or administrative action
     */
    public static synchronized void log(String category, String action) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = String.format("[%s] [%-11s] %s", timestamp, category.toUpperCase(), action);

        sessionLogs.add(entry);
        System.out.println("[AUDIT] " + entry);

        try {
            File file = new File(AUDIT_LOG_PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }

    /**
     * Returns an unmodifiable snapshot of logs recorded in the current runtime session.
     */
    public static synchronized List<String> getSessionLogs() {
        return new ArrayList<>(sessionLogs);
    }

    /**
     * Clears in-memory session logs (useful for test isolation).
     */
    public static synchronized void clearSessionLogs() {
        sessionLogs.clear();
    }
}
