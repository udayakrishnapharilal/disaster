/**
 * Disaster Management System - Main Application Controller
 * Handles navigation routing across all 17 required pages, form handling, modals, and CSV downloads.
 */

// Global Router
function navigateTo(viewId) {
    // Hide all view containers
    document.querySelectorAll('.view-section').forEach(sec => sec.classList.add('d-none'));

    // Show target container
    const target = document.getElementById(`view-${viewId}`);
    if (target) {
        target.classList.remove('d-none');
    } else {
        console.warn(`View view-${viewId} not found, defaulting to home`);
        document.getElementById('view-home').classList.remove('d-none');
        viewId = 'home';
    }

    // Update active state in sidebar and topnav
    document.querySelectorAll('.nav-link-custom').forEach(link => {
        link.classList.remove('active');
        if (link.dataset.view === viewId) link.classList.add('active');
    });

    // View-specific initialization triggers
    switch (viewId) {
        case 'admin-dashboard':
            loadAdminDashboard();
            break;
        case 'victim-dashboard':
            loadVictimDashboard();
            break;
        case 'volunteer-dashboard':
            loadVolunteerDashboard();
            break;
        case 'rescue-team-dashboard':
            loadRescueTeamDashboard();
            break;
        case 'disaster-map':
            disasterMap.init('disaster-map');
            break;
        case 'manage-disasters':
            loadDisastersManagementTable();
            break;
        case 'manage-operations':
            loadOperationsManagementTable();
            break;
        case 'manage-users':
            loadUsersManagementTable();
            break;
        case 'shelters':
            loadSheltersDirectory();
            break;
        case 'resources':
            loadResourcesInventory();
            break;
        case 'reports-analytics':
            loadAnalyticsReportPage();
            break;
        case 'profile':
            loadUserProfile();
            break;
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Toast Notification Utility
function showToast(title, message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toastId = 'toast-' + Date.now();
    const bgClass = type === 'success' ? 'bg-success text-white' :
                    type === 'error' ? 'bg-danger text-white' :
                    type === 'warning' ? 'bg-warning text-dark' : 'bg-primary text-white';

    const html = `
        <div id="${toastId}" class="toast align-items-center ${bgClass} border-0 shadow" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <strong>${title}</strong><br>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', html);
    const toastEl = document.getElementById(toastId);
    const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
    bsToast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

// -------------------------------------------------------------
// Admin Dashboard
// -------------------------------------------------------------
async function loadAdminDashboard() {
    try {
        const stats = await api.getDashboardStats();

        // Update statistics cards
        document.getElementById('stat-total-disasters').textContent = stats.totalDisasterReports;
        document.getElementById('stat-active-emergencies').textContent = stats.activeEmergencies;
        document.getElementById('stat-shelter-occupancy').textContent = `${stats.totalShelterOccupancy} / ${stats.totalShelterCapacity}`;
        document.getElementById('stat-resources-stock').textContent = stats.totalResourcesInStock.toLocaleString();
        document.getElementById('stat-available-teams').textContent = `${stats.availableRescueTeams} / ${stats.totalRescueTeams}`;
        document.getElementById('stat-available-vols').textContent = stats.availableVolunteers;

        // Render Chart.js
        chartsManager.renderCharts();

        // Load Recent Activity Stream
        const activities = await api.getRecentActivities(8);
        const logContainer = document.getElementById('admin-activity-stream');
        if (logContainer) {
            logContainer.innerHTML = activities.map(act => `
                <li class="list-group-item d-flex justify-content-between align-items-start px-0 py-2 border-0 border-bottom">
                    <div class="ms-2 me-auto">
                        <div class="fw-bold">${act.details}</div>
                        <span class="small text-muted"><i class="bi bi-clock me-1"></i>${new Date(act.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})} by <strong>${act.actorUsername}</strong> (${act.actorRole})</span>
                    </div>
                    <span class="badge bg-light text-dark border">${act.action}</span>
                </li>
            `).join('');
        }
    } catch (err) {
        console.error('Error loading admin dashboard', err);
    }
}

// -------------------------------------------------------------
// Victim Dashboard
// -------------------------------------------------------------
async function loadVictimDashboard() {
    const user = auth.getUser();
    const nameEl = document.getElementById('victim-welcome-name');
    if (nameEl) nameEl.textContent = user.fullName || user.username;

    const requests = await api.getRequests();
    const myRequests = requests.filter(r => !user.id || r.requesterId == user.id || r.requesterName == user.fullName);

    const tableBody = document.getElementById('victim-my-requests-tbody');
    if (tableBody) {
        if (myRequests.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4">You have no active emergency requests. If you need assistance, please submit a request.</td></tr>`;
        } else {
            tableBody.innerHTML = myRequests.map(r => `
                <tr>
                    <td><strong>#${r.id}</strong></td>
                    <td><span class="badge ${r.requestType === 'RESCUE' ? 'bg-danger' : 'bg-primary'}">${r.requestType}</span></td>
                    <td>${r.locationName}</td>
                    <td><span class="badge ${getRequestStatusBadge(r.status)}">${r.status}</span></td>
                    <td>${r.assignedRescueTeamName || r.assignedVolunteerName || '<span class="text-muted">Dispatch Pending</span>'}</td>
                </tr>
            `).join('');
        }
    }

    // Nearby Shelters summary
    const shelters = await api.getShelters();
    const sheltersContainer = document.getElementById('victim-shelters-cards');
    if (sheltersContainer) {
        sheltersContainer.innerHTML = shelters.slice(0, 3).map(s => `
            <div class="col-md-4 mb-3">
                <div class="card h-100 border shadow-sm">
                    <div class="card-body">
                        <h6 class="fw-bold text-navy mb-1">${s.name}</h6>
                        <p class="small text-muted mb-2"><i class="bi bi-geo-alt-fill text-danger me-1"></i>${s.address}</p>
                        <div class="small mb-1">Available Beds: <strong class="text-success">${s.remainingCapacity !== undefined ? s.remainingCapacity : (s.totalCapacity - s.currentOccupancy)}</strong> / ${s.totalCapacity}</div>
                        <div class="small text-muted"><i class="bi bi-telephone-fill me-1"></i>${s.contactPhone || 'N/A'}</div>
                    </div>
                </div>
            </div>
        `).join('');
    }
}

