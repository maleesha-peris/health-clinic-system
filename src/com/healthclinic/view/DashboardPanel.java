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
 * Features gradient metric cards, quick action buttons, and
 * a clean modern table with status badges (matching reference UI).
 */
public class DashboardPanel extends JPanel {

    private final ClinicController clinicController;
    private final MainFrame mainFrame;

    private GradientCardPanel cardPatients;
    private GradientCardPanel cardDoctors;
    private GradientCardPanel cardAppointments;

    private JTable recentTable;
    private DefaultTableModel recentModel;

    public DashboardPanel(ClinicController clinicController, MainFrame mainFrame) {
        this.clinicController = clinicController;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(18, 18));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(18, 20, 20, 20));

        initUI();
        refreshData();
    }

    private void initUI() {
        // --- TOP ROW: 3 GRADIENT METRIC CARDS (Exact match to reference design) ---
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(800, 135));

        // 1. Blue-Indigo Card
        cardPatients = new GradientCardPanel(
                "Total Registered Patients",
                "0",
                "Community clinic database",
                new Color(79, 110, 247), // #4F6EF7
                new Color(108, 92, 231), // #6C5CE7
                IconFactory.createPatientIcon(18, Color.WHITE)
        );

        // 2. Purple-Violet Card
        cardDoctors = new GradientCardPanel(
                "Active Clinic Doctors",
                "0",
                "Consultants & specialists",
                new Color(156, 39, 176), // #9C27B0
                new Color(186, 104, 200), // #BA68C8
                IconFactory.createDoctorIcon(18, Color.WHITE)
        );

        // 3. Golden-Amber Card
        cardAppointments = new GradientCardPanel(
                "Scheduled Appointments",
                "0",
                "Organized chronologically",
                new Color(230, 162, 25), // #E6A219
                new Color(246, 194, 62), // #F6C23E
                IconFactory.createCalendarIcon(18, Color.WHITE)
        );

        metricsPanel.add(cardPatients);
        metricsPanel.add(cardDoctors);
        metricsPanel.add(cardAppointments);

        add(metricsPanel, BorderLayout.NORTH);

        // --- CENTER: STANDARD TABLE DESIGN CARD (Matching reference image) ---
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(12, 12));

        // Header above table
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);

        JLabel lblTableHeader = new JLabel("Standard Table Design");
        lblTableHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTableHeader.setForeground(UITheme.TEXT_PRIMARY);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickActions.setOpaque(false);

        ModernButton btnBookQuick = new ModernButton("Book New Appointment", IconFactory.createCalendarIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnBookQuick.addActionListener(e -> mainFrame.showView("APPOINTMENTS"));

        ModernButton btnAddPatient = new ModernButton("Register Patient", IconFactory.createPatientIcon(14, Color.WHITE), ModernButton.ButtonStyle.ACCENT);
        btnAddPatient.addActionListener(e -> mainFrame.showView("PATIENTS"));

        quickActions.add(btnBookQuick);
        quickActions.add(btnAddPatient);

        tableHeaderPanel.add(lblTableHeader, BorderLayout.WEST);
        tableHeaderPanel.add(quickActions, BorderLayout.EAST);

        centerCard.add(tableHeaderPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Appt ID", "Patient Name", "Doctor Assigned", "Scheduled Date & Time", "Status"};
        recentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        recentTable = new JTable(recentModel);
        UITheme.styleTable(recentTable);

        // Apply Status Badge Renderer to the "Status" column (Column 4)
        recentTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        recentTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        centerCard.add(scrollPane, BorderLayout.CENTER);

        add(centerCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        Clinic clinic = clinicController.getClinic();

        int pCount = clinic.getPatients().size();
        int dCount = clinic.getDoctors().size();
        long scheduledCount = clinic.getAppointments().stream()
                .filter(a -> "SCHEDULED".equalsIgnoreCase(a.getStatus()))
                .count();

        cardPatients.setValue(String.valueOf(pCount));
        cardPatients.setSubtitle(pCount + " Registered active patients");

        cardDoctors.setValue(String.valueOf(dCount));
        cardDoctors.setSubtitle(dCount + " Consulting doctors on roster");

        cardAppointments.setValue(String.valueOf(scheduledCount));
        cardAppointments.setSubtitle(scheduledCount + " Pending upcoming sessions");

        // Populate table
        recentModel.setRowCount(0);
        ArrayList<Appointment> sorted = clinicController.getAppointmentController().getAppointmentsSortedByDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm");

        for (Appointment a : sorted) {
            var p = clinic.findPatientById(a.getPatientId());
            var d = clinic.findDoctorById(a.getDoctorId());

            String pName = p != null ? p.getName() + " (" + a.getPatientId() + ")" : a.getPatientId();
            String dName = d != null ? "Dr. " + d.getName() : a.getDoctorId();
            String dtStr = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(dtf) : "N/A";

            recentModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    pName,
                    dName,
                    dtStr,
                    a.getStatus()
            });
        }
    }
}