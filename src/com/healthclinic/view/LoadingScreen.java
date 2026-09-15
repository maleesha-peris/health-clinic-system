package com.healthclinic.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Loading Screen / Splash Screen.
 * Displays before entering the main dashboard, featuring clinic branding,
 * an animated progress bar, and status updates.
 */
public class LoadingScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;

    public LoadingScreen() {
        setSize(480, 280);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0)); // Transparent window for rounded corners

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, UITheme.SIDEBAR_BG_START, getWidth(), getHeight(), UITheme.SIDEBAR_BG_END);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                // Decorative watermark circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 160, -40, 220, 220);
                g2.fillOval(getWidth() - 100, getHeight() - 120, 180, 180);

                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(30, 35, 25, 35));
        content.setOpaque(false);

        // Center: Logo & Brand
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel(IconFactory.createLogoIcon(48));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("HealthClinic");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Community Care System • MVC Architecture");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(200, 230, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblLogo);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lblTitle);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(lblSub);

        content.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Status & Progress
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setOpaque(false);

        lblStatus = new JLabel("Starting clinic management system...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(220, 240, 255));

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 8));
        progressBar.setForeground(new Color(0, 210, 211));
        progressBar.setBackground(new Color(255, 255, 255, 40));
        progressBar.setBorderPainted(false);

        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        content.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }

    public void setProgress(int value, String statusText) {
        progressBar.setValue(value);
        lblStatus.setText(statusText);
    }
}