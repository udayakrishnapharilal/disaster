/**
 * Disaster Management System - Unified API Client
 * Transparently communicates with Spring Boot REST APIs when available,
 * and automatically falls back to in-browser dataStore when offline.
 */

const API_BASE_URL = window.location.origin.includes('8080') 
    ? window.location.origin 
    : 'http://localhost:8080';

class ApiClient {
    constructor() {
        this.backendAvailable = false;
        this.checkBackend();
    }

    async checkBackend() {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 1200);
            const res = await fetch(`${API_BASE_URL}/api/dashboard/stats`, { signal: controller.signal });
            clearTimeout(timeoutId);
            if (res.ok) {
                this.backendAvailable = true;
                this.updateBackendIndicator(true);
                return;
            }
        } catch (e) {
            // Backend not active or CORS blocked - fallback to mock
        }
        this.backendAvailable = false;
        this.updateBackendIndicator(false);
    }

    updateBackendIndicator(isLive) {
        const badge = document.getElementById('backend-status-badge');
        if (badge) {
            if (isLive) {
                badge.className = 'badge bg-success';
                badge.innerHTML = '<i class="bi bi-hdd-network-fill me-1"></i> Spring Boot Live';
            } else {
                badge.className = 'badge bg-primary';
                badge.innerHTML = '<i class="bi bi-laptop-fill me-1"></i> Standalone Client Mode';
            }
        }
    }

    getAuthHeader() {
        const token = localStorage.getItem('DMS_AUTH_TOKEN');
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }

    // Generic REST helper
    async request(endpoint, options = {}) {
        if (!this.backendAvailable) {
            return null; // Will trigger mock fallback in specific methods
        }
        try {
            const headers = {
                'Content-Type': 'application/json',
                ...this.getAuthHeader(),
                ...(options.headers || {})
            };
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                ...options,
                headers
            });
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'API request failed');
            }
            return data.data !== undefined ? data.data : data;
        } catch (err) {
            console.warn(`Backend call failed to ${endpoint}, using local data store:`, err.message);
            return null;
        }
    }

    // --- Authentication ---
    async login(username, password) {
        if (this.backendAvailable) {
            const data = await this.request('/api/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            });
            if (data) return data;
        }
        // Fallback
        const users = dataStore.get('DMS_USERS');
        const user = users.find(u => (u.username === username || u.email === username));
        if (user) {
            const token = 'mock-jwt-token-' + user.id + '-' + Date.now();
            return { token, ...user };
        }
        throw new Error('Invalid username or password');
    }

    async register(userData) {
        if (this.backendAvailable) {
            const data = await this.request('/api/auth/register', {
                method: 'POST',
                body: JSON.stringify(userData)
            });
            if (data) return data;
        }
        const users = dataStore.get('DMS_USERS');
        if (users.find(u => u.username === userData.username)) {
            throw new Error('Username already exists');
        }
        const newUser = { id: Date.now(), ...userData, status: 'ACTIVE' };
        users.push(newUser);
        dataStore.set('DMS_USERS', users);
        if (userData.role === 'VOLUNTEER') {
            const vols = dataStore.get('DMS_VOLUNTEERS');
            vols.push(newUser);
            dataStore.set('DMS_VOLUNTEERS', vols);
        }
        return newUser;
    }

    // --- Disasters ---
    async getDisasters() {
        const live = await this.request('/api/disasters');
        if (live) return live;
        return dataStore.get('DMS_DISASTERS');
    }

    async createDisaster(data) {
        const live = await this.request('/api/disasters', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const list = dataStore.get('DMS_DISASTERS');
        const newItem = { id: Date.now(), ...data, status: 'REPORTED', reportedAt: new Date().toISOString() };
        list.unshift(newItem);
        dataStore.set('DMS_DISASTERS', list);
        this.logActivity('Victim', 'VICTIM', 'REPORT_DISASTER', 'DisasterReport', newItem.id, 'Reported incident: ' + newItem.title);
        return newItem;
    }

    async verifyDisaster(id) {
        const live = await this.request(`/api/disasters/${id}/verify`, { method: 'POST' });
        if (live) return live;

        const list = dataStore.get('DMS_DISASTERS');
        const item = list.find(d => d.id == id);
        if (item) {
            item.status = 'VERIFIED';
            item.verifiedByName = 'Dr. Robert Vance';
            item.verifiedAt = new Date().toISOString();
            dataStore.set('DMS_DISASTERS', list);
            this.logActivity('admin', 'ADMIN', 'VERIFY_REPORT', 'DisasterReport', id, 'Verified disaster report #' + id);
        }
        return item;
    }

    async updateDisasterStatus(id, status) {
        const live = await this.request(`/api/disasters/${id}/status?status=${status}`, { method: 'PATCH' });
        if (live) return live;

        const list = dataStore.get('DMS_DISASTERS');
        const item = list.find(d => d.id == id);
        if (item) {
            item.status = status;
            dataStore.set('DMS_DISASTERS', list);
        }
        return item;
    }

    // --- Emergency Requests ---
    async getRequests() {
        const live = await this.request('/api/requests');
        if (live) return live;
        return dataStore.get('DMS_REQUESTS');
    }

    async createRequest(data) {
        const live = await this.request('/api/requests', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const list = dataStore.get('DMS_REQUESTS');
        const newItem = { id: Date.now(), ...data, status: 'PENDING', requestedAt: new Date().toISOString() };
        list.unshift(newItem);
        dataStore.set('DMS_REQUESTS', list);
        this.logActivity('Victim', 'VICTIM', 'CREATE_EMERGENCY_REQUEST', 'EmergencyRequest', newItem.id, 'New emergency request at ' + newItem.locationName);
        return newItem;
    }

    async assignRescueTeam(requestId, teamId) {
        const live = await this.request(`/api/requests/${requestId}/assign-team?rescueTeamId=${teamId}`, { method: 'POST' });
        if (live) return live;

        const reqs = dataStore.get('DMS_REQUESTS');
        const teams = dataStore.get('DMS_TEAMS');
        const req = reqs.find(r => r.id == requestId);
        const team = teams.find(t => t.id == teamId);
        if (req && team) {
            req.assignedRescueTeamId = team.id;
            req.assignedRescueTeamName = team.teamName;
            req.status = 'IN_PROGRESS';
            team.status = 'DISPATCHED';
            dataStore.set('DMS_REQUESTS', reqs);
            dataStore.set('DMS_TEAMS', teams);
            this.logActivity('admin', 'ADMIN', 'ASSIGN_RESCUE_TEAM', 'EmergencyRequest', req.id, 'Dispatched ' + team.teamName + ' to request #' + req.id);
        }
        return req;
    }

    async assignVolunteer(requestId, volunteerId) {
        const live = await this.request(`/api/requests/${requestId}/assign-volunteer?volunteerId=${volunteerId}`, { method: 'POST' });
        if (live) return live;

        const reqs = dataStore.get('DMS_REQUESTS');
        const vols = dataStore.get('DMS_VOLUNTEERS');
        const req = reqs.find(r => r.id == requestId);
        const vol = vols.find(v => v.id == volunteerId);
        if (req && vol) {
            req.assignedVolunteerId = vol.id;
            req.assignedVolunteerName = vol.fullName;
            req.status = 'ASSIGNED';
            vol.availabilityStatus = 'BUSY';
            vol.assignedTasksCount = (vol.assignedTasksCount || 0) + 1;
            dataStore.set('DMS_REQUESTS', reqs);
            dataStore.set('DMS_VOLUNTEERS', vols);
            this.logActivity('admin', 'ADMIN', 'ASSIGN_VOLUNTEER', 'EmergencyRequest', req.id, 'Assigned volunteer ' + vol.fullName);
        }
        return req;
    }

    async updateRequestStatus(requestId, status) {
        const live = await this.request(`/api/requests/${requestId}/status?status=${status}`, { method: 'PATCH' });
        if (live) return live;

        const reqs = dataStore.get('DMS_REQUESTS');
        const req = reqs.find(r => r.id == requestId);
        if (req) {
            req.status = status;
            if (status === 'FULFILLED') {
                req.fulfilledAt = new Date().toISOString();
            }
            dataStore.set('DMS_REQUESTS', reqs);
        }
        return req;
    }

    // --- Shelters ---
    async getShelters() {
        const live = await this.request('/api/shelters');
        if (live) return live;
        return dataStore.get('DMS_SHELTERS');
    }

    async createShelter(data) {
        const live = await this.request('/api/shelters', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const list = dataStore.get('DMS_SHELTERS');
        const total = parseInt(data.totalCapacity);
        const newItem = {
            id: Date.now(),
            ...data,
            totalCapacity: total,
            currentOccupancy: 0,
            remainingCapacity: total,
            occupancyRate: 0,
            status: 'ACTIVE'
        };
        list.push(newItem);
        dataStore.set('DMS_SHELTERS', list);
        return newItem;
    }

    async allocateShelter(shelterId, victimId, peopleCount) {
        const count = parseInt(peopleCount) || 1;
        const live = await this.request('/api/shelters/allocate', {
            method: 'POST',
            body: JSON.stringify({ shelterId, victimId, peopleCount: count })
        });
        if (live) return live;

        const shelters = dataStore.get('DMS_SHELTERS');
        const s = shelters.find(sh => sh.id == shelterId);
        if (!s) throw new Error('Shelter not found');

        if (s.currentOccupancy + count > s.totalCapacity) {
            throw new Error(`Shelter capacity exceeded! Available: ${s.totalCapacity - s.currentOccupancy}, Requested: ${count}`);
        }

        s.currentOccupancy += count;
        s.remainingCapacity = s.totalCapacity - s.currentOccupancy;
        s.occupancyRate = ((s.currentOccupancy / s.totalCapacity) * 100).toFixed(1);
        if (s.remainingCapacity === 0) s.status = 'FULL';
        dataStore.set('DMS_SHELTERS', shelters);

        this.logActivity('admin', 'ADMIN', 'ALLOCATE_SHELTER', 'Shelter', s.id, `Allocated ${count} people to ${s.name}`);
        return { success: true, shelter: s };
    }

    // --- Resources ---
    async getResources() {
        const live = await this.request('/api/resources');
        if (live) return live;
        return dataStore.get('DMS_RESOURCES');
    }

    async createResource(data) {
        const live = await this.request('/api/resources', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const list = dataStore.get('DMS_RESOURCES');
        const qty = parseInt(data.quantity) || 0;
        const newItem = { id: Date.now(), ...data, quantity: qty, status: qty > 0 ? 'IN_STOCK' : 'OUT_OF_STOCK' };
        list.push(newItem);
        dataStore.set('DMS_RESOURCES', list);
        return newItem;
    }

    async allocateResource(resourceId, quantity, destination) {
        const qty = parseInt(quantity) || 1;
        const live = await this.request('/api/resources/allocate', {
            method: 'POST',
            body: JSON.stringify({ resourceId, quantityAllocated: qty, remarks: destination })
        });
        if (live) return live;

        const resources = dataStore.get('DMS_RESOURCES');
        const r = resources.find(res => res.id == resourceId);
        if (!r) throw new Error('Resource not found');

        if (qty > r.quantity) {
            throw new Error(`Insufficient inventory! In stock: ${r.quantity}, Requested: ${qty}`);
        }

        r.quantity -= qty;
        if (r.quantity === 0) r.status = 'OUT_OF_STOCK';
        else if (r.quantity <= r.minThreshold) r.status = 'LOW_STOCK';
        dataStore.set('DMS_RESOURCES', resources);

        this.logActivity('admin', 'ADMIN', 'ALLOCATE_RESOURCE', 'Resource', r.id, `Distributed ${qty} ${r.unit} of ${r.name} to ${destination}`);
        return { success: true, resource: r };
    }

    async restockResource(resourceId, amount) {
        const amt = parseInt(amount) || 0;
        const live = await this.request(`/api/resources/${resourceId}/restock?amount=${amt}`, { method: 'POST' });
        if (live) return live;

        const resources = dataStore.get('DMS_RESOURCES');
        const r = resources.find(res => res.id == resourceId);
        if (r) {
            r.quantity += amt;
            if (r.quantity > r.minThreshold) r.status = 'IN_STOCK';
            dataStore.set('DMS_RESOURCES', resources);
            this.logActivity('admin', 'ADMIN', 'RESTOCK_RESOURCE', 'Resource', r.id, `Restocked ${amt} ${r.unit} of ${r.name}`);
        }
        return r;
    }

    // --- Rescue Teams & Volunteers ---
    async getRescueTeams() {
        const live = await this.request('/api/rescue-teams');
        if (live) return live;
        return dataStore.get('DMS_TEAMS');
    }

    async createRescueTeam(data) {
        const live = await this.request('/api/rescue-teams', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const teams = dataStore.get('DMS_TEAMS');
        const newItem = { id: Date.now(), ...data, status: 'AVAILABLE' };
        teams.push(newItem);
        dataStore.set('DMS_TEAMS', teams);
        return newItem;
    }

    async updateRescueTeamStatus(teamId, status) {
        const live = await this.request(`/api/rescue-teams/${teamId}/status?status=${status}`, { method: 'PATCH' });
        if (live) return live;

        const teams = dataStore.get('DMS_TEAMS');
        const t = teams.find(team => team.id == teamId);
        if (t) {
            t.status = status;
            dataStore.set('DMS_TEAMS', teams);
        }
        return t;
    }

    async getVolunteers() {
        const live = await this.request('/api/volunteers');
        if (live) return live;
        return dataStore.get('DMS_VOLUNTEERS');
    }

    async approveVolunteer(volunteerId) {
        const live = await this.request(`/api/volunteers/${volunteerId}/approve`, { method: 'POST' });
        if (live) return live;

        const vols = dataStore.get('DMS_VOLUNTEERS');
        const v = vols.find(vol => vol.id == volunteerId);
        if (v) {
            v.approvedByAdmin = true;
            dataStore.set('DMS_VOLUNTEERS', vols);
            this.logActivity('admin', 'ADMIN', 'APPROVE_VOLUNTEER', 'Volunteer', v.id, 'Approved volunteer: ' + v.fullName);
        }
        return v;
    }

    // --- Tasks ---
    async getTasks() {
        const live = await this.request('/api/tasks');
        if (live) return live;
        return dataStore.get('DMS_TASKS');
    }

    async createTask(data) {
        const live = await this.request('/api/tasks', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (live) return live;

        const tasks = dataStore.get('DMS_TASKS');
        const newItem = { id: Date.now(), ...data, status: 'PENDING', assignedDate: new Date().toISOString() };
        tasks.unshift(newItem);
        dataStore.set('DMS_TASKS', tasks);
        return newItem;
    }

    async updateTaskStatus(taskId, status, notes = '') {
        const live = await this.request(`/api/tasks/${taskId}/status?status=${status}&notes=${encodeURIComponent(notes)}`, { method: 'PATCH' });
        if (live) return live;

        const tasks = dataStore.get('DMS_TASKS');
        const t = tasks.find(task => task.id == taskId);
        if (t) {
            t.status = status;
            t.notes = notes;
            if (status === 'COMPLETED') t.completedDate = new Date().toISOString();
            dataStore.set('DMS_TASKS', tasks);
        }
        return t;
    }

    // --- Dashboard Stats & Activity Logs ---
    async getDashboardStats() {
        const live = await this.request('/api/dashboard/stats');
        if (live) return live;

        const disasters = dataStore.get('DMS_DISASTERS');
        const requests = dataStore.get('DMS_REQUESTS');
        const shelters = dataStore.get('DMS_SHELTERS');
        const resources = dataStore.get('DMS_RESOURCES');
        const teams = dataStore.get('DMS_TEAMS');
        const vols = dataStore.get('DMS_VOLUNTEERS');
        const users = dataStore.get('DMS_USERS');

        const totalCap = shelters.reduce((acc, s) => acc + (s.totalCapacity || 0), 0);
        const totalOcc = shelters.reduce((acc, s) => acc + (s.currentOccupancy || 0), 0);
        const totalRes = resources.reduce((acc, r) => acc + (r.quantity || 0), 0);

        // Disaster type counts
        const disasterTypeCounts = { FLOOD: 0, LANDSLIDE: 0, EARTHQUAKE: 0, CYCLONE: 0, FIRE: 0, TSUNAMI: 0, OTHER: 0 };
        disasters.forEach(d => { if (disasterTypeCounts[d.disasterType] !== undefined) disasterTypeCounts[d.disasterType]++; });

        // Request status counts
        const requestStatusCounts = { PENDING: 0, ASSIGNED: 0, IN_PROGRESS: 0, FULFILLED: 0, CANCELLED: 0 };
        requests.forEach(r => { if (requestStatusCounts[r.status] !== undefined) requestStatusCounts[r.status]++; });

        // Resource category quantities
        const resourceCategoryQuantities = { FOOD: 0, WATER: 0, MEDICAL: 0, CLOTHING: 0, SHELTER_EQUIPMENT: 0, RESCUE_GEAR: 0 };
        resources.forEach(r => { if (resourceCategoryQuantities[r.type] !== undefined) resourceCategoryQuantities[r.type] += (r.quantity || 0); });

        return {
            totalDisasterReports: disasters.length,
            activeEmergencies: requests.filter(r => r.status !== 'FULFILLED' && r.status !== 'CANCELLED').length,
            totalRegisteredVictims: users.filter(u => u.role === 'VICTIM').length,
            availableVolunteers: vols.filter(v => v.availabilityStatus === 'AVAILABLE').length,
            totalRescueTeams: teams.length,
            availableRescueTeams: teams.filter(t => t.status === 'AVAILABLE').length,
            totalShelters: shelters.length,
            totalShelterCapacity: totalCap,
            totalShelterOccupancy: totalOcc,
            availableShelterCapacity: Math.max(0, totalCap - totalOcc),
            totalResourcesInStock: totalRes,
            pendingRequests: requests.filter(r => r.status === 'PENDING').length,
            fulfilledRequests: requests.filter(r => r.status === 'FULFILLED').length,
            disasterTypeCounts,
            requestStatusCounts,
            resourceCategoryQuantities
        };
    }

    async getRecentActivities(limit = 10) {
        const live = await this.request(`/api/dashboard/activities?limit=${limit}`);
        if (live) return live;
        return dataStore.get('DMS_LOGS').slice(0, limit);
    }

    logActivity(actorUsername, actorRole, action, entityType, entityId, details) {
        const logs = dataStore.get('DMS_LOGS');
        logs.unshift({
            id: Date.now(),
            actorUsername,
            actorRole,
            action,
            entityType,
            entityId,
            details,
            timestamp: new Date().toISOString()
        });
        dataStore.set('DMS_LOGS', logs);
    }
}

const api = new ApiClient();
