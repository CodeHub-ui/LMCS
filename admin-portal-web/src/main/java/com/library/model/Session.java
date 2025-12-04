package com.library.model;

public class Session {
    private int id;
    private String name;
    private String email;

    // Default constructor
    public Session() {}

    // Constructor
    public Session(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
