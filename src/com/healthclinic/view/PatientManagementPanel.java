package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.PatientController;
import com.healthclinic.model.Patient;
import com.healthclinic.util.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Task 5 - Patient Registration & Management.
 * Redesigned with clean Top-Table & Bottom-Form layout for spacious, sweet UI.
 * Features auto-generated sequential Patient IDs and Binary/Linear search.
 */
public class PatientManagementPanel extends JPanel {

    private final ClinicController clinicController;
    private final PatientController patientController;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtDob;
    private JComboBox<String> cmbGender;
    private JComboBox<String> cmbBloodGroup;
    private JTextField txtEmergency;
    private JTextField txtMedicalHistory;

    private PlaceholderTextField txtSearchQuery;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientManagementPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.patientController = clinicController.getPatientController();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        refreshTable();
        loadNextAutoId();
    }

    private void initUI() {
        // --- TOP SECTION: SEARCH TOOLBAR + FULL-WIDTH TABLE ---
        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new BorderLayout(10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Registered Patients Directory");
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchControls.setOpaque(false);

        txtSearchQuery = new PlaceholderTextField("Search patient by ID, name, or phone...", 20);

        ModernButton btnBinarySearch = new ModernButton("Binary Search (ID)", IconFactory.createSearchIcon(14, Color.WHITE), ModernButton.ButtonStyle.ACCENT);
        btnBinarySearch.setToolTipText("Fast O(log n) Binary Search by Patient ID");
        btnBinarySearch.addActionListener(e -> onBinarySearch());

        ModernButton btnLinearSearch = new ModernButton("Search All", ModernButton.ButtonStyle.SECONDARY);
        btnLinearSearch.setToolTipText("Linear Search across all fields");
        btnLinearSearch.addActionListener(e -> onLinearSearch());

        ModernButton btnReset = new ModernButton("Show All", ModernButton.ButtonStyle.SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearchQuery.setText("");
            refreshTable();
        });

        ModernButton btnDelete = new ModernButton("Delete Patient", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());

        searchControls.add(txtSearchQuery);
        searchControls.add(btnBinarySearch);
        searchControls.add(btnLinearSearch);
        searchControls.add(btnReset);
        searchControls.add(btnDelete);

        toolbar.add(lblTableTitle, BorderLayout.WEST);
        toolbar.add(searchControls, BorderLayout.EAST);
        topCard.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Full Name", "Phone", "Email Address", "Date of Birth", "Gender", "Blood Group", "Emergency Contact", "Medical History"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        UITheme.styleTable(patientTable);
        patientTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        patientTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtDob.setText(tableModel.getValueAt(selectedRow, 4).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                cmbBloodGroup.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
                txtEmergency.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "");
                txtMedicalHistory.setText(tableModel.getValueAt(selectedRow, 8) != null ? tableModel.getValueAt(selectedRow, 8).toString() : "");
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 240));

        topCard.add(scrollPane, BorderLayout.CENTER);
        add(topCard, BorderLayout.CENTER);

        // --- BOTTOM SECTION: FULL-WIDTH REGISTRATION & EDIT FORM ---
        JPanel bottomCard = UITheme.createCardPanel();
        bottomCard.setLayout(new BorderLayout(10, 10));

        JLabel lblFormTitle = new JLabel("Patient Details Form (Register / Update)");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        bottomCard.add(lblFormTitle, BorderLayout.NORTH);

        // 3-Column Grid for spacious input fields
        JPanel gridForm = new JPanel(new GridLayout(3, 6, 12, 10));
        gridForm.setOpaque(false);

        txtId = createTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(241, 245, 249));
        txtId.setToolTipText("Auto-generated unique Patient ID");

        txtName = createTextField();
        txtPhone = createTextField();
        txtEmail = createTextField();
        txtDob = createTextField();
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbBloodGroup = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        txtEmergency = createTextField();
        txtMedicalHistory = createTextField();

        // Row 1
        gridForm.add(createFieldLabel("Patient ID (Auto):"));
        gridForm.add(txtId);
        gridForm.add(createFieldLabel("Full Name: *"));
        gridForm.add(txtName);
        gridForm.add(createFieldLabel("Phone Number: *"));
        gridForm.add(txtPhone);

        // Row 2
        gridForm.add(createFieldLabel("Email Address: *"));
        gridForm.add(txtEmail);
        gridForm.add(createFieldLabel("DOB (YYYY-MM-DD): *"));
        gridForm.add(txtDob);
        gridForm.add(createFieldLabel("Gender: *"));
        gridForm.add(cmbGender);

        // Row 3
        gridForm.add(createFieldLabel("Blood Group: *"));
        gridForm.add(cmbBloodGroup);
        gridForm.add(createFieldLabel("Emergency Contact:"));
        gridForm.add(txtEmergency);
        gridForm.add(createFieldLabel("Medical History:"));
        gridForm.add(txtMedicalHistory);

        bottomCard.add(gridForm, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnRow.setOpaque(false);

        ModernButton btnRegister = new ModernButton("Register Patient", IconFactory.createPatientIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnRegister.addActionListener(e -> onRegister());

        ModernButton btnUpdate = new ModernButton("Update Selected", ModernButton.ButtonStyle.SECONDARY);
        btnUpdate.addActionListener(e -> onUpdate());

        ModernButton btnClear = new ModernButton("Clear / New Patient", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(btnRegister);
        btnRow.add(btnUpdate);
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
        txtId.setText(patientController.getNextPatientId());
    }

    private void onRegister() {
        try {
            patientController.registerPatient(
                    txtId.getText(),
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtDob.getText(),
                    (String) cmbGender.getSelectedItem(),
                    (String) cmbBloodGroup.getSelectedItem(),
                    txtEmergency.getText(),
                    txtMedicalHistory.getText()
            );
            JOptionPane.showMessageDialog(this, "Patient " + txtId.getText() + " registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        try {
            patientController.updatePatient(
                    txtId.getText(),
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtDob.getText(),
                    (String) cmbGender.getSelectedItem(),
                    (String) cmbBloodGroup.getSelectedItem(),
                    txtEmergency.getText(),
                    txtMedicalHistory.getText()
            );
            JOptionPane.showMessageDialog(this, "Patient " + txtId.getText() + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the table to delete.", "Select Record", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Patient " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientController.deletePatient(id);
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onBinarySearch() {
        String id = txtSearchQuery.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID in the search box for Binary Search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long startTime = System.nanoTime();
        Patient found = patientController.searchPatientById(id);
        long elapsed = System.nanoTime() - startTime;

        if (found != null) {
            tableModel.setRowCount(0);
            addPatientRow(found);
            JOptionPane.showMessageDialog(this,
                    "Binary Search Result (found in " + (elapsed / 1000.0) + " μs):\n" +
                    "ID: " + found.getId() + "\n" +
                    "Name: " + found.getName() + "\n" +
                    "Phone: " + found.getPhone() + "\n" +
                    "Blood Group: " + found.getBloodGroup() + "\n" +
                    "DOB: " + found.getDateOfBirth(),
                    "Binary Search Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Binary Search: No patient found with ID '" + id + "'.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onLinearSearch() {
        String query = txtSearchQuery.getText().trim();
        if (query.isEmpty()) {
            refreshTable();
            return;
        }

        ArrayList<Patient> matches = patientController.searchPatients(query);
        tableModel.setRowCount(0);
        for (Patient p : matches) {
            addPatientRow(p);
        }
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Linear Search: No matching patient records found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient p : patientController.getAllPatients()) {
            addPatientRow(p);
        }
    }

    private void addPatientRow(Patient p) {
        tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getPhone(),
                p.getEmail(),
                p.getDateOfBirth(),
                p.getGender(),
                p.getBloodGroup(),
                p.getEmergencyContact(),
                p.getMedicalHistory()
        });
    }

    private void clearForm() {
        loadNextAutoId();
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtDob.setText("");
        cmbGender.setSelectedIndex(0);
        cmbBloodGroup.setSelectedIndex(0);
        txtEmergency.setText("");
        txtMedicalHistory.setText("");
        patientTable.clearSelection();
    }
}