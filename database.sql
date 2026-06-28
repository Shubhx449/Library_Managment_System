-- ============================================================
--  Library Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ---------------------------------------------------------------
-- Table: users  (base table for all roles)
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'LIBRARIAN', 'STUDENT') NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- Table: librarians
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS librarians (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL UNIQUE,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    phone       VARCHAR(15),
    address     TEXT,
    joined_at   DATE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- Table: students
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL UNIQUE,
    student_id      VARCHAR(20) UNIQUE NOT NULL,
    phone           VARCHAR(15),
    department      VARCHAR(100),
    semester        INT,
    max_books       INT DEFAULT 3,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- Table: categories
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- Table: books
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    author          VARCHAR(150) NOT NULL,
    isbn            VARCHAR(20) UNIQUE NOT NULL,
    category_id     INT,
    publisher       VARCHAR(150),
    publish_year    YEAR,
    total_copies    INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    shelf_location  VARCHAR(50),
    added_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------
-- Table: issued_books
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS issued_books (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    book_id         INT NOT NULL,
    librarian_id    INT NOT NULL,
    issued_date     DATE NOT NULL,
    due_date        DATE NOT NULL,
    return_date     DATE,
    status          ENUM('ISSUED', 'RETURNED', 'OVERDUE') DEFAULT 'ISSUED',
    FOREIGN KEY (student_id)   REFERENCES students(id),
    FOREIGN KEY (book_id)      REFERENCES books(id),
    FOREIGN KEY (librarian_id) REFERENCES librarians(id)
);

-- ---------------------------------------------------------------
-- Table: reservations
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reservations (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    book_id         INT NOT NULL,
    reserved_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date     DATE NOT NULL,
    status          ENUM('PENDING', 'FULFILLED', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING',
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (book_id)    REFERENCES books(id)
);

-- ---------------------------------------------------------------
-- Table: fines
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fines (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    issue_id        INT NOT NULL UNIQUE,
    student_id      INT NOT NULL,
    amount          DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    paid_amount     DECIMAL(8,2) DEFAULT 0.00,
    status          ENUM('PENDING', 'PAID', 'WAIVED') DEFAULT 'PENDING',
    collected_by    INT,
    collected_at    TIMESTAMP,
    FOREIGN KEY (issue_id)      REFERENCES issued_books(id),
    FOREIGN KEY (student_id)    REFERENCES students(id),
    FOREIGN KEY (collected_by)  REFERENCES librarians(id)
);

-- ---------------------------------------------------------------
-- Table: activity_logs
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activity_logs (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    action      VARCHAR(200) NOT NULL,
    details     TEXT,
    logged_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ---------------------------------------------------------------
-- Seed: Default Admin
-- password: admin123 (BCrypt hash)
-- ---------------------------------------------------------------
INSERT IGNORE INTO users (name, email, password, role)
VALUES ('Administrator', 'admin@library.com',
        'Random@123',
        'ADMIN');

-- ---------------------------------------------------------------
-- Seed: Sample Categories
-- ---------------------------------------------------------------
INSERT IGNORE INTO categories (name, description) VALUES
    ('Computer Science', 'Programming, algorithms, and computing'),
    ('Mathematics',      'Pure and applied mathematics'),
    ('Physics',          'Classical and modern physics'),
    ('Fiction',          'Novels and short stories'),
    ('Reference',        'Encyclopedias, dictionaries, and atlases');