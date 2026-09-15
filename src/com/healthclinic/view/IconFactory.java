package com.healthclinic.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * High-DPI Vector Icon Factory.
 * Draws clean, modern, antialiased vector icons directly via Graphics2D.
 * Ensures zero external image file dependencies and razor-sharp rendering on all displays.
 */
public final class IconFactory {

    private IconFactory() {}

    public static Icon createDashboardIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            int m = s / 5;
            int w = s / 2 - m;
            g2.fillRoundRect(m, m, w, w, 4, 4);
            g2.fillRoundRect(s / 2 + 2, m, w, w, 4, 4);
            g2.fillRoundRect(m, s / 2 + 2, w, w, 4, 4);
            g2.fillRoundRect(s / 2 + 2, s / 2 + 2, w, w, 4, 4);
        });
    }

    public static Icon createPatientIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            // Head
            int headR = s / 4;
            g2.fillOval(s / 2 - headR / 2, s / 5, headR, headR);
            // Body
            g2.fillArc(s / 5, s / 2, s * 3 / 5, s * 2 / 5, 0, 180);
        });
    }

    public static Icon createDoctorIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            // Stethoscope & cross
            int cx = s / 2;
            int cy = s / 2;
            int w = s / 3;
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Medical cross
            g2.drawLine(cx, cy - w, cx, cy + w);
            g2.drawLine(cx - w, cy, cx + w, cy);
            // Circle around
            g2.drawOval(cx - s * 3 / 8, cy - s * 3 / 8, s * 3 / 4, s * 3 / 4);
        });
    }

    public static Icon createCalendarIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f));
            int pad = s / 5;
            int w = s - pad * 2;
            int h = s - pad * 2;
            g2.drawRoundRect(pad, pad, w, h, 4, 4);
            g2.drawLine(pad, pad + h / 3, pad + w, pad + h / 3);
            // Pegs
            g2.fillRect(pad + 4, pad - 2, 3, 4);
            g2.fillRect(pad + w - 7, pad - 2, 3, 4);
        });
    }

    public static Icon createPillIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f));
            // Capsule shape
            int w = s * 3 / 5;
            int h = s / 3;
            AffineTransform old = g2.getTransform();
            g2.translate(s / 2, s / 2);
            g2.rotate(Math.toRadians(45));
            g2.drawRoundRect(-w / 2, -h / 2, w, h, h, h);
            g2.drawLine(0, -h / 2, 0, h / 2);
            g2.setTransform(old);
        });
    }

    public static Icon createReportIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f));
            int pad = s / 4;
            // Document outline
            g2.drawRect(pad, pad - 2, s / 2, s * 3 / 5);
            // Text lines
            g2.drawLine(pad + 3, pad + 3, pad + s / 2 - 3, pad + 3);
            g2.drawLine(pad + 3, pad + 7, pad + s / 2 - 3, pad + 7);
            g2.drawLine(pad + 3, pad + 11, pad + s / 2 - 5, pad + 11);
        });
    }

    public static Icon createSearchIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int r = s * 3 / 8;
            g2.drawOval(s / 5, s / 5, r, r);
            g2.drawLine(s / 5 + r - 2, s / 5 + r - 2, s * 4 / 5, s * 4 / 5);
        });
    }

    public static Icon createLogoIcon(int size) {
        return new VectorIcon(size, (g2, s) -> {
            // Heart/cross medical logo
            int cx = s / 2;
            int cy = s / 2;
            int r = s / 2 - 2;
            // Circular background
            g2.setColor(new Color(0, 180, 216));
            g2.fillOval(2, 2, s - 4, s - 4);

            // White cross inside
            g2.setColor(Color.WHITE);
            int barW = s / 5;
            int barL = s * 3 / 5;
            g2.fillRoundRect(cx - barW / 2, cy - barL / 2, barW, barL, 3, 3);
            g2.fillRoundRect(cx - barL / 2, cy - barW / 2, barL, barW, 3, 3);
        });
    }

    public static Icon createSortIcon(int size, Color color) {
        return new VectorIcon(size, (g2, s) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(s / 4, s / 4, s / 4, s * 3 / 4);
            g2.drawLine(s / 4 - 3, s / 2, s / 4, s * 3 / 4);
            g2.drawLine(s / 4 + 3, s / 2, s / 4, s * 3 / 4);

            g2.drawLine(s * 3 / 4, s * 3 / 4, s * 3 / 4, s / 4);
            g2.drawLine(s * 3 / 4 - 3, s / 2, s * 3 / 4, s / 4);
            g2.drawLine(s * 3 / 4 + 3, s / 2, s * 3 / 4, s / 4);
        });
    }

    @FunctionalInterface
    public interface IconPainter {
        void paint(Graphics2D g2, int size);
    }

    private static class VectorIcon implements Icon {
        private final int size;
        private final IconPainter painter;

        public VectorIcon(int size, IconPainter painter) {
            this.size = size;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            painter.paint(g2, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}