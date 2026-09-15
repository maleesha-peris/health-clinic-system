package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.ReportController;
import com.healthclinic.model.Doctor;
import com.healthclinic.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Task 5 - Reports Screen.
 * Generates Appointment Reports, Doctor Schedules, and clinic statistics.
 */
public class ReportsPanel extends JPanel {

    private final ClinicController clinicController;
    private final ReportController reportController;

    private JComboBox<String> cmbReportType;
    private JComboBox<String> cmbDoctorFilter;
    private JTextField txtFromDate;
    private JTextField txtToDate;
    private JTextArea txtReportOutput;

    public ReportsPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.reportController = clinicController.getReportController();

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 18, 18, 18));

        initUI();
        refreshDoctorFilter();
        generateReport();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Clinic Reports & Doctor Schedules");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Filter Card
        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

        filterCard.add(createFieldLabel("Report Type:"));
        cmbReportType = new JComboBox<>(new String[]{
                "Appointment Report",
                "Doctor Schedules",
                "Clinic Summary Statistics"
        });
        filterCard.add(cmbReportType);

        filterCard.add(createFieldLabel("Doctor Filter:"));
        cmbDoctorFilter = new JComboBox<>();
        filterCard.add(cmbDoctorFilter);

        filterCard.add(createFieldLabel("From:"));
        txtFromDate = createTextField();
        txtFromDate.setText(LocalDate.now().minusDays(30).toString());
        txtFromDate.setPreferredSize(new Dimension(95, 32));
        filterCard.add(txtFromDate);

        filterCard.add(createFieldLabel("To:"));
        txtToDate = createTextField();
        txtToDate.setText(LocalDate.now().plusDays(30).toString());
        txtToDate.setPreferredSize(new Dimension(95, 32));
        filterCard.add(txtToDate);

        ModernButton btnGenerate = new ModernButton("Generate Report", IconFactory.createReportIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnGenerate.addActionListener(e -> generateReport());
        filterCard.add(btnGenerate);

        ModernButton btnExport = new ModernButton("Export (.txt)", ModernButton.ButtonStyle.ACCENT);
        btnExport.addActionListener(e -> exportReport());
        filterCard.add(btnExport);

        add(filterCard, BorderLayout.NORTH);

        // Output Area
        JPanel reportCard = UITheme.createCardPanel();
        reportCard.setLayout(new BorderLayout(10, 10));

        JLabel lblPreview = new JLabel("Report Preview", SwingConstants.LEFT);
        lblPreview.setFont(UITheme.FONT_HEADER);
        lblPreview.setForeground(UITheme.TEXT_PRIMARY);
        reportCard.add(lblPreview, BorderLayout.NORTH);

        txtReportOutput = new JTextArea();
        txtReportOutput.setEditable(false);
        txtReportOutput.setFont(UITheme.FONT_MONO);
        txtReportOutput.setBackground(new Color(250, 250, 250));
        txtReportOutput.setMargin(new Insets(14, 14, 14, 14));

        JScrollPane scrollPane = new JScrollPane(txtReportOutput);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        reportCard.add(scrollPane, BorderLayout.CENTER);

        add(reportCard, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_PRIMARY);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    public void refreshDoctorFilter() {
        cmbDoctorFilter.removeAllItems();
        cmbDoctorFilter.addItem("-- All Doctors --");
        for (Doctor d : clinicController.getClinic().getDoctors()) {
            cmbDoctorFilter.addItem(d.getId() + " - Dr. " + d.getName());
        }
    }

    private void generateReport() {
        String reportType = (String) cmbReportType.getSelectedItem();
        String docSel = (String) cmbDoctorFilter.getSelectedItem();
        String doctorId = (docSel != null && !docSel.startsWith("--")) ? docSel.split(" - ")[0].trim() : null;

        LocalDate from = null;
        LocalDate to = null;

        try {
            if (!txtFromDate.getText().trim().isEmpty()) {
                from = ValidationUtil.parseAndValidateDate(txtFromDate.getText().trim(), "From Date");
            }
            if (!txtToDate.getText().trim().isEmpty()) {
                to = ValidationUtil.parseAndValidateDate(txtToDate.getText().trim(), "To Date");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Date format error: " + ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Appointment Report".equals(reportType)) {
            String report = reportController.generateAppointmentReport(from, to, doctorId);
            txtReportOutput.setText(report);
        } else if ("Doctor Schedules".equals(reportType)) {
            String report = reportController.generateDoctorScheduleReport(doctorId);
            txtReportOutput.setText(report);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================================================================\n");
            sb.append("                         COMMUNITY HEALTH CLINIC - EXECUTIVE SUMMARY                   \n");
            sb.append("========================================================================================\n");
            sb.append("Generated On: ").append(LocalDate.now()).append("\n\n");
            sb.append(reportController.getClinicStatistics()).append("\n\n");
            sb.append("========================================================================================\n");
            txtReportOutput.setText(sb.toString());
        }
        txtReportOutput.setCaretPosition(0);
    }

    private void exportReport() {
        String content = txtReportOutput.getText();
        if (content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report content to export.", "Empty Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("clinic_report_" + System.currentTimeMillis() + ".txt"));
        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File target = fileChooser.getSelectedFile();
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8)) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Report exported successfully to: " + target.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}