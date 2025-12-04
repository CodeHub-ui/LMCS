package com.library;

import com.library.dao.DatabaseUtil;
import com.library.dao.IssuedBookDAO;
import com.library.dao.BookDAO;
import com.library.model.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Test class to verify book issuing functionality with multiple copies support.
 */
public class BookIssueTest {
    public static void main(String[] args) {
        System.out.println("Testing book issuing functionality with multiple copies...");

        try {
            // Initialize database (this will run migrations)
            DatabaseUtil.initializeDatabase();
            System.out.println("Database initialized with migrations.");

            // Create test data if not exists
            createTestData();

            // Test book search
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.searchBooks("test");
            System.out.println("Book search test: Found " + books.size() + " books.");

            // Test multiple book issuing
            IssuedBookDAO issuedDAO = new IssuedBookDAO();

            // Get a book with multiple copies (assuming barcode "TEST001" exists)
            Book testBook = bookDAO.getBookByBarcodeOrName("TEST001");
            if (testBook != null) {
                System.out.println("Test book found: " + testBook.getName() + " (Available: " + testBook.isAvailable() + ")");

                // Issue first copy to student 1
                boolean issued1 = issuedDAO.issueBook(1, "TEST001");
                System.out.println("First issue to student 1: " + (issued1 ? "SUCCESS" : "FAILED"));

                // Check if book is still available
                testBook = bookDAO.getBookByBarcodeOrName("TEST001");
                System.out.println("Book still available after first issue: " + testBook.isAvailable());

                // Issue second copy to student 2
                boolean issued2 = issuedDAO.issueBook(2, "TEST001");
                System.out.println("Second issue to student 2: " + (issued2 ? "SUCCESS" : "FAILED"));

                // Check issued counts
                int issuedCountForBook = issuedDAO.getIssuedCountForBook(testBook.getId());
                System.out.println("Total issued copies for book: " + issuedCountForBook);

                // Try third issue (should fail if only 2 copies)
                boolean issued3 = issuedDAO.issueBook(3, "TEST001");
                System.out.println("Third issue attempt: " + (issued3 ? "SUCCESS (unexpected)" : "FAILED (expected)"));

                // Test return
                boolean returned = issuedDAO.returnBook(1, "TEST001");
                System.out.println("Return by student 1: " + (returned ? "SUCCESS" : "FAILED"));

                // Check availability after return
                testBook = bookDAO.getBookByBarcodeOrName("TEST001");
                System.out.println("Book available after return: " + testBook.isAvailable());
            } else {
                System.out.println("Test book not found. Please ensure test data exists.");
            }

            // Test faculty issuing
            System.out.println("\nTesting faculty book issuing...");

            // Issue to faculty (assuming faculty ID 1)
            boolean facultyIssued = issuedDAO.issueBookForFaculty(1, "TEST001");
            System.out.println("Issue to faculty: " + (facultyIssued ? "SUCCESS" : "FAILED"));

            // Check faculty issued books
            List<String[]> facultyBooks = issuedDAO.getIssuedBooksForFaculty(1);
            System.out.println("Faculty issued books count: " + facultyBooks.size());

            // Check faculty issued count
            int facultyIssuedCount = issuedDAO.getIssuedCountForFaculty(1);
            System.out.println("Faculty issued count: " + facultyIssuedCount);

            // Test faculty return
            boolean facultyReturned = issuedDAO.returnBookForFaculty(1, "TEST001");
            System.out.println("Faculty return: " + (facultyReturned ? "SUCCESS" : "FAILED"));

            System.out.println("All tests completed! Multiple copies and faculty functionality should now work.");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestData() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Insert test book with 2 copies if not exists
            String insertBook = "INSERT INTO books (name, author, barcode, category_id, quantity, available) " +
                    "VALUES ('Test Book', 'Test Author', 'TEST001', 1, 2, true) " +
                    "ON CONFLICT DO NOTHING";
            try (PreparedStatement stmt = conn.prepareStatement(insertBook)) {
                stmt.executeUpdate();
            }

            // Insert test students if not exist
            String insertStudent1 = "INSERT INTO students (name, student_id, email, mobile, rfid, course, active) " +
                    "VALUES ('Test Student 1', 'STU001', 'stu1@test.com', '1234567890', 'RFID001', 'CS', true) " +
                    "ON CONFLICT DO NOTHING";
            String insertStudent2 = "INSERT INTO students (name, student_id, email, mobile, rfid, course, active) " +
                    "VALUES ('Test Student 2', 'STU002', 'stu2@test.com', '1234567891', 'RFID002', 'CS', true) " +
                    "ON CONFLICT DO NOTHING";
            String insertStudent3 = "INSERT INTO students (name, student_id, email, mobile, rfid, course, active) " +
                    "VALUES ('Test Student 3', 'STU003', 'stu3@test.com', '1234567892', 'RFID003', 'CS', true) " +
                    "ON CONFLICT DO NOTHING";

            // Insert test faculty if not exist
            String insertFaculty = "INSERT INTO faculty (name, faculty_id, email, mobile, rfid, active) " +
                    "VALUES ('Test Faculty', 'FAC001', 'fac@test.com', '1234567893', 'RFIDFAC', true) " +
                    "ON CONFLICT DO NOTHING";

            try (PreparedStatement stmt1 = conn.prepareStatement(insertStudent1);
                 PreparedStatement stmt2 = conn.prepareStatement(insertStudent2);
                 PreparedStatement stmt3 = conn.prepareStatement(insertStudent3);
                 PreparedStatement stmtFac = conn.prepareStatement(insertFaculty)) {
                stmt1.executeUpdate();
                stmt2.executeUpdate();
                stmt3.executeUpdate();
                stmtFac.executeUpdate();
            }

            System.out.println("Test data created.");
        } catch (SQLException e) {
            System.err.println("Error creating test data: " + e.getMessage());
        }
    }
}
