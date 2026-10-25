package com.dms.config;

import com.dms.model.*;
import com.dms.model.enums.*;
import com.dms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initializes demonstration data on application startup.
 * Provides realistic demo records across all roles and modules.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private DisasterReportRepository disasterReportRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already exists
        }

        // 1. Create Default Users (OOP Inheritance demonstration)
        Administrator admin = new Administrator(
                "admin",
                "admin@disasterrelief.org",
                passwordEncoder.encode("admin123"),
                "Dr. Robert Vance",
                "+1-800-555-0199",
                "National Disaster Response Center",
                2
        );
        userRepository.save(admin);

        Victim victim = new Victim(
                "victim1",
                "victim1@email.com",
                passwordEncoder.encode("victim123"),
                "Alice Johnson",
                "+1-555-0142",
                "+1-555-0199 (Uncle)",
                3,
                "Elderly mother requires insulin and clean drinking water."
        );
        victim.setCurrentCondition("Displaced");
        userRepository.save(victim);

        Volunteer volunteer = new Volunteer(
                "volunteer1",
                "volunteer1@email.com",
                passwordEncoder.encode("volunteer123"),
                "Sarah Connor",
                "+1-555-0178",
                "First Aid, Search & Rescue, Medical Triage, Driving",
                28.6139,
                77.2090
        );
        volunteer.setApprovedByAdmin(true);
        userRepository.save(volunteer);

        Volunteer volunteer2 = new Volunteer(
                "volunteer2",
                "volunteer2@email.com",
                passwordEncoder.encode("volunteer123"),
                "David Miller",
                "+1-555-0188",
                "Cooking & Food Distribution, Shelter Logistics, Ham Radio",
                28.6250,
                77.2150
        );
        volunteer2.setApprovedByAdmin(true);
        userRepository.save(volunteer2);

        // 2. Create Rescue Teams
        RescueTeam teamAlpha = new RescueTeam(
                "NDRF Bravo Quick Response",
                "Captain James Wilson",
                "+1-555-9988",
                "Flood Water Rescue & Boat Evacuation",
                8,
                "Sector 4 Staging Ground",
                28.6304,
                77.2177
        );
        teamAlpha.setStatus("AVAILABLE");
        rescueTeamRepository.save(teamAlpha);

        RescueTeam teamBravo = new RescueTeam(
                "Urban Search & Structural Collapse Squad",
                "Major Priya Sharma",
                "+1-555-9977",
                "Structural Collapse & Heavy Extrication",
                12,
                "Central Fire HQ",
                28.6050,
                77.2280
        );
        teamBravo.setStatus("DISPATCHED");
        rescueTeamRepository.save(teamBravo);

        RescueTeam teamCharlie = new RescueTeam(
                "Aero-Medical Airborne Triage Unit",
                "Lt. Marcus Brody",
                "+1-555-9966",
                "Helicopter Evacuation & Trauma Care",
                6,
                "Metro Heliport Bay 2",
                28.5800,
                77.2000
        );
        teamCharlie.setStatus("AVAILABLE");
        rescueTeamRepository.save(teamCharlie);

        // 3. Create Shelters
        Shelter shelter1 = new Shelter(
                "Central High School Relief Shelter",
                "124 Civic Center Road, North Zone",
                "Metro City",
                28.6448,
                77.2167,
                250,
                "Officer Mark Davis",
                "+1-555-3344",
                "Medical Ward, Clean Water Tankers, Emergency Generator, Hot Meals Kitchen, Sleeping Cots"
        );
        shelter1.allocate(35); // Initial occupancy
        shelterRepository.save(shelter1);

        Shelter shelter2 = new Shelter(
                "St. Jude Community Auditorium",
                "88 Church Hill Avenue, South District",
                "Metro City",
                28.5921,
                77.2290,
                120,
                "Sister Teresa Wood",
                "+1-555-4455",
                "First Aid Clinic, 24/7 Dining Hall, Sanitized Washrooms, Child Care Corner"
        );
        shelter2.allocate(115); // Almost full
        shelterRepository.save(shelter2);

        Shelter shelter3 = new Shelter(
                "Riverside Sports Complex",
                "15 Riverbank Boulevard, East Sector",
                "Metro City",
                28.6200,
                77.2500,
                500,
                "Inspector Arun Kumar",
                "+1-555-5566",
                "Field Hospital, Helipad Access, Telecommunication Desk, Blanket Depot"
        );
        shelterRepository.save(shelter3);

        // 4. Create Disaster Reports
        DisasterReport report1 = new DisasterReport(
                "Severe Flash Flood along Yamuna River Embankment",
                DisasterType.FLOOD,
                DisasterSeverity.CRITICAL,
                "River water has overflowed the primary dyke by 1.8 meters. Approximately 200 households partially submerged. Immediate boat evacuation required.",
                "Yamuna Embankment Lowland, Sector 12",
                "Metro City",
                28.6350,
                77.2400,
                victim,
                "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=600&q=80"
        );
        report1.setCasualtiesCount(2);
        report1.setAffectedCount(450);
        report1.verify(admin);
        disasterReportRepository.save(report1);

        DisasterReport report2 = new DisasterReport(
                "Major Landslide Blocking Highway NH-44",
                DisasterType.LANDSLIDE,
                DisasterSeverity.HIGH,
                "Heavy rock and mud slide completely obstructed four lanes of NH-44. At least 15 passenger vehicles trapped between checkpoints.",
                "Highway NH-44 Milepost 32",
                "Metro City Outskirts",
                28.6700,
                77.1800,
                volunteer,
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80"
        );
        report2.setAffectedCount(65);
        report2.setStatus(DisasterStatus.IN_PROGRESS);
        report2.verify(admin);
        disasterReportRepository.save(report2);

        DisasterReport report3 = new DisasterReport(
                "Commercial Complex Structural Fire Incident",
                DisasterType.FIRE,
                DisasterSeverity.CRITICAL,
                "3-story shopping complex on fire due to electrical short circuit after heavy thunderstorms. Smoke billowing; individuals trapped on roof terrace.",
                "Grand Plaza Mall, Central Avenue",
                "Metro City",
                28.6100,
                77.2100,
                admin,
                "https://images.unsplash.com/photo-1542385151-efd9000785a0?auto=format&fit=crop&w=600&q=80"
        );
        report3.setCasualtiesCount(1);
        report3.setAffectedCount(120);
        report3.verify(admin);
        disasterReportRepository.save(report3);

        DisasterReport report4 = new DisasterReport(
                "Moderate Seismic Tremors (5.4 Richter Scale)",
                DisasterType.EARTHQUAKE,
                DisasterSeverity.MEDIUM,
                "Shaking felt across city for 18 seconds. Hairline cracks reported in older brick structures; no immediate structural collapse observed.",
                "Downtown Metropolitan Ward 3",
                "Metro City",
                28.5850,
                77.2350,
                victim,
                ""
        );
        report4.setStatus(DisasterStatus.REPORTED);
        disasterReportRepository.save(report4);

        // 5. Create Emergency Requests
        EmergencyRequest req1 = new EmergencyRequest(
                RequestType.RESCUE,
                RequestPriority.CRITICAL,
                "Family of 5 stranded on second-floor balcony due to rising flood waters. Water level rising fast; 1 infant present.",
                5,
                "House #42, Lane 3, Yamuna Floodplain",
                "Metro City",
                28.6360,
                77.2420,
                victim,
                "+1-555-0142"
        );
        req1.assignToRescueTeam(teamAlpha);
        req1.markInProgress();
        emergencyRequestRepository.save(req1);

        EmergencyRequest req2 = new EmergencyRequest(
                RequestType.FOOD_WATER,
                RequestPriority.HIGH,
                "20 families sheltered at municipal community hall without drinking water or dry food packets since 18 hours.",
                45,
                "Old Municipal Hall, Gandhi Road",
                "Metro City",
                28.6280,
                77.2050,
                victim,
                "+1-555-7788"
        );
        req2.assignToVolunteer(volunteer);
        req2.setStatus(RequestStatus.ASSIGNED);
        emergencyRequestRepository.save(req2);

        EmergencyRequest req3 = new EmergencyRequest(
                RequestType.MEDICAL_AID,
                RequestPriority.HIGH,
                "Elderly diabetic patient suffering severe dehydration and fever. Requires doctor attention and IV saline.",
                1,
                "Flat 102, Block B, Silver Palms Apt",
                "Metro City",
                28.6180,
                77.2220,
                victim,
                "+1-555-3322"
        );
        req3.setStatus(RequestStatus.PENDING);
        emergencyRequestRepository.save(req3);

        // 6. Create Relief Resources
        resourceRepository.save(new Resource("Emergency Food Rations (3-Day)", ResourceType.FOOD, 1200, ResourceUnit.PACKETS, "Central Warehouse Bay A", "WH-01", 100));
        resourceRepository.save(new Resource("Purified Mineral Water (2L)", ResourceType.WATER, 3400, ResourceUnit.LITERS, "Sector 8 Depot", "WH-02", 300));
        resourceRepository.save(new Resource("Trauma & First Aid Medical Kits", ResourceType.MEDICAL, 85, ResourceUnit.KITS, "Metro Health Logistics Center", "WH-MED", 40));
        resourceRepository.save(new Resource("Heavy Duty Thermal Blankets", ResourceType.CLOTHING, 450, ResourceUnit.BLANKETS, "Relief Supply Hub 3", "WH-03", 80));
        resourceRepository.save(new Resource("Inflatable Rescue Rafts", ResourceType.RESCUE_GEAR, 14, ResourceUnit.UNITS, "Fire Station Marina", "WH-MAR", 5));
        resourceRepository.save(new Resource("Emergency Family Tents (6-person)", ResourceType.SHELTER_EQUIPMENT, 60, ResourceUnit.UNITS, "Civil Defense Compound", "WH-CD", 20));

        // 7. Seed Initial Activity Logs
        activityLogRepository.save(new ActivityLog("System", "SYSTEM", "SYSTEM_INITIALIZED", "System", 1L, "Disaster Management System seeded with baseline demonstration data."));
        activityLogRepository.save(new ActivityLog("admin", "ADMIN", "VERIFY_REPORT", "DisasterReport", report1.getId(), "Verified high-priority flood report along Yamuna embankment."));
        activityLogRepository.save(new ActivityLog("admin", "ADMIN", "ASSIGN_TEAM", "EmergencyRequest", req1.getId(), "Dispatched NDRF Bravo Team for urgent flood water rescue."));
    }
}
