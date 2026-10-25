-- ====================================================================
-- Disaster Management System (DMS)
-- MySQL Sample Data Insertion (DML)
-- Passwords below are standard BCrypt hashes of:
--   'admin123' for admin
--   'victim123' for victim1
--   'volunteer123' for volunteer1 and volunteer2
-- ====================================================================

USE `disaster_management_db`;

-- Users
INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `phone`, `address`, `role`, `status`) VALUES
(1, 'admin', 'admin@disasterrelief.org', '$2a$10$wT0l8o/q.6BwS0Gk6hL8eunrB5aYnQjZ7Wq6eE9e0Yp4Y9Y1E9Yqe', 'Dr. Robert Vance', '+1-800-555-0199', 'HQ Emergency Command Center', 'ADMIN', 'ACTIVE'),
(2, 'victim1', 'victim1@email.com', '$2a$10$wT0l8o/q.6BwS0Gk6hL8eunrB5aYnQjZ7Wq6eE9e0Yp4Y9Y1E9Yqe', 'Alice Johnson', '+1-555-0142', 'Block C, Yamuna Embankment', 'VICTIM', 'ACTIVE'),
(3, 'volunteer1', 'volunteer1@email.com', '$2a$10$wT0l8o/q.6BwS0Gk6hL8eunrB5aYnQjZ7Wq6eE9e0Yp4Y9Y1E9Yqe', 'Sarah Connor', '+1-555-0178', 'North Ridge Colony', 'VOLUNTEER', 'ACTIVE'),
(4, 'volunteer2', 'volunteer2@email.com', '$2a$10$wT0l8o/q.6BwS0Gk6hL8eunrB5aYnQjZ7Wq6eE9e0Yp4Y9Y1E9Yqe', 'David Miller', '+1-555-0188', 'East Suburb Avenue', 'VOLUNTEER', 'ACTIVE')
ON DUPLICATE KEY UPDATE `username`=`username`;

-- Administrators
INSERT INTO `administrators` (`id`, `department`, `admin_level`) VALUES
(1, 'National Disaster Response Center', 2)
ON DUPLICATE KEY UPDATE `department`=`department`;

-- Shelters
INSERT INTO `shelters` (`id`, `name`, `address`, `city`, `latitude`, `longitude`, `total_capacity`, `current_occupancy`, `contact_person`, `contact_phone`, `facilities`, `status`) VALUES
(1, 'Central High School Relief Shelter', '124 Civic Center Road, North Zone', 'Metro City', 28.6448, 77.2167, 250, 35, 'Officer Mark Davis', '+1-555-3344', 'Medical Ward, Clean Water Tankers, Emergency Generator, Hot Meals Kitchen, Sleeping Cots', 'ACTIVE'),
(2, 'St. Jude Community Auditorium', '88 Church Hill Avenue, South District', 'Metro City', 28.5921, 77.2290, 120, 115, 'Sister Teresa Wood', '+1-555-4455', 'First Aid Clinic, 24/7 Dining Hall, Sanitized Washrooms, Child Care Corner', 'ACTIVE'),
(3, 'Riverside Sports Complex', '15 Riverbank Boulevard, East Sector', 'Metro City', 28.6200, 77.2500, 500, 0, 'Inspector Arun Kumar', '+1-555-5566', 'Field Hospital, Helipad Access, Telecommunication Desk, Blanket Depot', 'ACTIVE')
ON DUPLICATE KEY UPDATE `name`=`name`;

-- Victims
INSERT INTO `victims` (`id`, `current_condition`, `emergency_contact`, `medical_needs`, `family_members_count`, `allocated_shelter_id`) VALUES
(2, 'Displaced', '+1-555-0199 (Uncle)', 'Elderly mother requires insulin and clean drinking water.', 3, 1)
ON DUPLICATE KEY UPDATE `current_condition`=`current_condition`;

-- Volunteers
INSERT INTO `volunteers` (`id`, `skills`, `availability_status`, `assigned_tasks_count`, `approved_by_admin`, `latitude`, `longitude`) VALUES
(3, 'First Aid, Search & Rescue, Medical Triage, Driving', 'AVAILABLE', 1, TRUE, 28.6139, 77.2090),
(4, 'Cooking & Food Distribution, Shelter Logistics, Ham Radio', 'AVAILABLE', 0, TRUE, 28.6250, 77.2150)
ON DUPLICATE KEY UPDATE `skills`=`skills`;

