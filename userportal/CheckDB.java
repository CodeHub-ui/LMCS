import java.sql.*;
import com.library.dao.DatabaseUtil;

public class CheckDB {
    public static void main(String[] args) {
        // Initialize the database to ensure tables exist
        DatabaseUtil.initializeDatabase();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "Harshit@123";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("DESCRIBE students");
            System.out.println("Students table structure:");
            while (rs.next()) {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type") + " - " + rs.getString("Null") + " - " + rs.getString("Key") + " - " + rs.getString("Default") + " - " + rs.getString("Extra"));
            }

            rs = stmt.executeQuery("DESCRIBE books");
            System.out.println("\nBooks table structure:");
            while (rs.next()) {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type") + " - " + rs.getString("Null") + " - " + rs.getString("Key") + " - " + rs.getString("Default") + " - " + rs.getString("Extra"));
            }

            rs = stmt.executeQuery("SELECT * FROM books");
            System.out.println("\nBooks data:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Available: " + rs.getBoolean("available"));
            }

            rs = stmt.executeQuery("SELECT * FROM students");
            System.out.println("\nStudents data:");
            while (rs.next()) {
                System.out.println("Student ID: " + rs.getString("student_id") + ", Name: " + rs.getString("name") + ", RFID: " + rs.getString("rfid") + ", Active: " + rs.getBoolean("active"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
