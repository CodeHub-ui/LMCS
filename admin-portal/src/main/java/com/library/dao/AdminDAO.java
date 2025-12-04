// Placeholder for AdminDAO.java
package com.library.dao;

import com.library.model.Admin;
import com.library.util.PasswordUtil;
import com.library.util.EmailService;
import com.library.util.MobileNumberValidator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class AdminDAO {
    private static String lastErrorMessage = null;

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean register(Admin admin) {
        // Check if email is already taken
        if (getAdminByEmail(admin.getEmail()) != null) {
            lastErrorMessage = "Email is already in use by another admin";
            return false;
        }

        String sql = "INSERT INTO admins (admin_id, name, email, mobile, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getAdminId());
            stmt.setString(2, admin.getName());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getMobile());
            stmt.setString(5, PasswordUtil.hashPassword(admin.getPasswordHash()));
            stmt.executeUpdate();
            lastErrorMessage = null;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public Admin login(String adminId, String password) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hash)) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setAdminId(rs.getString("admin_id"));
                    admin.setName(rs.getString("name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setMobile(rs.getString("mobile"));
                    admin.setPasswordHash(hash);
                    lastErrorMessage = null;
                    return admin;
                } else {
                    // Password mismatch
                    lastErrorMessage = "Incorrect password";
                    return null;
                }
            } else {
                // No such admin
                lastErrorMessage = "Admin not found";
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = e.getMessage();
        }
        return null;
    }

    public Admin getAdminByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setAdminId(rs.getString("admin_id"));
                admin.setName(rs.getString("name"));
                admin.setEmail(rs.getString("email"));
                admin.setMobile(rs.getString("mobile"));
                admin.setPasswordHash(rs.getString("password_hash"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int adminId, String newPasswordHash) {
        String sql = "UPDATE admins SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hashPassword(newPasswordHash));
            stmt.setInt(2, adminId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int adminId, String name, String email, String mobile) {
        String sql = "UPDATE admins SET name = ?, email = ?, mobile = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, mobile);
            stmt.setInt(4, adminId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateResetCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

    public boolean sendPasswordResetEmail(String email) {
        Admin admin = getAdminByEmail(email);
        if (admin == null) {
            return false;
        }
        String resetCode = generateResetCode();
        // Store reset code temporarily, but for simplicity, we'll send it directly
        String subject = "Password Reset Verification";
        String body = "Your password reset code is: " + resetCode + "\n\nUse this code to reset your password.";
        EmailService emailService = new EmailService();
        return emailService.sendEmail(email, subject, body);
    }

    public boolean isEmailTakenByAnotherAdmin(String email, int currentAdminId) {
        String sql = "SELECT COUNT(*) FROM admins WHERE email = ? AND id != ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, currentAdminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================
    // Get admin by mobile
    // ============================
    public Admin getAdminByMobile(String mobile) {
        String sql = "SELECT * FROM admins WHERE mobile = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mobile);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setAdminId(rs.getString("admin_id"));
                admin.setName(rs.getString("name"));
                admin.setEmail(rs.getString("email"));
                admin.setMobile(rs.getString("mobile"));
                admin.setPasswordHash(rs.getString("password_hash"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
