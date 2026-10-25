-- ====================================================================
-- Quick MySQL Setup Script for Disaster Management System
-- Run this in MySQL Workbench or mysql CLI:
--   mysql -u root -p < setup_mysql.sql
-- ====================================================================

CREATE DATABASE IF NOT EXISTS disaster_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Optional: Create application user with privileges
-- CREATE USER IF NOT EXISTS 'dms_user'@'localhost' IDENTIFIED BY 'dms_password123';
-- GRANT ALL PRIVILEGES ON disaster_management_db.* TO 'dms_user'@'localhost';
-- FLUSH PRIVILEGES;

USE disaster_management_db;
SOURCE schema.sql;
SOURCE data.sql;

SELECT 'Database disaster_management_db configured successfully!' AS Message;
