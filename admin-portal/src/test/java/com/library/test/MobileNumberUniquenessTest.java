package com.library.test;

import com.library.util.MobileNumberValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MobileNumberUniquenessTest {

    @Test
    public void testValidMobileNumber() {
        // Test valid 10-digit mobile number
        String result = MobileNumberValidator.validateMobileNumberUniqueness("1234567890");
        assertEquals("", result, "Valid mobile number should return empty string");
    }

    @Test
    public void testInvalidMobileNumberTooShort() {
        // Test mobile number with less than 10 digits
        String result = MobileNumberValidator.validateMobileNumberUniqueness("123456789");
        assertEquals("Mobile number must be exactly 10 digits long.", result, "Short mobile number should be invalid");
    }

    @Test
    public void testInvalidMobileNumberTooLong() {
        // Test mobile number with more than 10 digits
        String result = MobileNumberValidator.validateMobileNumberUniqueness("12345678901");
        assertEquals("Mobile number must be exactly 10 digits long.", result, "Long mobile number should be invalid");
    }

    @Test
    public void testInvalidMobileNumberNonNumeric() {
        // Test mobile number with non-numeric characters
        String result = MobileNumberValidator.validateMobileNumberUniqueness("123456789a");
        assertEquals("Mobile number must contain only numeric digits (0-9).", result, "Non-numeric mobile number should be invalid");
    }

    @Test
    public void testNullMobileNumber() {
        // Test null mobile number
        String result = MobileNumberValidator.validateMobileNumberUniqueness(null);
        assertEquals("Mobile number is required.", result, "Null mobile number should be invalid");
    }

    @Test
    public void testEmptyMobileNumber() {
        // Test empty mobile number
        String result = MobileNumberValidator.validateMobileNumberUniqueness("");
        assertEquals("Mobile number is required.", result, "Empty mobile number should be invalid");
    }

    @Test
    public void testMobileNumberWithSpaces() {
        // Test mobile number with spaces
        String result = MobileNumberValidator.validateMobileNumberUniqueness("123 456 7890");
        assertEquals("Mobile number must be exactly 10 digits long.", result, "Mobile number with spaces should be invalid");
    }

    @Test
    public void testMobileNumberStartingWithZero() {
        // Test mobile number starting with zero (valid case)
        String result = MobileNumberValidator.validateMobileNumberUniqueness("0123456789");
        assertEquals("", result, "Mobile number starting with zero should be valid");
    }

    @Test
    public void testMobileNumberWithSpecialCharacters() {
        // Test mobile number with special characters
        String result = MobileNumberValidator.validateMobileNumberUniqueness("123-456-7890");
        assertEquals("Mobile number must be exactly 10 digits long.", result, "Mobile number with special characters should be invalid");
    }
}
