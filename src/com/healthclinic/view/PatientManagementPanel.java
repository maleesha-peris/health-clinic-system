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
 * Task 5 - Register Patient & Management Screen.
 * Provides Patient registration form, update, delete, and
 * Task 8 Searching (Binary Search by ID and Linear Search).
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

    private JTextField txtSearchQuery;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientManagementPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.patientController = clinicController.getPatientController();

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        refreshTable();
    }

    private void initUI() {
        // Title Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Patient Registration & Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Split Layout: Left Form, Right Table & Search
        JPanel splitPanel = new JPanel(new BorderLayout(15, 15));
        splitPanel.setOpaque(false);

        // --- LEFT: REGISTRATION FORM ---
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(10, 10));
        formCard.setPreferredSize(new Dimension(360, 500));

        JLabel lblFormTitle = new JLabel("Patient Details Form");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(9, 2, 8, 10));
        fieldsPanel.setOpaque(false);

        txtId = new JTextField();
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtDob = new JTextField(); // YYYY-MM-DD
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbBloodGroup = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        txtEmergency = new JTextField();
        txtMedicalHistory = new JTextField();

        fieldsPanel.add(new JLabel("Patient ID: *"));
        fieldsPanel.add(txtId);
        fieldsPanel.add(new JLabel("Full Name: *"));
        fieldsPanel.add(txtName);
        fieldsPanel.add(new JLabel("Phone Number: *"));
        fieldsPanel.add(txtPhone);
        fieldsPanel.add(new JLabel("Email Address: *"));
        fieldsPanel.add(txtEmail);
        fieldsPanel.add(new JLabel("DOB (YYYY-MM-DD): *"));
        fieldsPanel.add(txtDob);
        fieldsPanel.add(new JLabel("Gender: *"));
        fieldsPanel.add(cmbGender);
        fieldsPanel.add(new JLabel("Blood Group: *"));
        fieldsPanel.add(cmbBloodGroup);
        fieldsPanel.add(new JLabel("Emergency Contact:"));
        fieldsPanel.add(txtEmergency);
        fieldsPanel.add(new JLabel("Medical History:"));
        fieldsPanel.add(txtMedicalHistory);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Form Action Buttons
        JPanel formBtnRow = new JPanel(new GridLayout(1, 3, 8, 0));
        formBtnRow.setOpaque(false);

        JButton btnRegister = UITheme.createPrimaryButton("Register");
        btnRegister.addActionListener(e -> onRegister());

        JButton btnUpdate = UITheme.createSecondaryButton("Update");
        btnUpdate.addActionListener(e -> onUpdate());

        JButton btnClear = UITheme.createSecondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        formBtnRow.add(btnRegister);
        formBtnRow.add(btnUpdate);
        formBtnRow.add(btnClear);

        formCard.add(formBtnRow, BorderLayout.SOUTH);
        splitPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT: SEARCH BAR & DATA TABLE ---
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(10, 10));

        // Search Bar (Linear & Binary Search)
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setOpaque(false);

        searchBar.add(new JLabel("Search:"));
        txtSearchQuery = new JTextField(12);
        searchBar.add(txtSearchQuery);

        JButton btnBinarySearch = UITheme.createAccentButton("Binary Search (by ID)");
        btnBinarySearch.setToolTipText("Performs O(log n) Binary Search on Patient ID");
        btnBinarySearch.addActionListener(e -> onBinarySearch());
        searchBar.add(btnBinarySearch);

        JButton btnLinearSearch = UITheme.createSecondaryButton("Linear Search (All)");
        btnLinearSearch.setToolTipText("Performs O(n) Linear Search matching ID, Name, or Phone");
        btnLinearSearch.addActionListener(e -> onLinearSearch());
        searchBar.add(btnLinearSearch);

        JButton btnReset = UITheme.createSecondaryButton("Show All");
        btnReset.addActionListener(e -> refreshTable());
        searchBar.add(btnReset);

        rightCard.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Phone", "Email", "DOB", "Gender", "Blood", "Emergency", "History"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(24);
        patientTable.setFont(UITheme.FONT_REGULAR);
        patientTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        patientTable.getTableHeader().setBackground(UITheme.PRIMARY_LIGHT);

        // Selection Listener to populate form
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
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Table Bottom Actions
        JPanel tableBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableBottom.setOpaque(false);

        JButton btnDelete = UITheme.createDangerButton("Delete Selected Patient");
        btnDelete.addActionListener(e -> onDelete());
        tableBottom.add(btnDelete);

        rightCard.add(tableBottom, BorderLayout.SOUTH);
        splitPanel.add(rightCard, BorderLayout.CENTER);

        add(splitPanel, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(this, "Patient successfully registered and saved to disk!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Patient details updated and saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a patient from the table to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Patient " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientController.deletePatient(id);
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        txtId.setText("");
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