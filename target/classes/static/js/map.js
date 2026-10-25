/**
 * Disaster Management System - Interactive Leaflet & OpenStreetMap Module
 */

class DisasterMapManager {
    constructor() {
        this.map = null;
        this.disasterMarkers = [];
        this.shelterMarkers = [];
        this.activeFilter = 'ALL';
    }

    init(elementId = 'disaster-map') {
        const container = document.getElementById(elementId);
        if (!container) return;

        // If map already initialized, invalidate size and return
        if (this.map) {
            this.map.invalidateSize();
            return;
        }

        // Center around Metro City / NCR coordinates
        this.map = L.map(elementId).setView([28.6200, 77.2200], 12);

        // OpenStreetMap Tile Layer
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | Disaster Relief Grid'
        }).addTo(this.map);

        this.loadMarkers();

        // Fix potential render issue when map is inside hidden tab initially
        setTimeout(() => {
            if (this.map) this.map.invalidateSize();
        }, 300);
    }

    async loadMarkers() {
        if (!this.map) return;

        // Clear existing markers
        this.clearMarkers();

        try {
            const disasters = await api.getDisasters();
            const shelters = await api.getShelters();

            // Plot Disasters
            disasters.forEach(d => {
                if (!d.latitude || !d.longitude) return;

                const markerColor = this.getDisasterMarkerColor(d.disasterType, d.severity);
                const markerIcon = L.divIcon({
                    className: 'custom-map-pin',
                    html: `<div style="background-color: ${markerColor}; width: 28px; height: 28px; border-radius: 50%; border: 3px solid #ffffff; box-shadow: 0 2px 6px rgba(0,0,0,0.3); display: flex; align-items: center; justify-content: center; color: #ffffff; font-size: 13px;">
                               <i class="bi ${this.getDisasterIcon(d.disasterType)}"></i>
                           </div>`,
                    iconSize: [28, 28],
                    iconAnchor: [14, 14],
                    popupAnchor: [0, -16]
                });

                const verifiedBadge = d.status === 'VERIFIED'
                    ? '<span class="badge bg-primary ms-1"><i class="bi bi-patch-check-fill me-1"></i>Verified</span>'
                    : '<span class="badge bg-warning text-dark ms-1">Reported</span>';

                const popupContent = `
                    <div style="min-width: 220px; font-family: inherit;">
                        <div class="d-flex align-items-center justify-content-between mb-1">
                            <span class="badge bg-danger">${d.disasterType}</span>
                            ${verifiedBadge}
                        </div>
                        <h6 class="fw-bold mb-1" style="color: #0f172a;">${d.title}</h6>
                        <p class="small text-muted mb-2"><i class="bi bi-geo-alt-fill text-danger me-1"></i>${d.locationName || 'Unknown Location'}</p>
                        <div class="small mb-2 p-2 bg-light rounded border">
                            <div><strong>Severity:</strong> <span class="text-uppercase fw-semibold ${d.severity === 'CRITICAL' ? 'text-danger' : 'text-warning'}">${d.severity}</span></div>
                            <div><strong>Casualties:</strong> ${d.casualtiesCount || 0} | <strong>Affected:</strong> ${d.affectedCount || 0}</div>
                        </div>
                        <p class="small mb-2">${d.description || ''}</p>
                        <button class="btn btn-sm btn-outline-danger w-100" onclick="viewDisasterDetails(${d.id})">
                            <i class="bi bi-eye-fill me-1"></i>View Incident File
                        </button>
                    </div>
                `;

                const marker = L.marker([d.latitude, d.longitude], { icon: markerIcon })
                    .bindPopup(popupContent);

                marker.dmsType = 'DISASTER';
                marker.dmsStatus = d.status;
                marker.addTo(this.map);
                this.disasterMarkers.push(marker);
            });

            // Plot Shelters
            shelters.forEach(s => {
                if (!s.latitude || !s.longitude) return;

                const shelterIcon = L.divIcon({
                    className: 'custom-shelter-pin',
                    html: `<div style="background-color: #059669; width: 30px; height: 30px; border-radius: 6px; border: 3px solid #ffffff; box-shadow: 0 2px 6px rgba(0,0,0,0.3); display: flex; align-items: center; justify-content: center; color: #ffffff; font-size: 15px;">
                               <i class="bi bi-house-door-fill"></i>
                           </div>`,
                    iconSize: [30, 30],
                    iconAnchor: [15, 15],
                    popupAnchor: [0, -18]
                });

                const occupancyPct = s.occupancyRate !== undefined 
                    ? s.occupancyRate 
                    : ((s.currentOccupancy / s.totalCapacity) * 100).toFixed(0);

                const popupContent = `
                    <div style="min-width: 230px; font-family: inherit;">
                        <span class="badge bg-success mb-1"><i class="bi bi-shield-check me-1"></i>Emergency Shelter</span>
                        <h6 class="fw-bold mb-1" style="color: #0f172a;">${s.name}</h6>
                        <p class="small text-muted mb-2"><i class="bi bi-geo-alt me-1"></i>${s.address}</p>
                        
                        <div class="mb-2">
                            <div class="d-flex justify-content-between small mb-1">
                                <span>Occupancy:</span>
                                <strong>${s.currentOccupancy} / ${s.totalCapacity} beds (${occupancyPct}%)</strong>
                            </div>
                            <div class="progress" style="height: 6px;">
                                <div class="progress-bar ${occupancyPct > 90 ? 'bg-danger' : 'bg-success'}" style="width: ${occupancyPct}%"></div>
                            </div>
                            <div class="small text-muted mt-1">Remaining Beds: <strong>${s.remainingCapacity !== undefined ? s.remainingCapacity : (s.totalCapacity - s.currentOccupancy)}</strong></div>
                        </div>

                        <div class="small mb-2 p-1 bg-light rounded text-muted">
                            <i class="bi bi-telephone-fill me-1"></i>${s.contactPhone || 'N/A'} (${s.contactPerson || 'Coordinator'})
                        </div>

                        <button class="btn btn-sm btn-outline-success w-100" onclick="openShelterModal(${s.id})">
                            <i class="bi bi-people-fill me-1"></i>Allocate Victims
                        </button>
                    </div>
                `;

                const marker = L.marker([s.latitude, s.longitude], { icon: shelterIcon })
                    .bindPopup(popupContent);

                marker.dmsType = 'SHELTER';
                marker.addTo(this.map);
                this.shelterMarkers.push(marker);
            });

        } catch (err) {
            console.error('Failed to load map markers', err);
        }
    }

    getDisasterMarkerColor(type, severity) {
        if (severity === 'CRITICAL') return '#dc2626'; // Red
        switch (type) {
            case 'FLOOD': return '#2563eb'; // Blue
            case 'FIRE': return '#ea580c'; // Orange-Red
            case 'EARTHQUAKE': return '#b45309'; // Amber
            case 'LANDSLIDE': return '#78350f'; // Brown
            case 'CYCLONE': return '#7c3aed'; // Purple
            default: return '#dc2626';
        }
    }

    getDisasterIcon(type) {
        switch (type) {
            case 'FLOOD': return 'bi-water';
            case 'FIRE': return 'bi-fire';
            case 'EARTHQUAKE': return 'bi-activity';
            case 'LANDSLIDE': return 'bi-mountain';
            case 'CYCLONE': return 'bi-wind';
            default: return 'bi-exclamation-triangle-fill';
        }
    }

    filterMap(type) {
        this.activeFilter = type;
        if (!this.map) return;

        this.disasterMarkers.forEach(m => {
            if (type === 'SHELTERS') {
                this.map.removeLayer(m);
            } else if (type === 'VERIFIED_ONLY') {
                if (m.dmsStatus === 'VERIFIED') this.map.addLayer(m);
                else this.map.removeLayer(m);
            } else {
                this.map.addLayer(m);
            }
        });

        this.shelterMarkers.forEach(m => {
            if (type === 'DISASTERS' || type === 'VERIFIED_ONLY') {
                this.map.removeLayer(m);
            } else {
                this.map.addLayer(m);
            }
        });
    }

    clearMarkers() {
        if (this.disasterMarkers) {
            this.disasterMarkers.forEach(m => this.map.removeLayer(m));
            this.disasterMarkers = [];
        }
        if (this.shelterMarkers) {
            this.shelterMarkers.forEach(m => this.map.removeLayer(m));
            this.shelterMarkers = [];
        }
    }

    recenter() {
        if (this.map) {
            this.map.setView([28.6200, 77.2200], 12);
        }
    }
}

const disasterMap = new DisasterMapManager();
