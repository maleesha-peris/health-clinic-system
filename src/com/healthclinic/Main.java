package com.healthclinic;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.view.MainFrame;

import javax.swing.*;

/**
 * Application Entry Point.
 * Bootstraps MVC layers and starts the Swing desktop application.
 */
public class Main {

    public static void main(String[] args) {
        // Set System Look and Feel for native OS appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to standard look and feel
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Controller (which initializes Model and Data persistence)
                ClinicController clinicController = new ClinicController();

                // Initialize View with Controller coordination
                MainFrame mainFrame = new MainFrame(clinicController);
                mainFrame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize Health Clinic System:\n" + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}