// -------------------------------------------------------------
// Volunteer Dashboard
// -------------------------------------------------------------
async function loadVolunteerDashboard() {
    const user = auth.getUser();
    const nameEl = document.getElementById('volunteer-welcome-name');
    if (nameEl) nameEl.textContent = user.fullName || user.username;

    const tasks = await api.getTasks();
    const myTasks = tasks.filter(t => !user.id || t.assignedVolunteerId == user.id || t.assignedVolunteerName == user.fullName);

    const tbody = document.getElementById('volunteer-tasks-tbody');
    if (tbody) {
        if (myTasks.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4">No tasks currently assigned to you. Stand by for relief assignments.</td></tr>`;
        } else {
            tbody.innerHTML = myTasks.map(t => `
                <tr>
                    <td><strong>${t.title}</strong><br><small class="text-muted">${t.description}</small></td>
                    <td><span class="badge ${t.priority === 'CRITICAL' ? 'bg-danger' : 'bg-warning text-dark'}">${t.priority}</span></td>
                    <td><span class="badge ${getTaskStatusBadge(t.status)}">${t.status}</span></td>
                    <td>${new Date(t.assignedDate).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</td>
                    <td>
                        ${t.status === 'PENDING' ? `
                            <button class="btn btn-sm btn-outline-primary" onclick="updateTaskStatusAction(${t.id}, 'ACCEPTED')">Accept</button>
                        ` : t.status === 'ACCEPTED' ? `
                            <button class="btn btn-sm btn-outline-warning" onclick="updateTaskStatusAction(${t.id}, 'IN_PROGRESS')">Start</button>
                        ` : t.status === 'IN_PROGRESS' ? `
                            <button class="btn btn-sm btn-success" onclick="updateTaskStatusAction(${t.id}, 'COMPLETED')"><i class="bi bi-check-lg me-1"></i>Complete</button>
                        ` : `<span class="badge bg-light text-dark">Completed</span>`}
                    </td>
                </tr>
            `).join('');
        }
    }
}

