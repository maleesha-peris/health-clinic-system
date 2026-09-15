package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.data.DataPersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Task 5 - Main Desktop Application Frame.
 * Coordinates navigation across all views (Main Menu, Register Patient,
 * Register Doctor, Appointment Booking, Treatment Entry, Reports)
 * using CardLayout and modern sidebar navigation.
 */
public class MainFrame extends JFrame {

    private final ClinicController clinicController;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel lblStatus;

    // View Panels
    private DashboardPanel dashboardPanel;
    private PatientManagementPanel patientPanel;
    private DoctorManagementPanel doctorPanel;
    private AppointmentBookingPanel appointmentPanel;
    private TreatmentEntryPanel treatmentPanel;
    private ReportsPanel reportsPanel;

    // Nav buttons for active state highlighting
    private JButton btnNavDash;
    private JButton btnNavPatients;
    private JButton btnNavDoctors;
    private JButton btnNavAppts;
    private JButton btnNavTreatments;
    private JButton btnNavReports;

    public MainFrame(ClinicController clinicController) {
        this.clinicController = clinicController;

        setTitle("Community Health Clinic - Management System (MVC)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        initMenuBar();
        initLayout();
        showView("DASHBOARD");
        updateStatusBar("Ready. System initialized with local File I/O persistence.");
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem itemSave = new JMenuItem("Save All Data (CSV)");
        itemSave.addActionListener(e -> onSaveAll());

        JMenuItem itemReload = new JMenuItem("Reload Data from Files");
        itemReload.addActionListener(e -> onReloadAll());

        JMenuItem itemExit = new JMenuItem("Exit Application");
        itemExit.addActionListener(e -> System.exit(0));

        fileMenu.add(itemSave);
        fileMenu.add(itemReload);
        fileMenu.addSeparator();
        fileMenu.add(itemExit);

        // Navigation Menu
        JMenu navMenu = new JMenu("Screens");
        JMenuItem navDash = new JMenuItem("Main Menu / Dashboard");
        navDash.addActionListener(e -> showView("DASHBOARD"));
        JMenuItem navPat = new JMenuItem("Register Patient");
        navPat.addActionListener(e -> showView("PATIENTS"));
        JMenuItem navDoc = new JMenuItem("Register Doctor");
        navDoc.addActionListener(e -> showView("DOCTORS"));
        JMenuItem navAppt = new JMenuItem("Appointment Booking");
        navAppt.addActionListener(e -> showView("APPOINTMENTS"));
        JMenuItem navTreat = new JMenuItem("Treatment Entry");
        navTreat.addActionListener(e -> showView("TREATMENTS"));
        JMenuItem navRep = new JMenuItem("Reports & Schedules");
        navRep.addActionListener(e -> showView("REPORTS"));

        navMenu.add(navDash);
        navMenu.add(navPat);
        navMenu.add(navDoc);
        navMenu.add(navAppt);
        navMenu.add(navTreat);
        navMenu.add(navRep);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About System");
        itemAbout.addActionListener(e -> showAboutDialog());
        helpMenu.add(itemAbout);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initLayout() {
        getContentPane().setLayout(new BorderLayout());

        // Left Navigation Sidebar
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBackground(UITheme.PRIMARY_DARK);

        // Brand Banner
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblBrandTitle = new JLabel("HEALTH CLINIC");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrandTitle.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Community Care System");
        lblBrandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBrandSub.setForeground(new Color(200, 225, 255));

        brandPanel.add(lblBrandTitle, BorderLayout.NORTH);
        brandPanel.add(lblBrandSub, BorderLayout.SOUTH);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Nav Buttons Stack
        JPanel navButtons = new JPanel(new GridLayout(6, 1, 0, 4));
        navButtons.setOpaque(false);
        navButtons.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnNavDash = createNavButton(" Dashboard / Menu", "DASHBOARD");
        btnNavPatients = createNavButton(" Patients Register", "PATIENTS");
        btnNavDoctors = createNavButton(" Doctors Register", "DOCTORS");
        btnNavAppts = createNavButton(" Book Appointment", "APPOINTMENTS");
        btnNavTreatments = createNavButton(" Treatment Entry", "TREATMENTS");
        btnNavReports = createNavButton(" Reports & Rosters", "REPORTS");

        navButtons.add(btnNavDash);
        navButtons.add(btnNavPatients);
        navButtons.add(btnNavDoctors);
        navButtons.add(btnNavAppts);
        navButtons.add(btnNavTreatments);
        navButtons.add(btnNavReports);

        sidebar.add(navButtons, BorderLayout.CENTER);

        // Sidebar Footer
        JPanel sideFooter = new JPanel(new BorderLayout());
        sideFooter.setOpaque(false);
        sideFooter.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton btnQuickSave = UITheme.createAccentButton("Quick Save All");
        btnQuickSave.addActionListener(e -> onSaveAll());
        sideFooter.add(btnQuickSave, BorderLayout.CENTER);

        sidebar.add(sideFooter, BorderLayout.SOUTH);

        getContentPane().add(sidebar, BorderLayout.WEST);

        // Center: CardLayout Views
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel(clinicController, this);
        patientPanel = new PatientManagementPanel(clinicController);
        doctorPanel = new DoctorManagementPanel(clinicController);
        appointmentPanel = new AppointmentBookingPanel(clinicController);
        treatmentPanel = new TreatmentEntryPanel(clinicController);
        reportsPanel = new ReportsPanel(clinicController);

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(patientPanel, "PATIENTS");
        mainContentPanel.add(doctorPanel, "DOCTORS");
        mainContentPanel.add(appointmentPanel, "APPOINTMENTS");
        mainContentPanel.add(treatmentPanel, "TREATMENTS");
        mainContentPanel.add(reportsPanel, "REPORTS");

        getContentPane().add(mainContentPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(1000, 26));
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        lblStatus = new JLabel(" Ready");
        lblStatus.setFont(UITheme.FONT_REGULAR);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(lblStatus, BorderLayout.WEST);

        JLabel lblClinicName = new JLabel("Community Health Clinic • MVC Edition  ");
        lblClinicName.setFont(UITheme.FONT_REGULAR);
        lblClinicName.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(lblClinicName, BorderLayout.EAST);

        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    private JButton createNavButton(String title, String viewName) {
        JButton btn = new JButton(title);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showView(viewName));
        return btn;
    }

    public void showView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);

        // Highlight active button
        resetNavButtonStyles();
        if ("DASHBOARD".equals(viewName)) {
            highlightNavButton(btnNavDash);
            dashboardPanel.refreshData();
        } else if ("PATIENTS".equals(viewName)) {
            highlightNavButton(btnNavPatients);
            patientPanel.refreshTable();
        } else if ("DOCTORS".equals(viewName)) {
            highlightNavButton(btnNavDoctors);
            doctorPanel.refreshTable();
        } else if ("APPOINTMENTS".equals(viewName)) {
            highlightNavButton(btnNavAppts);
            appointmentPanel.refreshDropdowns();
            appointmentPanel.refreshTable();
        } else if ("TREATMENTS".equals(viewName)) {
            highlightNavButton(btnNavTreatments);
            treatmentPanel.refreshDropdowns();
            treatmentPanel.refreshTable();
        } else if ("REPORTS".equals(viewName)) {
            highlightNavButton(btnNavReports);
            reportsPanel.refreshDoctorFilter();
        }

        updateStatusBar("Active Screen: " + viewName);
    }

