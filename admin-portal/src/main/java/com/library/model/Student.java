// =====================================
// Student.java
// Model class representing a student in the library system.
// Includes personal details, RFID for identification, and course information.
// =====================================

package com.library.model;

public class Student {
    private int id;
    private String name;
    private String studentId;
    private String email;
    private String mobile;
    private String rfid;
    private String course; // New field for course
    private boolean active;

    // Default constructor
    public Student() {
    }

    // Constructor for creating a new student
    public Student(String name, String studentId, String email, String mobile, String rfid, String course, boolean active) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.mobile = mobile;
        this.rfid = rfid;
        this.course = course;
        this.active = active;
    }



    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getRfid() { return rfid; }
    public void setRfid(String rfid) { this.rfid = rfid; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
