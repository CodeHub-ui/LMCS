// Placeholder for Faculty.java
package com.library.model;

public class Faculty {
    private int id;
    private String name;
    private String facultyId;
    private String email;
    private String mobile;
    private boolean active;
    private String rfid;
    private String department;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getRfid() { return rfid; }
    public void setRfid(String rfid) { this.rfid = rfid; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
