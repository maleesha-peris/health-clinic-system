package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Task 5 - Main Menu / Dashboard Screen.
 * Provides high-level clinic metrics, quick action navigation, and recent appointments overview.
 */
public class DashboardPanel extends JPanel {

    private final ClinicController clinicController;
    private final MainFrame mainFrame;

    private JLabel lblTotalPatients;
    private JLabel lblTotalDoctors;
    private JLabel lblScheduledAppts;
    private JLabel lblCompletedTreatments;

    private JTable recentTable;
    private DefaultTableModel recentModel;

    public DashboardPanel(ClinicController clinicController, MainFrame mainFrame) {
        this.clinicController = clinicController;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initUI();
        refreshData();
    }

    private void initUI() {
        // Top Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Clinic Overview & Main Menu");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Community Health Clinic • Patient Appointments & Medical Records Management");
        lblSubtitle.setFont(UITheme.FONT_REGULAR);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        // KPI Metric Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setOpaque(false);

        lblTotalPatients = new JLabel("0", SwingConstants.CENTER);
        lblTotalDoctors = new JLabel("0", SwingConstants.CENTER);
        lblScheduledAppts = new JLabel("0", SwingConstants.CENTER);
        lblCompletedTreatments = new JLabel("0", SwingConstants.CENTER);

        metricsPanel.add(createMetricCard("Registered Patients", lblTotalPatients, UITheme.PRIMARY));
        metricsPanel.add(createMetricCard("Active Doctors", lblTotalDoctors, UITheme.ACCENT));
        metricsPanel.add(createMetricCard("Scheduled Appts", lblScheduledAppts, UITheme.WARNING));
        metricsPanel.add(createMetricCard("Completed Treatments", lblCompletedTreatments, UITheme.SUCCESS));

        centerPanel.add(metricsPanel, BorderLayout.NORTH);

        // Quick Actions & Recent Appointments Split
        JPanel contentGrid = new JPanel(new BorderLayout(15, 15));
        contentGrid.setOpaque(false);

        // Left: Quick Action Navigation Buttons
        JPanel actionCard = UITheme.createCardPanel();
        actionCard.setLayout(new GridLayout(4, 1, 10, 10));
        actionCard.setPreferredSize(new Dimension(280, 200));

        JLabel lblActions = new JLabel("Quick Navigation", SwingConstants.LEFT);
        lblActions.setFont(UITheme.FONT_HEADER);

        JButton btnGoPatient = UITheme.createPrimaryButton("Register New Patient");
        btnGoPatient.addActionListener(e -> mainFrame.showView("PATIENTS"));

        JButton btnGoDoctor = UITheme.createSecondaryButton("Register New Doctor");
        btnGoDoctor.addActionListener(e -> mainFrame.showView("DOCTORS"));

        JButton btnGoAppt = UITheme.createAccentButton("Book Appointment");
        btnGoAppt.addActionListener(e -> mainFrame.showView("APPOINTMENTS"));

        JButton btnGoReports = UITheme.createSecondaryButton("View Reports & Schedules");
        btnGoReports.addActionListener(e -> mainFrame.showView("REPORTS"));

        actionCard.add(btnGoPatient);
        actionCard.add(btnGoDoctor);
        actionCard.add(btnGoAppt);
        actionCard.add(btnGoReports);

        JPanel leftWrap = new JPanel(new BorderLayout());
        leftWrap.setOpaque(false);
        leftWrap.add(lblActions, BorderLayout.NORTH);
        leftWrap.add(actionCard, BorderLayout.CENTER);

        contentGrid.add(leftWrap, BorderLayout.WEST);

        // Right: Recent Appointments Table
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        JLabel lblRecent = new JLabel("Upcoming Appointments Schedule", SwingConstants.LEFT);
        lblRecent.setFont(UITheme.FONT_HEADER);

        String[] cols = {"Appt ID", "Date & Time", "Patient", "Doctor", "Status"};
        recentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTable = new JTable(recentModel);
        recentTable.setFont(UITheme.FONT_REGULAR);
        recentTable.setRowHeight(26);
        recentTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        recentTable.getTableHeader().setBackground(UITheme.PRIMARY_LIGHT);

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        tableCard.add(lblRecent, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentGrid.add(tableCard, BorderLayout.CENTER);
        centerPanel.add(contentGrid, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_REGULAR);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        Clinic clinic = clinicController.getClinic();
        lblTotalPatients.setText(String.valueOf(clinic.getPatients().size()));
        lblTotalDoctors.setText(String.valueOf(clinic.getDoctors().size()));

        long scheduled = clinic.getAppointments().stream()
                .filter(a -> "SCHEDULED".equalsIgnoreCase(a.getStatus()))
                .count();
        lblScheduledAppts.setText(String.valueOf(scheduled));

        lblCompletedTreatments.setText(String.valueOf(clinic.getTreatments().size()));

        // Populate table
        recentModel.setRowCount(0);
        ArrayList<Appointment> sorted = clinicController.getAppointmentController().getAppointmentsSortedByDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int limit = Math.min(10, sorted.size());
        for (int i = 0; i < limit; i++) {
            Appointment a = sorted.get(i);
            var p = clinic.findPatientById(a.getPatientId());
            var d = clinic.findDoctorById(a.getDoctorId());

            String pName = p != null ? p.getName() + " (" + a.getPatientId() + ")" : a.getPatientId();
            String dName = d != null ? "Dr. " + d.getName() : a.getDoctorId();
            String dtStr = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(dtf) : "N/A";

            recentModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    dtStr,
                    pName,
                    dName,
                    a.getStatus()
            });
        }
    }
}