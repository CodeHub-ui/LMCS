package com.library.service;

import com.library.util.DuplicateAccountFinder;
import com.library.util.DuplicateAccountFinder.ExistingAccountDetails;

/**
 * RegistrationService provides centralized validation for user registrations
 * across Students, Faculty, and Admins. It prevents duplicate registrations
 * based on email or mobile number.
 */
public class RegistrationService {

    /**
     * RegistrationResult encapsulates the outcome of a registration attempt.
     */
    public static class RegistrationResult {
        private boolean success;
        private String errorMessage;
        private ExistingAccountDetails existingAccount;

        public RegistrationResult(boolean success, String errorMessage, ExistingAccountDetails existingAccount) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.existingAccount = existingAccount;
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public ExistingAccountDetails getExistingAccount() { return existingAccount; }

        @Override
        public String toString() {
            if (success) {
                return "Registration successful";
            } else if (existingAccount != null) {
                return "Duplicate account found: " + existingAccount.toString();
            } else {
                return "Registration failed: " + errorMessage;
            }
        }
    }

    /**
     * Validates registration data for duplicates.
     * Checks if the provided email or mobile number already exists in any user table.
     *
     * @param email the email address to check
     * @param mobile the mobile number to check
     * @return RegistrationResult indicating success or failure with details
     */
    public static RegistrationResult validateRegistration(String email, String mobile) {
        // Check for existing account by email
        if (email != null && !email.trim().isEmpty()) {
            ExistingAccountDetails existingByEmail = DuplicateAccountFinder.findExistingAccountByEmail(email.trim());
            if (existingByEmail != null) {
                return new RegistrationResult(false, "Email address already registered", existingByEmail);
            }
        }

        // Check for existing account by mobile
        if (mobile != null && !mobile.trim().isEmpty()) {
            ExistingAccountDetails existingByMobile = DuplicateAccountFinder.findExistingAccountByMobile(mobile.trim());
            if (existingByMobile != null) {
                return new RegistrationResult(false, "Mobile number already registered", existingByMobile);
            }
        }

        // No duplicates found
        return new RegistrationResult(true, null, null);
    }

    /**
     * Validates registration data for duplicates using combined check.
     * This is a convenience method that checks both email and mobile in one call.
     *
     * @param email the email address to check
     * @param mobile the mobile number to check
     * @return RegistrationResult indicating success or failure with details
     */
    public static RegistrationResult validateRegistrationCombined(String email, String mobile) {
        ExistingAccountDetails existing = DuplicateAccountFinder.findExistingAccount(email, mobile);
        if (existing != null) {
            String conflictField = (email != null && !email.trim().isEmpty() &&
                                   existing.getEmail().equals(email.trim())) ? "email" : "mobile";
            return new RegistrationResult(false, conflictField + " already registered", existing);
        }
        return new RegistrationResult(true, null, null);
    }
}
