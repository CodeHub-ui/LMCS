package com.library.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.library.dao.DatabaseUtil;

/**
 * MobileNumberValidator provides validation methods for Indian mobile numbers.
 * All Indian mobile numbers must be exactly 10 digits long, contain only numeric characters,
 * and be globally unique across all user types (Student, Faculty, Admin).
 */
public class MobileNumberValidator {

    /**
     * Validates if the given mobile number is a valid Indian mobile number.
     * Must be exactly 10 digits and contain only numeric characters.
     *
     * @param mobileNumber the mobile number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidIndianMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            return false;
        }

        // Check if exactly 10 digits and contains only numbers
        return mobileNumber.matches("\\d{10}");
    }

    /**
     * Validates an Indian mobile number and returns an error message if invalid.
     * Returns empty string if valid.
     *
     * @param mobileNumber the mobile number to validate
     * @return empty string if valid, error message if invalid
     */
    public static String validateIndianMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return "Mobile number is required.";
        }

        mobileNumber = mobileNumber.trim();

        if (mobileNumber.length() != 10) {
            return "Mobile number must be exactly 10 digits long.";
        }

        if (!mobileNumber.matches("\\d{10}")) {
            return "Mobile number must contain only numeric digits (0-9).";
        }

        return ""; // Valid
    }

    /**
     * Checks if the mobile number is globally unique across all user types.
     * Queries students, faculty, and admins tables to ensure no duplicate mobile number exists.
     *
     * @param mobileNumber the mobile number to check for uniqueness
     * @return true if mobile number is unique across all user tables, false if it already exists
     */
    public static boolean isMobileNumberGloballyUnique(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return false; // Invalid mobile number cannot be unique
        }

        mobileNumber = mobileNumber.trim();

        // First validate mobile number format
        if (!isValidIndianMobileNumber(mobileNumber)) {
            return false; // Invalid format cannot be unique
        }

        // SQL query to check across all user tables using UNION
        String sql = "SELECT COUNT(*) as total FROM (" +
                     "SELECT mobile FROM students WHERE mobile = ? " +
                     "UNION " +
                     "SELECT mobile FROM faculty WHERE mobile = ? " +
                     "UNION " +
                     "SELECT mobile FROM admins WHERE mobile = ?" +
                     ") as combined_mobiles";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the mobile parameter for all three SELECT statements
            stmt.setString(1, mobileNumber);
            stmt.setString(2, mobileNumber);
            stmt.setString(3, mobileNumber);

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
     * Validates mobile number format and global uniqueness.
     * Returns a descriptive error message if validation fails.
     *
     * @param mobileNumber the mobile number to validate
     * @return empty string if valid and unique, error message otherwise
     */
    public static String validateMobileNumberUniqueness(String mobileNumber) {
        String formatError = validateIndianMobileNumber(mobileNumber);
        if (!formatError.isEmpty()) {
            return formatError;
        }

        if (!isMobileNumberGloballyUnique(mobileNumber)) {
            return "This mobile number is already registered in the system.";
        }

        return ""; // Valid and unique
    }
}
