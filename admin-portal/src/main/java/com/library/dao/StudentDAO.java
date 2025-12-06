package com.library.dao;

import com.library.model.Student;
import com.library.util.MobileNumberValidator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ============================
    // Get total registered students
    // ============================
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================
    // Get count of blocked/inactive students
    // ============================
    public int getBlockedStudents() {
        String sql = "SELECT COUNT(*) FROM students WHERE active = FALSE";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ============================
    // Fetch all students (active / inactive)
    // ============================
    public List<Student> getAllStudents(boolean active) {
        String sql = "SELECT * FROM students WHERE active = ?";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setStudentId(rs.getString("student_id"));
                s.setEmail(rs.getString("email"));
                s.setMobile(rs.getString("mobile"));
                s.setRfid(rs.getString("rfid"));
                s.setCourse(rs.getString("course"));
                s.setActive(rs.getBoolean("active"));
                students.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // ============================
    // Search student by name or ID
    // ============================
    public List<Student> searchStudents(String query) {
        String sql = "SELECT * FROM students WHERE LOWER(name) LIKE LOWER(?) OR LOWER(student_id) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR LOWER(mobile) LIKE LOWER(?) OR LOWER(course) LIKE LOWER(?)";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setStudentId(rs.getString("student_id"));
                s.setEmail(rs.getString("email"));
                s.setMobile(rs.getString("mobile"));
                s.setRfid(rs.getString("rfid"));
                s.setCourse(rs.getString("course"));
                s.setActive(rs.getBoolean("active"));
                students.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // ============================
    // Register a new student
    // ============================
    public boolean register(Student student) {
        // Check if email is globally unique across all user types
        if (!com.library.util.EmailValidator.isEmailGloballyUnique(student.getEmail())) {
            System.err.println("Email already exists in the system: " + student.getEmail());
            return false;
        }

        // Check if mobile number is valid and globally unique across all user types
        String mobileValidationError = MobileNumberValidator.validateMobileNumberUniqueness(student.getMobile());
        if (!mobileValidationError.isEmpty()) {
            System.err.println("Mobile number validation failed: " + mobileValidationError);
            return false;
        }

        String sql = "INSERT INTO students (name, student_id, email, mobile, rfid, course) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getStudentId());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getMobile());
            stmt.setString(5, student.getRfid());
            stmt.setString(6, student.getCourse());
            stmt.executeUpdate();
            LogDAO.log("Student registered: " + student.getName());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================
    // Block student (set active = false)
    // ============================
    public boolean blockStudent(int id) {
        String sql = "UPDATE students SET active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            LogDAO.log("Student blocked: ID " + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================
    // Unblock student (set active = true)
    // ============================
    public boolean unblockStudent(int id) {
        String sql = "UPDATE students SET active = TRUE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            LogDAO.log("Student unblocked: ID " + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================
    // Update student details
    // ============================
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, student_id = ?, email = ?, mobile = ?, rfid = ?, course = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getStudentId());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getMobile());
            stmt.setString(5, student.getRfid());
            stmt.setString(6, student.getCourse());
            stmt.setBoolean(7, student.isActive());
            stmt.setInt(8, student.getId());
            stmt.executeUpdate();
            LogDAO.log("Student updated: ID " + student.getId());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================
    // Check if student is registered and active
    // ============================
    public boolean isStudentRegistered(String studentId) throws SQLException {
        String sql = "SELECT active FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("active");
                }
            }
        }
        return false;
    }

    // ============================
    // Get student details by student_id (for display)
    // ============================
    public Student getStudentById(String studentId) throws SQLException {
        String sql = "SELECT id, name, email, mobile FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setMobile(rs.getString("mobile"));
                    return s;
                }
            }
        }
        return null;
    }

    // ============================
    // ✅ NEW: Get student by RFID number
    // Used to ensure one RFID is used only once
    // ============================
    public Student getStudentByRFID(String rfid) {
        Student student = null;
        String sql = "SELECT * FROM students WHERE rfid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rfid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setStudentId(rs.getString("student_id"));
                student.setEmail(rs.getString("email"));
                student.setMobile(rs.getString("mobile"));
                student.setRfid(rs.getString("rfid"));
                student.setActive(rs.getBoolean("active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    /**
     * Deletes a student by their internal ID, but only if they have no issued books.
     * This prevents data inconsistency by ensuring all books are returned before deletion.
     *
     * @param id The internal ID of the student to delete.
     * @return true if the student was successfully deleted, false otherwise (e.g., if books are issued).
     */
    public boolean deleteStudent(int id) {
        try {
            // Check if the student has any issued books
            IssuedBookDAO issuedDao = new IssuedBookDAO();
            String studentIdStr = getStudentIdById(id);
            Student student = getStudentById(studentIdStr);
            if (student != null) {
                List<String> issuedBooks = issuedDao.getIssuedBooksForStudent(student.getRfid());
                if (!issuedBooks.isEmpty()) {
                    // Student has issued books, cannot delete
                    return false;
                }
            }

            String sql = "DELETE FROM students WHERE id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    LogDAO.log("Student deleted: ID " + id);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the student ID (string) by the internal ID (integer).
     * This is a helper method used internally for checking issued books.
     *
     * @param id The internal ID of the student.
     * @return The student ID string, or null if not found.
     */
    private String getStudentIdById(int id) {
        String sql = "SELECT student_id FROM students WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("student_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================
    // Get student by email
    // ============================
    public Student getStudentByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setStudentId(rs.getString("student_id"));
                s.setEmail(rs.getString("email"));
                s.setMobile(rs.getString("mobile"));
                s.setRfid(rs.getString("rfid"));
                s.setCourse(rs.getString("course"));
                s.setActive(rs.getBoolean("active"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================
    // Get student by mobile
    // ============================
    public Student getStudentByMobile(String mobile) {
        String sql = "SELECT * FROM students WHERE mobile = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mobile);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setStudentId(rs.getString("student_id"));
                s.setEmail(rs.getString("email"));
                s.setMobile(rs.getString("mobile"));
                s.setRfid(rs.getString("rfid"));
                s.setCourse(rs.getString("course"));
                s.setActive(rs.getBoolean("active"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
