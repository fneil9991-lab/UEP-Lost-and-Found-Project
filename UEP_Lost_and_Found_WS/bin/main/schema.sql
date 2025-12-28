

-- MySQL Database Schema for UEP Lost and Found System

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS uep_lost_and_found_ws;
USE uep_lost_and_found_ws;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fname VARCHAR(50) NOT NULL,
    mname VARCHAR(50),
    lname VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'Active',
    request_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items table
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50),
    location VARCHAR(100),
    image VARCHAR(255),
    user_id INT NOT NULL,
    date_reported DATE NOT NULL,
    reported_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Claims table for found items
CREATE TABLE IF NOT EXISTS claims (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    claimant_id INT NOT NULL,
    claimant_username VARCHAR(50) NOT NULL,
    claim_description TEXT,
    status VARCHAR(20) DEFAULT 'Pending',
    date_submitted DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (claimant_id) REFERENCES users(id) ON DELETE CASCADE
);



-- Insert default admin user
-- Username: admin
-- Password: uep123 (BCrypt hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi)
-- Use INSERT IGNORE to avoid duplicate errors
INSERT IGNORE INTO users (fname, mname, lname, type, email, username, password, request_admin, status) VALUES
('System', '', 'Admin', 'Admin', 'admin@uep.edu.ph', 'admin', 'uep123', false, 'Active');

-- Alternative: Create admin user with username uep123 (if preferred)
-- INSERT IGNORE INTO users (fname, mname, lname, type, email, username, password, request_admin, status) VALUES
-- ('System', '', 'Administrator', 'Admin', 'admin@uep.edu.ph', 'uep123', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', false, 'Active');
