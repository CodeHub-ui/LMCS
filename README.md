# 📚 ALMS – Automated Library Management System

ALMS (Automated Library Management System) is a Java-based desktop application developed to automate and modernize library operations.  
The system integrates **barcode-based book scanning** and **RFID card-based user authentication**, enabling fast, secure, and error-free library transactions.

The project consists of **two independent desktop applications**:
- 🛠️ **Admin Portal (EXE)**
- 👤 **User Portal (EXE)**

A lightweight **website is used only as a distribution platform** to download and install these desktop portals easily.

---

## 📌 Project Overview

Traditional library systems rely heavily on manual book handling and user verification, which is time-consuming and prone to errors.  
ALMS automates the entire library workflow by combining desktop applications with hardware integration.

### ✅ Key Highlights
- 📷 Barcode-based book scanning  
- 🪪 RFID card-based user authentication  
- 🖥️ Separate Admin and User desktop applications  
- 🌐 Website used as a centralized download hub  

> Note: The website does **not** perform any library operations.  
> All core functionalities are handled by the desktop applications.

---

## 🌐 Website Role (Important)

The website included in the project is used **only for distribution purposes**.

### ✅ Website Responsibilities
- Provides download links for:
  - 🛠️ Admin Portal (`Admin.exe`)
  - 👤 User Portal (`User.exe`)
- Helps install the application on multiple systems
- Acts as a central access point for updated builds

### ❌ Website Does NOT
- Manage users or books  
- Connect to the database  
- Perform issue or return actions  

---

## 🏗️ System Architecture

- 🖥️ JavaFX-based desktop applications  
- 🔐 Role-based access (Admin / User)  
- 🗄️ Centralized MySQL database  
- 🧩 MVC (Model–View–Controller) architecture  
- 📷 Barcode scanner integration  
- 🪪 RFID-based authentication  
- 🔒 Secure session handling  

---

## 🛠️ Technology Stack

- 🟦 **Language:** Java  
- 🎨 **UI Framework:** JavaFX  
- 🗄️ **Database:** MySQL  
- 🔗 **Database Connectivity:** JDBC  
- 📦 **Build Tool:** Maven  
- 🧩 **Architecture:** MVC  
- 🔐 **Security:** BCrypt password hashing  
- ✉️ **Email Service:** Jakarta Mail API  
- 📷 **Hardware:** Barcode Scanner  
- 🪪 **Hardware:** RFID Reader  
- 🌐 **Website:** HTML / CSS  

---

## 📂 Modules and Features

### 🛠️ Admin Portal (Desktop EXE)

The Admin Portal provides complete control over library operations.

**Features:**
- Secure admin login and session handling  
- Dashboard with library statistics  
- Book management using barcode scanning  
- Student and faculty management  
- Issue and return tracking  
- Advanced search and monitoring  
- Email-based password reset  

> Barcodes allow admins to instantly fetch or store book information, reducing manual work.

---

### 👤 User Portal (Desktop EXE)

The User Portal is designed for library members.

**Features:**
- RFID card-based login (no username/password)  
- View available and issued books  
- Automated issue and return process  
- Borrowing history  
- User profile view  

> Users authenticate by tapping their RFID card for quick and secure access.

---

## 🔐 Automation & Smart Features

- 📷 Barcode scanning for book identification  
- 🪪 RFID-based user authentication  
- ⚡ Reduced manual data entry  
- ❌ Minimal human errors  
- ⏱️ Faster issue and return workflow  

---

## 🗂️ Project Structure

```text
ALMS/
├── admin-portal/
│   ├── controller/        # Admin-side UI controllers
│   ├── dao/               # Database access logic
│   ├── model/             # Entity and data models
│   ├── util/              # Utility and hardware helpers
│   └── Main.java          # Admin application entry point
│
├── user-portal/
│   ├── controller/        # User-side UI controllers
│   ├── dao/               # Database access logic
│   ├── model/             # Entity and data models
│   ├── util/              # RFID utilities
│   └── Main.java          # User application entry point
│
├── database/              # SQL schema and scripts
├── website/               # EXE download website (index.html)
├── pom.xml                # Maven configuration

```
---

## 📬✨ Contact

Have questions, feedback, or ideas to improve this project?  
Feel free to reach out to the team 👇

- 👨‍💼 **Project Lead:** Harshit Gupta  
- 👥 **Project Team:** ALMS Development Team  
- 📌 **Project:** ALMS – Automated Library Management System  
- 📧 **Email:** mrharshitgupta81@gmail.com  
- 🌐 **GitHub Repository:** https://github.com/CodeHub-ui 

---

## 🎉🙏 Thank You

Thank you for checking out **ALMS – Automated Library Management System** 📚✨  

This project was built as a **team effort under the guidance of the Project Lead**, focusing on:
- ⚡ Automation using Barcode & RFID  
- 🔐 Secure and role-based system design  
- 🖥️ JavaFX desktop application development  
- 🗄️ Real-world database handling  

Your time, feedback, and suggestions are highly appreciated 💙  
Happy coding and learning 🚀😄
