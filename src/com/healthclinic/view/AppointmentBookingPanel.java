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
 * Features appointment scheduling, Quick Sort by date, and status pill badges.
 */
public class AppointmentBookingPanel extends JPanel {

    private final ClinicController clinicController;
    private final AppointmentController appointmentController;

    private JTextField txtApptId;
    private JComboBox<String> cmbPatients;
    private JComboBox<String> cmbDoctors;
    private JTextField txtDateTime;
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
        setBorder(new EmptyBorder(16, 18, 18, 18));

        initUI();
        refreshDropdowns();
        refreshTable();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Appointment Booking & Scheduling");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new BorderLayout(15, 15));
        splitPanel.setOpaque(false);

        // --- LEFT: BOOKING FORM ---
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(12, 12));
        formCard.setPreferredSize(new Dimension(370, 490));

        JLabel lblFormTitle = new JLabel("Schedule Appointment");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 8, 12));
        fieldsPanel.setOpaque(false);

        txtApptId = createTextField();
        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtDateTime = createTextField();
        txtDateTime.setText(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).format(DTF));
        cmbStatus = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});
        txtNotes = createTextField();

        fieldsPanel.add(createFieldLabel("Appointment ID: *"));
        fieldsPanel.add(txtApptId);
        fieldsPanel.add(createFieldLabel("Select Patient: *"));
        fieldsPanel.add(cmbPatients);
        fieldsPanel.add(createFieldLabel("Select Doctor: *"));
        fieldsPanel.add(cmbDoctors);
        fieldsPanel.add(createFieldLabel("Date & Time: *"));
        fieldsPanel.add(txtDateTime);
        fieldsPanel.add(createFieldLabel("Status:"));
        fieldsPanel.add(cmbStatus);
        fieldsPanel.add(createFieldLabel("Reason / Notes:"));
        fieldsPanel.add(txtNotes);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnCol = new JPanel(new GridLayout(3, 1, 6, 8));
        btnCol.setOpaque(false);

        ModernButton btnBook = new ModernButton("Book Appointment", IconFactory.createCalendarIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnBook.addActionListener(e -> onBook());

        JPanel statusBtnRow = new JPanel(new GridLayout(1, 2, 6, 0));
        statusBtnRow.setOpaque(false);
        ModernButton btnComplete = new ModernButton("Mark Completed", ModernButton.ButtonStyle.SUCCESS);
        btnComplete.addActionListener(e -> onUpdateStatus("COMPLETED"));
        ModernButton btnCancelAppt = new ModernButton("Mark Cancelled", ModernButton.ButtonStyle.SECONDARY);
        btnCancelAppt.addActionListener(e -> onUpdateStatus("CANCELLED"));
        statusBtnRow.add(btnComplete);
        statusBtnRow.add(btnCancelAppt);

        ModernButton btnClear = new ModernButton("Clear Form", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        btnCol.add(btnBook);
        btnCol.add(statusBtnRow);
        btnCol.add(btnClear);

        formCard.add(btnCol, BorderLayout.SOUTH);
        splitPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT: APPOINTMENTS TABLE ---
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        ModernButton btnSortQuick = new ModernButton("Sort by Date (Quick Sort)", IconFactory.createSortIcon(14, Color.WHITE), ModernButton.ButtonStyle.ACCENT);
        btnSortQuick.setToolTipText("Sorts appointments chronologically using O(n log n) Quick Sort");
        btnSortQuick.addActionListener(e -> onQuickSort());
        toolbar.add(btnSortQuick);

        toolbar.add(new JLabel("  Filter:"));
        txtSearch = createTextField();
        txtSearch.setPreferredSize(new Dimension(110, 32));
        toolbar.add(txtSearch);

        ModernButton btnSearch = new ModernButton("Search", ModernButton.ButtonStyle.SECONDARY);
        btnSearch.addActionListener(e -> onSearch());
        toolbar.add(btnSearch);

        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.ButtonStyle.SECONDARY);
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
        UITheme.styleTable(apptTable);

        // Render Status with colored pill badges
        apptTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

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
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setOpaque(false);

        ModernButton btnDelete = new ModernButton("Delete Selected Appointment", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());
        bottomRow.add(btnDelete);

        rightCard.add(bottomRow, BorderLayout.SOUTH);
        splitPanel.add(rightCard, BorderLayout.CENTER);

        add(splitPanel, BorderLayout.CENTER);
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

            JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select an appointment first.", "Notice", JOptionPane.WARNING_MESSAGE);
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
                "Quick Sort executed in " + (duration / 1000.0) + " μs!\n" +
                "All " + sorted.size() + " appointments sorted chronologically.",
                "Quick Sort", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No appointments matched query: " + q, "Search Results", JOptionPane.INFORMATION_MESSAGE);
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