async function updateTaskStatusAction(taskId, status) {
    await api.updateTaskStatus(taskId, status);
    showToast('Task Updated', `Task marked as ${status}`, 'success');
    loadVolunteerDashboard();
}

// -------------------------------------------------------------
// Rescue Team Dashboard
// -------------------------------------------------------------
async function loadRescueTeamDashboard() {
    const requests = await api.getRequests();
    const assignedOps = requests.filter(r => r.assignedRescueTeamName || r.requestType === 'RESCUE');

    const tbody = document.getElementById('rescue-ops-tbody');
    if (tbody) {
        tbody.innerHTML = assignedOps.map(op => `
            <tr>
                <td><strong>#${op.id}</strong></td>
                <td><span class="badge bg-danger">${op.requestType}</span></td>
                <td><strong>${op.numberOfPeople} people</strong><br><small class="text-muted">${op.locationName}</small></td>
                <td><span class="badge ${op.priority === 'CRITICAL' ? 'bg-danger' : 'bg-warning text-dark'}">${op.priority}</span></td>
                <td><span class="badge ${getRequestStatusBadge(op.status)}">${op.status}</span></td>
                <td>${op.assignedRescueTeamName || 'Unassigned'}</td>
                <td>
                    ${op.status !== 'FULFILLED' ? `
                        <button class="btn btn-sm btn-success" onclick="fulfillEmergencyRequest(${op.id})">
                            <i class="bi bi-check2-circle me-1"></i>Mark Rescued
                        </button>
                    ` : `<span class="badge bg-success"><i class="bi bi-shield-check me-1"></i>Fulfilled</span>`}
                </td>
            </tr>
        `).join('');
    }
}

async function fulfillEmergencyRequest(requestId) {
    await api.updateRequestStatus(requestId, 'FULFILLED');
    showToast('Operation Completed', `Rescue request #${requestId} marked as Fulfilled and survivors secured!`, 'success');
    loadRescueTeamDashboard();
    if (!document.getElementById('view-manage-operations').classList.contains('d-none')) {
        loadOperationsManagementTable();
    }
}

