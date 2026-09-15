package com.healthclinic.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Modern Table Cell Renderer for status pills (like PENDING, APPROVED, REJECT in reference image).
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));

        String text = value != null ? value.toString().toUpperCase() : "";

        Color bg;
        Color fg;

        switch (text) {
            case "SCHEDULED":
            case "PENDING":
                bg = new Color(245, 158, 11); // Amber
                fg = Color.WHITE;
                break;
            case "COMPLETED":
            case "APPROVED":
                bg = new Color(16, 185, 129); // Emerald Green
                fg = Color.WHITE;
                break;
            case "CANCELLED":
            case "REJECT":
                bg = new Color(239, 68, 68);  // Red
                fg = Color.WHITE;
                break;
            case "MALE":
                bg = new Color(59, 130, 246); // Blue
                fg = Color.WHITE;
                break;
            case "FEMALE":
                bg = new Color(236, 72, 153); // Pink
                fg = Color.WHITE;
                break;
            default:
                if (isSelected) {
                    return label;
                }
                label.setOpaque(true);
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                label.setForeground(new Color(30, 41, 59));
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return label;
        }

        // Return custom pill panel
        return new PillBadgePanel(text, bg, fg, isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
    }

    private static class PillBadgePanel extends JPanel {
        private final String text;
        private final Color badgeBg;
        private final Color badgeFg;

        public PillBadgePanel(String text, Color badgeBg, Color badgeFg, Color rowBg) {
            this.text = text;
            this.badgeBg = badgeBg;
            this.badgeFg = badgeFg;
            setBackground(rowBg);
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = new Font("Segoe UI", Font.BOLD, 11);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();

            int pillW = textW + 20;
            int pillH = 22;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(badgeBg);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);

            g2.setColor(badgeFg);
            g2.drawString(text, x + 10, y + (pillH + textH) / 2 - 2);

            g2.dispose();
        }
    }
}