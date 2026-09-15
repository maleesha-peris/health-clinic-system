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
 * Task 5 - Treatment Records Entry & Management.
 * Redesigned with clean Top-Table & Bottom-Form layout for spacious, sweet UI.
 * Features auto-generated sequential Treatment IDs.
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

    private PlaceholderTextField txtSearch;
    private JTable treatTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TreatmentEntryPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.treatmentController = clinicController.getTreatmentController();

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

        JLabel lblTableTitle = new JLabel("Recorded Clinical Treatments & Prescriptions");
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        txtSearch = new PlaceholderTextField("Filter by diagnosis or patient...", 18);
        txtSearch.addActionListener(e -> onSearch());

        ModernButton btnSearch = new ModernButton("Filter", ModernButton.ButtonStyle.SECONDARY);
        btnSearch.addActionListener(e -> onSearch());

        ModernButton btnRefresh = new ModernButton("Show All", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            refreshDropdowns();
            refreshTable();
        });

        ModernButton btnDelete = new ModernButton("Delete Record", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());

        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);
        controls.add(btnDelete);

        toolbar.add(lblTableTitle, BorderLayout.WEST);
        toolbar.add(controls, BorderLayout.EAST);
        topCard.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Treatment ID", "Linked Appt", "Patient Name", "Attending Doctor", "Treatment Date", "Clinical Diagnosis", "Prescription", "Cost ($)", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        treatTable = new JTable(tableModel);
        UITheme.styleTable(treatTable);

        treatTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = treatTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtTreatId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtDate.setText(tableModel.getValueAt(selectedRow, 4).toString());
                txtDiagnosis.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtPrescription.setText(tableModel.getValueAt(selectedRow, 6).toString());
                txtCost.setText(tableModel.getValueAt(selectedRow, 7).toString());
                txtNotes.setText(tableModel.getValueAt(selectedRow, 8) != null ? tableModel.getValueAt(selectedRow, 8).toString() : "");
            }
        });

        JScrollPane scrollPane = new JScrollPane(treatTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 240));

        topCard.add(scrollPane, BorderLayout.CENTER);
        add(topCard, BorderLayout.CENTER);

        // --- BOTTOM SECTION: ENTRY FORM ---
        JPanel bottomCard = UITheme.createCardPanel();
        bottomCard.setLayout(new BorderLayout(10, 10));

        JLabel lblFormTitle = new JLabel("Treatment Details Form (Record / Update)");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        bottomCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel gridForm = new JPanel(new GridLayout(3, 6, 12, 10));
        gridForm.setOpaque(false);

        txtTreatId = createTextField();
        txtTreatId.setEditable(false);
        txtTreatId.setBackground(new Color(241, 245, 249));
        txtTreatId.setToolTipText("Auto-generated unique Treatment ID");

        cmbAppointments = new JComboBox<>();
        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtDate = createTextField();
        txtDate.setText(LocalDate.now().format(DF));
        txtDiagnosis = createTextField();
        txtCost = createTextField();
        txtNotes = createTextField();

        txtPrescription = new JTextArea(2, 15);
        txtPrescription.setFont(UITheme.FONT_REGULAR);
        txtPrescription.setLineWrap(true);
        txtPrescription.setWrapStyleWord(true);
        JScrollPane scrollPresc = new JScrollPane(txtPrescription);
        scrollPresc.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));

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

        // Row 1
        gridForm.add(createFieldLabel("Treatment ID (Auto):"));
        gridForm.add(txtTreatId);
        gridForm.add(createFieldLabel("Linked Appointment:"));
        gridForm.add(cmbAppointments);
        gridForm.add(createFieldLabel("Select Patient: *"));
        gridForm.add(cmbPatients);

        // Row 2
        gridForm.add(createFieldLabel("Attending Doctor: *"));
        gridForm.add(cmbDoctors);
        gridForm.add(createFieldLabel("Date (YYYY-MM-DD): *"));
        gridForm.add(txtDate);
        gridForm.add(createFieldLabel("Diagnosis: *"));
        gridForm.add(txtDiagnosis);

        // Row 3
        gridForm.add(createFieldLabel("Prescription: *"));
        gridForm.add(scrollPresc);
        gridForm.add(createFieldLabel("Treatment Cost ($): *"));
        gridForm.add(txtCost);
        gridForm.add(createFieldLabel("Clinical Notes:"));
        gridForm.add(txtNotes);

        bottomCard.add(gridForm, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnRow.setOpaque(false);

        ModernButton btnSave = new ModernButton("Save Treatment Record", IconFactory.createPillIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> onSave());

        ModernButton btnClear = new ModernButton("Clear / New Record", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(btnSave);
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
        txtTreatId.setText(treatmentController.getNextTreatmentId());
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

            JOptionPane.showMessageDialog(this, "Treatment record " + txtTreatId.getText() + " saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a treatment record to delete.", "Select Record", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Treatment " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                treatmentController.deleteTreatment(id);
                JOptionPane.showMessageDialog(this, "Treatment deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting treatment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            refreshTable();
            return;
        }
        tableModel.setRowCount(0);
        Clinic clinic = clinicController.getClinic();
        for (Treatment t : treatmentController.getAllTreatments()) {
            if (t.getDiagnosis().toLowerCase().contains(q) || t.getPatientId().toLowerCase().contains(q) || t.getTreatmentId().toLowerCase().contains(q)) {
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
        loadNextAutoId();
        cmbAppointments.setSelectedIndex(0);
        txtDate.setText(LocalDate.now().format(DF));
        txtDiagnosis.setText("");
        txtPrescription.setText("");
        txtCost.setText("");
        txtNotes.setText("");
        treatTable.clearSelection();
    }
}