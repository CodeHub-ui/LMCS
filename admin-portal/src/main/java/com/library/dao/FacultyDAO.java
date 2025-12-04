// Placeholder for FacultyDAO.java
package com.library.dao;

import com.library.model.Faculty;
import com.library.util.EmailValidator;
import com.library.util.MobileNumberValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {
    public int getTotalFaculty() {
        String sql = "SELECT COUNT(*) FROM faculty";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getBlockedFaculty() {
        String sql = "SELECT COUNT(*) FROM faculty WHERE active = FALSE";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Faculty> getAllFaculty(boolean active) {
        String sql = "SELECT * FROM faculty WHERE active = ?";
        List<Faculty> faculty = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Faculty f = new Faculty();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setFacultyId(rs.getString("faculty_id"));
                f.setEmail(rs.getString("email"));
                f.setMobile(rs.getString("mobile"));
                f.setActive(rs.getBoolean("active"));
                f.setRfid(rs.getString("rfid"));
                faculty.add(f);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return faculty;
    }

    public List<Faculty> searchFaculty(String query) {
        String sql = "SELECT * FROM faculty WHERE name LIKE ? OR faculty_id LIKE ?";
        List<Faculty> faculty = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Faculty f = new Faculty();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setFacultyId(rs.getString("faculty_id"));
                f.setEmail(rs.getString("email"));
                f.setMobile(rs.getString("mobile"));
                f.setActive(rs.getBoolean("active"));
                f.setRfid(rs.getString("rfid"));
                faculty.add(f);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return faculty;
    }

    /**
     * Deletes all faculty records from the database.
     *
     * @return true if successful, false otherwise
     */
    public boolean deleteAllFaculty() {
        String sql = "DELETE FROM faculty";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            LogDAO.log("All faculty records deleted. Rows affected: " + affectedRows);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if RFID exists in faculty table.
     * @param rfid the RFID to check
     * @return true if RFID exists, false otherwise
     */
    private boolean isRfidExistsInFaculty(String rfid) {
        String sql = "SELECT COUNT(*) FROM faculty WHERE rfid = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if RFID exists in students table.
     * @param rfid the RFID to check
     * @return true if RFID exists, false otherwise
     */
    private boolean isRfidExistsInStudents(String rfid) {
        String sql = "SELECT COUNT(*) FROM students WHERE rfid = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if RFID exists in either faculty or student table.
     * @param rfid the RFID to check
     * @return true if exists, false otherwise
     */
    private boolean isRfidDuplicate(String rfid) {
        return isRfidExistsInFaculty(rfid) || isRfidExistsInStudents(rfid);
    }

    /**
     * Registers a new faculty after ensuring RFID uniqueness.
     * @param faculty Faculty entity to insert
     * @return true if inserted successfully, false otherwise (duplicate RFID or error)
     */
    public boolean register(Faculty faculty) {
        if (isRfidDuplicate(faculty.getRfid())) {
            System.err.println("Duplicate RFID detected: " + faculty.getRfid());
            return false;
        }

        String sql = "INSERT INTO faculty (name, faculty_id, email, mobile, rfid) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, faculty.getName());
            stmt.setString(2, faculty.getFacultyId());
            stmt.setString(3, faculty.getEmail());
            stmt.setString(4, faculty.getMobile());
            stmt.setString(5, faculty.getRfid());
            stmt.executeUpdate();
            LogDAO.log("Faculty registered: " + faculty.getName());
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Faculty getFacultyByRFID(String rfid) {
        String sql = "SELECT * FROM faculty WHERE rfid = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Faculty f = new Faculty();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setFacultyId(rs.getString("faculty_id"));
                f.setEmail(rs.getString("email"));
                f.setMobile(rs.getString("mobile"));
                f.setActive(rs.getBoolean("active"));
                f.setRfid(rs.getString("rfid"));
                return f;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean blockFaculty(int id) {
        String sql = "UPDATE faculty SET active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            LogDAO.log("Faculty blocked: ID " + id);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean unblockFaculty(int id) {
        String sql = "UPDATE faculty SET active = TRUE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            LogDAO.log("Faculty unblocked: ID " + id);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateFaculty(Faculty faculty) {
        String sql = "UPDATE faculty SET name = ?, faculty_id = ?, email = ?, mobile = ?, rfid = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, faculty.getName());
            stmt.setString(2, faculty.getFacultyId());
            stmt.setString(3, faculty.getEmail());
            stmt.setString(4, faculty.getMobile());
            stmt.setString(5, faculty.getRfid());
            stmt.setBoolean(6, faculty.isActive());
            stmt.setInt(7, faculty.getId());
            stmt.executeUpdate();
            LogDAO.log("Faculty updated: ID " + faculty.getId());
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteFaculty(int id) {
        String sql = "DELETE FROM faculty WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            LogDAO.log("Faculty deleted: ID " + id);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ============================
    // Get faculty by email
    // ============================
    public Faculty getFacultyByEmail(String email) {
        String sql = "SELECT * FROM faculty WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Faculty f = new Faculty();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setFacultyId(rs.getString("faculty_id"));
                f.setEmail(rs.getString("email"));
                f.setMobile(rs.getString("mobile"));
                f.setActive(rs.getBoolean("active"));
                f.setRfid(rs.getString("rfid"));
                return f;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ============================
    // Get faculty by mobile
    // ============================
    public Faculty getFacultyByMobile(String mobile) {
        String sql = "SELECT * FROM faculty WHERE mobile = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mobile);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Faculty f = new Faculty();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setFacultyId(rs.getString("faculty_id"));
                f.setEmail(rs.getString("email"));
                f.setMobile(rs.getString("mobile"));
                f.setActive(rs.getBoolean("active"));
                f.setRfid(rs.getString("rfid"));
                return f;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