// -------------------------------------------------------------
// Disasters Management Table
// -------------------------------------------------------------
async function loadDisastersManagementTable() {
    const disasters = await api.getDisasters();
    const tbody = document.getElementById('disasters-management-tbody');
    if (!tbody) return;

    tbody.innerHTML = disasters.map(d => `
        <tr>
            <td><strong>#${d.id}</strong></td>
            <td>
                <span class="fw-bold">${d.title}</span><br>
                <small class="text-muted"><i class="bi bi-geo-alt me-1"></i>${d.locationName}</small>
            </td>
            <td><span class="badge bg-dark">${d.disasterType}</span></td>
            <td><span class="badge ${d.severity === 'CRITICAL' ? 'badge-critical' : d.severity === 'HIGH' ? 'badge-high' : 'badge-medium'}">${d.severity}</span></td>
            <td><span class="badge ${getDisasterStatusBadge(d.status)}">${d.status}</span></td>
            <td>${d.casualtiesCount || 0} / ${d.affectedCount || 0}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    ${d.status === 'REPORTED' ? `
                        <button class="btn btn-sm btn-outline-primary" onclick="verifyDisasterReportAction(${d.id})" title="Verify incident">
                            <i class="bi bi-check-circle-fill me-1"></i>Verify
                        </button>
                    ` : ''}
                    <button class="btn btn-sm btn-outline-secondary" onclick="viewDisasterDetails(${d.id})" title="View Details">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-success" onclick="updateDisasterStatusPrompt(${d.id})" title="Update Status">
                        <i class="bi bi-arrow-repeat"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

async function verifyDisasterReportAction(id) {
    await api.verifyDisaster(id);
    showToast('Verified', `Disaster report #${id} verified by Administrator.`, 'success');
    loadDisastersManagementTable();
}

async function updateDisasterStatusPrompt(id) {
    const newStatus = prompt('Enter new status (REPORTED, VERIFIED, IN_PROGRESS, RESOLVED, REJECTED):', 'RESOLVED');
    if (newStatus) {
        await api.updateDisasterStatus(id, newStatus.toUpperCase().trim());
        showToast('Updated', `Incident #${id} status changed to ${newStatus}`, 'info');
        loadDisastersManagementTable();
    }
}

function viewDisasterDetails(id) {
    const disasters = dataStore.get('DMS_DISASTERS');
    const d = disasters.find(item => item.id == id);
    if (!d) return;

    alert(`[Incident Report #${d.id}]\nTitle: ${d.title}\nType: ${d.disasterType}\nSeverity: ${d.severity}\nStatus: ${d.status}\nLocation: ${d.locationName}\nCasualties: ${d.casualtiesCount}\nAffected: ${d.affectedCount}\nDetails: ${d.description}`);
}

// -------------------------------------------------------------
// Operations Management (Requests Dispatch)
// -------------------------------------------------------------
async function loadOperationsManagementTable() {
    const requests = await api.getRequests();
    const teams = await api.getRescueTeams();
    const volunteers = await api.getVolunteers();

    const tbody = document.getElementById('operations-management-tbody');
    if (!tbody) return;

    tbody.innerHTML = requests.map(r => `
        <tr>
            <td><strong>#${r.id}</strong></td>
            <td><span class="badge ${r.requestType === 'RESCUE' ? 'bg-danger' : 'bg-primary'}">${r.requestType}</span></td>
            <td><strong>${r.numberOfPeople} people</strong><br><small class="text-muted">${r.locationName}</small></td>
            <td><span class="badge ${r.priority === 'CRITICAL' ? 'bg-danger' : 'bg-warning text-dark'}">${r.priority}</span></td>
            <td><span class="badge ${getRequestStatusBadge(r.status)}">${r.status}</span></td>
            <td>${r.assignedRescueTeamName || r.assignedVolunteerName || '<span class="text-danger fw-semibold">Pending Dispatch</span>'}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-sm btn-outline-danger" onclick="openDispatchTeamModal(${r.id})" title="Dispatch Rescue Team">
                        <i class="bi bi-shield-shaded me-1"></i>Team
                    </button>
                    <button class="btn btn-sm btn-outline-primary" onclick="openDispatchVolunteerModal(${r.id})" title="Assign Volunteer">
                        <i class="bi bi-person-badge me-1"></i>Volunteer
                    </button>
                    <button class="btn btn-sm btn-outline-success" onclick="fulfillEmergencyRequest(${r.id})" title="Mark Fulfilled">
                        <i class="bi bi-check-lg"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openDispatchTeamModal(requestId) {
    const teamId = prompt("Enter Rescue Team ID to dispatch (1: NDRF Bravo, 2: Urban Search, 3: Aero-Medical):", "1");
    if (teamId) {
        api.assignRescueTeam(requestId, teamId).then(() => {
            showToast('Dispatched', `Rescue Team #${teamId} dispatched to Request #${requestId}`, 'success');
            loadOperationsManagementTable();
        }).catch(err => showToast('Error', err.message, 'error'));
    }
}

