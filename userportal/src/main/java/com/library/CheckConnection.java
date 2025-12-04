package com.library;

import java.sql.*;
import com.library.dao.DatabaseUtil;

public class CheckConnection {
    public static void main(String[] args) {
        // Initialize the database to ensure tables exist
        DatabaseUtil.initializeDatabase();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if students table has data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Total students in database: " + count);
            }

            // List all students
            rs = stmt.executeQuery("SELECT id, name, student_id, rfid, active FROM students");
            System.out.println("\nStudents data:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Name: " + rs.getString("name") +
                                   ", Student ID: " + rs.getString("student_id") +
                                   ", RFID: " + rs.getString("rfid") +
                                   ", Active: " + rs.getBoolean("active"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
