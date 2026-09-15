package com.healthclinic.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * UI Theme constants and styling helpers for modern desktop GUI.
 */
public final class UITheme {

    // Modern Clinical Color Palette
    public static final Color SIDEBAR_BG_START = new Color(3, 114, 166);  // Deep Ocean Teal (as in reference image)
    public static final Color SIDEBAR_BG_END   = new Color(0, 79, 117);
    public static final Color SIDEBAR_ACTIVE   = new Color(0, 168, 204);  // Vibrant Pill Blue/Cyan

    public static final Color PRIMARY       = new Color(24, 115, 204);   // Clinical Blue
    public static final Color PRIMARY_HOVER = new Color(28, 130, 230);
    public static final Color ACCENT        = new Color(128, 56, 220);   // Modern Violet
    public static final Color SUCCESS       = new Color(16, 185, 129);   // Emerald
    public static final Color WARNING       = new Color(245, 158, 11);   // Amber
    public static final Color DANGER        = new Color(239, 68, 68);    // Modern Red

    public static final Color BG_MAIN       = new Color(245, 247, 250);  // Soft Gray Canvas
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color TEXT_PRIMARY  = new Color(30, 41, 59);     // Slate 800
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);  // Slate 500
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);  // Slate 200

    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER    = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_MONO      = new Font("Consolas", Font.PLAIN, 12);

    private UITheme() {}

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(FONT_REGULAR);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(100, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
    }
}