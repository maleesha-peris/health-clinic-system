package com.healthclinic.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Modern Interactive Calendar & Time Picker Dialog.
 * Supports Date-Only selection (YYYY-MM-DD) and Date-Time selection (YYYY-MM-DD HH:mm).
 * Built with pure Java Swing and java.time.
 */
public class DatePickerDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final boolean includeTime;
    private LocalDate selectedDate;
    private int selectedHour = 10;
    private int selectedMinute = 0;
    private boolean confirmed = false;

    // UI elements
    private JLabel lblMonthYear;
    private JPanel daysPanel;
    private JComboBox<String> cmbHour;
    private JComboBox<String> cmbMinute;

    private int currentYear;
    private int currentMonth; // 1-12

    public DatePickerDialog(Window owner, String title, LocalDate initialDate, int initialHour, int initialMinute, boolean includeTime) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.includeTime = includeTime;
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.currentYear = this.selectedDate.getYear();
        this.currentMonth = this.selectedDate.getMonthValue();
        this.selectedHour = initialHour;
        this.selectedMinute = initialMinute;

        setSize(includeTime ? 380 : 360, includeTime ? 440 : 380);
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        // --- 1. Month / Year Navigation Header ---
        JPanel navHeader = new JPanel(new BorderLayout(8, 0));
        navHeader.setOpaque(false);

        ModernButton btnPrev = new ModernButton(" < ", ModernButton.ButtonStyle.SECONDARY);
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrev.addActionListener(e -> prevMonth());

        ModernButton btnNext = new ModernButton(" > ", ModernButton.ButtonStyle.SECONDARY);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.addActionListener(e -> nextMonth());

        lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblMonthYear.setForeground(UITheme.TEXT_PRIMARY);

        navHeader.add(btnPrev, BorderLayout.WEST);
        navHeader.add(lblMonthYear, BorderLayout.CENTER);
        navHeader.add(btnNext, BorderLayout.EAST);

        content.add(navHeader, BorderLayout.NORTH);

        // --- 2. Days Grid Panel ---
        JPanel calendarWrapper = new JPanel(new BorderLayout(4, 4));
        calendarWrapper.setOpaque(false);

        // Day names row (Su Mo Tu We Th Fr Sa)
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7, 4, 4));
        dayNamesPanel.setOpaque(false);
        String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(new Color(100, 116, 139)); // Slate
            dayNamesPanel.add(lbl);
        }
        calendarWrapper.add(dayNamesPanel, BorderLayout.NORTH);

        daysPanel = new JPanel(new GridLayout(6, 7, 4, 4));
        daysPanel.setOpaque(false);
        calendarWrapper.add(daysPanel, BorderLayout.CENTER);

        content.add(calendarWrapper, BorderLayout.CENTER);

        // --- 3. Bottom Controls (Time Selector + Action Buttons) ---
        JPanel bottomContainer = new JPanel(new BorderLayout(10, 10));
        bottomContainer.setOpaque(false);

        if (includeTime) {
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            timePanel.setOpaque(false);
            timePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                    new EmptyBorder(8, 0, 0, 0)
            ));

            JLabel lblTime = new JLabel("Select Time:");
            lblTime.setFont(UITheme.FONT_BOLD);
            lblTime.setForeground(UITheme.TEXT_PRIMARY);

            // Hours 00-23
            String[] hours = new String[24];
            for (int i = 0; i < 24; i++) hours[i] = String.format("%02d", i);
            cmbHour = new JComboBox<>(hours);
            cmbHour.setSelectedItem(String.format("%02d", selectedHour));

            // Minutes 00-55 in 5m increments
            String[] minutes = new String[12];
            for (int i = 0; i < 12; i++) minutes[i] = String.format("%02d", i * 5);
            cmbMinute = new JComboBox<>(minutes);
            int minBucket = (selectedMinute / 5) * 5;
            cmbMinute.setSelectedItem(String.format("%02d", minBucket));

            timePanel.add(lblTime);
            timePanel.add(cmbHour);
            timePanel.add(new JLabel(":"));
            timePanel.add(cmbMinute);

            bottomContainer.add(timePanel, BorderLayout.NORTH);
        }

        // Action Buttons Row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionRow.setOpaque(false);

        ModernButton btnToday = new ModernButton("Today", ModernButton.ButtonStyle.SECONDARY);
        btnToday.addActionListener(e -> {
            selectedDate = LocalDate.now();
            currentYear = selectedDate.getYear();
            currentMonth = selectedDate.getMonthValue();
            renderCalendar();
        });

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        ModernButton btnConfirm = new ModernButton("Confirm Date", ModernButton.ButtonStyle.PRIMARY);
        btnConfirm.addActionListener(e -> {
            confirmed = true;
            if (includeTime && cmbHour != null && cmbMinute != null) {
                selectedHour = Integer.parseInt((String) cmbHour.getSelectedItem());
                selectedMinute = Integer.parseInt((String) cmbMinute.getSelectedItem());
            }
            dispose();
        });

        actionRow.add(btnToday);
        actionRow.add(btnCancel);
        actionRow.add(btnConfirm);

        bottomContainer.add(actionRow, BorderLayout.SOUTH);
        content.add(bottomContainer, BorderLayout.SOUTH);

        setContentPane(content);
        renderCalendar();
    }

    private void prevMonth() {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        renderCalendar();
    }

    private void nextMonth() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        renderCalendar();
    }

    private void renderCalendar() {
        YearMonth ym = YearMonth.of(currentYear, currentMonth);
        lblMonthYear.setText(ym.getMonth().name() + " " + currentYear);

        daysPanel.removeAll();

        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeekIndex = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0, Monday = 1, ...
        int daysInMonth = ym.lengthOfMonth();

        // Previous month filler spaces
        YearMonth prevYm = ym.minusMonths(1);
        int prevMonthDays = prevYm.lengthOfMonth();
        for (int i = dayOfWeekIndex - 1; i >= 0; i--) {
            int d = prevMonthDays - i;
            JLabel lbl = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(new Color(203, 213, 225)); // Disabled gray
            daysPanel.add(lbl);
        }

        // Days of current month
        LocalDate today = LocalDate.now();
        for (int day = 1; day <= daysInMonth; day++) {
            final int d = day;
            JButton btnDay = new JButton(String.valueOf(d));
            btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDay.setFocusPainted(false);
            btnDay.setBorderPainted(false);
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDay.setMargin(new Insets(2, 2, 2, 2));

            boolean isSelected = selectedDate.getYear() == currentYear &&
                    selectedDate.getMonthValue() == currentMonth &&
                    selectedDate.getDayOfMonth() == d;

            boolean isToday = today.getYear() == currentYear &&
                    today.getMonthValue() == currentMonth &&
                    today.getDayOfMonth() == d;

            if (isSelected) {
                btnDay.setBackground(UITheme.PRIMARY);
                btnDay.setForeground(Color.WHITE);
                btnDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (isToday) {
                btnDay.setBackground(new Color(224, 242, 254)); // Soft cyan
                btnDay.setForeground(UITheme.PRIMARY);
                btnDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                btnDay.setBackground(Color.WHITE);
                btnDay.setForeground(UITheme.TEXT_PRIMARY);
            }

            btnDay.addActionListener(e -> {
                selectedDate = LocalDate.of(currentYear, currentMonth, d);
                renderCalendar();
            });

            daysPanel.add(btnDay);
        }

        // Next month filler spaces to complete 42 cells (6 rows x 7 cols)
        int cellsUsed = dayOfWeekIndex + daysInMonth;
        int remainingCells = 42 - cellsUsed;
        for (int i = 1; i <= remainingCells; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(new Color(203, 213, 225));
            daysPanel.add(lbl);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    /**
     * Static helper: Open calendar dialog to pick Date (YYYY-MM-DD).
     */
    public static String showDatePicker(Component parent, String initialDateStr) {
        LocalDate initDate = LocalDate.now();
        if (initialDateStr != null && !initialDateStr.trim().isEmpty()) {
            try {
                initDate = LocalDate.parse(initialDateStr.trim(), DATE_FMT);
            } catch (Exception ignored) {}
        }

        Window win = SwingUtilities.getWindowAncestor(parent);
        DatePickerDialog dlg = new DatePickerDialog(win, "Select Date", initDate, 0, 0, false);
        dlg.setVisible(true);

        if (dlg.confirmed) {
            return dlg.selectedDate.format(DATE_FMT);
        }
        return null;
    }

    /**
     * Static helper: Open calendar dialog to pick Date & Time (YYYY-MM-DD HH:mm).
     */
    public static String showDateTimePicker(Component parent, String initialDateTimeStr) {
        LocalDate initDate = LocalDate.now();
        int hour = 10;
        int min = 0;

        if (initialDateTimeStr != null && !initialDateTimeStr.trim().isEmpty()) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(initialDateTimeStr.trim(), DATE_TIME_FMT);
                initDate = ldt.toLocalDate();
                hour = ldt.getHour();
                min = ldt.getMinute();
            } catch (Exception ignored) {}
        }

        Window win = SwingUtilities.getWindowAncestor(parent);
        DatePickerDialog dlg = new DatePickerDialog(win, "Select Date & Time", initDate, hour, min, true);
        dlg.setVisible(true);

        if (dlg.confirmed) {
            LocalDateTime ldt = dlg.selectedDate.atTime(dlg.selectedHour, dlg.selectedMinute);
            return ldt.format(DATE_TIME_FMT);
        }
        return null;
    }
}