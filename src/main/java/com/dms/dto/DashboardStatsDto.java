package com.dms.dto;

import java.util.Map;

public class DashboardStatsDto {
    private long totalDisasterReports;
    private long activeEmergencies;
    private long totalRegisteredVictims;
    private long availableVolunteers;
    private long totalRescueTeams;
    private long availableRescueTeams;
    private long totalShelters;
    private long totalShelterCapacity;
    private long totalShelterOccupancy;
    private long availableShelterCapacity;
    private long totalResourcesInStock;
    private long pendingRequests;
    private long fulfilledRequests;

    private Map<String, Long> disasterTypeCounts;
    private Map<String, Long> requestStatusCounts;
    private Map<String, Integer> resourceCategoryQuantities;

    public DashboardStatsDto() {
    }

    public long getTotalDisasterReports() {
        return totalDisasterReports;
    }

    public void setTotalDisasterReports(long totalDisasterReports) {
        this.totalDisasterReports = totalDisasterReports;
    }

    public long getActiveEmergencies() {
        return activeEmergencies;
    }

    public void setActiveEmergencies(long activeEmergencies) {
        this.activeEmergencies = activeEmergencies;
    }

    public long getTotalRegisteredVictims() {
        return totalRegisteredVictims;
    }

    public void setTotalRegisteredVictims(long totalRegisteredVictims) {
        this.totalRegisteredVictims = totalRegisteredVictims;
    }

    public long getAvailableVolunteers() {
        return availableVolunteers;
    }

    public void setAvailableVolunteers(long availableVolunteers) {
        this.availableVolunteers = availableVolunteers;
    }

    public long getTotalRescueTeams() {
        return totalRescueTeams;
    }

    public void setTotalRescueTeams(long totalRescueTeams) {
        this.totalRescueTeams = totalRescueTeams;
    }

    public long getAvailableRescueTeams() {
        return availableRescueTeams;
    }

    public void setAvailableRescueTeams(long availableRescueTeams) {
        this.availableRescueTeams = availableRescueTeams;
    }

    public long getTotalShelters() {
        return totalShelters;
    }

    public void setTotalShelters(long totalShelters) {
        this.totalShelters = totalShelters;
    }

    public long getTotalShelterCapacity() {
        return totalShelterCapacity;
    }

    public void setTotalShelterCapacity(long totalShelterCapacity) {
        this.totalShelterCapacity = totalShelterCapacity;
    }

    public long getTotalShelterOccupancy() {
        return totalShelterOccupancy;
    }

    public void setTotalShelterOccupancy(long totalShelterOccupancy) {
        this.totalShelterOccupancy = totalShelterOccupancy;
    }

    public long getAvailableShelterCapacity() {
        return availableShelterCapacity;
    }

    public void setAvailableShelterCapacity(long availableShelterCapacity) {
        this.availableShelterCapacity = availableShelterCapacity;
    }

    public long getTotalResourcesInStock() {
        return totalResourcesInStock;
    }

    public void setTotalResourcesInStock(long totalResourcesInStock) {
        this.totalResourcesInStock = totalResourcesInStock;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getFulfilledRequests() {
        return fulfilledRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public Map<String, Long> getDisasterTypeCounts() {
        return disasterTypeCounts;
    }

    public void setDisasterTypeCounts(Map<String, Long> disasterTypeCounts) {
        this.disasterTypeCounts = disasterTypeCounts;
    }

    public Map<String, Long> getRequestStatusCounts() {
        return requestStatusCounts;
    }

    public void setRequestStatusCounts(Map<String, Long> requestStatusCounts) {
        this.requestStatusCounts = requestStatusCounts;
    }

    public Map<String, Integer> getResourceCategoryQuantities() {
        return resourceCategoryQuantities;
    }

    public void setResourceCategoryQuantities(Map<String, Integer> resourceCategoryQuantities) {
        this.resourceCategoryQuantities = resourceCategoryQuantities;
    }
}
