package com.library.dao;

import com.library.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Book entities.
 * Provides methods to interact with the books table in the database, including retrieval, search, and availability checks.
 */
public class BookDAO {

    /**
     * Retrieves all books from the database.
     * This method is used for displaying or searching books in the user portal.
     *
     * @return a list of all Book objects
     * @throws SQLException if a database access error occurs
     */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, name, author, barcode, category_id, quantity FROM books";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("name"), rs.getString("author"), rs.getString("barcode"), rs.getInt("category_id"));
                int quantity = rs.getInt("quantity");
                int issuedCount = new IssuedBookDAO().getIssuedCountForBook(rs.getInt("id"));
                book.setAvailable(issuedCount < quantity);
                books.add(book);
            }
        }
        return books;
    }

    /**
     * Retrieves a book by its unique ID.
     * Used for issuing books or fetching specific book details.
     *
     * @param id the unique ID of the book
     * @return the Book object if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT id, name, author, barcode, category_id, quantity FROM books WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book(rs.getInt("id"), rs.getString("name"), rs.getString("author"), rs.getString("barcode"), rs.getInt("category_id"));
                    int quantity = rs.getInt("quantity");
                    int issuedCount = new IssuedBookDAO().getIssuedCountForBook(rs.getInt("id"));
                    book.setAvailable(issuedCount < quantity);
                    return book;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a book by its barcode or name.
     * Useful for issuing books when the user provides either the barcode or the book title.
     *
     * @param input the barcode or name of the book
     * @return the Book object if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public Book getBookByBarcodeOrName(String input) throws SQLException {
        String sql = "SELECT id, name, author, barcode, category_id, quantity FROM books WHERE barcode = ? OR name = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, input);
            stmt.setString(2, input);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book(rs.getInt("id"), rs.getString("name"), rs.getString("author"), rs.getString("barcode"), rs.getInt("category_id"));
                    int quantity = rs.getInt("quantity");
                    int issuedCount = new IssuedBookDAO().getIssuedCountForBook(rs.getInt("id"));
                    book.setAvailable(issuedCount < quantity);
                    return book;
                }
            }
        }
        return null;
    }

    /**
     * Searches for books by name or barcode, showing only books with available copies.
     * This method is used for autocomplete functionality in the UI, limiting results to 10 for performance.
     * Updated to be case-insensitive and include debug logging.
     *
     * @param query the search query (partial name or barcode)
     * @return a list of available Book objects matching the query
     * @throws SQLException if a database access error occurs
     */
    public List<Book> searchBooks(String query) throws SQLException {
        List<Book> books = new ArrayList<>();
        // Updated SQL: Make it case-insensitive using UPPER() for broader matching
        String sql = "SELECT id, name, author, barcode, category_id, quantity FROM books WHERE UPPER(name) LIKE UPPER(?) OR UPPER(barcode) LIKE UPPER(?) LIMIT 20";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeQuery = "%" + query + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("id");
                    int quantity = rs.getInt("quantity");
                    int issuedCount = new IssuedBookDAO().getIssuedCountForBook(bookId);
                    if (issuedCount < quantity) {
                        Book book = new Book(bookId, rs.getString("name"), rs.getString("author"), rs.getString("barcode"), rs.getInt("category_id"));
                        book.setAvailable(true);
                        books.add(book);
                        if (books.size() >= 10) break; // Limit to 10 results
                    }
                }
            }
        }
        // Debug: Print what was searched and how many results (remove after testing)
        System.out.println("DEBUG: Searched for '" + query + "', found " + books.size() + " available books.");
        for (Book b : books) {
            System.out.println("  - " + b.getName() + " (" + b.getBarcode() + ")");
        }
        return books;
    }

    /**
     * Checks if a book has available copies for issuing.
     *
     * @param bookId the ID of the book to check
     * @return true if the book has available copies, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isBookAvailable(int bookId) throws SQLException {
        String sql = "SELECT quantity FROM books WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    int issuedCount = new IssuedBookDAO().getIssuedCountForBook(bookId);
                    return issuedCount < quantity;
                }
            }
        }
        return false;
    }
}
