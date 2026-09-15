package com.healthclinic.view;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.controller.DoctorController;
import com.healthclinic.model.Doctor;
import com.healthclinic.util.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Task 5 - Doctor Registration & Management.
 * Redesigned with clean Top-Table & Bottom-Form layout for a sweet, clean UI.
 * Features auto-generated sequential Doctor IDs.
 */
public class DoctorManagementPanel extends JPanel {

    private final ClinicController clinicController;
    private final DoctorController doctorController;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<String> cmbSpecialization;
    private JTextField txtLicense;
    private JTextField txtFee;
    private JTextField txtDays;

    private PlaceholderTextField txtSearch;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorManagementPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.doctorController = clinicController.getDoctorController();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        refreshTable();
        loadNextAutoId();
    }

    private void initUI() {
        // --- TOP SECTION: DIRECTORY TABLE ---
        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new BorderLayout(10, 10));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Clinic Doctors & Specialists Roster");
        lblTableTitle.setFont(UITheme.FONT_HEADER);
        lblTableTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        txtSearch = new PlaceholderTextField("Filter doctors by name or specialty...", 20);
        txtSearch.addActionListener(e -> onFilter());

        ModernButton btnFilter = new ModernButton("Filter", ModernButton.ButtonStyle.SECONDARY);
        btnFilter.addActionListener(e -> onFilter());

        ModernButton btnShowAll = new ModernButton("Show All", ModernButton.ButtonStyle.SECONDARY);
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });

        ModernButton btnDelete = new ModernButton("Delete Doctor", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());

        rightControls.add(txtSearch);
        rightControls.add(btnFilter);
        rightControls.add(btnShowAll);
        rightControls.add(btnDelete);

        toolbar.add(lblTableTitle, BorderLayout.WEST);
        toolbar.add(rightControls, BorderLayout.EAST);
        topCard.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Doctor Name", "Contact Phone", "Email Address", "Specialization", "Medical License", "Consultation Fee ($)", "Available Consultation Days"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        UITheme.styleTable(doctorTable);

        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cmbSpecialization.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
                txtLicense.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtFee.setText(tableModel.getValueAt(selectedRow, 6).toString());
                txtDays.setText(tableModel.getValueAt(selectedRow, 7).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 240));

        topCard.add(scrollPane, BorderLayout.CENTER);
        add(topCard, BorderLayout.CENTER);

        // --- BOTTOM SECTION: REGISTRATION & EDIT FORM ---
        JPanel bottomCard = UITheme.createCardPanel();
        bottomCard.setLayout(new BorderLayout(10, 10));

        JLabel lblFormTitle = new JLabel("Doctor Profile Form (Register / Update)");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        bottomCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel gridForm = new JPanel(new GridLayout(3, 6, 12, 10));
        gridForm.setOpaque(false);

        txtId = createTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(241, 245, 249));
        txtId.setToolTipText("Auto-generated unique Doctor ID");

        txtName = createTextField();
        txtPhone = createTextField();
        txtEmail = createTextField();
        cmbSpecialization = new JComboBox<>(new String[]{
                "General Practice", "Cardiology", "Pediatrics", "Dermatology",
                "Orthopedics", "General Surgery", "Neurology", "Diagnostic Medicine"
        });
        txtLicense = createTextField();
        txtFee = createTextField();
        txtDays = createTextField();

        // Row 1
        gridForm.add(createFieldLabel("Doctor ID (Auto):"));
        gridForm.add(txtId);
        gridForm.add(createFieldLabel("Doctor Full Name: *"));
        gridForm.add(txtName);
        gridForm.add(createFieldLabel("Phone Number: *"));
        gridForm.add(txtPhone);

        // Row 2
        gridForm.add(createFieldLabel("Email Address: *"));
        gridForm.add(txtEmail);
        gridForm.add(createFieldLabel("Specialization: *"));
        gridForm.add(cmbSpecialization);
        gridForm.add(createFieldLabel("Medical License: *"));
        gridForm.add(txtLicense);

        // Row 3
        gridForm.add(createFieldLabel("Consultation Fee ($): *"));
        gridForm.add(txtFee);
        gridForm.add(createFieldLabel("Available Days: *"));
        gridForm.add(txtDays);
        gridForm.add(new JLabel("")); // spacer
        gridForm.add(new JLabel("")); // spacer

        bottomCard.add(gridForm, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnRow.setOpaque(false);

        ModernButton btnRegister = new ModernButton("Register Doctor", IconFactory.createDoctorIcon(14, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        btnRegister.addActionListener(e -> onRegister());

        ModernButton btnUpdate = new ModernButton("Update Selected", ModernButton.ButtonStyle.SECONDARY);
        btnUpdate.addActionListener(e -> onUpdate());

        ModernButton btnClear = new ModernButton("Clear / New Doctor", ModernButton.ButtonStyle.SECONDARY);
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
        txtId.setText(doctorController.getNextDoctorId());
    }

    private void onRegister() {
        try {
            doctorController.registerDoctor(
                    txtId.getText(),
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    (String) cmbSpecialization.getSelectedItem(),
                    txtLicense.getText(),
                    txtFee.getText(),
                    txtDays.getText()
            );
            JOptionPane.showMessageDialog(this, "Doctor " + txtId.getText() + " registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            doctorController.updateDoctor(
                    txtId.getText(),
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    (String) cmbSpecialization.getSelectedItem(),
                    txtLicense.getText(),
                    txtFee.getText(),
                    txtDays.getText()
            );
            JOptionPane.showMessageDialog(this, "Doctor " + txtId.getText() + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Select Record", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorController.deleteDoctor(id);
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onFilter() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            refreshTable();
            return;
        }
        tableModel.setRowCount(0);
        for (Doctor d : doctorController.getAllDoctors()) {
            if (d.getName().toLowerCase().contains(q) || d.getSpecialization().toLowerCase().contains(q) || d.getId().toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{
                        d.getId(),
                        d.getName(),
                        d.getPhone(),
                        d.getEmail(),
                        d.getSpecialization(),
                        d.getLicenseNumber(),
                        String.format("%.2f", d.getConsultationFee()),
                        d.getAvailableDays()
                });
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Doctor d : doctorController.getAllDoctors()) {
            tableModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    d.getPhone(),
                    d.getEmail(),
                    d.getSpecialization(),
                    d.getLicenseNumber(),
                    String.format("%.2f", d.getConsultationFee()),
                    d.getAvailableDays()
            });
        }
    }

    private void clearForm() {
        loadNextAutoId();
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cmbSpecialization.setSelectedIndex(0);
        txtLicense.setText("");
        txtFee.setText("");
        txtDays.setText("");
        doctorTable.clearSelection();
    }
}