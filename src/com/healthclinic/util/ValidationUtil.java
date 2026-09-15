package com.healthclinic.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Input validation utility supporting Task 6 (Controller Layer).
 * High Cohesion: Pure utility for validating formatting rules.
 */
public final class ValidationUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private ValidationUtil() {}

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        requireNonBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email + ". Example: name@example.com");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        requireNonBlank(phone, "Phone Number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone number format: " + phone + ". Digits and dashes only.");
        }
    }

    public static LocalDate parseAndValidateDate(String dateStr, String fieldName) throws ValidationException {
        requireNonBlank(dateStr, fieldName);
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must match format YYYY-MM-DD (e.g. 2026-05-12).");
        }
    }

    public static LocalDateTime parseAndValidateDateTime(String dateTimeStr, String fieldName) throws ValidationException {
        requireNonBlank(dateTimeStr, fieldName);
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must match format YYYY-MM-DD HH:mm (e.g. 2026-05-12 14:30).");
        }
    }

    public static double parseAndValidatePositiveDouble(String numStr, String fieldName) throws ValidationException {
        requireNonBlank(numStr, fieldName);
        try {
            double val = Double.parseDouble(numStr.trim());
            if (val < 0) {
                throw new ValidationException(fieldName + " must be a non-negative number.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid numeric amount.");
        }
    }
}