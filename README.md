# ALMS – Automated Library Management System

ALMS is a Java-based desktop application developed to automate and manage library operations efficiently.  
The system consists of two separate portals: **Admin Portal** and **User Portal**, both connected to a centralized database.

This project follows industry-level practices such as MVC architecture, secure authentication, and structured database communication.

---

## Project Overview

Traditional library management involves manual tracking of books, users, and issue-return records, which often leads to errors and delays.  
ALMS solves this problem by providing a fully digital system where administrators manage the library and users interact with it through controlled access.

The project is developed for academic and practical learning purposes with real-world implementation logic.

---

## System Architecture

- Desktop application built using JavaFX  
- Separate Admin and User portals  
- Centralized MySQL database  
- MVC (Model–View–Controller) architecture  
- Secure authentication and session handling  

---

## Technology Stack

- Language: Java  
- UI Framework: JavaFX  
- Database: MySQL  
- Database Connectivity: JDBC  
- Build Tool: Maven  
- Architecture Pattern: MVC  
- Security: BCrypt password encryption  
- Email Service: Jakarta Mail API  

---

## Modules and Features

### Admin Portal

The Admin Portal provides full control over the library system.

Features include:
- Admin login and profile management  
- Dashboard with system statistics  
- Book management (add, update, delete, categorize)  
- Student and faculty management  
- Book issue and return tracking  
- Advanced search and monitoring  
- Password reset using email verification  

---

### User Portal

The User Portal is designed for students and faculty members.

Features include:
- Secure user login  
- Dashboard showing available and issued books  
- Book search and availability checking  
- Book issue request and issue history  
- Book return request  
- Profile viewing and limited updates  

---

## Project Structure

```text
ALMS/
├── admin-portal/
│   ├── controller/        # Admin-side UI controllers
│   ├── dao/               # Database access logic (JDBC)
│   ├── model/             # Entity and data models
│   ├── util/              # Utility and helper classes
│   └── Main.java          # Admin application entry point
│
├── user-portal/
│   ├── controller/        # User-side UI controllers
│   ├── dao/               # Database access logic (JDBC)
│   ├── model/             # Entity and data models
│   ├── util/              # Utility and helper classes
│   └── Main.java          # User application entry point
│
├── database/              # Database schema / SQL files
├── pom.xml                # Maven configuration file




