package com.healthclinic.util;

import com.healthclinic.model.Appointment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Report and Data Export Utility.
 * Facilitates exporting clinical summaries, appointment schedules,
 * and roster reports to external text and CSV files on the local filesystem.
 */
public class ReportExporter {

    private static final String DEFAULT_EXPORT_DIR = "exports";
    private static final DateTimeFormatter FILE_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Exports raw text content (such as generated clinic reports) to a designated file.
     *
     * @param content         The report text content
     * @param destinationFile The target destination file
     * @throws IOException If file writing fails
     */
    public static void exportTextReport(String content, File destinationFile) throws IOException {
        if (destinationFile.getParentFile() != null && !destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            writer.write(content);
        }

        AuditLogger.log("EXPORT", "Exported report to file: " + destinationFile.getAbsolutePath());
    }

    /**
     * Generates a suggested export filename with a timestamp.
     *
     * @param prefix Base name for the report (e.g., "appointment_report")
     * @param extension Extension without dot (e.g., "txt" or "csv")
     * @return Formatted filename, e.g., "appointment_report_20260917_143000.txt"
     */
    public static String generateExportFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(FILE_TS_FORMAT);
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }

    /**
     * Gets or creates the default exports directory.
     */
    public static File getDefaultExportDirectory() {
        File dir = new File(DEFAULT_EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