    private void resetNavButtonStyles() {
        JButton[] buttons = {btnNavDash, btnNavPatients, btnNavDoctors, btnNavAppts, btnNavTreatments, btnNavReports};
        for (JButton b : buttons) {
            b.setBackground(UITheme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        }
    }

    private void highlightNavButton(JButton btn) {
        btn.setBackground(UITheme.PRIMARY);
        btn.setForeground(Color.WHITE);
    }

    public void updateStatusBar(String text) {
        int pCount = clinicController.getClinic().getPatients().size();
        int dCount = clinicController.getClinic().getDoctors().size();
        int aCount = clinicController.getClinic().getAppointments().size();
        int tCount = clinicController.getClinic().getTreatments().size();

        lblStatus.setText(" " + text + " | [Stats: Patients: " + pCount + ", Doctors: " + dCount +
                ", Appts: " + aCount + ", Treatments: " + tCount + "]");
    }

    private void onSaveAll() {
        try {
            clinicController.saveAllData();
            JOptionPane.showMessageDialog(this,
                    "All clinic data saved successfully to CSV files in the 'data/' directory!",
                    "Data Saved", JOptionPane.INFORMATION_MESSAGE);
            updateStatusBar("Data successfully persisted to CSV files.");
        } catch (DataPersistenceException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save data: " + e.getMessage(),
                    "Persistence Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onReloadAll() {
        try {
            clinicController.reloadAllData();
            dashboardPanel.refreshData();
            patientPanel.refreshTable();
            doctorPanel.refreshTable();
            appointmentPanel.refreshDropdowns();
            appointmentPanel.refreshTable();
            treatmentPanel.refreshDropdowns();
            treatmentPanel.refreshTable();
            reportsPanel.refreshDoctorFilter();

            JOptionPane.showMessageDialog(this,
                    "Data reloaded from disk.", "Data Reloaded", JOptionPane.INFORMATION_MESSAGE);
            updateStatusBar("Data refreshed from files.");
        } catch (DataPersistenceException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to reload data: " + e.getMessage(),
                    "Persistence Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Community Health Clinic Management System\n" +
                "Architecture: Model-View-Controller (MVC)\n" +
                "Principles: High Cohesion, Low Coupling, SOLID\n" +
                "Persistence: File I/O (CSV CRUD)\n" +
                "Algorithms: Binary/Linear Search & Quick/Bubble Sort\n\n" +
                "Developed for Desktop Java (Swing) Environment.",
                "About System", JOptionPane.INFORMATION_MESSAGE);
    }
}