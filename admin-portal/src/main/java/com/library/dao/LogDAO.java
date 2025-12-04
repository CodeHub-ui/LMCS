// Placeholder for LogDAO.java
package com.library.dao;

import com.library.model.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    public static void log(String action) {
        String sql = "INSERT INTO logs (action) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Log> getAllLogs() {
        String sql = "SELECT * FROM logs ORDER BY timestamp DESC";
        List<Log> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Log l = new Log();
                l.setId(rs.getInt("id"));
                l.setAction(rs.getString("action"));
                l.setTimestamp(rs.getString("timestamp"));
                logs.add(l);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }
}