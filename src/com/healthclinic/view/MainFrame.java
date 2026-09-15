package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.data.DataPersistenceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Task 5 - Main Desktop Application Frame.
 * Modern UI with responsive sidebar navigation, top search bar,
 * status indicator, and CardLayout view transitions.
 */
public class MainFrame extends JFrame {

    private final ClinicController clinicController;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel lblStatus;
    private JTextField txtGlobalSearch;

    // View Panels
    private DashboardPanel dashboardPanel;
    private PatientManagementPanel patientPanel;
    private DoctorManagementPanel doctorPanel;
    private AppointmentBookingPanel appointmentPanel;
    private TreatmentEntryPanel treatmentPanel;
    private ReportsPanel reportsPanel;

    // Modern Nav buttons
    private ModernButton btnNavDash;
    private ModernButton btnNavPatients;
    private ModernButton btnNavDoctors;
    private ModernButton btnNavAppts;
    private ModernButton btnNavTreatments;
    private ModernButton btnNavReports;

    public MainFrame(ClinicController clinicController) {
        this.clinicController = clinicController;

        setTitle("HealthClinic - Community Care System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 820);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        initMenuBar();
        initLayout();
        showView("DASHBOARD");
        updateStatusBar("Ready. System initialized with local File I/O persistence.");
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));

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

