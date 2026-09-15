package com.healthclinic;

import com.healthclinic.controller.ClinicController;
import com.healthclinic.view.LoadingScreen;
import com.healthclinic.view.MainFrame;

import javax.swing.*;

/**
 * Application Entry Point.
 * Displays a modern splash loading screen before transitioning to MainFrame.
 */
public class Main {

    public static void main(String[] args) {
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
                    // Step 1: Initialize System
                    publish(20);
                    Thread.sleep(400);

                    // Step 2: Load Data Persistence
                    publish(50);
                    clinicController = new ClinicController();
                    Thread.sleep(400);

                    // Step 3: Build GUI Views
                    publish(85);
                    MainFrame frame = new MainFrame(clinicController);
                    Thread.sleep(400);

                    // Step 4: Finalize
                    publish(100);
                    Thread.sleep(300);

                    return frame;
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
                        MainFrame mainFrame = get();
                        splash.dispose();
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