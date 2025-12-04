package com.library.model;

public class Book {
    private int id;
    private String name;
    private String author;
    private String barcode;
    private int categoryId;
    private Category category;
    private int quantity;
    private boolean available;

    public Book() {}

    public Book(int id, String name, String author, String barcode, int categoryId, int quantity) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.available = quantity > 0;
    }

    public Book(int id, String name, String author, String barcode, int categoryId, Category category, int quantity) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.category = category;
        this.quantity = quantity;
        this.available = quantity > 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; this.available = quantity > 0; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

