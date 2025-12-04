package com.library.dao;

import com.library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for User entities.
 * Provides methods to interact with the users table in the database.
 */
public class UserDAO {

    /**
     * Authenticates a user using their RFID tag.
     * Retrieves user details if the RFID matches an active user record in either students or faculty table.
     *
     * @param rfid the RFID tag of the user
     * @return a User object if authentication succeeds, null otherwise
     */
    public User loginByRfid(String rfid) {
        // First, try to find in students table
        String studentSql = "SELECT * FROM students WHERE rfid = ? AND active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(studentSql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setStudentId(rs.getString("student_id"));
                u.setEmail(rs.getString("email"));
                u.setMobile(rs.getString("mobile"));
                u.setRfid(rs.getString("rfid"));
                u.setCourse(rs.getString("course"));
                u.setActive(rs.getBoolean("active"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }

        // If not found in students, try faculty table
        String facultySql = "SELECT * FROM faculty WHERE rfid = ? AND active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(facultySql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setStudentId(rs.getString("faculty_id")); // Use faculty_id as studentId for compatibility
                u.setEmail(rs.getString("email"));
                u.setMobile(rs.getString("mobile"));
                u.setRfid(rs.getString("rfid"));
                u.setCourse(""); // Faculty does not have a course field, set to empty
                u.setActive(rs.getBoolean("active"));
                return u;
            } else {
                // Check if RFID exists but is inactive in faculty
                String checkSql = "SELECT active FROM faculty WHERE rfid = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, rfid);
                    ResultSet checkRs = checkStmt.executeQuery();
                    if (checkRs.next()) {
                        boolean active = checkRs.getBoolean("active");
                        if (!active) {
                            throw new RuntimeException("Account is inactive. Please contact administrator.");
                        }
                    } else {
                        throw new RuntimeException("RFID not found in database.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        } catch (RuntimeException e) {
            throw e; // Re-throw custom exceptions
        }
        return null;
    }

    /**
     * Authenticates a user using username and password.
     * Retrieves user details if the username and password match an active user record.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication succeeds, false otherwise
     */
    public boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ? AND active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a user by username.
     * @param username the username of the user
     * @return a User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setStudentId(rs.getString("user_id"));
                u.setEmail(rs.getString("email"));
                u.setMobile(rs.getString("mobile"));
                u.setRfid(rs.getString("rfid"));
                u.setCourse(rs.getString("course"));
                u.setActive(rs.getBoolean("active"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the total number of users in the database.
     * This method can be used for statistics or reporting purposes.
     *
     * @return the total count of users
     */
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
