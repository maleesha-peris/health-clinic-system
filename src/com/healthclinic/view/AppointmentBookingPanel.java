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
 * Task 5 - Appointment Booking & Management.
 * Redesigned with clean Top-Table & Bottom-Form layout for spacious, sweet UI.
 * Features auto-generated sequential Appointment IDs and Quick Sort by date.
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

    private PlaceholderTextField txtSearch;
    private JTable apptTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentBookingPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.appointmentController = clinicController.getAppointmentController();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        refreshDropdowns();
        refreshTable();
        loadNextAutoId();
    }

    private void initUI() {
        // --- TOP SECTION: DIRECTORY TABLE ---
        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new BorderLayout(10, 10));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Scheduled Appointments Roster");
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        ModernButton btnSortQuick = new ModernButton("Sort by Date (Quick Sort)", IconFactory.createSortIcon(14, Color.WHITE), ModernButton.ButtonStyle.ACCENT);
        btnSortQuick.setToolTipText("Sorts appointments chronologically using O(n log n) Quick Sort");
        btnSortQuick.addActionListener(e -> onQuickSort());

        txtSearch = new PlaceholderTextField("Filter by patient, doctor, or status...", 18);
        txtSearch.addActionListener(e -> onSearch());

        ModernButton btnSearch = new ModernButton("Search", ModernButton.ButtonStyle.SECONDARY);
        btnSearch.addActionListener(e -> onSearch());

        ModernButton btnRefresh = new ModernButton("Show All", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            refreshDropdowns();
            refreshTable();
        });

        ModernButton btnDelete = new ModernButton("Delete Appt", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());

        controls.add(btnSortQuick);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);
        controls.add(btnDelete);

        toolbar.add(lblTableTitle, BorderLayout.WEST);
        toolbar.add(controls, BorderLayout.EAST);
        topCard.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Appt ID", "Patient Name & ID", "Doctor Assigned", "Scheduled Date & Time", "Status", "Clinical Purpose / Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        apptTable = new JTable(tableModel);
        UITheme.styleTable(apptTable);
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
        scrollPane.setPreferredSize(new Dimension(800, 240));

        topCard.add(scrollPane, BorderLayout.CENTER);
        add(topCard, BorderLayout.CENTER);

        // --- BOTTOM SECTION: BOOKING & EDIT FORM ---
        JPanel bottomCard = UITheme.createCardPanel();
        bottomCard.setLayout(new BorderLayout(10, 10));

        JLabel lblFormTitle = new JLabel("Appointment Details Form (Book / Update)");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        bottomCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel gridForm = new JPanel(new GridLayout(2, 6, 12, 10));
        gridForm.setOpaque(false);

        txtApptId = createTextField();
        txtApptId.setEditable(false);
        txtApptId.setBackground(new Color(241, 245, 249));
        txtApptId.setToolTipText("Auto-generated unique Appointment ID");

        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtDateTime = createTextField();
        txtDateTime.setText(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).format(DTF));
        cmbStatus = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});
        txtNotes = createTextField();

        // Row 1
        gridForm.add(createFieldLabel("Appt ID (Auto):"));
        gridForm.add(txtApptId);
        gridForm.add(createFieldLabel("Select Patient: *"));
        gridForm.add(cmbPatients);
        gridForm.add(createFieldLabel("Select Doctor: *"));
        gridForm.add(cmbDoctors);

        // Row 2
        gridForm.add(createFieldLabel("Date & Time: *"));
        gridForm.add(txtDateTime);
        gridForm.add(createFieldLabel("Status:"));
        gridForm.add(cmbStatus);
        gridForm.add(createFieldLabel("Purpose / Notes:"));
        gridForm.add(txtNotes);

        bottomCard.add(gridForm, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnRow.setOpaque(false);

        ModernButton btnBook = new ModernButton("Book Appointment", IconFactory.createCalendarIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnBook.addActionListener(e -> onBook());

        ModernButton btnComplete = new ModernButton("Mark Completed", ModernButton.ButtonStyle.SUCCESS);
        btnComplete.addActionListener(e -> onUpdateStatus("COMPLETED"));

        ModernButton btnCancelAppt = new ModernButton("Mark Cancelled", ModernButton.ButtonStyle.SECONDARY);
        btnCancelAppt.addActionListener(e -> onUpdateStatus("CANCELLED"));

        ModernButton btnClear = new ModernButton("Clear / New Appt", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(btnBook);
        btnRow.add(btnComplete);
        btnRow.add(btnCancelAppt);
        btnRow.add(btnClear);

        bottomCard.add(btnRow, BorderLayout.SOUTH);
        add(bottomCard, BorderLayout.SOUTH);
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

    private void loadNextAutoId() {
        txtApptId.setText(appointmentController.getNextAppointmentId());
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

            JOptionPane.showMessageDialog(this, "Appointment " + txtApptId.getText() + " booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table first.", "Select Record", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Select Record", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Appointment " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentController.deleteAppointment(id);
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
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
        loadNextAutoId();
        txtNotes.setText("");
        txtDateTime.setText(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).format(DTF));
        cmbStatus.setSelectedIndex(0);
        apptTable.clearSelection();
    }
}