package com.library.model;

public class Session {
    private static Object loggedInUser;

    private int id;
    private String name;
    private String email;

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

    public static Object getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(Object user) {
        loggedInUser = user;
    }

    public static void logout() {
        loggedInUser = null;
    }

    public static Admin getLoggedInAdmin() {
        return loggedInUser instanceof Admin ? (Admin) loggedInUser : null;
    }

    public static Student getLoggedInStudent() {
        return loggedInUser instanceof Student ? (Student) loggedInUser : null;
    }

    public static Faculty getLoggedInFaculty() {
        return loggedInUser instanceof Faculty ? (Faculty) loggedInUser : null;
    }
}
