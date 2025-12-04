package com.library.util;

import com.library.model.Student;
import com.library.model.Faculty;
import com.library.model.Admin;
import com.library.dao.StudentDAO;
import com.library.dao.FacultyDAO;
import com.library.dao.AdminDAO;

/**
 * Utility class to find existing account details when duplicates are detected
 * during registration attempts.
 */
public class DuplicateAccountFinder {

    /**
     * Response object containing details of an existing account
     */
    public static class ExistingAccountDetails {
        private String userType; // "Student", "Faculty", or "Admin"
        private String name;
        private String email;
        private String mobile;
        private String userId; // student_id, faculty_id, or admin_id

        public ExistingAccountDetails(String userType, String name, String email, String mobile, String userId) {
            this.userType = userType;
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.userId = userId;
        }

        // Getters
        public String getUserType() { return userType; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getMobile() { return mobile; }
        public String getUserId() { return userId; }

        @Override
        public String toString() {
            return String.format("Existing Account Details:\n" +
                               "Type: %s\n" +
                               "Name: %s\n" +
                               "ID: %s\n" +
                               "Email: %s\n" +
                               "Mobile: %s",
                               userType, name, userId, email, mobile);
        }
    }

    /**
     * Finds existing account details by email address
     * Searches across all user types (Student, Faculty, Admin)
     *
     * @param email the email address to search for
     * @return ExistingAccountDetails if found, null if not found
     */
    public static ExistingAccountDetails findExistingAccountByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        email = email.trim();

        // Search in Students table
        StudentDAO studentDAO = new StudentDAO();
        try {
            Student student = studentDAO.getStudentByEmail(email);
            if (student != null) {
                return new ExistingAccountDetails("Student", student.getName(),
                                                student.getEmail(), student.getMobile(),
                                                student.getStudentId());
            }
        } catch (Exception e) {
            // Continue searching other tables
        }

        // Search in Faculty table
        FacultyDAO facultyDAO = new FacultyDAO();
        Faculty faculty = facultyDAO.getFacultyByEmail(email);
        if (faculty != null) {
            return new ExistingAccountDetails("Faculty", faculty.getName(),
                                            faculty.getEmail(), faculty.getMobile(),
                                            faculty.getFacultyId());
        }

        // Search in Admins table
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.getAdminByEmail(email);
        if (admin != null) {
            return new ExistingAccountDetails("Admin", admin.getName() != null ? admin.getName() : "N/A",
                                            admin.getEmail(), admin.getMobile(),
                                            admin.getAdminId());
        }

        return null; // Not found
    }

    /**
     * Finds existing account details by mobile number
     * Searches across all user types (Student, Faculty, Admin)
     *
     * @param mobile the mobile number to search for
     * @return ExistingAccountDetails if found, null if not found
     */
    public static ExistingAccountDetails findExistingAccountByMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return null;
        }

        mobile = mobile.trim();

        // Search in Students table
        StudentDAO studentDAO = new StudentDAO();
        try {
            Student student = studentDAO.getStudentByMobile(mobile);
            if (student != null) {
                return new ExistingAccountDetails("Student", student.getName(),
                                                student.getEmail(), student.getMobile(),
                                                student.getStudentId());
            }
        } catch (Exception e) {
            // Continue searching other tables
        }

        // Search in Faculty table
        FacultyDAO facultyDAO = new FacultyDAO();
        Faculty faculty = facultyDAO.getFacultyByMobile(mobile);
        if (faculty != null) {
            return new ExistingAccountDetails("Faculty", faculty.getName(),
                                            faculty.getEmail(), faculty.getMobile(),
                                            faculty.getFacultyId());
        }

        // Search in Admins table
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.getAdminByMobile(mobile);
        if (admin != null) {
            return new ExistingAccountDetails("Admin", admin.getName() != null ? admin.getName() : "N/A",
                                            admin.getEmail(), admin.getMobile(),
                                            admin.getAdminId());
        }

        return null; // Not found
    }

    /**
     * Finds existing account details by either email or mobile number
     * Returns the first match found (prioritizes email if both are provided)
     *
     * @param email the email address to search for (optional)
     * @param mobile the mobile number to search for (optional)
     * @return ExistingAccountDetails if found, null if not found
     */
    public static ExistingAccountDetails findExistingAccount(String email, String mobile) {
        // First try email if provided
        if (email != null && !email.trim().isEmpty()) {
            ExistingAccountDetails result = findExistingAccountByEmail(email);
            if (result != null) {
                return result;
            }
        }

        // Then try mobile if provided
        if (mobile != null && !mobile.trim().isEmpty()) {
            ExistingAccountDetails result = findExistingAccountByMobile(mobile);
            if (result != null) {
                return result;
            }
        }

        return null; // Not found
    }
}