function openDispatchVolunteerModal(requestId) {
    const volId = prompt("Enter Volunteer ID to assign (3: Sarah Connor, 4: David Miller):", "3");
    if (volId) {
        api.assignVolunteer(requestId, volId).then(() => {
            showToast('Assigned', `Volunteer #${volId} assigned to Request #${requestId}`, 'success');
            loadOperationsManagementTable();
        }).catch(err => showToast('Error', err.message, 'error'));
    }
}

// -------------------------------------------------------------
// Shelters Directory
// -------------------------------------------------------------
async function loadSheltersDirectory() {
    const shelters = await api.getShelters();
    const container = document.getElementById('shelters-grid');
    if (!container) return;

    container.innerHTML = shelters.map(s => {
        const occPct = s.occupancyRate !== undefined 
            ? s.occupancyRate 
            : ((s.currentOccupancy / s.totalCapacity) * 100).toFixed(0);
        const remBeds = s.remainingCapacity !== undefined 
            ? s.remainingCapacity 
            : (s.totalCapacity - s.currentOccupancy);

        return `
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm border ${occPct > 90 ? 'border-danger' : ''}">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center">
                        <span class="badge ${s.status === 'FULL' ? 'bg-danger' : 'bg-success'}">${s.status}</span>
                        <small class="text-muted"><i class="bi bi-geo-alt"></i> ${s.city || 'Metro Area'}</small>
                    </div>
                    <div class="card-body">
                        <h5 class="fw-bold text-navy">${s.name}</h5>
                        <p class="small text-muted mb-3">${s.address}</p>

                        <div class="mb-3">
                            <div class="d-flex justify-content-between small mb-1">
                                <span>Occupancy:</span>
                                <strong>${s.currentOccupancy} / ${s.totalCapacity} (${occPct}%)</strong>
                            </div>
                            <div class="progress" style="height: 8px;">
                                <div class="progress-bar ${occPct > 90 ? 'bg-danger' : occPct > 70 ? 'bg-warning' : 'bg-success'}" style="width: ${occPct}%"></div>
                            </div>
                            <div class="d-flex justify-content-between small text-muted mt-1">
                                <span>Remaining Beds: <strong class="text-dark">${remBeds}</strong></span>
                            </div>
                        </div>

                        <div class="small mb-3 p-2 bg-light rounded">
                            <strong>Facilities:</strong><br>
                            <span class="text-muted">${s.facilities || 'Standard Relief Accommodations'}</span>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted"><i class="bi bi-telephone-fill me-1"></i>${s.contactPhone}</small>
                            <button class="btn btn-sm btn-navy" onclick="openShelterModal(${s.id})">
                                <i class="bi bi-people-fill me-1"></i>Allocate
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function openShelterModal(shelterId) {
    const peopleCount = prompt("How many individuals / victims to allocate to this shelter?", "5");
    if (!peopleCount) return;

    api.allocateShelter(shelterId, 2, peopleCount)
        .then(() => {
            showToast('Allocated', `Successfully allocated ${peopleCount} victims. Invariant validated.`, 'success');
            loadSheltersDirectory();
            if (disasterMap) disasterMap.loadMarkers();
        })
        .catch(err => {
            showToast('Allocation Failed', err.message, 'error');
        });
}

// -------------------------------------------------------------
// Relief Resources Inventory
// -------------------------------------------------------------
async function loadResourcesInventory() {
    const resources = await api.getResources();
    const tbody = document.getElementById('resources-inventory-tbody');
    if (!tbody) return;

    tbody.innerHTML = resources.map(r => `
        <tr class="${r.status === 'LOW_STOCK' ? 'table-warning' : r.status === 'OUT_OF_STOCK' ? 'table-danger' : ''}">
            <td><strong>${r.name}</strong><br><small class="text-muted">Code: ${r.warehouseCode || 'WH'}</small></td>
            <td><span class="badge bg-secondary">${r.type}</span></td>
            <td><strong>${r.quantity}</strong> ${r.unit}</td>
            <td>${r.storageLocation}</td>
            <td><span class="badge ${r.status === 'IN_STOCK' ? 'bg-success' : r.status === 'LOW_STOCK' ? 'bg-warning text-dark' : 'bg-danger'}">${r.status}</span></td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-sm btn-outline-primary" onclick="openAllocateResourceModal(${r.id})" title="Distribute Resource">
                        <i class="bi bi-box-arrow-right me-1"></i>Distribute
                    </button>
                    <button class="btn btn-sm btn-outline-success" onclick="openRestockResourceModal(${r.id})" title="Restock">
                        <i class="bi bi-plus-circle me-1"></i>Restock
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openAllocateResourceModal(resourceId) {
    const amount = prompt("Quantity to allocate / distribute:", "50");
    if (!amount) return;
    const destination = prompt("Destination (Shelter, Camp, or Request ID):", "Central High School Shelter");

    api.allocateResource(resourceId, amount, destination)
        .then(() => {
            showToast('Distributed', `Distributed ${amount} units. Inventory deducted automatically.`, 'success');
            loadResourcesInventory();
        })
        .catch(err => {
            showToast('Allocation Blocked', err.message, 'error');
        });
}

function openRestockResourceModal(resourceId) {
    const amount = prompt("Quantity to restock:", "100");
    if (!amount) return;

    api.restockResource(resourceId, amount)
        .then(() => {
            showToast('Restocked', `Restocked inventory by +${amount} units.`, 'success');
            loadResourcesInventory();
        })
        .catch(err => showToast('Error', err.message, 'error'));
}

// -------------------------------------------------------------
// User & Volunteer Management
// -------------------------------------------------------------
async function loadUsersManagementTable() {
    const volunteers = await api.getVolunteers();
    const tbody = document.getElementById('volunteers-management-tbody');
    if (!tbody) return;

    tbody.innerHTML = volunteers.map(v => `
        <tr>
            <td><strong>${v.fullName}</strong><br><small class="text-muted">${v.email}</small></td>
            <td>${v.phone || 'N/A'}</td>
            <td><span class="small">${v.skills || 'General Support'}</span></td>
            <td><span class="badge ${v.availabilityStatus === 'AVAILABLE' ? 'bg-success' : 'bg-warning text-dark'}">${v.availabilityStatus}</span></td>
            <td>
                ${v.approvedByAdmin ? `
                    <span class="badge bg-success"><i class="bi bi-patch-check-fill me-1"></i>Approved</span>
                ` : `
                    <button class="btn btn-sm btn-warning" onclick="approveVolunteerAction(${v.id})">
                        <i class="bi bi-check-lg me-1"></i>Approve
                    </button>
                `}
            </td>
        </tr>
    `).join('');
}

async function approveVolunteerAction(volunteerId) {
    await api.approveVolunteer(volunteerId);
    showToast('Volunteer Approved', 'Volunteer is now authorized for emergency task dispatch.', 'success');
    loadUsersManagementTable();
}

// -------------------------------------------------------------
// Analytics and CSV Reports
// -------------------------------------------------------------
function loadAnalyticsReportPage() {
    chartsManager.renderCharts();
}

function exportCsvReport(type) {
    // If backend is active, use backend endpoint
    if (api.backendAvailable) {
        window.open(`${API_BASE_URL}/api/reports/export/${type}`, '_blank');
        showToast('CSV Download', `Downloading ${type}.csv from Spring Boot backend`, 'info');
        return;
    }

    // Client-side CSV generation fallback
    let csvContent = "data:text/csv;charset=utf-8,";
    let filename = `${type}_report.csv`;

    if (type === 'disasters') {
        const disasters = dataStore.get('DMS_DISASTERS');
        csvContent += "ID,Title,Type,Severity,Status,Location,Casualties,Affected\r\n";
        disasters.forEach(d => {
            csvContent += `"${d.id}","${d.title}","${d.disasterType}","${d.severity}","${d.status}","${d.locationName}",${d.casualtiesCount || 0},${d.affectedCount || 0}\r\n`;
        });
    } else if (type === 'requests') {
        const reqs = dataStore.get('DMS_REQUESTS');
        csvContent += "ID,Type,Priority,Status,PeopleCount,Location,ContactPhone\r\n";
        reqs.forEach(r => {
            csvContent += `"${r.id}","${r.requestType}","${r.priority}","${r.status}",${r.numberOfPeople},"${r.locationName}","${r.contactPhone || ''}"\r\n`;
        });
    } else if (type === 'shelters') {
        const shelters = dataStore.get('DMS_SHELTERS');
        csvContent += "ID,Name,Address,TotalCapacity,CurrentOccupancy,RemainingBeds,Status\r\n";
        shelters.forEach(s => {
            csvContent += `"${s.id}","${s.name}","${s.address}",${s.totalCapacity},${s.currentOccupancy},${s.totalCapacity - s.currentOccupancy},"${s.status}"\r\n`;
        });
    } else if (type === 'resources') {
        const res = dataStore.get('DMS_RESOURCES');
        csvContent += "ID,Name,Type,Quantity,Unit,StorageLocation,Status\r\n";
        res.forEach(r => {
            csvContent += `"${r.id}","${r.name}","${r.type}",${r.quantity},"${r.unit}","${r.storageLocation}","${r.status}"\r\n`;
        });
    }

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast('CSV Exported', `Generated ${filename} successfully.`, 'success');
}

// -------------------------------------------------------------
// User Profile Page
// -------------------------------------------------------------
function loadUserProfile() {
    const user = auth.getUser();
    if (!user) return;

    document.getElementById('profile-username').textContent = user.username;
    document.getElementById('profile-email').textContent = user.email;
    document.getElementById('profile-role').textContent = user.role;
    document.getElementById('profile-fullname-input').value = user.fullName || '';
    document.getElementById('profile-phone-input').value = user.phone || '';

    // OOP Role Description polymorphic explanation display
    const descEl = document.getElementById('profile-oop-description');
    if (descEl) {
        if (user.role === 'ADMIN') {
            descEl.textContent = "Polymorphic Role: Administrator (Subclass of User) - Holds systemic authority to verify incidents, approve emergency dispatches, manage shelter allocations, and maintain relief resource inventories.";
        } else if (user.role === 'VICTIM') {
            descEl.textContent = "Polymorphic Role: Victim (Subclass of User) - Impacted citizen or community member who reports live disaster events and requests rescue, food, water, or shelter aid.";
        } else if (user.role === 'VOLUNTEER') {
            descEl.textContent = "Polymorphic Role: Volunteer (Subclass of User, implements EmergencyDispatchable) - Field operative who can accept assigned relief tasks, update status, and distribute resources.";
        } else {
            descEl.textContent = "Polymorphic Role: Rescue Team - Specialized operational unit dispatched to emergency situations.";
        }
    }
}

// Helper Badge Classes
function getRequestStatusBadge(status) {
    switch (status) {
        case 'PENDING': return 'bg-warning text-dark';
        case 'ASSIGNED': return 'bg-primary';
        case 'IN_PROGRESS': return 'bg-info text-dark';
        case 'FULFILLED': return 'bg-success';
        case 'CANCELLED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

function getTaskStatusBadge(status) {
    switch (status) {
        case 'PENDING': return 'bg-warning text-dark';
        case 'ACCEPTED': return 'bg-primary';
        case 'IN_PROGRESS': return 'bg-info text-dark';
        case 'COMPLETED': return 'bg-success';
        case 'CANCELLED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

function getDisasterStatusBadge(status) {
    switch (status) {
        case 'REPORTED': return 'badge-medium';
        case 'VERIFIED': return 'badge-verified';
        case 'IN_PROGRESS': return 'badge-in-progress';
        case 'RESOLVED': return 'badge-resolved';
        case 'REJECTED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

// -------------------------------------------------------------
// Form Submissions
// -------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    // Check initial session & UI
    auth.updateUi();

    // 1. Disaster Report Form Submit
    const disasterForm = document.getElementById('form-report-disaster');
    if (disasterForm) {
        disasterForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = {
                title: document.getElementById('report-title').value,
                disasterType: document.getElementById('report-type').value,
                severity: document.getElementById('report-severity').value,
                description: document.getElementById('report-desc').value,
                locationName: document.getElementById('report-location').value,
                city: document.getElementById('report-city').value,
                latitude: parseFloat(document.getElementById('report-lat').value) || 28.6139,
                longitude: parseFloat(document.getElementById('report-lng').value) || 77.2090,
                casualtiesCount: parseInt(document.getElementById('report-casualties').value) || 0,
                affectedCount: parseInt(document.getElementById('report-affected').value) || 0,
                photoUrl: document.getElementById('report-photo-url').value
            };

            try {
                await api.createDisaster(data);
                showToast('Report Submitted', 'Disaster incident reported. Emergency coordinators notified.', 'success');
                disasterForm.reset();
                navigateTo('manage-disasters');
            } catch (err) {
                showToast('Error', err.message, 'error');
            }
        });
    }

    // 2. Emergency Request Form Submit
    const requestForm = document.getElementById('form-request-aid');
    if (requestForm) {
        requestForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const user = auth.getUser();
            const data = {
                requestType: document.getElementById('req-type').value,
                priority: document.getElementById('req-priority').value,
                description: document.getElementById('req-desc').value,
                numberOfPeople: parseInt(document.getElementById('req-people').value) || 1,
                locationName: document.getElementById('req-location').value,
                city: document.getElementById('req-city').value,
                contactPhone: document.getElementById('req-phone').value,
                requesterId: user.id,
                requesterName: user.fullName || user.username
            };

            try {
                await api.createRequest(data);
                showToast('Request Submitted', 'Your emergency assistance request has been submitted to dispatch queue.', 'success');
                requestForm.reset();
                navigateTo('victim-dashboard');
            } catch (err) {
                showToast('Error', err.message, 'error');
            }
        });
    }

    // 3. Login Form Submit
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const u = document.getElementById('login-username').value;
            const p = document.getElementById('login-password').value;

            try {
                const user = await api.login(u, p);
                auth.setUser(user, user.token);
                showToast('Logged In', `Welcome back, ${user.fullName || user.username}!`, 'success');
                navigateTo(auth.isAdmin() ? 'admin-dashboard' : auth.isVolunteer() ? 'volunteer-dashboard' : 'victim-dashboard');
            } catch (err) {
                showToast('Authentication Failed', err.message, 'error');
            }
        });
    }

    // 4. Registration Form Submit
    const regForm = document.getElementById('form-register');
    if (regForm) {
        regForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = {
                username: document.getElementById('reg-username').value,
                email: document.getElementById('reg-email').value,
                password: document.getElementById('reg-password').value,
                fullName: document.getElementById('reg-fullname').value,
                phone: document.getElementById('reg-phone').value,
                role: document.getElementById('reg-role').value,
                skills: document.getElementById('reg-skills') ? document.getElementById('reg-skills').value : '',
                emergencyContact: document.getElementById('reg-emergency-contact') ? document.getElementById('reg-emergency-contact').value : ''
            };

            try {
                const user = await api.register(data);
                auth.setUser(user);
                showToast('Registered Successfully', `Welcome to DMS, ${user.fullName}!`, 'success');
                navigateTo(data.role === 'VOLUNTEER' ? 'volunteer-dashboard' : 'victim-dashboard');
            } catch (err) {
                showToast('Registration Error', err.message, 'error');
            }
        });
    }

    // Default start view
    navigateTo('home');
});
