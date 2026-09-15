package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.TreatmentController;
import com.healthclinic.model.Appointment;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Doctor;
import com.healthclinic.model.Patient;
import com.healthclinic.model.Treatment;
import com.healthclinic.util.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Task 5 - Treatment Entry Screen.
 * Modern UI for logging clinical diagnoses, prescriptions, costs, and notes.
 */
public class TreatmentEntryPanel extends JPanel {

    private final ClinicController clinicController;
    private final TreatmentController treatmentController;

    private JTextField txtTreatId;
    private JComboBox<String> cmbAppointments;
    private JComboBox<String> cmbPatients;
    private JComboBox<String> cmbDoctors;
    private JTextField txtDate;
    private JTextField txtDiagnosis;
    private JTextArea txtPrescription;
    private JTextField txtCost;
    private JTextField txtNotes;

    private JTable treatTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TreatmentEntryPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.treatmentController = clinicController.getTreatmentController();

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
        JLabel lblTitle = new JLabel("Treatment Records & Prescription Entry");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new BorderLayout(15, 15));
        splitPanel.setOpaque(false);

        // --- LEFT: ENTRY FORM ---
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(12, 12));
        formCard.setPreferredSize(new Dimension(380, 520));

        JLabel lblFormTitle = new JLabel("Clinical Record Details");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTreatId = createTextField();
        cmbAppointments = new JComboBox<>();
        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtDate = createTextField();
        txtDate.setText(LocalDate.now().format(DF));
        txtDiagnosis = createTextField();
        txtPrescription = new JTextArea(3, 20);
        txtPrescription.setLineWrap(true);
        txtPrescription.setWrapStyleWord(true);
        txtPrescription.setFont(UITheme.FONT_REGULAR);
        JScrollPane scrollPresc = new JScrollPane(txtPrescription);
        scrollPresc.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        txtCost = createTextField();
        txtNotes = createTextField();

        cmbAppointments.addActionListener(e -> {
            String selected = (String) cmbAppointments.getSelectedItem();
            if (selected != null && selected.contains(" - ")) {
                String apptId = selected.split(" - ")[0].trim();
                Appointment a = clinicController.getClinic().findAppointmentById(apptId);
                if (a != null) {
                    selectPatientInDropdown(a.getPatientId());
                    selectDoctorInDropdown(a.getDoctorId());
                }
            }
        });

        int row = 0;
        addRow(fieldsPanel, gbc, row++, "Treatment ID: *", txtTreatId);
        addRow(fieldsPanel, gbc, row++, "Linked Appointment:", cmbAppointments);
        addRow(fieldsPanel, gbc, row++, "Patient: *", cmbPatients);
        addRow(fieldsPanel, gbc, row++, "Attending Doctor: *", cmbDoctors);
        addRow(fieldsPanel, gbc, row++, "Date (YYYY-MM-DD): *", txtDate);
        addRow(fieldsPanel, gbc, row++, "Diagnosis: *", txtDiagnosis);
        addRow(fieldsPanel, gbc, row++, "Prescription: *", scrollPresc);
        addRow(fieldsPanel, gbc, row++, "Cost ($): *", txtCost);
        addRow(fieldsPanel, gbc, row++, "Clinical Notes:", txtNotes);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);

        ModernButton btnSave = new ModernButton("Save Treatment", IconFactory.createPillIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> onSave());

        ModernButton btnClear = new ModernButton("Clear Form", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(btnSave);
        btnRow.add(btnClear);

        formCard.add(btnRow, BorderLayout.SOUTH);
        splitPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT: TABLE ---
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(12, 12));

        JLabel lblTableTitle = new JLabel("Treatment History Log", SwingConstants.LEFT);
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_PRIMARY);
        rightCard.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Appt", "Patient", "Doctor", "Date", "Diagnosis", "Prescription", "Cost ($)", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        treatTable = new JTable(tableModel);
        UITheme.styleTable(treatTable);

        JScrollPane scrollPane = new JScrollPane(treatTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom delete
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setOpaque(false);

        ModernButton btnDelete = new ModernButton("Delete Selected Treatment", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());
        bottomRow.add(btnDelete);

        rightCard.add(bottomRow, BorderLayout.SOUTH);
        splitPanel.add(rightCard, BorderLayout.CENTER);

        add(splitPanel, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(comp, gbc);
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
        cmbAppointments.removeAllItems();
        cmbAppointments.addItem("-- None (Standalone) --");
        for (Appointment a : clinicController.getClinic().getAppointments()) {
            cmbAppointments.addItem(a.getAppointmentId() + " - " + a.getPatientId() + " (" + a.getStatus() + ")");
        }

        cmbPatients.removeAllItems();
        for (Patient p : clinicController.getClinic().getPatients()) {
            cmbPatients.addItem(p.getId() + " - " + p.getName());
        }

        cmbDoctors.removeAllItems();
        for (Doctor d : clinicController.getClinic().getDoctors()) {
            cmbDoctors.addItem(d.getId() + " - Dr. " + d.getName());
        }
    }

    private void selectPatientInDropdown(String patientId) {
        for (int i = 0; i < cmbPatients.getItemCount(); i++) {
            if (cmbPatients.getItemAt(i).startsWith(patientId + " - ")) {
                cmbPatients.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectDoctorInDropdown(String doctorId) {
        for (int i = 0; i < cmbDoctors.getItemCount(); i++) {
            if (cmbDoctors.getItemAt(i).startsWith(doctorId + " - ")) {
                cmbDoctors.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onSave() {
        try {
            String apptSel = (String) cmbAppointments.getSelectedItem();
            String apptId = (apptSel != null && !apptSel.startsWith("--")) ? apptSel.split(" - ")[0].trim() : "";

            String patSel = (String) cmbPatients.getSelectedItem();
            String docSel = (String) cmbDoctors.getSelectedItem();

            if (patSel == null || docSel == null) {
                JOptionPane.showMessageDialog(this, "Please select valid patient and doctor.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String patientId = patSel.split(" - ")[0].trim();
            String doctorId = docSel.split(" - ")[0].trim();

            treatmentController.recordTreatment(
                    txtTreatId.getText(),
                    apptId,
                    patientId,
                    doctorId,
                    txtDate.getText(),
                    txtDiagnosis.getText(),
                    txtPrescription.getText(),
                    txtCost.getText(),
                    txtNotes.getText()
            );

            JOptionPane.showMessageDialog(this, "Treatment record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
            refreshDropdowns();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = treatTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a treatment record to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Treatment " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                treatmentController.deleteTreatment(id);
                JOptionPane.showMessageDialog(this, "Treatment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting treatment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        Clinic clinic = clinicController.getClinic();
        for (Treatment t : treatmentController.getAllTreatments()) {
            Patient p = clinic.findPatientById(t.getPatientId());
            Doctor d = clinic.findDoctorById(t.getDoctorId());

            String pName = p != null ? p.getName() + " (" + t.getPatientId() + ")" : t.getPatientId();
            String dName = d != null ? "Dr. " + d.getName() : t.getDoctorId();
            String dateStr = t.getTreatmentDate() != null ? t.getTreatmentDate().format(DF) : "N/A";

            tableModel.addRow(new Object[]{
                    t.getTreatmentId(),
                    t.getAppointmentId(),
                    pName,
                    dName,
                    dateStr,
                    t.getDiagnosis(),
                    t.getPrescription(),
                    String.format("%.2f", t.getCost()),
                    t.getNotes()
            });
        }
    }

    private void clearForm() {
        txtTreatId.setText("");
        cmbAppointments.setSelectedIndex(0);
        txtDate.setText(LocalDate.now().format(DF));
        txtDiagnosis.setText("");
        txtPrescription.setText("");
        txtCost.setText("");
        txtNotes.setText("");
        treatTable.clearSelection();
    }
}