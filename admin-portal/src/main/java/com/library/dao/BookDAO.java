package com.library.dao;

import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
public List<Book> getAllBooks() {
        LogDAO.log("Entering getAllBooks method.");
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM books b JOIN categories c ON b.category_id = c.id";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("category_name"));
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setName(rs.getString("name"));
                book.setAuthor(rs.getString("author"));
                book.setBarcode(rs.getString("barcode"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setCategory(category);
                book.setQuantity(rs.getInt("quantity"));
                books.add(book);
            }
            LogDAO.log("Retrieved " + books.size() + " books.");
        } catch (SQLException e) {
            LogDAO.log("SQLException in getAllBooks: " + e.getMessage());
            e.printStackTrace();
        }
        LogDAO.log("Exiting getAllBooks method.");
        return books;
    }

    public List<Book> getBooksByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setName(rs.getString("name"));
                book.setAuthor(rs.getString("author"));
                book.setBarcode(rs.getString("barcode"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                books.add(book);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }

public boolean addBook(Book book) {
        LogDAO.log("Entering addBook method with book: " + book.getName() + ", Author: " + book.getAuthor() + ", Barcode: " + book.getBarcode());
        String sql = "INSERT INTO books (name, author, barcode, category_id, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getBarcode());
            stmt.setInt(4, book.getCategoryId());
            stmt.setInt(5, book.getQuantity());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Successfully added book: " + book.getName());
            } else {
                LogDAO.log("Failed to add book: " + book.getName());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LogDAO.log("SQLException in addBook: " + e.getMessage());
            System.err.println("Error adding book to the database: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting addBook method.");
        }
    }

public boolean updateBook(Book book) {
        LogDAO.log("Entering updateBook method for book ID: " + book.getId());
        String sql = "UPDATE books SET name = ?, author = ?, barcode = ?, category_id = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getBarcode());
            stmt.setInt(4, book.getCategoryId());
            stmt.setInt(5, book.getQuantity());
            stmt.setInt(6, book.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LogDAO.log("Successfully updated book: " + book.getName());
            } else {
                LogDAO.log("Failed to update book: " + book.getName());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LogDAO.log("SQLException in updateBook: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting updateBook method for book ID: " + book.getId());
        }
    }

/**
     * Deletes all books and issued book records from the database.
     * This is used for a "fresh start" to clear all book-related data.
     * Note: This operation is irreversible and should be used with caution.
     * This method now requires an admin password to proceed.
     *
     * @param adminPassword password for admin verification
     * @return true if all deletions were successful and password verified, false otherwise.
     */
    public boolean deleteAllBooks(String adminPassword) {
        LogDAO.log("Attempt to delete all books initiated at " + new java.util.Date());
        // Simple password check, replace with actual authentication check if available
        if (!"admin123".equals(adminPassword)) {
            LogDAO.log("Unauthorized attempt to delete all books. Invalid admin password at " + new java.util.Date());
            return false;
        }
        LogDAO.log("Admin password verification successful. Proceeding with deletion at " + new java.util.Date());
        try {
            IssuedBookDAO issuedDao = new IssuedBookDAO();
            String deleteIssuedSql = "DELETE FROM issued_books";
            try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
                int issuedDeleted = stmt.executeUpdate(deleteIssuedSql);
                LogDAO.log("Deleted " + issuedDeleted + " issued book records for fresh start at " + new java.util.Date());
            }
            String deleteBooksSql = "DELETE FROM books";
            try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
                int booksDeleted = stmt.executeUpdate(deleteBooksSql);
                LogDAO.log("Deleted " + booksDeleted + " books for fresh start at " + new java.util.Date());
                return true;
            }
        } catch (SQLException e) {
            LogDAO.log("SQLException in deleteAllBooks: " + e.getMessage() + " at " + new java.util.Date());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting deleteAllBooks method at " + new java.util.Date());
        }
    }

    public boolean deleteBook(int id) {
        LogDAO.log("Attempt to delete book started for book ID: " + id + " at " + new java.util.Date());
        try {
            // Get barcode for the book
            String barcodeSql = "SELECT barcode FROM books WHERE id = ?";
            String barcode = null;
            try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement barcodeStmt = conn.prepareStatement(barcodeSql)) {
                barcodeStmt.setInt(1, id);
                try (ResultSet rs = barcodeStmt.executeQuery()) {
                    if (rs.next()) {
                        barcode = rs.getString("barcode");
                    } else {
                        LogDAO.log("No book found with id " + id + " to get barcode at " + new java.util.Date());
                    }
                }
            }
            if (barcode != null) {
                // Check if the book is currently issued
                if (new IssuedBookDAO().isBookIssued(barcode)) {
                    LogDAO.log("Cannot delete book ID " + id + " because it is currently issued at " + new java.util.Date());
                    return false;
                }
            } else {
                LogDAO.log("Barcode is null for book ID " + id + ", aborting delete at " + new java.util.Date());
                return false;
            }
            String sql = "DELETE FROM books WHERE id = ?";
            try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    LogDAO.log("Successfully deleted book: ID " + id + " at " + new java.util.Date());
                } else {
                    LogDAO.log("Failed to delete book: ID " + id + " at " + new java.util.Date());
                }
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            LogDAO.log("SQLException in deleteBook: " + e.getMessage() + " at " + new java.util.Date());
            e.printStackTrace();
            return false;
        } finally {
            LogDAO.log("Exiting deleteBook method for book ID: " + id + " at " + new java.util.Date());
        }
    }

    public int getTotalBooks() {
        String sql = "SELECT SUM(quantity) FROM books";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Searches for books by name or barcode, showing all books with correct availability based on quantity vs issued count.
     * This method is used for centralized search, limiting results to 10 for performance.
     *
     * @param query the search query (partial name or barcode)
     * @return a list of Book objects matching the query with correct availability
     * @throws SQLException if a database access error occurs
     */
    public List<Book> searchBooks(String query) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, name, author, barcode, category_id, quantity FROM books WHERE LOWER(name) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?) OR LOWER(barcode) LIKE LOWER(?) LIMIT 10";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setName(rs.getString("name"));
                    book.setAuthor(rs.getString("author"));
                    book.setBarcode(rs.getString("barcode"));
                    book.setCategoryId(rs.getInt("category_id"));
                    book.setQuantity(rs.getInt("quantity"));
                    // Calculate availability: available if quantity > issued count
                    int issuedCount = new IssuedBookDAO().getIssuedCountForBook(book.getId());
                    book.setAvailable(book.getQuantity() > issuedCount);
                    books.add(book);
                }
            }
        }
        return books;
    }
}