-- Rescue Teams
INSERT INTO `rescue_teams` (`id`, `team_name`, `leader_name`, `contact_number`, `specialization`, `member_count`, `status`, `current_location`, `latitude`, `longitude`) VALUES
(1, 'NDRF Bravo Quick Response', 'Captain James Wilson', '+1-555-9988', 'Flood Water Rescue & Boat Evacuation', 8, 'AVAILABLE', 'Sector 4 Staging Ground', 28.6304, 77.2177),
(2, 'Urban Search & Structural Collapse Squad', 'Major Priya Sharma', '+1-555-9977', 'Structural Collapse & Heavy Extrication', 12, 'DISPATCHED', 'Central Fire HQ', 28.6050, 77.2280),
(3, 'Aero-Medical Airborne Triage Unit', 'Lt. Marcus Brody', '+1-555-9966', 'Helicopter Evacuation & Trauma Care', 6, 'AVAILABLE', 'Metro Heliport Bay 2', 28.5800, 77.2000)
ON DUPLICATE KEY UPDATE `team_name`=`team_name`;

-- Disaster Reports
INSERT INTO `disaster_reports` (`id`, `title`, `disaster_type`, `severity`, `status`, `description`, `location_name`, `city`, `latitude`, `longitude`, `reported_by_user_id`, `verified_by_admin_id`, `casualties_count`, `affected_count`, `photo_url`) VALUES
(1, 'Severe Flash Flood along Yamuna River Embankment', 'FLOOD', 'CRITICAL', 'VERIFIED', 'River water has overflowed the primary dyke by 1.8 meters. Approximately 200 households partially submerged. Immediate boat evacuation required.', 'Yamuna Embankment Lowland, Sector 12', 'Metro City', 28.6350, 77.2400, 2, 1, 2, 450, 'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=600&q=80'),
(2, 'Major Landslide Blocking Highway NH-44', 'LANDSLIDE', 'HIGH', 'IN_PROGRESS', 'Heavy rock and mud slide completely obstructed four lanes of NH-44. At least 15 passenger vehicles trapped between checkpoints.', 'Highway NH-44 Milepost 32', 'Metro City Outskirts', 28.6700, 77.1800, 3, 1, 0, 65, 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80'),
(3, 'Commercial Complex Structural Fire Incident', 'FIRE', 'CRITICAL', 'VERIFIED', '3-story shopping complex on fire due to electrical short circuit after heavy thunderstorms. Smoke billowing; individuals trapped on roof terrace.', 'Grand Plaza Mall, Central Avenue', 'Metro City', 28.6100, 77.2100, 1, 1, 1, 120, 'https://images.unsplash.com/photo-1542385151-efd9000785a0?auto=format&fit=crop&w=600&q=80')
ON DUPLICATE KEY UPDATE `title`=`title`;

-- Emergency Requests
INSERT INTO `emergency_requests` (`id`, `request_type`, `priority`, `status`, `description`, `number_of_people`, `location_name`, `city`, `latitude`, `longitude`, `requester_id`, `assigned_rescue_team_id`, `assigned_volunteer_id`, `contact_phone`) VALUES
(1, 'RESCUE', 'CRITICAL', 'IN_PROGRESS', 'Family of 5 stranded on second-floor balcony due to rising flood waters. Water level rising fast; 1 infant present.', 5, 'House #42, Lane 3, Yamuna Floodplain', 'Metro City', 28.6360, 77.2420, 2, 1, NULL, '+1-555-0142'),
(2, 'FOOD_WATER', 'HIGH', 'ASSIGNED', '20 families sheltered at municipal community hall without drinking water or dry food packets since 18 hours.', 45, 'Old Municipal Hall, Gandhi Road', 'Metro City', 28.6280, 77.2050, 2, NULL, 3, '+1-555-7788')
ON DUPLICATE KEY UPDATE `description`=`description`;

-- Resources
INSERT INTO `resources` (`id`, `name`, `type`, `quantity`, `unit`, `storage_location`, `warehouse_code`, `min_threshold`, `status`) VALUES
(1, 'Emergency Food Rations (3-Day)', 'FOOD', 1200, 'PACKETS', 'Central Warehouse Bay A', 'WH-01', 100, 'IN_STOCK'),
(2, 'Purified Mineral Water (2L)', 'WATER', 3400, 'LITERS', 'Sector 8 Depot', 'WH-02', 300, 'IN_STOCK'),
(3, 'Trauma & First Aid Medical Kits', 'MEDICAL', 85, 'KITS', 'Metro Health Logistics Center', 'WH-MED', 40, 'IN_STOCK'),
(4, 'Heavy Duty Thermal Blankets', 'CLOTHING', 450, 'BLANKETS', 'Relief Supply Hub 3', 'WH-03', 80, 'IN_STOCK'),
(5, 'Inflatable Rescue Rafts', 'RESCUE_GEAR', 14, 'UNITS', 'Fire Station Marina', 'WH-MAR', 5, 'IN_STOCK'),
(6, 'Emergency Family Tents (6-person)', 'SHELTER_EQUIPMENT', 60, 'UNITS', 'Civil Defense Compound', 'WH-CD', 20, 'IN_STOCK')
ON DUPLICATE KEY UPDATE `name`=`name`;
