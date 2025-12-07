# 📚 ALMS – Automated Library Management System

ALMS is a Java-based desktop application designed to automate and modernize library operations.  
The system supports **barcode-based book scanning** and **RFID card-based user authentication**, making library transactions faster and more secure.

The application consists of two independent portals: **🛠️ Admin Portal** and **👤 User Portal**, connected to a centralized database.

---

## 📌 Project Overview

Traditional library systems rely heavily on manual data entry, which is time-consuming and error-prone.  
ALMS eliminates this by introducing:
- 📷 **Barcode scanning for books**
- 🪪 **RFID card based user login**

Admins manage the complete system, while users interact through a secure and automated process.

---

## 🏗️ System Architecture

- 🖥️ JavaFX-based desktop application  
- 🔐 Separate Admin and User portals  
- 🗄️ Centralized MySQL database  
- 🧩 MVC (Model–View–Controller) architecture  
- 📷 Barcode scanner integration  
- 🪪 RFID-based user authentication  
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
- 📷 **Hardware Integration:** Barcode Scanner & RFID Reader  

---

## 📂 Modules and Features

### 🛠️ Admin Portal

The Admin Portal gives administrators complete control.

✅ Key Features:
- 🔐 Admin login and secure session handling  
- 📊 Dashboard with system statistics  
- 📚 Book management using **barcode scanning**  
- 👥 Student and faculty management  
- 🔄 Book issue and return tracking  
- 🔍 Advanced search and monitoring  
- ✉️ Password reset via email verification  

> Admins can scan book barcodes to instantly fetch or store book details, reducing manual errors.

---

### 👤 User Portal

The User Portal is designed for library users.

✅ Key Features:
- 🪪 **RFID card-based login** (no manual username/password)  
- 📊 Dashboard showing issued and available books  
- 🔍 Book search and availability check  
- 📥 Automated book issue using barcode scan  
- 📤 Fast return process  
- 🧾 Profile viewing and borrowing history  

> Users authenticate themselves by tapping their RFID card, enabling quick and secure access.

---

## 🔐 Automation & Smart Features

- 📷 Barcode scanning for book identification  
- 🪪 RFID-based user authentication  
- ⚡ Reduced manual data entry  
- ❌ Minimizes human errors  
- ⏱️ Faster issue and return workflow  

---

## 🗂️ Project Structure

```text
ALMS/
├── admin-portal/
│   ├── controller/        # 🛠️ Admin-side UI controllers
│   ├── dao/               # 🗄️ Database access logic (JDBC)
│   ├── model/             # 📦 Entity and data models
│   ├── util/              # ⚙️ Utility and hardware helpers
│   └── Main.java          # ▶️ Admin application entry point
│
├── user-portal/
│   ├── controller/        # 👤 User-side UI controllers
│   ├── dao/               # 🗄️ Database access logic (JDBC)
│   ├── model/             # 📦 Entity and data models
│   ├── util/              # ⚙️ RFID and utility helpers
│   └── Main.java          # ▶️ User application entry point
│
├── database/              # 🗃️ Database schema / SQL files
├── pom.xml                # 📦 Maven configuration file
 
