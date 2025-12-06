package com.library.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    /**
     * Database configuration for Supabase (PostgreSQL).
     * Configurable via system properties or environment variables:
     * - DB_HOST (default: db.uvlrhmcnjdasygnpsqna.supabase.co)
     * - DB_PORT (default: 5432)
     * - DB_NAME (default: postgres)
     * - DB_USER (default: postgres)
     * - DB_PASS (default: your password)
     */

    public static Connection getConnection() throws SQLException {
        String host = System.getProperty("db.host", System.getenv().getOrDefault("DB_HOST", "db.yctoxgzswavkcxscoxyk.supabase.co"));
        String port = System.getProperty("db.port", System.getenv().getOrDefault("DB_PORT", "5432"));
        String name = System.getProperty("db.name", System.getenv().getOrDefault("DB_NAME", "postgres"));
        String user = System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", "postgres"));
        String pass = System.getProperty("db.pass", System.getenv().getOrDefault("DB_PASS", "2005"));

        String url = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require&connectTimeout=30&socketTimeout=30", host, port, name);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Add the PostgreSQL connector dependency.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, pass);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "id SERIAL PRIMARY KEY, " +
                    "admin_id VARCHAR(50), " +
                    "name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "mobile VARCHAR(15), " +
                    "password_hash VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "student_id VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "mobile VARCHAR(15), " +
                    "rfid VARCHAR(50), " +
                    "course VARCHAR(100), " +
                    "active BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS faculty (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "faculty_id VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "mobile VARCHAR(15), " +
                    "rfid VARCHAR(50), " +
                    "active BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "author VARCHAR(100), " +
                    "barcode VARCHAR(50), " +
                    "category_id INT REFERENCES categories(id), " +
                    "quantity INT DEFAULT 0, " +
                    "available BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS issued_books (" +
                    "id SERIAL PRIMARY KEY, " +
                    "student_id INT REFERENCES students(id), " +
                    "faculty_id INT REFERENCES faculty(id), " +
                    "book_id INT REFERENCES books(id), " +
                    "issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS returned_books (" +
                    "id SERIAL PRIMARY KEY, " +
                    "student_id INT REFERENCES students(id), " +
                    "book_id INT REFERENCES books(id), " +
                    "return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS logs (" +
                    "id SERIAL PRIMARY KEY, " +
                    "action VARCHAR(255), " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Run migrations for existing tables
            runMigrations(conn);

            System.out.println("Database initialized successfully for Supabase PostgreSQL.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // PostgreSQL me TRUNCATE CASCADE use karke foreign key constraints handle karo
            stmt.execute("TRUNCATE TABLE logs, issued_books, returned_books, books, categories, faculty, students, admins RESTART IDENTITY CASCADE");

            System.out.println("All data cleared from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs database migrations to update existing tables with new columns.
     * @param conn the database connection
     */
    private static void runMigrations(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Add available column to books table if it doesn't exist
            stmt.execute("ALTER TABLE books ADD COLUMN IF NOT EXISTS available BOOLEAN DEFAULT TRUE");

            // Add faculty_id column to issued_books table if it doesn't exist
            stmt.execute("ALTER TABLE issued_books ADD COLUMN IF NOT EXISTS faculty_id INT REFERENCES faculty(id)");

            // Add faculty_id column to returned_books table if it doesn't exist
            stmt.execute("ALTER TABLE returned_books ADD COLUMN IF NOT EXISTS faculty_id INT REFERENCES faculty(id)");

            System.out.println("Database migrations completed successfully.");
        } catch (SQLException e) {
            System.err.println("Error running migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts sample data for testing purposes.
     * @param conn the database connection
     */
    private static void insertSampleData(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Clear existing sample data to avoid foreign key conflicts
            stmt.execute("TRUNCATE TABLE issued_books, returned_books, books, categories, faculty, students, admins RESTART IDENTITY CASCADE");

            // Insert sample categories
            stmt.execute("INSERT INTO categories (name) VALUES ('Fiction')");
            stmt.execute("INSERT INTO categories (name) VALUES ('Non-Fiction')");
            stmt.execute("INSERT INTO categories (name) VALUES ('Science')");

            // Insert sample books
            stmt.execute("INSERT INTO books (name, author, barcode, category_id, quantity) VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', 'BK001', 1, 5)");
            stmt.execute("INSERT INTO books (name, author, barcode, category_id, quantity) VALUES ('To Kill a Mockingbird', 'Harper Lee', 'BK002', 1, 3)");
            stmt.execute("INSERT INTO books (name, author, barcode, category_id, quantity) VALUES ('Sapiens', 'Yuval Noah Harari', 'BK003', 2, 4)");
            stmt.execute("INSERT INTO books (name, author, barcode, category_id, quantity) VALUES ('A Brief History of Time', 'Stephen Hawking', 'BK004', 3, 2)");

            // Insert sample students
            stmt.execute("INSERT INTO students (name, student_id, email, mobile, rfid, course) VALUES ('John Doe', 'STU001', 'john.doe@example.com', '1234567890', 'RFID001', 'Computer Science')");
            stmt.execute("INSERT INTO students (name, student_id, email, mobile, rfid, course) VALUES ('Jane Smith', 'STU002', 'jane.smith@example.com', '0987654321', 'RFID002', 'Mathematics')");

            // Insert sample faculty
            stmt.execute("INSERT INTO faculty (name, faculty_id, email, mobile, rfid) VALUES ('Dr. Alice Johnson', 'FAC001', 'alice.johnson@example.com', '1112223333', 'RFID003')");
            stmt.execute("INSERT INTO faculty (name, faculty_id, email, mobile, rfid) VALUES ('Prof. Bob Wilson', 'FAC002', 'bob.wilson@example.com', '4445556666', 'RFID004')");

            System.out.println("Sample data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