        // Screens Menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem navDash = new JMenuItem("Dashboard");
        navDash.addActionListener(e -> showView("DASHBOARD"));
        JMenuItem navPat = new JMenuItem("Patient Register");
        navPat.addActionListener(e -> showView("PATIENTS"));
        JMenuItem navDoc = new JMenuItem("Doctor Register");
        navDoc.addActionListener(e -> showView("DOCTORS"));
        JMenuItem navAppt = new JMenuItem("Book Appointment");
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
        JMenuItem itemAbout = new JMenuItem("About HealthClinic");
        itemAbout.addActionListener(e -> showAboutDialog());
        helpMenu.add(itemAbout);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initLayout() {
        getContentPane().setLayout(new BorderLayout());

        // 1. LEFT MODERN SIDEBAR (Gradient background)
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.SIDEBAR_BG_START, 0, getHeight(), UITheme.SIDEBAR_BG_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 700));

        // Brand Banner
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 20));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(IconFactory.createLogoIcon(32));
        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);

        JLabel lblBrandTitle = new JLabel("HealthClinic");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBrandTitle.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Community Care");
        lblBrandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBrandSub.setForeground(new Color(200, 230, 255));

        brandText.add(lblBrandTitle);
        brandText.add(lblBrandSub);

        brandPanel.add(lblLogo);
        brandPanel.add(brandText);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Stack
        JPanel navCenter = new JPanel();
        navCenter.setOpaque(false);
        navCenter.setLayout(new BoxLayout(navCenter, BoxLayout.Y_AXIS));
        navCenter.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblMenuHead = new JLabel("MAIN MENU");
        lblMenuHead.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenuHead.setForeground(new Color(180, 215, 240, 180));
        lblMenuHead.setBorder(new EmptyBorder(6, 12, 6, 0));
        lblMenuHead.setAlignmentX(Component.LEFT_ALIGNMENT);
        navCenter.add(lblMenuHead);

        btnNavDash = new ModernButton("Dashboard", IconFactory.createDashboardIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnNavPatients = new ModernButton("Patient Register", IconFactory.createPatientIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnNavDoctors = new ModernButton("Doctor Register", IconFactory.createDoctorIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnNavAppts = new ModernButton("Book Appointment", IconFactory.createCalendarIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnNavTreatments = new ModernButton("Treatment Entry", IconFactory.createPillIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnNavReports = new ModernButton("Reports & Rosters", IconFactory.createReportIcon(16, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);

        Dimension btnSize = new Dimension(206, 42);
        for (ModernButton b : new ModernButton[]{btnNavDash, btnNavPatients, btnNavDoctors, btnNavAppts, btnNavTreatments, btnNavReports}) {
            b.setMaximumSize(btnSize);
            b.setPreferredSize(btnSize);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        btnNavDash.addActionListener(e -> showView("DASHBOARD"));
        btnNavPatients.addActionListener(e -> showView("PATIENTS"));
        btnNavDoctors.addActionListener(e -> showView("DOCTORS"));
        btnNavAppts.addActionListener(e -> showView("APPOINTMENTS"));
        btnNavTreatments.addActionListener(e -> showView("TREATMENTS"));
        btnNavReports.addActionListener(e -> showView("REPORTS"));

        navCenter.add(btnNavDash);
        navCenter.add(Box.createVerticalStrut(4));
        navCenter.add(btnNavPatients);
        navCenter.add(Box.createVerticalStrut(4));
        navCenter.add(btnNavDoctors);
        navCenter.add(Box.createVerticalStrut(4));
        navCenter.add(btnNavAppts);
        navCenter.add(Box.createVerticalStrut(4));
        navCenter.add(btnNavTreatments);
        navCenter.add(Box.createVerticalStrut(4));
        navCenter.add(btnNavReports);

        navCenter.add(Box.createVerticalStrut(15));
        JLabel lblDataHead = new JLabel("DATA MANAGEMENT");
        lblDataHead.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblDataHead.setForeground(new Color(180, 215, 240, 180));
        lblDataHead.setBorder(new EmptyBorder(6, 12, 6, 0));
        lblDataHead.setAlignmentX(Component.LEFT_ALIGNMENT);
        navCenter.add(lblDataHead);

        ModernButton btnQuickSave = new ModernButton("Save All to Files", IconFactory.createReportIcon(14, Color.WHITE), ModernButton.ButtonStyle.SIDEBAR);
        btnQuickSave.setMaximumSize(btnSize);
        btnQuickSave.setPreferredSize(btnSize);
        btnQuickSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnQuickSave.addActionListener(e -> onSaveAll());
        navCenter.add(btnQuickSave);

        sidebar.add(navCenter, BorderLayout.CENTER);
        getContentPane().add(sidebar, BorderLayout.WEST);

        // 2. TOP APP BAR & CENTER CONTAINER
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(UITheme.BG_MAIN);

        // Top App Bar
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setPreferredSize(new Dimension(1000, 52));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 20, 8, 20)
        ));

        // Global Search on left of Top Bar (matching reference image)
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBox.setOpaque(false);
        JLabel searchIcon = new JLabel(IconFactory.createSearchIcon(16, UITheme.TEXT_MUTED));
        txtGlobalSearch = new JTextField("Search patients, appointments, doctors...", 25);
        txtGlobalSearch.setFont(UITheme.FONT_REGULAR);
        txtGlobalSearch.setForeground(UITheme.TEXT_MUTED);
        txtGlobalSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        txtGlobalSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtGlobalSearch.getText().startsWith("Search")) {
                    txtGlobalSearch.setText("");
                    txtGlobalSearch.setForeground(UITheme.TEXT_PRIMARY);
                }
            }
        });
        txtGlobalSearch.addActionListener(e -> onGlobalSearch(txtGlobalSearch.getText().trim()));
        searchBox.add(searchIcon);
        searchBox.add(txtGlobalSearch);
        topBar.add(searchBox, BorderLayout.WEST);

        // Right side info badges
        JPanel topBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topBarRight.setOpaque(false);

        JLabel lblDate = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        lblDate.setFont(UITheme.FONT_BOLD);
        lblDate.setForeground(UITheme.TEXT_MUTED);

        JLabel lblRoleBadge = new JLabel(" Administrator ");
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRoleBadge.setOpaque(true);
        lblRoleBadge.setBackground(new Color(237, 242, 247));
        lblRoleBadge.setForeground(UITheme.PRIMARY);
        lblRoleBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        topBarRight.add(lblDate);
        topBarRight.add(lblRoleBadge);
        topBar.add(topBarRight, BorderLayout.EAST);

        centerContainer.add(topBar, BorderLayout.NORTH);

        // 3. MAIN CONTENT (CardLayout Views)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.BG_MAIN);

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

        centerContainer.add(mainContentPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(1000, 26));
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        lblStatus = new JLabel(" Ready");
        lblStatus.setFont(UITheme.FONT_REGULAR);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(lblStatus, BorderLayout.WEST);

        JLabel lblClinicName = new JLabel("Community Health Clinic • Standard MVC  ");
        lblClinicName.setFont(UITheme.FONT_REGULAR);
        lblClinicName.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(lblClinicName, BorderLayout.EAST);

        centerContainer.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(centerContainer, BorderLayout.CENTER);
    }

    public void showView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);

        // Reset all buttons
        for (ModernButton b : new ModernButton[]{btnNavDash, btnNavPatients, btnNavDoctors, btnNavAppts, btnNavTreatments, btnNavReports}) {
            b.setActive(false);
        }

        if ("DASHBOARD".equals(viewName)) {
            btnNavDash.setActive(true);
            dashboardPanel.refreshData();
        } else if ("PATIENTS".equals(viewName)) {
            btnNavPatients.setActive(true);
            patientPanel.refreshTable();
        } else if ("DOCTORS".equals(viewName)) {
            btnNavDoctors.setActive(true);
            doctorPanel.refreshTable();
        } else if ("APPOINTMENTS".equals(viewName)) {
            btnNavAppts.setActive(true);
            appointmentPanel.refreshDropdowns();
            appointmentPanel.refreshTable();
        } else if ("TREATMENTS".equals(viewName)) {
            btnNavTreatments.setActive(true);
            treatmentPanel.refreshDropdowns();
            treatmentPanel.refreshTable();
        } else if ("REPORTS".equals(viewName)) {
            btnNavReports.setActive(true);
            reportsPanel.refreshDoctorFilter();
        }

        updateStatusBar("Active Screen: " + viewName);
    }

    public void updateStatusBar(String text) {
        int pCount = clinicController.getClinic().getPatients().size();
        int dCount = clinicController.getClinic().getDoctors().size();
        int aCount = clinicController.getClinic().getAppointments().size();
        int tCount = clinicController.getClinic().getTreatments().size();

        lblStatus.setText(" " + text + " | Patients: " + pCount + " | Doctors: " + dCount +
                " | Appointments: " + aCount + " | Treatments: " + tCount);
    }

    private void onGlobalSearch(String query) {
        if (query.isEmpty() || query.startsWith("Search")) {
            return;
        }
        showView("PATIENTS");
    }

    private void onSaveAll() {
        try {
            clinicController.saveAllData();
            JOptionPane.showMessageDialog(this,
                    "All clinic records successfully saved to CSV files!",
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
            showView("DASHBOARD");
            JOptionPane.showMessageDialog(this,
                    "Clinic data reloaded from files.", "Data Reloaded", JOptionPane.INFORMATION_MESSAGE);
            updateStatusBar("Data refreshed from files.");
        } catch (DataPersistenceException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to reload data: " + e.getMessage(),
                    "Persistence Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "HealthClinic Management System (MVC)\n" +
                "Architecture: Strict MVC Pattern\n" +
                "Design: High Cohesion, Low Coupling, SOLID Principles\n" +
                "Persistence: File I/O (CSV CRUD)\n" +
                "Algorithms: Binary Search & Quick Sort\n\n" +
                "Developed for Community Health Clinics.",
                "About HealthClinic", JOptionPane.INFORMATION_MESSAGE);
    }
}