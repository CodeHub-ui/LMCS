package com.library.dao;

import com.library.dao.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for IssuedBook entities in admin portal.
 * Handles operations related to issued_books table.
 */
public class IssuedBookDAO {

    /**
     * Retrieves a list of books issued to a specific student.
     * Returns a list of string arrays for each book: [book_title, barcode, author].
     *
     * @param studentId the ID of the student
     * @return a list of string arrays representing issued books
     */
    public List<String[]> getIssuedBooks(int studentId) {
        String sql = "SELECT b.name, b.barcode, b.author FROM issued_books ib JOIN books b ON ib.book_id = b.id WHERE ib.student_id = ?";
        List<String[]> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new String[]{rs.getString("name"), rs.getString("barcode"), rs.getString("author")});
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issued books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Issues a book to a student.
     *
     * @param studentId the ID of the student
     * @param bookId the ID of the book
     * @return true if issued successfully, false otherwise
     */
    public boolean issueBook(int studentId, int bookId) {
        String sql = "INSERT INTO issued_books (student_id, book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Issues a book to a faculty member.
     *
     * @param facultyId the ID of the faculty
     * @param bookId the ID of the book
     * @return true if issued successfully, false otherwise
     */
    public boolean issueBookToFaculty(int facultyId, int bookId) {
        String sql = "INSERT INTO issued_books (faculty_id, book_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.setInt(2, bookId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error issuing book to faculty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns a book from a student.
     *
     * @param studentId the ID of the student
     * @param bookId the ID of the book
     * @return true if returned successfully, false otherwise
     */
    public boolean returnBook(int studentId, int bookId) {
        String sql = "DELETE FROM issued_books WHERE student_id = ? AND book_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns a book from a student using barcode.
     * Inserts a record into returned_books before deleting from issued_books.
     *
     * @param studentId the ID of the student
     * @param barcode the barcode of the book
     * @return true if returned successfully, false otherwise
     */
    public boolean returnBook(int studentId, String barcode) {
        // First get the book_id for the barcode
        String getBookIdSql = "SELECT id FROM books WHERE barcode = ? LIMIT 1";
        // Insert into returned_books
        String insertSql = "INSERT INTO returned_books (student_id, book_id) VALUES (?, ?)";
        // Then delete one issued_books entry for this student and book
        String deleteSql = "DELETE FROM issued_books WHERE student_id = ? AND book_id = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get book_id
            try (PreparedStatement getBookStmt = conn.prepareStatement(getBookIdSql)) {
                getBookStmt.setString(1, barcode);
                ResultSet rs = getBookStmt.executeQuery();
                if (rs.next()) {
                    int bookId = rs.getInt("id");

                    // Insert into returned_books
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, studentId);
                        insertStmt.setInt(2, bookId);
                        insertStmt.executeUpdate();
                    }

                    // Delete one entry
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, studentId);
                        deleteStmt.setInt(2, bookId);
                        int rows = deleteStmt.executeUpdate();
                        return rows > 0;
                    }
                } else {
                    System.err.println("Book with barcode " + barcode + " not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns a book from a faculty member using barcode.
     * Inserts a record into returned_books before deleting from issued_books.
     *
     * @param facultyId the ID of the faculty
     * @param barcode the barcode of the book
     * @return true if returned successfully, false otherwise
     */
    public boolean returnBookFromFaculty(int facultyId, String barcode) {
        // First get the book_id for the barcode
        String getBookIdSql = "SELECT id FROM books WHERE barcode = ? LIMIT 1";
        // Insert into returned_books
        String insertSql = "INSERT INTO returned_books (faculty_id, book_id) VALUES (?, ?)";
        // Then delete one issued_books entry for this faculty and book
        String deleteSql = "DELETE FROM issued_books WHERE faculty_id = ? AND book_id = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get book_id
            try (PreparedStatement getBookStmt = conn.prepareStatement(getBookIdSql)) {
                getBookStmt.setString(1, barcode);
                ResultSet rs = getBookStmt.executeQuery();
                if (rs.next()) {
                    int bookId = rs.getInt("id");

                    // Insert into returned_books
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, facultyId);
                        insertStmt.setInt(2, bookId);
                        insertStmt.executeUpdate();
                    }

                    // Delete one entry
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, facultyId);
                        deleteStmt.setInt(2, bookId);
                        int rows = deleteStmt.executeUpdate();
                        return rows > 0;
                    }
                } else {
                    System.err.println("Book with barcode " + barcode + " not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error returning book from faculty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of book names issued to a student identified by RFID.
     * 
     * @param studentRfid the RFID of the student
     * @return list of book names issued to the student
     */
    public List<String> getIssuedBooksForStudent(String studentRfid) {
        List<String> issuedBooks = new ArrayList<>();
        String sql = "SELECT b.name FROM issued_books ib "
                   + "JOIN students s ON ib.student_id = s.id "
                   + "JOIN books b ON ib.book_id = b.id "
                   + "WHERE s.rfid = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentRfid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                issuedBooks.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issued books for student RFID: " + e.getMessage());
            e.printStackTrace();
        }
        return issuedBooks;
    }

    /**
     * Checks if a book with the given barcode is currently issued.
     *
     * @param barcode the barcode of the book
     * @return true if the book is issued, false otherwise
     */
    public boolean isBookIssued(String barcode) {
        String sql = "SELECT COUNT(*) FROM issued_books ib "
                   + "JOIN books b ON ib.book_id = b.id "
                   + "WHERE b.barcode = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book is issued: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Searches for issued books by book name, barcode, user name, or user ID.
     * Includes both students and faculty issued books.
     *
     * @param query the search query
     * @return a list of issued book details as string arrays [book_name, barcode, user_name_with_type, user_id, issue_date]
     *         where user_name_with_type is "Name (student)" or "Name (faculty)"
     */
    public List<String[]> searchIssuedBooks(String query) {
        List<String[]> issuedBooks = new ArrayList<>();
        String sql = "SELECT b.name AS book_name, b.barcode, " +
                     "CASE WHEN ib.student_id IS NOT NULL THEN CONCAT(s.name, ' (student)') ELSE CONCAT(f.name, ' (faculty)') END AS user_name_with_type, " +
                     "CASE WHEN ib.student_id IS NOT NULL THEN s.student_id ELSE f.faculty_id END AS user_id, " +
                     "ib.issue_date " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.id " +
                     "LEFT JOIN students s ON ib.student_id = s.id " +
                     "LEFT JOIN faculty f ON ib.faculty_id = f.id " +
                     "WHERE b.name LIKE ? OR b.barcode LIKE ? OR " +
                     "(ib.student_id IS NOT NULL AND (s.name LIKE ? OR s.student_id LIKE ?)) OR " +
                     "(ib.faculty_id IS NOT NULL AND (f.name LIKE ? OR f.faculty_id LIKE ?))";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeQuery = query + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            stmt.setString(3, likeQuery);
            stmt.setString(4, likeQuery);
            stmt.setString(5, likeQuery);
            stmt.setString(6, likeQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issuedBooks.add(new String[]{
                        rs.getString("book_name"),
                        rs.getString("barcode"),
                        rs.getString("user_name_with_type"),
                        rs.getString("user_id"),
                        rs.getString("issue_date")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching issued books: " + e.getMessage());
            e.printStackTrace();
        }
        return issuedBooks;
    }

    /**
     * Gets the total number of issued books.
     *
     * @return total count of issued books
     * @throws SQLException if database access error occurs
     */
    public int getTotalIssuedBooks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issued_books";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             if (rs.next()) {
                 return rs.getInt(1);
             }
        }
        return 0;
    }

    /**
     * Gets the number of issued copies for a specific book.
     *
     * @param bookId the ID of the book
     * @return the count of issued copies
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
            System.err.println("Error getting issued count for book: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the number of books issued to a specific student.
     *
     * @param studentId the ID of the student
     * @return the count of issued books
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
            System.err.println("Error getting issued count for student: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Issues a book to a student using barcode.
     *
     * @param studentId the ID of the student
     * @param barcode the barcode of the book
     * @return true if issued successfully, false otherwise
     */
    public boolean issueBook(int studentId, String barcode) {
        // First get bookId from barcode
        String getBookIdSql = "SELECT id FROM books WHERE barcode = ? LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement getBookStmt = conn.prepareStatement(getBookIdSql)) {
            getBookStmt.setString(1, barcode);
            ResultSet rs = getBookStmt.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("id");
                return issueBook(studentId, bookId);
            } else {
                System.err.println("Book with barcode " + barcode + " not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error issuing book by barcode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes orphaned issued book records where the book no longer exists in the books table.
     * This is useful for cleaning up after direct database deletions.
     *
     * @return the number of orphaned records removed
     */
    public int removeOrphanedIssuedBooks() {
        String sql = "DELETE FROM issued_books WHERE book_id NOT IN (SELECT id FROM books)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Removed " + rowsAffected + " orphaned issued book records at " + new java.util.Date());
            }
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error removing orphaned issued books: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Searches for returned books by book name, barcode, user name, or user ID.
     * Includes both students and faculty returned books.
     *
     * @param query the search query
     * @return a list of returned book details as string arrays [book_name, barcode, user_name_with_type, user_id, issue_date, return_date]
     *         where user_name_with_type is "Name (student)" or "Name (faculty)"
     */
    public List<String[]> searchReturnedBooks(String query) {
        List<String[]> returnedBooks = new ArrayList<>();
        String sql = "SELECT b.name AS book_name, b.barcode, " +
                     "CASE WHEN rb.student_id IS NOT NULL THEN CONCAT(s.name, ' (student)') ELSE CONCAT(f.name, ' (faculty)') END AS user_name_with_type, " +
                     "CASE WHEN rb.student_id IS NOT NULL THEN s.student_id ELSE f.faculty_id END AS user_id, " +
                     "ib.issue_date, " +
                     "rb.return_date " +
                     "FROM returned_books rb " +
                     "JOIN books b ON rb.book_id = b.id " +
                     "LEFT JOIN issued_books ib ON ((rb.student_id IS NOT NULL AND ib.student_id = rb.student_id) OR (rb.faculty_id IS NOT NULL AND ib.faculty_id = rb.faculty_id)) AND ib.book_id = rb.book_id " +
                     "LEFT JOIN students s ON rb.student_id = s.id " +
                     "LEFT JOIN faculty f ON rb.faculty_id = f.id " +
                     "WHERE b.name LIKE ? OR b.barcode LIKE ? OR " +
                     "(rb.student_id IS NOT NULL AND (s.name LIKE ? OR s.student_id LIKE ?)) OR " +
                     "(rb.faculty_id IS NOT NULL AND (f.name LIKE ? OR f.faculty_id LIKE ?)) " +
                     "ORDER BY rb.return_date DESC";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeQuery = "%" + query + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            stmt.setString(3, likeQuery);
            stmt.setString(4, likeQuery);
            stmt.setString(5, likeQuery);
            stmt.setString(6, likeQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    returnedBooks.add(new String[]{
                        rs.getString("book_name"),
                        rs.getString("barcode"),
                        rs.getString("user_name_with_type"),
                        rs.getString("user_id"),
                        rs.getString("issue_date"),
                        rs.getString("return_date")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching returned books: " + e.getMessage());
            e.printStackTrace();
        }
        return returnedBooks;
    }
}
