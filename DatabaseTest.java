import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        // Test admin-portal database connection
        System.out.println("Testing admin-portal database connection...");
        testConnection("db.yctoxgzswavkcxscoxyk.supabase.co", "5432", "postgres", "postgres", "2005");

        // Test userportal database connection (assuming same credentials for now)
        System.out.println("\nTesting userportal database connection...");
        testConnection("db.yctoxgzswavkcxscoxyk.supabase.co", "5432", "postgres", "postgres", "2005");
    }

    private static void testConnection(String host, String port, String name, String user, String pass) {
        String url = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, name);
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            if (conn != null) {
                System.out.println("✅ Database connection successful!");
                conn.close();
            } else {
                System.out.println("❌ Database connection failed!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ PostgreSQL JDBC Driver not found.");
        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
        }
    }
}
