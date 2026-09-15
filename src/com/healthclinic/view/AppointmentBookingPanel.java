package com.healthclinic.view;

import com.healthclinic.controller.AppointmentController;
import com.healthclinic.controller.ClinicController;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Doctor;
import com.healthclinic.model.Patient;
import com.healthclinic.util.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Task 5 - Appointment Booking Screen.
 * Provides appointment scheduling, cancellation, status tracking,
 * searching, and Task 8 Quick Sort by Date.
 */
public class AppointmentBookingPanel extends JPanel {

    private final ClinicController clinicController;
    private final AppointmentController appointmentController;

    private JTextField txtApptId;
    private JComboBox<String> cmbPatients;
    private JComboBox<String> cmbDoctors;
    private JTextField txtDateTime; // YYYY-MM-DD HH:mm
    private JTextField txtNotes;
    private JComboBox<String> cmbStatus;

    private JTextField txtSearch;
    private JTable apptTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentBookingPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.appointmentController = clinicController.getAppointmentController();

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        refreshDropdowns();
        refreshTable();
    }

    private void initUI() {
        // Top Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Appointment Scheduling & Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new BorderLayout(15, 15));
        splitPanel.setOpaque(false);

        // --- LEFT: BOOKING FORM ---
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(10, 10));
        formCard.setPreferredSize(new Dimension(360, 480));

        JLabel lblFormTitle = new JLabel("Schedule New Appointment");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 8, 12));
        fieldsPanel.setOpaque(false);

        txtApptId = new JTextField();
        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtDateTime = new JTextField(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).format(DTF));
        cmbStatus = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});
        txtNotes = new JTextField();

        fieldsPanel.add(new JLabel("Appointment ID: *"));
        fieldsPanel.add(txtApptId);
        fieldsPanel.add(new JLabel("Select Patient: *"));
        fieldsPanel.add(cmbPatients);
        fieldsPanel.add(new JLabel("Select Doctor: *"));
        fieldsPanel.add(cmbDoctors);
        fieldsPanel.add(new JLabel("Date & Time: *"));
        fieldsPanel.add(txtDateTime);
        fieldsPanel.add(new JLabel("Status:"));
        fieldsPanel.add(cmbStatus);
        fieldsPanel.add(new JLabel("Reason / Notes:"));
        fieldsPanel.add(txtNotes);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnCol = new JPanel(new GridLayout(3, 1, 6, 8));
        btnCol.setOpaque(false);

        JButton btnBook = UITheme.createPrimaryButton("Book Appointment");
        btnBook.addActionListener(e -> onBook());

        JPanel statusBtnRow = new JPanel(new GridLayout(1, 2, 6, 0));
        statusBtnRow.setOpaque(false);
        JButton btnComplete = UITheme.createAccentButton("Mark Completed");
        btnComplete.addActionListener(e -> onUpdateStatus("COMPLETED"));
        JButton btnCancelAppt = UITheme.createSecondaryButton("Mark Cancelled");
        btnCancelAppt.addActionListener(e -> onUpdateStatus("CANCELLED"));
        statusBtnRow.add(btnComplete);
        statusBtnRow.add(btnCancelAppt);

        JButton btnClear = UITheme.createSecondaryButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());

        btnCol.add(btnBook);
        btnCol.add(statusBtnRow);
        btnCol.add(btnClear);

        formCard.add(btnCol, BorderLayout.SOUTH);
        splitPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT: APPOINTMENTS TABLE & SORT/SEARCH CONTROLS ---
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(10, 10));

        // Control Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);

        JButton btnSortQuick = UITheme.createAccentButton("Sort by Date (Quick Sort)");
        btnSortQuick.setToolTipText("Sorts appointments chronologically using O(n log n) Quick Sort algorithm");
        btnSortQuick.addActionListener(e -> onQuickSort());
        toolbar.add(btnSortQuick);

        toolbar.add(new JLabel("  Filter:"));
        txtSearch = new JTextField(10);
        toolbar.add(txtSearch);

        JButton btnSearch = UITheme.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> onSearch());
        toolbar.add(btnSearch);

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh All");
        btnRefresh.addActionListener(e -> {
            refreshDropdowns();
            refreshTable();
        });
        toolbar.add(btnRefresh);

        rightCard.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Appt ID", "Patient", "Doctor", "Date & Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        apptTable = new JTable(tableModel);
        apptTable.setRowHeight(24);
        apptTable.setFont(UITheme.FONT_REGULAR);
        apptTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        apptTable.getTableHeader().setBackground(UITheme.PRIMARY_LIGHT);

        apptTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = apptTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtApptId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtDateTime.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
                txtNotes.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
            }
        });

        JScrollPane scrollPane = new JScrollPane(apptTable);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRow.setOpaque(false);

        JButton btnDelete = UITheme.createDangerButton("Delete Selected Appointment");
        btnDelete.addActionListener(e -> onDelete());
        bottomRow.add(btnDelete);

        rightCard.add(bottomRow, BorderLayout.SOUTH);
        splitPanel.add(rightCard, BorderLayout.CENTER);

        add(splitPanel, BorderLayout.CENTER);
    }

    public void refreshDropdowns() {
        cmbPatients.removeAllItems();
        for (Patient p : clinicController.getClinic().getPatients()) {
            cmbPatients.addItem(p.getId() + " - " + p.getName());
        }

        cmbDoctors.removeAllItems();
        for (Doctor d : clinicController.getClinic().getDoctors()) {
            cmbDoctors.addItem(d.getId() + " - Dr. " + d.getName() + " (" + d.getSpecialization() + ")");
        }
    }

    private void onBook() {
        try {
            String patientSelection = (String) cmbPatients.getSelectedItem();
            String doctorSelection = (String) cmbDoctors.getSelectedItem();

            if (patientSelection == null || doctorSelection == null) {
                JOptionPane.showMessageDialog(this, "Please ensure at least one patient and doctor exist.", "Missing Entity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String patientId = patientSelection.split(" - ")[0].trim();
            String doctorId = doctorSelection.split(" - ")[0].trim();

            appointmentController.bookAppointment(
                    txtApptId.getText(),
                    patientId,
                    doctorId,
                    txtDateTime.getText(),
                    txtNotes.getText()
            );

            JOptionPane.showMessageDialog(this, "Appointment scheduled and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateStatus(String newStatus) {
        int selectedRow = apptTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            appointmentController.updateAppointmentStatus(id, newStatus);
            JOptionPane.showMessageDialog(this, "Appointment " + id + " marked as " + newStatus + ".", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = apptTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Appointment " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentController.deleteAppointment(id);
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onQuickSort() {
        long start = System.nanoTime();
        ArrayList<Appointment> sorted = appointmentController.getAppointmentsSortedByDate();
        long duration = System.nanoTime() - start;

        tableModel.setRowCount(0);
        for (Appointment a : sorted) {
            addAppointmentRow(a);
        }

        JOptionPane.showMessageDialog(this,
                "Quick Sort completed in " + (duration / 1000.0) + " μs!\n" +
                "All " + sorted.size() + " appointments are now ordered chronologically by date/time.",
                "Quick Sort Executed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onSearch() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            refreshTable();
            return;
        }
        ArrayList<Appointment> results = appointmentController.searchAppointments(q);
        tableModel.setRowCount(0);
        for (Appointment a : results) {
            addAppointmentRow(a);
        }
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments matched your query: " + q, "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Appointment a : appointmentController.getAllAppointments()) {
            addAppointmentRow(a);
        }
    }

    private void addAppointmentRow(Appointment a) {
        Clinic clinic = clinicController.getClinic();
        Patient p = clinic.findPatientById(a.getPatientId());
        Doctor d = clinic.findDoctorById(a.getDoctorId());

        String pName = p != null ? p.getName() + " (" + a.getPatientId() + ")" : a.getPatientId();
        String dName = d != null ? "Dr. " + d.getName() + " (" + a.getDoctorId() + ")" : a.getDoctorId();
        String dtStr = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(DTF) : "N/A";

        tableModel.addRow(new Object[]{
                a.getAppointmentId(),
                pName,
                dName,
                dtStr,
                a.getStatus(),
                a.getNotes()
        });
    }

    private void clearForm() {
        txtApptId.setText("");
        txtNotes.setText("");
        txtDateTime.setText(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).format(DTF));
        cmbStatus.setSelectedIndex(0);
        apptTable.clearSelection();
    }
}