package com.healthclinic;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.view.LoadingScreen;
import com.healthclinic.view.MainFrame;

import javax.swing.*;
import java.net.ServerSocket;

/**
 * Application Entry Point.
 * Implements single-instance protection to prevent duplicate overlapping windows,
 * and displays a modern splash loading screen before launching the dashboard.
 */
public class Main {

    private static final int APP_LOCK_PORT = 48567;
    private static ServerSocket lockSocket;

    public static void main(String[] args) {
        // Enforce Single-Instance to prevent duplicate overlapping windows
        try {
            lockSocket = new ServerSocket(APP_LOCK_PORT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "HealthClinic is already running on this computer.\nPlease switch to the existing window.",
                    "Application Already Running", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        // Set System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoadingScreen splash = new LoadingScreen();
            splash.setVisible(true);

            // Background worker to simulate initialization and load models
            SwingWorker<MainFrame, Integer> loader = new SwingWorker<>() {
                private ClinicController clinicController;

                @Override
                protected MainFrame doInBackground() throws Exception {
                    publish(20);
                    Thread.sleep(350);

                    publish(50);
                    clinicController = new ClinicController();
                    Thread.sleep(350);

                    publish(85);
                    Thread.sleep(300);

                    publish(100);
                    Thread.sleep(200);

                    return null;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int val = chunks.get(chunks.size() - 1);
                    String msg;
                    if (val <= 25) msg = "Loading clinical core models and algorithms...";
                    else if (val <= 60) msg = "Retrieving patient and appointment records from disk...";
                    else if (val <= 90) msg = "Constructing interactive Swing dashboard...";
                    else msg = "System ready! Launching Clinic Management...";
                    splash.setProgress(val, msg);
                }

                @Override
                protected void done() {
                    try {
                        splash.dispose();
                        // Construct GUI strictly on EDT
                        MainFrame mainFrame = new MainFrame(clinicController);
                        mainFrame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        splash.dispose();
                        JOptionPane.showMessageDialog(null,
                                "Initialization Error:\n" + e.getMessage(),
                                "Startup Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            };

            loader.execute();
        });
    }
}