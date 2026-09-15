package com.healthclinic.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom Painted Modern Button.
 * Bypasses OS native Look & Feel overrides to ensure 100% visible,
 * crisp, beautifully styled buttons with smooth hover and active states.
 */
public class ModernButton extends JButton {

    public enum ButtonStyle {
        PRIMARY, SECONDARY, ACCENT, DANGER, SUCCESS, SIDEBAR
    }

    private ButtonStyle style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private boolean isActive = false; // For sidebar active tab
    private int cornerRadius = 8;

    public ModernButton(String text, ButtonStyle style) {
        this(text, null, style);
    }

    public ModernButton(String text, Icon icon, ButtonStyle style) {
        super(text, icon);
        this.style = style;
        init();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setIconTextGap(10);

        if (style == ButtonStyle.SIDEBAR) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            setForeground(new Color(220, 235, 252));
            cornerRadius = 10;
        } else {
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (style == ButtonStyle.SIDEBAR) {
            setForeground(active ? Color.WHITE : new Color(200, 220, 245));
        }
        repaint();
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bgColor;
        Color textColor;
        Color borderColor = null;

        switch (style) {
            case SIDEBAR:
                if (isActive) {
                    bgColor = new Color(0, 168, 204); // Vibrant Cyan Pill (like reference image)
                    textColor = Color.WHITE;
                } else if (isHovered) {
                    bgColor = new Color(255, 255, 255, 30); // Subtle translucent hover
                    textColor = Color.WHITE;
                } else {
                    bgColor = new Color(0, 0, 0, 0); // Transparent
                    textColor = new Color(200, 225, 245);
                }
                break;

            case PRIMARY:
                if (isPressed) bgColor = new Color(20, 100, 180);
                else if (isHovered) bgColor = new Color(28, 130, 230);
                else bgColor = new Color(24, 115, 204);
                textColor = Color.WHITE;
                break;

            case ACCENT:
                if (isPressed) bgColor = new Color(110, 40, 200);
                else if (isHovered) bgColor = new Color(145, 70, 240);
                else bgColor = new Color(128, 56, 220); // Modern Violet
                textColor = Color.WHITE;
                break;

            case DANGER:
                if (isPressed) bgColor = new Color(175, 30, 30);
                else if (isHovered) bgColor = new Color(225, 55, 55);
                else bgColor = new Color(210, 45, 45);
                textColor = Color.WHITE;
                break;

            case SUCCESS:
                if (isPressed) bgColor = new Color(35, 120, 60);
                else if (isHovered) bgColor = new Color(50, 165, 85);
                else bgColor = new Color(42, 145, 75);
                textColor = Color.WHITE;
                break;

            case SECONDARY:
            default:
                if (isPressed) bgColor = new Color(230, 235, 242);
                else if (isHovered) bgColor = new Color(242, 245, 250);
                else bgColor = Color.WHITE;
                textColor = new Color(40, 50, 65);
                borderColor = new Color(210, 218, 228);
                break;
        }

        // Paint background shape
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // Paint border if secondary
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, w - 1.2f, h - 1.2f, cornerRadius, cornerRadius));
        }

        // Set text color explicitly so it NEVER turns invisible!
        setForeground(textColor);

        g2.dispose();
        super.paintComponent(g);
    }
}