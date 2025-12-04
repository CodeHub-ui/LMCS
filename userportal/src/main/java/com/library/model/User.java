package com.library.model;

/**
 * Represents a User entity in the library system.
 * This class encapsulates user information including personal details and RFID for authentication.
 */
public class User {
    private int id;           // Unique identifier for the student
    private String name;      // Full name of the student
    private String studentId; // Student ID (e.g., enrollment number)
    private String email;     // Email address of the student
    private String mobile;    // Mobile phone number
    private String rfid;      // RFID tag for card-based login
    private String course;    // Course of the student
    private boolean active;   // Indicates if the student account is active

    /**
     * Gets the unique ID of the student.
     * @return the student ID
     */
    public int getId() { return id; }

    /**
     * Sets the unique ID of the student.
     * @param id the student ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gets the full name of the student.
     * @return the student's name
     */
    public String getName() { return name; }

    /**
     * Sets the full name of the student.
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the student ID (e.g., enrollment number).
     * @return the student ID
     */
    public String getStudentId() { return studentId; }

    /**
     * Sets the student ID.
     * @param studentId the student ID to set
     */
    public void setStudentId(String studentId) { this.studentId = studentId; }

    /**
     * Gets the email address of the student.
     * @return the email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the email address of the student.
     * @param email the email to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the mobile phone number of the student.
     * @return the mobile number
     */
    public String getMobile() { return mobile; }

    /**
     * Sets the mobile phone number of the student.
     * @param mobile the mobile number to set
     */
    public void setMobile(String mobile) { this.mobile = mobile; }

    /**
     * Gets the RFID tag associated with the student.
     * @return the RFID tag
     */
    public String getRfid() { return rfid; }

    /**
     * Sets the RFID tag for the student.
     * @param rfid the RFID tag to set
     */
    public void setRfid(String rfid) { this.rfid = rfid; }

    /**
     * Gets the course of the student.
     * @return the course
     */
    public String getCourse() { return course; }

    /**
     * Sets the course of the student.
     * @param course the course to set
     */
    public void setCourse(String course) { this.course = course; }

    /**
     * Checks if the student account is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() { return active; }

    /**
     * Sets the active status of the student account.
     * @param active true to activate, false to deactivate
     */
    public void setActive(boolean active) { this.active = active; }
}
