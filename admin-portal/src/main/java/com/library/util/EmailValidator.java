package com.library.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import com.library.dao.DatabaseUtil;

/**
 * EmailValidator provides validation methods for email addresses.
 * Ensures email addresses are properly formatted, contain domain information,
 * and are globally unique across all user types (Student, Faculty, Admin).
 */
public class EmailValidator {

    // Regex pattern for basic email validation
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Validates if the given email address is properly formatted.
     * Must contain @ symbol and a valid domain.
     *
     * @param email the email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates an email address and returns an error message if invalid.
     * Returns empty string if valid.
     *
     * @param email the email address to validate
     * @return empty string if valid, error message if invalid
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required.";
        }

        email = email.trim();

        if (!email.contains("@")) {
            return "Email address must contain '@' symbol.";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address (e.g., user@domain.com).";
        }

        // Check for common domain issues
        String domain = email.substring(email.indexOf("@") + 1);
        if (domain.length() < 4) {
            return "Email domain appears to be incomplete.";
        }

        return ""; // Valid
    }

    /**
     * Checks if the email contains a complete domain part.
     * This helps identify if the domain part is being truncated.
     *
     * @param email the email address to check
     * @return true if domain is present, false otherwise
     */
    public static boolean hasCompleteDomain(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }

        String domain = email.substring(email.indexOf("@") + 1);
        return domain.contains(".") && domain.length() >= 3;
    }

    /**
     * Checks if the email address is globally unique across all user types.
     * Queries students, faculty, and admins tables to ensure no duplicate email exists.
     *
     * @param email the email address to check for uniqueness
     * @return true if email is unique across all user tables, false if it already exists
     */
    public static boolean isEmailGloballyUnique(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Invalid email cannot be unique
        }

        email = email.trim();

        // First validate email format
        if (!isValidEmail(email)) {
            return false; // Invalid format cannot be unique
        }

        // SQL query to check across all user tables using UNION
        String sql = "SELECT COUNT(*) as total FROM (" +
                     "SELECT email FROM students WHERE email = ? " +
                     "UNION " +
                     "SELECT email FROM faculty WHERE email = ? " +
                     "UNION " +
                     "SELECT email FROM admins WHERE email = ?" +
                     ") as combined_emails";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the email parameter for all three SELECT statements
            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.setString(3, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("total");
                    return count == 0; // Unique if no records found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // In case of database error, assume not unique for safety
            return false;
        }

        return false; // Default to not unique on error
    }

    /**
     * Validates email format and global uniqueness.
     * Returns a descriptive error message if validation fails.
     *
     * @param email the email address to validate
     * @return empty string if valid and unique, error message otherwise
     */
    public static String validateEmailUniqueness(String email) {
        String formatError = validateEmail(email);
        if (!formatError.isEmpty()) {
            return formatError;
        }

        if (!isEmailGloballyUnique(email)) {
            return "This email address is already registered in the system.";
        }

        return ""; // Valid and unique
    }
}
