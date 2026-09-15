package com.healthclinic.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Metric Card with smooth gradient and circular watermark background
 * inspired directly by modern dashboards (as in reference image).
 */
public class GradientCardPanel extends JPanel {

    private final Color startColor;
    private final Color endColor;
    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtitle;

    public GradientCardPanel(String title, String initialValue, String subtitle, Color startColor, Color endColor, Icon icon) {
        this.startColor = startColor;
        this.endColor = endColor;

        setOpaque(false);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Top Row: Icon badge + Title
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            topRow.add(iconLabel);
        }

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(255, 255, 255, 220));
        topRow.add(lblTitle);

        add(topRow, BorderLayout.NORTH);

        // Center: Huge bold value
        lblValue = new JLabel(initialValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 0));
        add(lblValue, BorderLayout.CENTER);

        // Bottom: Trend / Subtitle
        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        add(lblSubtitle, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtitle(String sub) {
        lblSubtitle.setText(sub);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Diagonal Gradient
        GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

        // 2. Subtle decorative circular watermark curves on the right (like the reference design)
        g2.setColor(new Color(255, 255, 255, 25));
        g2.fillOval(w - (int)(h * 1.1), -h / 4, (int)(h * 1.3), (int)(h * 1.3));
        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillOval(w - (int)(h * 0.7), -h / 6, (int)(h * 1.0), (int)(h * 1.0));

        g2.dispose();
        super.paintComponent(g);
    }
}