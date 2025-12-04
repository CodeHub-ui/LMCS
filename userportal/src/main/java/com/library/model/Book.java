package com.library.model;

/**
 * Represents a Book entity in the library system.
 * This class encapsulates book information including title, author, barcode, and category.
 */
public class Book {
    private int id;          // Unique identifier for the book
    private String name;     // Title of the book
    private String author;   // Author of the book
    private String barcode;  // Unique barcode for the book
    private int categoryId;  // ID of the category this book belongs to
    private boolean available; // Availability status of the book

    /**
     * Default constructor for Book.
     */
    public Book() {}

    /**
     * Constructor for Book with all fields.
     * @param id the unique ID of the book
     * @param name the title of the book
     * @param author the author of the book
     * @param barcode the barcode of the book
     * @param categoryId the category ID of the book
     */
    public Book(int id, String name, String author, String barcode, int categoryId) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.available = true; // Default to available
    }

    /**
     * Gets the unique ID of the book.
     * @return the book ID
     */
    public int getId() { return id; }

    /**
     * Sets the unique ID of the book.
     * @param id the book ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gets the title of the book.
     * @return the book name
     */
    public String getName() { return name; }

    /**
     * Sets the title of the book.
     * @param name the book name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the author of the book.
     * @return the author name
     */
    public String getAuthor() { return author; }

    /**
     * Sets the author of the book.
     * @param author the author to set
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Gets the barcode of the book.
     * @return the barcode
     */
    public String getBarcode() { return barcode; }

    /**
     * Sets the barcode of the book.
     * @param barcode the barcode to set
     */
    public void setBarcode(String barcode) { this.barcode = barcode; }

    /**
     * Gets the category ID of the book.
     * @return the category ID
     */
    public int getCategoryId() { return categoryId; }

    /**
     * Sets the category ID of the book.
     * @param categoryId the category ID to set
     */
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    /**
     * Gets the availability status of the book.
     * @return true if the book is available, false otherwise
     */
    public boolean isAvailable() { return available; }

    /**
     * Sets the availability status of the book.
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) { this.available = available; }
}
