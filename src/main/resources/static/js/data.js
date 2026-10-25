/**
 * Disaster Management System - Demo Data Store
 * Provides rich fallback data and localStorage persistence for offline/client-side execution.
 */

const DMS_MOCK_DATA = {
    users: [
        { id: 1, username: 'admin', email: 'admin@disasterrelief.org', fullName: 'Dr. Robert Vance', phone: '+1-800-555-0199', role: 'ADMIN', status: 'ACTIVE', department: 'National Crisis Coordination', adminLevel: 2 },
        { id: 2, username: 'victim1', email: 'victim1@email.com', fullName: 'Alice Johnson', phone: '+1-555-0142', role: 'VICTIM', status: 'ACTIVE', emergencyContact: '+1-555-0199', familyMembersCount: 3, medicalNeeds: 'Elderly mother requires insulin and clean drinking water.', currentCondition: 'Displaced', allocatedShelterId: 1 },
        { id: 3, username: 'volunteer1', email: 'volunteer1@email.com', fullName: 'Sarah Connor', phone: '+1-555-0178', role: 'VOLUNTEER', status: 'ACTIVE', skills: 'First Aid, Search & Rescue, Medical Triage, Driving', availabilityStatus: 'AVAILABLE', assignedTasksCount: 1, approvedByAdmin: true, latitude: 28.6139, longitude: 77.2090 },
        { id: 4, username: 'volunteer2', email: 'volunteer2@email.com', fullName: 'David Miller', phone: '+1-555-0188', role: 'VOLUNTEER', status: 'ACTIVE', skills: 'Cooking & Food Distribution, Shelter Logistics, Ham Radio', availabilityStatus: 'AVAILABLE', assignedTasksCount: 0, approvedByAdmin: true, latitude: 28.6250, longitude: 77.2150 }
    ],
    rescueTeams: [
        { id: 1, teamName: 'NDRF Bravo Quick Response', leaderName: 'Captain James Wilson', contactNumber: '+1-555-9988', specialization: 'Flood Water Rescue & Boat Evacuation', memberCount: 8, status: 'AVAILABLE', currentLocation: 'Sector 4 Staging Ground', latitude: 28.6304, longitude: 77.2177 },
        { id: 2, teamName: 'Urban Search & Collapse Squad', leaderName: 'Major Priya Sharma', contactNumber: '+1-555-9977', specialization: 'Structural Collapse & Heavy Extrication', memberCount: 12, status: 'DISPATCHED', currentLocation: 'Central Fire HQ', latitude: 28.6050, longitude: 77.2280 },
        { id: 3, teamName: 'Aero-Medical Airborne Unit', leaderName: 'Lt. Marcus Brody', contactNumber: '+1-555-9966', specialization: 'Helicopter Evacuation & Trauma Care', memberCount: 6, status: 'AVAILABLE', currentLocation: 'Metro Heliport Bay 2', latitude: 28.5800, longitude: 77.2000 }
    ],
    shelters: [
        { id: 1, name: 'Central High School Relief Shelter', address: '124 Civic Center Road, North Zone', city: 'Metro City', latitude: 28.6448, longitude: 77.2167, totalCapacity: 250, currentOccupancy: 35, remainingCapacity: 215, occupancyRate: 14.0, contactPerson: 'Officer Mark Davis', contactPhone: '+1-555-3344', facilities: 'Medical Ward, Clean Water Tankers, Emergency Generator, Hot Meals Kitchen, Sleeping Cots', status: 'ACTIVE' },
        { id: 2, name: 'St. Jude Community Auditorium', address: '88 Church Hill Avenue, South District', city: 'Metro City', latitude: 28.5921, longitude: 77.2290, totalCapacity: 120, currentOccupancy: 115, remainingCapacity: 5, occupancyRate: 95.8, contactPerson: 'Sister Teresa Wood', contactPhone: '+1-555-4455', facilities: 'First Aid Clinic, 24/7 Dining Hall, Sanitized Washrooms, Child Care Corner', status: 'ACTIVE' },
        { id: 3, name: 'Riverside Sports Complex', address: '15 Riverbank Boulevard, East Sector', city: 'Metro City', latitude: 28.6200, longitude: 77.2500, totalCapacity: 500, currentOccupancy: 0, remainingCapacity: 500, occupancyRate: 0.0, contactPerson: 'Inspector Arun Kumar', contactPhone: '+1-555-5566', facilities: 'Field Hospital, Helipad Access, Telecommunication Desk, Blanket Depot', status: 'ACTIVE' }
    ],
    disasters: [
        { id: 1, title: 'Severe Flash Flood along Yamuna River Embankment', disasterType: 'FLOOD', severity: 'CRITICAL', status: 'VERIFIED', description: 'River water has overflowed the primary dyke by 1.8 meters. Approximately 200 households partially submerged. Immediate boat evacuation required.', locationName: 'Yamuna Embankment Lowland, Sector 12', city: 'Metro City', latitude: 28.6350, longitude: 77.2400, reportedByName: 'Alice Johnson', reportedById: 2, verifiedByName: 'Dr. Robert Vance', verifiedById: 1, reportedAt: '2026-10-25T09:30:00', casualtiesCount: 2, affectedCount: 450, photoUrl: 'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=600&q=80' },
        { id: 2, title: 'Major Landslide Blocking Highway NH-44', disasterType: 'LANDSLIDE', severity: 'HIGH', status: 'IN_PROGRESS', description: 'Heavy rock and mud slide completely obstructed four lanes of NH-44. At least 15 passenger vehicles trapped between checkpoints.', locationName: 'Highway NH-44 Milepost 32', city: 'Metro City Outskirts', latitude: 28.6700, longitude: 77.1800, reportedByName: 'Sarah Connor', reportedById: 3, verifiedByName: 'Dr. Robert Vance', verifiedById: 1, reportedAt: '2026-10-25T11:15:00', casualtiesCount: 0, affectedCount: 65, photoUrl: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80' },
        { id: 3, title: 'Commercial Complex Structural Fire Incident', disasterType: 'FIRE', severity: 'CRITICAL', status: 'VERIFIED', description: '3-story shopping complex on fire due to electrical short circuit after heavy thunderstorms. Individuals trapped on roof terrace.', locationName: 'Grand Plaza Mall, Central Avenue', city: 'Metro City', latitude: 28.6100, longitude: 77.2100, reportedByName: 'Dr. Robert Vance', reportedById: 1, verifiedByName: 'Dr. Robert Vance', verifiedById: 1, reportedAt: '2026-10-25T13:40:00', casualtiesCount: 1, affectedCount: 120, photoUrl: 'https://images.unsplash.com/photo-1542385151-efd9000785a0?auto=format&fit=crop&w=600&q=80' },
        { id: 4, title: 'Moderate Seismic Tremors (5.4 Richter Scale)', disasterType: 'EARTHQUAKE', severity: 'MEDIUM', status: 'REPORTED', description: 'Shaking felt across city for 18 seconds. Hairline cracks reported in older brick structures; no immediate structural collapse observed.', locationName: 'Downtown Metropolitan Ward 3', city: 'Metro City', latitude: 28.5850, longitude: 77.2350, reportedByName: 'Alice Johnson', reportedById: 2, reportedAt: '2026-10-25T14:10:00', casualtiesCount: 0, affectedCount: 200, photoUrl: '' }
    ],
    emergencyRequests: [
        { id: 1, requestType: 'RESCUE', priority: 'CRITICAL', status: 'IN_PROGRESS', description: 'Family of 5 stranded on second-floor balcony due to rising flood waters. Water level rising fast; 1 infant present.', numberOfPeople: 5, locationName: 'House #42, Lane 3, Yamuna Floodplain', city: 'Metro City', latitude: 28.6360, longitude: 77.2420, requesterName: 'Alice Johnson', requesterId: 2, assignedRescueTeamName: 'NDRF Bravo Quick Response', assignedRescueTeamId: 1, contactPhone: '+1-555-0142', requestedAt: '2026-10-25T10:00:00' },
        { id: 2, requestType: 'FOOD_WATER', priority: 'HIGH', status: 'ASSIGNED', description: '20 families sheltered at municipal community hall without drinking water or dry food packets since 18 hours.', numberOfPeople: 45, locationName: 'Old Municipal Hall, Gandhi Road', city: 'Metro City', latitude: 28.6280, longitude: 77.2050, requesterName: 'Alice Johnson', requesterId: 2, assignedVolunteerName: 'Sarah Connor', assignedVolunteerId: 3, contactPhone: '+1-555-7788', requestedAt: '2026-10-25T11:45:00' },
        { id: 3, requestType: 'MEDICAL_AID', priority: 'HIGH', status: 'PENDING', description: 'Elderly diabetic patient suffering severe dehydration and fever. Requires doctor attention and IV saline.', numberOfPeople: 1, locationName: 'Flat 102, Block B, Silver Palms Apt', city: 'Metro City', latitude: 28.6180, longitude: 77.2220, requesterName: 'Alice Johnson', requesterId: 2, contactPhone: '+1-555-3322', requestedAt: '2026-10-25T12:50:00' }
    ],
    resources: [
        { id: 1, name: 'Emergency Food Rations (3-Day)', type: 'FOOD', quantity: 1200, unit: 'PACKETS', storageLocation: 'Central Warehouse Bay A', warehouseCode: 'WH-01', minThreshold: 100, status: 'IN_STOCK' },
        { id: 2, name: 'Purified Mineral Water (2L)', type: 'WATER', quantity: 3400, unit: 'LITERS', storageLocation: 'Sector 8 Depot', warehouseCode: 'WH-02', minThreshold: 300, status: 'IN_STOCK' },
        { id: 3, name: 'Trauma & First Aid Medical Kits', type: 'MEDICAL', quantity: 85, unit: 'KITS', storageLocation: 'Metro Health Logistics Center', warehouseCode: 'WH-MED', minThreshold: 40, status: 'IN_STOCK' },
        { id: 4, name: 'Heavy Duty Thermal Blankets', type: 'CLOTHING', quantity: 450, unit: 'BLANKETS', storageLocation: 'Relief Supply Hub 3', warehouseCode: 'WH-03', minThreshold: 80, status: 'IN_STOCK' },
        { id: 5, name: 'Inflatable Rescue Rafts', type: 'RESCUE_GEAR', quantity: 14, unit: 'UNITS', storageLocation: 'Fire Station Marina', warehouseCode: 'WH-MAR', minThreshold: 5, status: 'IN_STOCK' },
        { id: 6, name: 'Emergency Family Tents (6-person)', type: 'SHELTER_EQUIPMENT', quantity: 60, unit: 'UNITS', storageLocation: 'Civil Defense Compound', warehouseCode: 'WH-CD', minThreshold: 20, status: 'IN_STOCK' }
    ],
    taskAssignments: [
        { id: 1, title: 'Dispatch Relief Boat for Stranded Family', description: 'Evacuate 5 family members including infant from flooded lane 3.', assignedRescueTeamId: 1, assignedRescueTeamName: 'NDRF Bravo Quick Response', priority: 'CRITICAL', status: 'IN_PROGRESS', assignedDate: '2026-10-25T10:15:00', relatedRequestId: 1 },
        { id: 2, title: 'Distribute Drinking Water & Food Kits', description: 'Deliver 100 water packets and ration kits to municipal community hall.', assignedVolunteerId: 3, assignedVolunteerName: 'Sarah Connor', priority: 'HIGH', status: 'ACCEPTED', assignedDate: '2026-10-25T12:00:00', relatedRequestId: 2 }
    ],
    activityLogs: [
        { id: 1, actorUsername: 'admin', actorRole: 'ADMIN', action: 'VERIFY_REPORT', entityType: 'DisasterReport', entityId: 1, details: 'Verified high-priority flood report along Yamuna embankment.', timestamp: '2026-10-25T09:45:00' },
        { id: 2, actorUsername: 'admin', actorRole: 'ADMIN', action: 'ASSIGN_TEAM', entityType: 'EmergencyRequest', entityId: 1, details: 'Dispatched NDRF Bravo Team for urgent flood water rescue.', timestamp: '2026-10-25T10:15:00' },
        { id: 3, actorUsername: 'admin', actorRole: 'ADMIN', action: 'ASSIGN_VOLUNTEER', entityType: 'EmergencyRequest', entityId: 2, details: 'Assigned Sarah Connor to deliver water packets.', timestamp: '2026-10-25T12:00:00' }
    ]
};

// LocalStorage Persistence Layer
class MockDataStore {
    constructor() {
        this.init();
    }

    init() {
        if (!localStorage.getItem('DMS_INITIALIZED')) {
            localStorage.setItem('DMS_DISASTERS', JSON.stringify(DMS_MOCK_DATA.disasters));
            localStorage.setItem('DMS_REQUESTS', JSON.stringify(DMS_MOCK_DATA.emergencyRequests));
            localStorage.setItem('DMS_SHELTERS', JSON.stringify(DMS_MOCK_DATA.shelters));
            localStorage.setItem('DMS_RESOURCES', JSON.stringify(DMS_MOCK_DATA.resources));
            localStorage.setItem('DMS_TEAMS', JSON.stringify(DMS_MOCK_DATA.rescueTeams));
            localStorage.setItem('DMS_VOLUNTEERS', JSON.stringify(DMS_MOCK_DATA.users.filter(u => u.role === 'VOLUNTEER')));
            localStorage.setItem('DMS_USERS', JSON.stringify(DMS_MOCK_DATA.users));
            localStorage.setItem('DMS_TASKS', JSON.stringify(DMS_MOCK_DATA.taskAssignments));
            localStorage.setItem('DMS_LOGS', JSON.stringify(DMS_MOCK_DATA.activityLogs));
            localStorage.setItem('DMS_INITIALIZED', 'true');
        }
    }

    get(key) {
        return JSON.parse(localStorage.getItem(key) || '[]');
    }

    set(key, val) {
        localStorage.setItem(key, JSON.stringify(val));
    }
}

const dataStore = new MockDataStore();
