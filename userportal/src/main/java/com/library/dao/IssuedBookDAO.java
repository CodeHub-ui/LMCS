package com.library.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for IssuedBook entities.
 * Handles operations related to book issuing and returning, interacting with the issued_books table.
 */
public class IssuedBookDAO {

    /**
     * Retrieves a list of books issued to a specific student.
     * Returns an array of strings for each book: [book_title, barcode, author].
     *
     * @param studentId the ID of the student
     * @return a list of string arrays representing issued books
     */
    public List<String[]> getIssuedBooks(int studentId) {  // Returns list of [book_title, barcode, author]
        String sql = "SELECT b.name, b.barcode, b.author FROM issued_books ib JOIN books b ON ib.book_id = b.id WHERE ib.student_id = ?";
        List<String[]> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new String[]{rs.getString("name"), rs.getString("barcode"), rs.getString("author")});
            }
        } catch (SQLException e) {
            System.err.println("Error in getIssuedBooks: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Retrieves a list of books issued to a specific faculty member.
     * Returns an array of strings for each book: [book_title, barcode, author].
     *
     * @param facultyId the ID of the faculty member
     * @return a list of string arrays representing issued books
     */
    public List<String[]> getIssuedBooksForFaculty(int facultyId) {  // Returns list of [book_title, barcode, author]
        String sql = "SELECT b.name, b.barcode, b.author FROM issued_books ib JOIN books b ON ib.book_id = b.id WHERE ib.faculty_id = ?";
        List<String[]> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new String[]{rs.getString("name"), rs.getString("barcode"), rs.getString("author")});
            }
        } catch (SQLException e) {
            System.err.println("Error in getIssuedBooksForFaculty: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Issues a book to a student by inserting a record into the issued_books table.
     * Checks if the book has available copies before proceeding.
     *
     * @param studentId the ID of the student
     * @param barcode the barcode of the book to issue
     * @return true if the operation succeeds, false otherwise
     */
    public boolean issueBook(int studentId, String barcode) {
        // Get book ID from barcode
        int bookId = getBookIdByBarcode(barcode);
        if (bookId == -1) {
            System.err.println("Book with barcode " + barcode + " not found.");
            return false;
        }

        // Check if book has available copies (quantity > issued count)
        String checkSql = "SELECT quantity FROM books WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                int issuedCount = getIssuedCountForBook(bookId);
                if (issuedCount >= quantity) {
                    System.err.println("Book with barcode " + barcode + " has no available copies (quantity: " + quantity + ", issued: " + issuedCount + ").");
                    return false;
                }
            } else {
                System.err.println("Book with barcode " + barcode + " not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking book availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Proceed with issuing
        String sql = "INSERT INTO issued_books (student_id, book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error in issueBook: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Issues a book to a faculty member by inserting a record into the issued_books table.
     * Checks if the book has available copies before proceeding.
     *
     * @param facultyId the ID of the faculty member
     * @param barcode the barcode of the book to issue
     * @return true if the operation succeeds, false otherwise
     */
    public boolean issueBookForFaculty(int facultyId, String barcode) {
        // Get book ID from barcode
        int bookId = getBookIdByBarcode(barcode);
        if (bookId == -1) {
            System.err.println("Book with barcode " + barcode + " not found.");
            return false;
        }

        // Check if book has available copies (quantity > issued count)
        String checkSql = "SELECT quantity FROM books WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                int issuedCount = getIssuedCountForBook(bookId);
                if (issuedCount >= quantity) {
                    System.err.println("Book with barcode " + barcode + " has no available copies (quantity: " + quantity + ", issued: " + issuedCount + ").");
                    return false;
                }
            } else {
                System.err.println("Book with barcode " + barcode + " not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking book availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Proceed with issuing
        String sql = "INSERT INTO issued_books (faculty_id, book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.setInt(2, bookId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error in issueBookForFaculty: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns a book by deleting the corresponding record from the issued_books table.
     * Uses the student's ID and the book's barcode to identify the record.
     * Book availability is calculated dynamically based on quantity vs issued count.
     *
     * @param studentId the ID of the student
     * @param barcode the barcode of the book to return
     * @return true if the operation succeeds, false otherwise
     */
    public boolean returnBook(int studentId, String barcode) {
        String sql = "DELETE FROM issued_books WHERE student_id = ? AND book_id = (SELECT id FROM books WHERE barcode = ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, barcode);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                System.err.println("No issued book found for student " + studentId + " and barcode " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("Error in returnBook: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns a book by deleting the corresponding record from the issued_books table.
     * Uses the faculty member's ID and the book's barcode to identify the record.
     * Book availability is calculated dynamically based on quantity vs issued count.
     *
     * @param facultyId the ID of the faculty member
     * @param barcode the barcode of the book to return
     * @return true if the operation succeeds, false otherwise
     */
    public boolean returnBookForFaculty(int facultyId, String barcode) {
        String sql = "DELETE FROM issued_books WHERE faculty_id = ? AND book_id = (SELECT id FROM books WHERE barcode = ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.setString(2, barcode);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                System.err.println("No issued book found for faculty " + facultyId + " and barcode " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("Error in returnBookForFaculty: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the count of books issued to a specific student.
     *
     * @param studentId the ID of the student
     * @return the number of issued books
     */
    public int getIssuedCountForStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in getIssuedCountForStudent: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the count of books issued to a specific faculty member.
     *
     * @param facultyId the ID of the faculty member
     * @return the number of issued books
     */
    public int getIssuedCountForFaculty(int facultyId) {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE faculty_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in getIssuedCountForFaculty: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the count of issued copies for a specific book.
     *
     * @param bookId the ID of the book
     * @return the number of issued copies
     */
    public int getIssuedCountForBook(int bookId) {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE book_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in getIssuedCountForBook: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the book ID associated with a given barcode.
     *
     * @param barcode the barcode of the book
     * @return the book ID if found, -1 otherwise
     */
    public int getBookIdByBarcode(String barcode) {
        String sql = "SELECT id FROM books WHERE barcode = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.err.println("No book found with barcode: " + barcode);
            }
        } catch (SQLException e) {
            System.err.println("Error in getBookIdByBarcode: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}
