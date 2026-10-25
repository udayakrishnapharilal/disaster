-- ====================================================================
-- Disaster Management System (DMS)
-- MySQL Database Schema Definition (DDL)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `disaster_management_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `disaster_management_db`;

-- 1. Base Users Table (Inheritance Type: JOINED)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20),
    `address` VARCHAR(255),
    `role` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Administrators Table (Inherits users)
CREATE TABLE IF NOT EXISTS `administrators` (
    `id` BIGINT PRIMARY KEY,
    `department` VARCHAR(100),
    `admin_level` INT DEFAULT 1,
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Shelters Table
CREATE TABLE IF NOT EXISTS `shelters` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `address` TEXT NOT NULL,
    `city` VARCHAR(100),
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `total_capacity` INT NOT NULL,
    `current_occupancy` INT NOT NULL DEFAULT 0,
    `contact_person` VARCHAR(100),
    `contact_phone` VARCHAR(30),
    `facilities` TEXT,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_shelters_city` (`city`),
    INDEX `idx_shelters_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Victims Table (Inherits users)
CREATE TABLE IF NOT EXISTS `victims` (
    `id` BIGINT PRIMARY KEY,
    `current_condition` VARCHAR(100) DEFAULT 'Safe',
    `emergency_contact` VARCHAR(50),
    `medical_needs` TEXT,
    `family_members_count` INT DEFAULT 1,
    `allocated_shelter_id` BIGINT,
    CONSTRAINT `fk_victim_user` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_victim_shelter` FOREIGN KEY (`allocated_shelter_id`) REFERENCES `shelters` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Volunteers Table (Inherits users)
CREATE TABLE IF NOT EXISTS `volunteers` (
    `id` BIGINT PRIMARY KEY,
    `skills` TEXT,
    `availability_status` VARCHAR(50) DEFAULT 'AVAILABLE',
    `assigned_tasks_count` INT DEFAULT 0,
    `approved_by_admin` BOOLEAN DEFAULT FALSE,
    `latitude` DOUBLE,
    `longitude` DOUBLE,
    CONSTRAINT `fk_volunteer_user` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_volunteers_availability` (`availability_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Rescue Teams Table
CREATE TABLE IF NOT EXISTS `rescue_teams` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `team_name` VARCHAR(100) NOT NULL UNIQUE,
    `leader_name` VARCHAR(100) NOT NULL,
    `contact_number` VARCHAR(20) NOT NULL,
    `specialization` VARCHAR(100),
    `member_count` INT NOT NULL DEFAULT 5,
    `status` VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    `current_location` VARCHAR(150),
    `latitude` DOUBLE,
    `longitude` DOUBLE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_rescue_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Disaster Reports Table
CREATE TABLE IF NOT EXISTS `disaster_reports` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `disaster_type` VARCHAR(30) NOT NULL,
    `severity` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    `status` VARCHAR(20) NOT NULL DEFAULT 'REPORTED',
    `description` TEXT NOT NULL,
    `location_name` VARCHAR(200) NOT NULL,
    `city` VARCHAR(100),
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `reported_by_user_id` BIGINT,
    `verified_by_admin_id` BIGINT,
    `reported_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `verified_at` DATETIME,
    `photo_url` TEXT,
    `casualties_count` INT DEFAULT 0,
    `affected_count` INT DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_report_user` FOREIGN KEY (`reported_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_report_admin` FOREIGN KEY (`verified_by_admin_id`) REFERENCES `administrators` (`id`) ON DELETE SET NULL,
    INDEX `idx_report_type` (`disaster_type`),
    INDEX `idx_report_status` (`status`),
    INDEX `idx_report_severity` (`severity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Emergency Requests Table
CREATE TABLE IF NOT EXISTS `emergency_requests` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `request_type` VARCHAR(30) NOT NULL,
    `priority` VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `description` TEXT NOT NULL,
    `number_of_people` INT NOT NULL DEFAULT 1,
    `location_name` VARCHAR(200) NOT NULL,
    `city` VARCHAR(100),
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `requester_id` BIGINT,
    `assigned_rescue_team_id` BIGINT,
    `assigned_volunteer_id` BIGINT,
    `contact_phone` VARCHAR(30),
    `medical_notes` TEXT,
    `requested_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `assigned_at` DATETIME,
    `fulfilled_at` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_req_requester` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_req_team` FOREIGN KEY (`assigned_rescue_team_id`) REFERENCES `rescue_teams` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_req_volunteer` FOREIGN KEY (`assigned_volunteer_id`) REFERENCES `volunteers` (`id`) ON DELETE SET NULL,
    INDEX `idx_req_type` (`request_type`),
    INDEX `idx_req_status` (`status`),
    INDEX `idx_req_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Shelter Allocations Table
CREATE TABLE IF NOT EXISTS `shelter_allocations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `shelter_id` BIGINT NOT NULL,
    `victim_id` BIGINT NOT NULL,
    `people_count` INT NOT NULL DEFAULT 1,
    `allocated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `discharged_at` DATETIME,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ALLOCATED',
    `notes` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_alloc_shelter` FOREIGN KEY (`shelter_id`) REFERENCES `shelters` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_alloc_victim` FOREIGN KEY (`victim_id`) REFERENCES `victims` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Resources Inventory Table
CREATE TABLE IF NOT EXISTS `resources` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `type` VARCHAR(30) NOT NULL,
    `quantity` INT NOT NULL DEFAULT 0,
    `unit` VARCHAR(30) NOT NULL,
    `storage_location` VARCHAR(150) NOT NULL,
    `warehouse_code` VARCHAR(50),
    `min_threshold` INT NOT NULL DEFAULT 50,
    `status` VARCHAR(30) NOT NULL DEFAULT 'IN_STOCK',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_res_type` (`type`),
    INDEX `idx_res_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Resource Allocations Table
CREATE TABLE IF NOT EXISTS `resource_allocations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `resource_id` BIGINT NOT NULL,
    `emergency_request_id` BIGINT,
    `shelter_id` BIGINT,
    `quantity_allocated` INT NOT NULL,
    `allocated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `allocated_by` VARCHAR(100),
    `status` VARCHAR(30) NOT NULL DEFAULT 'DISTRIBUTED',
    `remarks` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_res_alloc_res` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_res_alloc_req` FOREIGN KEY (`emergency_request_id`) REFERENCES `emergency_requests` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_res_alloc_shl` FOREIGN KEY (`shelter_id`) REFERENCES `shelters` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Task Assignments Table
CREATE TABLE IF NOT EXISTS `task_assignments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT NOT NULL,
    `assigned_volunteer_id` BIGINT,
    `assigned_rescue_team_id` BIGINT,
    `disaster_report_id` BIGINT,
    `emergency_request_id` BIGINT,
    `priority` VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `assigned_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_date` DATETIME,
    `notes` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_task_vol` FOREIGN KEY (`assigned_volunteer_id`) REFERENCES `volunteers` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_task_team` FOREIGN KEY (`assigned_rescue_team_id`) REFERENCES `rescue_teams` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_task_rep` FOREIGN KEY (`disaster_report_id`) REFERENCES `disaster_reports` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_task_req` FOREIGN KEY (`emergency_request_id`) REFERENCES `emergency_requests` (`id`) ON DELETE SET NULL,
    INDEX `idx_task_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Activity Logs Table
CREATE TABLE IF NOT EXISTS `activity_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `actor_username` VARCHAR(100) NOT NULL,
    `actor_role` VARCHAR(50) NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_type` VARCHAR(50),
    `entity_id` BIGINT,
    `details` TEXT,
    `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_log_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
