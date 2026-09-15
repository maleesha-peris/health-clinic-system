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
 * Task 5 - Reports & Doctor Rosters Screen.
 * Redesigned with clean controls, non-clipping layout, and 
 * a dedicated Download / Export report button.
 */
public class ReportsPanel extends JPanel {

    private final ClinicController clinicController;
    private final ReportController reportController;

    private JComboBox<String> cmbReportType;
    private JComboBox<String> cmbDoctorFilter;
    private JTextField txtFromDate;
    private JTextField txtToDate;
    private JTextArea txtReportOutput;
    private JLabel lblStatusBanner;

    public ReportsPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.reportController = clinicController.getReportController();

        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 18, 18, 18));

        initUI();
        refreshDoctorFilter();
        generateReport();
    }

    private void initUI() {
        // Top Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Clinic Reports & Doctor Schedules");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        titlePanel.add(lblTitle, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Center Wrapper
        JPanel centerWrapper = new JPanel(new BorderLayout(12, 12));
        centerWrapper.setOpaque(false);

        // --- FILTER & ACTION CONTROLS CARD ---
        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new BorderLayout(10, 10));

        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controlRow.setOpaque(false);

        controlRow.add(createLabel("Report Type:"));
        cmbReportType = new JComboBox<>(new String[]{
                "Appointment Report",
                "Doctor Schedules",
                "Clinic Summary Statistics"
        });
        controlRow.add(cmbReportType);

        controlRow.add(createLabel("Doctor Filter:"));
        cmbDoctorFilter = new JComboBox<>();
        controlRow.add(cmbDoctorFilter);

        controlRow.add(createLabel("From:"));
        txtFromDate = createTextField();
        txtFromDate.setText(LocalDate.now().minusDays(30).toString());
        txtFromDate.setPreferredSize(new Dimension(95, 32));
        controlRow.add(txtFromDate);

        controlRow.add(createLabel("To:"));
        txtToDate = createTextField();
        txtToDate.setText(LocalDate.now().plusDays(30).toString());
        txtToDate.setPreferredSize(new Dimension(95, 32));
        controlRow.add(txtToDate);

        ModernButton btnGenerate = new ModernButton("Generate Report", IconFactory.createReportIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnGenerate.addActionListener(e -> generateReport());
        controlRow.add(btnGenerate);

        ModernButton btnDownload = new ModernButton("Download Report (TXT)", ModernButton.ButtonStyle.ACCENT);
        btnDownload.setToolTipText("Save and download report to file");
        btnDownload.addActionListener(e -> downloadReport());
        controlRow.add(btnDownload);

        filterCard.add(controlRow, BorderLayout.CENTER);
        centerWrapper.add(filterCard, BorderLayout.NORTH);

        // --- REPORT PREVIEW CARD ---
        JPanel previewCard = UITheme.createCardPanel();
        previewCard.setLayout(new BorderLayout(10, 10));

        JPanel previewHeader = new JPanel(new BorderLayout());
        previewHeader.setOpaque(false);

        JLabel lblPreviewTitle = new JLabel("Generated Clinical Report Document");
        lblPreviewTitle.setFont(UITheme.FONT_HEADER);
        lblPreviewTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblStatusBanner = new JLabel("● Report ready for download / export");
        lblStatusBanner.setFont(UITheme.FONT_REGULAR);
        lblStatusBanner.setForeground(UITheme.SUCCESS);

        previewHeader.add(lblPreviewTitle, BorderLayout.WEST);
        previewHeader.add(lblStatusBanner, BorderLayout.EAST);
        previewCard.add(previewHeader, BorderLayout.NORTH);

        txtReportOutput = new JTextArea();
        txtReportOutput.setEditable(false);
        txtReportOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReportOutput.setBackground(new Color(250, 252, 255));
        txtReportOutput.setForeground(new Color(30, 41, 59));
        txtReportOutput.setMargin(new Insets(16, 16, 16, 16));

        JScrollPane scrollPane = new JScrollPane(txtReportOutput);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        previewCard.add(scrollPane, BorderLayout.CENTER);

        centerWrapper.add(previewCard, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
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
            JOptionPane.showMessageDialog(this, "Date error: " + ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Appointment Report".equals(reportType)) {
            String report = reportController.generateAppointmentReport(from, to, doctorId);
            txtReportOutput.setText(report);
            lblStatusBanner.setText("● Appointment report generated successfully");
        } else if ("Doctor Schedules".equals(reportType)) {
            String report = reportController.generateDoctorScheduleReport(doctorId);
            txtReportOutput.setText(report);
            lblStatusBanner.setText("● Doctor schedule roster generated successfully");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================================================================\n");
            sb.append("                         COMMUNITY HEALTH CLINIC - EXECUTIVE SUMMARY                   \n");
            sb.append("========================================================================================\n");
            sb.append("Generated On: ").append(LocalDate.now()).append("\n\n");
            sb.append(reportController.getClinicStatistics()).append("\n\n");
            sb.append("========================================================================================\n");
            txtReportOutput.setText(sb.toString());
            lblStatusBanner.setText("● Executive summary statistics ready");
        }
        txtReportOutput.setCaretPosition(0);
    }

    private void downloadReport() {
        String content = txtReportOutput.getText();
        if (content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a report first before downloading.", "Empty Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        String fileName = "Clinic_Report_" + System.currentTimeMillis() + ".txt";
        fileChooser.setSelectedFile(new File(fileName));

        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File target = fileChooser.getSelectedFile();
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8)) {
                writer.write(content);
                JOptionPane.showMessageDialog(this,
                        "Report successfully downloaded and saved!\nLocation: " + target.getAbsolutePath(),
                        "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                lblStatusBanner.setText("✓ Report downloaded to: " + target.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Download failed: " + ex.getMessage(), "Download Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}