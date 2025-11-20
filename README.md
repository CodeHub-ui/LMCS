 # 📚 Automatic Library Management Checkout System

An automated library management system that uses **RFID** for user entry/exit tracking and **Barcode scanning** for book issuing and returning.  
Built using **Java, JavaFX, JDBC, and MySQL**, this system follows the **MVC architecture** for clean and scalable code.

---

## 👥 Project Team

- **Head of Project:** Mr. Harshit Gupta  
- **Co-Founder:** Bhaskar / Nandini Gupta  

---

## 🚀 Project Description

This system automates library operations by integrating RFID and barcode technologies.  
- **RFID** is used to record user entry and exit automatically.  
- **Barcode scanning** is used for issuing and returning books.  
- An **Admin Portal** provides dashboards, analytics, user management, and book management.  

---

## 🛠️ Technologies Used

### **Programming Language**
- Java

### **Frameworks / Tools**
- **JavaFX** – Graphical User Interface  
- **JDBC** – Database connectivity  
- **Maven** – Build and dependency management  

### **Architecture**
**MVC Pattern**
- `controller` – handles user interactions  
- `dao` – performs database operations  
- `model` – holds data structures (`Book`, `Student`, `Admin`)  

---

## ⭐ Features

### ✅ **Entry/Exit Tracking (RFID Based)**
- Automatic logging of user entry and exit  
- Stores name, ID, and time in the database  
- Useful for attendance and security  

### ✅ **Book Issue & Return (Barcode Based)**
- RFID-based user authentication  
- Barcode scanning for book details  
- Updates book status in real-time  

### ✅ **Admin Portal**
- Dashboard showing:
  - Total Books
  - Issued Books
  - Returned Books
  - Total Users
  - Entry/Exit Logs
- User and Book Management  
- Date-wise filters & reports  

### ✅ **Security & Usability**
- Contactless authentication using RFID  
- Smooth experience with clear instructions  

---

## 📋 Workflows

### **1. User Entry/Exit Process**
1. User taps RFID card  
2. System verifies user  
3. Logs entry/exit with time  
4. Gate opens  

### **2. Book Issue/Return**
1. User taps RFID for authentication  
2. Selects *Issue* or *Return*  
3. Scans book barcode  
4. System updates database  

### **3. Admin Dashboard**
1. Admin logs in  
2. Views real-time statistics  
3. Generates reports  
4. Manages books and users  

---

## 🧱 Suggested Architecture Diagram (ASCII)
---
+------------------------+
|     RFID Scanner       |
+-----------+------------+
            |
            v
+------------------------+
|   Entry/Exit Module    |
+-----------+------------+
            |
            v
+------------------------+
|      Database (SQL)    |
+-----------+------------+
            |
            v
+------------------------+
|  Admin & User Portals  |
|  - Dashboard           |
|  - Issue/Return        |
|  - Logs                |
+------------------------+

---

## 🗄️ Database

Recommended: **MySQL**  
Stores:
- Users  
- Books  
- Issue/Return Records  
- Entry/Exit Logs  

 
<!--
## 📌 Future Enhancements (Optional)

- Email/SMS alerts  
- Book reservation system  
- Mobile app integration  
-->"
