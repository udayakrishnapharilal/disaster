package com.dms.service.impl;

import com.dms.dto.DashboardStatsDto;
import com.dms.model.enums.DisasterType;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.ResourceType;
import com.dms.model.enums.UserRole;
import com.dms.repository.*;
import com.dms.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DisasterReportRepository disasterReportRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // 1. Overall counts
        stats.setTotalDisasterReports(disasterReportRepository.count());
        stats.setActiveEmergencies(emergencyRequestRepository.countByStatus(RequestStatus.PENDING) +
                emergencyRequestRepository.countByStatus(RequestStatus.ASSIGNED) +
                emergencyRequestRepository.countByStatus(RequestStatus.IN_PROGRESS));
        stats.setTotalRegisteredVictims(userRepository.countByRole(UserRole.VICTIM));
        stats.setAvailableVolunteers(volunteerRepository.countByAvailabilityStatus("AVAILABLE"));
        stats.setTotalRescueTeams(rescueTeamRepository.count());
        stats.setAvailableRescueTeams(rescueTeamRepository.countByStatus("AVAILABLE"));

        // 2. Shelter capacities
        stats.setTotalShelters(shelterRepository.count());
        Long totalCap = shelterRepository.sumTotalCapacity();
        Long totalOcc = shelterRepository.sumTotalOccupancy();
        stats.setTotalShelterCapacity(totalCap != null ? totalCap : 0);
        stats.setTotalShelterOccupancy(totalOcc != null ? totalOcc : 0);
        stats.setAvailableShelterCapacity(Math.max(0, stats.getTotalShelterCapacity() - stats.getTotalShelterOccupancy()));

        // 3. Resources & Requests
        Long totalInventory = resourceRepository.sumTotalInventoryQuantity();
        stats.setTotalResourcesInStock(totalInventory != null ? totalInventory : 0);
        stats.setPendingRequests(emergencyRequestRepository.countByStatus(RequestStatus.PENDING));
        stats.setFulfilledRequests(emergencyRequestRepository.countByStatus(RequestStatus.FULFILLED));

        // 4. Breakdown by Disaster Type
        Map<String, Long> typeMap = new HashMap<>();
        for (DisasterType dt : DisasterType.values()) {
            typeMap.put(dt.name(), 0L);
        }
        List<Object[]> typeCounts = disasterReportRepository.countReportsByType();
        for (Object[] row : typeCounts) {
            if (row[0] != null && row[1] != null) {
                typeMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setDisasterTypeCounts(typeMap);

        // 5. Breakdown by Request Status
        Map<String, Long> statusMap = new HashMap<>();
        for (RequestStatus rs : RequestStatus.values()) {
            statusMap.put(rs.name(), 0L);
        }
        List<Object[]> reqCounts = emergencyRequestRepository.countRequestsByStatus();
        for (Object[] row : reqCounts) {
            if (row[0] != null && row[1] != null) {
                statusMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setRequestStatusCounts(statusMap);

        // 6. Breakdown by Resource Type
        Map<String, Integer> resMap = new HashMap<>();
        for (ResourceType rt : ResourceType.values()) {
            resMap.put(rt.name(), 0);
        }
        List<Object[]> resCounts = resourceRepository.sumQuantitiesByType();
        for (Object[] row : resCounts) {
            if (row[0] != null && row[1] != null) {
                resMap.put(row[0].toString(), ((Number) row[1]).intValue());
            }
        }
        stats.setResourceCategoryQuantities(resMap);

        return stats;
    }
}
