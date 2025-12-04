package com.library;

import com.library.dao.DatabaseUtil;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtilTest {

    @Test
    public void testDatabaseConnection() {
        // Set Supabase credentials for testing
        System.setProperty("db.host", "db.yctoxgzswavkcxscoxyk.supabase.co");
        System.setProperty("db.port", "5432");
        System.setProperty("db.name", "postgres");
        System.setProperty("db.user", "postgres");
        System.setProperty("db.pass", "2005");

        try {
            Connection conn = DatabaseUtil.getConnection();
            if (conn != null) {
                System.out.println("Database connection successful!");
                conn.close();
            } else {
                System.out.println("Database connection failed!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testInitializeDatabase() {
        // Set Supabase credentials for testing
        System.setProperty("db.host", "db.yctoxgzswavkcxscoxyk.supabase.co");
        System.setProperty("db.port", "5432");
        System.setProperty("db.name", "postgres");
        System.setProperty("db.user", "postgres");
        System.setProperty("db.pass", "2005");

        DatabaseUtil.initializeDatabase();
        System.out.println("Database initialization completed.");
    }
}
