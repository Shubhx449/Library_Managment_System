# 📚 Library Management System

> A console-based Library Management System built with **Java**, **JDBC**, and **MySQL**, following a clean **4-layer architecture** with the **DAO design pattern**, role-based access control, and real-world software engineering practices.

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Roles & Permissions](#-roles--permissions)
- [Setup & Installation](#-setup--installation)
- [How to Run](#-how-to-run)
- [Default Credentials](#-default-credentials)
- [Key Design Decisions](#-key-design-decisions)
- [Business Rules](#-business-rules)
- [Future Enhancements](#-future-enhancements)

---

## 📌 Project Overview

The **Library Management System (LMS)** is a fully console-based Java application that simulates real-world library operations. It supports three distinct roles — **Admin**, **Librarian**, and **Student** — each with their own menu, dashboard, and set of operations.

The system handles everything from book management and student registration to book issuing, returning, reservations, and fine calculation — all backed by a relational MySQL database accessed through JDBC.

---

## ✨ Features

### 👨‍💼 Admin
- Dashboard with system-wide statistics
- Full Book & Category Management (Add, View, Search, Update, Delete)
- Librarian Management (Add, View, Search, Update, Deactivate)
- Student Management (View, Search, Update, Deactivate)
- View Issued, Overdue, and Returned Books
- Reports: Most Issued Books, Fine Collection Report, Active Students
- Change Password

### 👩‍💼 Librarian
- Dashboard with daily operational stats
- View and Search Books
- Register, View, Search, Update Students
- Issue Book, Return Book, Renew Book
- Manage Reservations (View, Cancel)
- View and Collect Fines
- Issue History and Today's Transactions Report
- Change Password

### 🎓 Student
- Personal Dashboard (borrowed books, reservations, pending fines)
- Search Books (by Title / Author)
- View Available Books and Book Details
- View My Borrowed Books and Borrow History
- Reserve Book, View Reservations, Cancel Reservation
- View Fine Details and Payment History
- View and Update Profile
- Change Password

---

## 🛠 Technology Stack

| Technology       | Purpose                          |
|------------------|----------------------------------|
| Java 11+         | Core application language        |
| JDBC             | Database connectivity            |
| MySQL 8.x        | Relational database              |
| mysql-connector-j| JDBC driver for MySQL            |
| Git & GitHub     | Version control                  |

---

## 🏗 Architecture

The project follows a strict **4-layer architecture** with clean separation of concerns:

```
┌──────────────────────────────────────┐
│           MENU / UI LAYER            │  ← MainMenu, AdminMenu, LibrarianMenu, StudentMenu
├──────────────────────────────────────┤
│          CONTROLLER LAYER            │  ← AdminController, LibrarianController, StudentController
├──────────────────────────────────────┤
│        SERVICE LAYER (Business)      │  ← BookService, IssueService, FineService, etc.
├──────────────────────────────────────┤
│           DAO LAYER (Data)           │  ← BookDAO, IssueDAO, FineDAO, etc. (interfaces + impl)
├──────────────────────────────────────┤
│           MySQL DATABASE             │  ← users, books, issued_books, fines, reservations, etc.
└──────────────────────────────────────┘
```

### Why This Architecture?

- **Menu Layer** handles only user input/output — no business logic.
- **Controller Layer** bridges menus and services — reads input, calls services, displays output.
- **Service Layer** contains all business rules (borrow limits, fine calculation, validation).
- **DAO Layer** handles only SQL — completely isolated from business logic.
- **Interfaces for every DAO** — enables easy swapping of database technology without touching any other layer (Open/Closed Principle).

---

## 📁 Project Structure

```
LibraryManagementSystem/
├── resources/
│       └── schema.sql                   ← Database schema + seed data
└── src/
    │
    ├── app/
    │   └── Main.java                    ← Entry point
    │
    ├── config/
    │   └── DBConnection.java            ← Singleton JDBC connection
    │
    ├── model/
    │   ├── User.java
    │   ├── Book.java
    │   ├── Student.java
    │   ├── Librarian.java
    │   ├── Category.java
    │   ├── Issue.java
    │   ├── Reservation.java
    │   └── Fine.java
    │
    ├── dao/                             ← Interfaces
    │   ├── UserDAO.java
    │   ├── BookDAO.java
    │   ├── StudentDAO.java
    │   ├── LibrarianDAO.java
    │   ├── CategoryDAO.java
    │   ├── IssueDAO.java
    │   ├── ReservationDAO.java
    │   ├── FineDAO.java
    │   └── ReportDAO.java
    │   └── impl/                        ← Implementations
    │       ├── UserDAOImpl.java
    │       ├── BookDAOImpl.java
    │       ├── StudentDAOImpl.java
    │       ├── LibrarianDAOImpl.java
    │       ├── CategoryDAOImpl.java
    │       ├── IssueDAOImpl.java
    │       ├── ReservationDAOImpl.java
    │       ├── FineDAOImpl.java
    │       └── ReportDAOImpl.java
    │
    ├── service/
    │   ├── AuthenticationService.java
    │   ├── BookService.java
    │   ├── StudentService.java
    │   ├── LibrarianService.java
    │   ├── CategoryService.java
    │   ├── IssueService.java
    │   ├── ReservationService.java
    │   ├── FineService.java
    │   ├── DashboardService.java
    │   └── ReportService.java
    │
    ├── controller/
    │   ├── LoginController.java
    │   ├── AdminController.java
    │   ├── LibrarianController.java
    │   └── StudentController.java
    │
    ├── menu/
    │   ├── MainMenu.java
    │   ├── AdminMenu.java
    │   ├── LibrarianMenu.java
    │   └── StudentMenu.java
    │
    ├── util/
    │   ├── InputUtil.java               ← Centralised console input
    │   ├── ValidationUtil.java          ← Email, ISBN, phone validators
    │   ├── PasswordUtil.java            ← BCrypt-ready password hashing
    │   └── DateUtil.java               ← Fine calculation, date formatting
    │
    └── exception/
        ├── AuthenticationException.java
        ├── BookNotFoundException.java
        ├── StudentNotFoundException.java
        └── InvalidChoiceException.java
```

---

## 🗄 Database Schema

The database contains **7 tables**:

```
users           ← Base account table for all roles
│
├── librarians  ← Extended profile for LIBRARIAN role
│
└── students    ← Extended profile for STUDENT role

categories      ← Book categories

books           ← Book catalog (linked to categories)

issued_books    ← Borrow records (student ↔ book ↔ librarian)

reservations    ← Book reservation requests

fines           ← Overdue fine records (linked to issued_books)

activity_logs   ← Audit trail for user actions
```

### Entity Relationships

```
users ──< librarians
users ──< students
categories ──< books
students ──< issued_books >── books
students ──< reservations >── books
issued_books ──< fines
librarians ──< fines (collected_by)
```

---

## 🔐 Roles & Permissions

| Feature                    | Admin | Librarian | Student |
|---------------------------|:-----:|:---------:|:-------:|
| Add / Delete Books         |  ✅   |    ✅     |   ❌    |
| Manage Categories          |  ✅   |    ❌     |   ❌    |
| Add / Remove Librarians    |  ✅   |    ❌     |   ❌    |
| Register Students          |  ❌   |    ✅     |   ❌    |
| Issue Book                 |  ❌   |    ✅     |   ❌    |
| Return Book                |  ❌   |    ✅     |   ❌    |
| Renew Book                 |  ❌   |    ✅     |   ❌    |
| Collect Fine               |  ❌   |    ✅     |   ❌    |
| Reserve Book               |  ❌   |    ❌     |   ✅    |
| View Own Borrowed Books    |  ❌   |    ❌     |   ✅    |
| Search Books               |  ✅   |    ✅     |   ✅    |
| View Reports               |  ✅   |    ✅     |   ❌    |
| View Own Fine              |  ❌   |    ❌     |   ✅    |
| Change Password            |  ✅   |    ✅     |   ✅    |

---

## ⚙ Setup & Installation

### Prerequisites

| Requirement        | Version   |
|--------------------|-----------|
| Java JDK           | 11 or above |
| MySQL Server       | 8.x       |
| mysql-connector-j  | 8.x       |
| IDE (optional)     | IntelliJ IDEA / Eclipse / VS Code |

---

### Step 1 — Clone or Extract the Project

```bash
git clone https://github.com/yourusername/LibraryManagementSystem.git
# OR extract the downloaded ZIP
```

---

### Step 2 — Set Up the Database

Open MySQL Workbench or terminal and run:

```bash
mysql -u root -p < src/com/library/resources/schema.sql
```

This will:
- Create the `library_db` database
- Create all 7 tables
- Insert a default Admin account
- Insert 5 sample categories

---

### Step 3 — Add MySQL JDBC Driver

Download `mysql-connector-j-8.x.x.jar` from:
👉 https://dev.mysql.com/downloads/connector/j/

**IntelliJ IDEA:**
> File → Project Structure → Libraries → Add JAR

**Eclipse:**
> Project → Properties → Java Build Path → Add External JARs

**Command Line:**
> Place the JAR in a `lib/` folder and add to classpath when compiling/running.

---

### Step 4 — Configure Database Credentials

Open `src/com/library/config/DBConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";         // your MySQL username
private static final String PASSWORD = "yourpassword"; // your MySQL password
```

---

### Step 5 — Compile and Run

#### Using IntelliJ IDEA / Eclipse
- Open the project, add the JAR, and run `Main.java`.

#### Using Command Line

```bash
# Compile
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out \
  src/com/library/**/*.java src/com/library/app/Main.java

# Run
java -cp "out:lib/mysql-connector-j-8.x.x.jar" com.library.app.Main

# Windows (use semicolon instead of colon)
java -cp "out;lib/mysql-connector-j-8.x.x.jar" com.library.app.Main
```

---

## ▶ How to Run

1. Start MySQL server
2. Make sure `library_db` database is created (run schema.sql once)
3. Run `Main.java`
4. The application will verify DB connection on startup
5. Login with your credentials — the system routes you to the correct menu automatically

### Login Flow

```
=== LIBRARY MANAGEMENT SYSTEM ===
1. Login
2. Exit
Enter Choice: 1

  USER LOGIN
  Email    : admin@library.com
  Password : ********

  [✓] Login successful! Welcome, Administrator [ADMIN]

  ══════════════════════════════
           ADMIN MENU
  ══════════════════════════════
  1. Dashboard
  ...
```

---

## 🔑 Default Credentials

| Role      | Email                    | Password   |
|-----------|--------------------------|------------|
| Admin     | admin@library.com        | admin123   |

> ⚠️ **Change the default admin password immediately after first login.**

New Librarians and Students are created through the application itself.

---

## 🧠 Key Design Decisions

### 1. DAO Pattern with Interfaces
Every DAO is defined as an interface (`BookDAO`) with a concrete implementation (`BookDAOImpl`). This means:
- The Service layer depends on the interface, not the implementation
- Swapping from MySQL to PostgreSQL only requires a new `impl` class
- This is the **Open/Closed Principle** in practice

### 2. Singleton DBConnection
Creating a JDBC connection for every query is expensive. The `DBConnection` class maintains a single shared connection and reconnects automatically if it drops. The `getConnection()` method is `synchronized` for thread safety.

### 3. Service Layer for Business Rules
Business rules are **never** in the DAO (which only does SQL) and **never** in the Menu (which only does I/O). The Service layer owns rules like:
- A student cannot borrow more than 3 books
- A student with pending fines cannot borrow new books
- A book cannot be renewed if it is overdue

### 4. `Optional<T>` Instead of null
All DAO find methods return `Optional<T>`, forcing callers to handle the "not found" case explicitly and preventing `NullPointerException`.

### 5. Transaction Management
Operations that touch two tables (registering a student writes to both `users` and `students`) are wrapped in a JDBC transaction with `setAutoCommit(false)` / `commit()` / `rollback()`.

### 6. PreparedStatements Everywhere
All SQL uses `PreparedStatement` with `?` placeholders — never string concatenation. This completely prevents **SQL Injection**.

### 7. Custom Exceptions
Domain-specific exceptions (`BookNotFoundException`, `AuthenticationException`) make error handling at the Menu layer clean and readable.

---

## 📏 Business Rules

| Rule                                | Detail                                        |
|-------------------------------------|-----------------------------------------------|
| Default borrow period               | 14 days                                       |
| Maximum books per student           | 3 (configurable per student)                  |
| Renewal extension                   | 7 days from current due date                  |
| Fine rate                           | ₹2.00 per overdue day                         |
| Reservation expiry                  | 3 days after reservation is made              |
| Cannot borrow same book twice       | Checked before issuing                        |
| Cannot borrow with pending fines    | Checked before issuing                        |
| Cannot renew overdue books          | Must return and pay fine first                |
| Cannot reserve available books      | Reservation is only for unavailable books     |

---

## 🚀 Future Enhancements

- [ ] **BCrypt Password Hashing** — Replace plain-text with `jbcrypt` (PasswordUtil is already prepared)
- [ ] **Swing / JavaFX GUI** — The layered architecture allows replacing the `menu/` package without touching anything else
- [ ] **Connection Pooling** — Replace the singleton with HikariCP for production use
- [ ] **Activity Logs** — The `activity_logs` table and `LogDAO` are ready to be wired in
- [ ] **Email Notifications** — Overdue alerts and reservation confirmations via JavaMail
- [ ] **Export Reports** — CSV/PDF export for fine and issue reports
- [ ] **REST API** — Wrap the Service layer with Spring Boot for a web frontend

---

