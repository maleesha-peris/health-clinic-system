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
 * Task 5 - Register Doctor & Management Screen.
 * Modern UI for registering and allocating doctors.
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

    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorManagementPanel(ClinicController clinicController) {
        this.clinicController = clinicController;
        this.doctorController = clinicController.getDoctorController();

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 18, 18, 18));

        initUI();
        refreshTable();
    }

    private void initUI() {
        // Title Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Doctor Registration & Allocation");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new BorderLayout(15, 15));
        splitPanel.setOpaque(false);

        // --- LEFT: REGISTRATION FORM ---
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(12, 12));
        formCard.setPreferredSize(new Dimension(370, 480));

        JLabel lblFormTitle = new JLabel("Doctor Profile Details");
        lblFormTitle.setFont(UITheme.FONT_HEADER);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(8, 2, 8, 10));
        fieldsPanel.setOpaque(false);

        txtId = createTextField();
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

        fieldsPanel.add(createFieldLabel("Doctor ID: *"));
        fieldsPanel.add(txtId);
        fieldsPanel.add(createFieldLabel("Doctor Name: *"));
        fieldsPanel.add(txtName);
        fieldsPanel.add(createFieldLabel("Phone: *"));
        fieldsPanel.add(txtPhone);
        fieldsPanel.add(createFieldLabel("Email: *"));
        fieldsPanel.add(txtEmail);
        fieldsPanel.add(createFieldLabel("Specialization: *"));
        fieldsPanel.add(cmbSpecialization);
        fieldsPanel.add(createFieldLabel("License No: *"));
        fieldsPanel.add(txtLicense);
        fieldsPanel.add(createFieldLabel("Consultation Fee ($): *"));
        fieldsPanel.add(txtFee);
        fieldsPanel.add(createFieldLabel("Available Days: *"));
        fieldsPanel.add(txtDays);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel formBtnRow = new JPanel(new GridLayout(1, 3, 8, 0));
        formBtnRow.setOpaque(false);

        ModernButton btnRegister = new ModernButton("Register", ModernButton.ButtonStyle.PRIMARY);
        btnRegister.addActionListener(e -> onRegister());

        ModernButton btnUpdate = new ModernButton("Update", ModernButton.ButtonStyle.SECONDARY);
        btnUpdate.addActionListener(e -> onUpdate());

        ModernButton btnClear = new ModernButton("Clear", ModernButton.ButtonStyle.SECONDARY);
        btnClear.addActionListener(e -> clearForm());

        formBtnRow.add(btnRegister);
        formBtnRow.add(btnUpdate);
        formBtnRow.add(btnClear);

        formCard.add(formBtnRow, BorderLayout.SOUTH);
        splitPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT: TABLE ---
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BorderLayout(12, 12));

        String[] cols = {"ID", "Doctor Name", "Phone", "Email", "Specialization", "License", "Fee ($)", "Available Days"};
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
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom action
        JPanel tableBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        tableBottom.setOpaque(false);

        ModernButton btnDelete = new ModernButton("Delete Selected Doctor", ModernButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDelete());
        tableBottom.add(btnDelete);

        rightCard.add(tableBottom, BorderLayout.SOUTH);
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
            JOptionPane.showMessageDialog(this, "Doctor registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Doctor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorController.deleteDoctor(id);
                JOptionPane.showMessageDialog(this, "Doctor removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        txtId.setText("");
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