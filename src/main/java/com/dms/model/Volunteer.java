package com.dms.model;

import com.dms.model.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Demonstrates OOP Inheritance and Polymorphism.
 * Volunteer subclass representing field volunteers who assist in relief, medical aid, and logistics.
 */
@Entity
@Table(name = "volunteers")
public class Volunteer extends User {

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills; // e.g. "First Aid, Search & Rescue, Driving, Food Distribution"

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus = "AVAILABLE"; // AVAILABLE, BUSY, ON_LEAVE

    @Column(name = "assigned_tasks_count")
    private Integer assignedTasksCount = 0;

    @Column(name = "approved_by_admin")
    private Boolean approvedByAdmin = false;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Volunteer() {
        super();
        setRole(UserRole.VOLUNTEER);
    }

    public Volunteer(String username, String email, String password, String fullName, String phone,
                     String skills, Double latitude, Double longitude) {
        super(username, email, password, fullName, phone, UserRole.VOLUNTEER);
        this.skills = skills;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availabilityStatus = "AVAILABLE";
        this.assignedTasksCount = 0;
        this.approvedByAdmin = false;
    }

    // OOP Polymorphism: Overridden methods
    @Override
    public String getRoleDescription() {
        return "Registered volunteer ready for relief operations, field logistics, and community assistance.";
    }

    @Override
    public boolean canPerformAction(String action) {
        if (action == null) return false;
        return switch (action.toUpperCase()) {
            case "VIEW_ASSIGNED_TASKS", "UPDATE_TASK_STATUS", "VIEW_DISASTERS", "VIEW_SHELTERS" -> true;
            default -> false;
        };
    }

    // Encapsulation
    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getAssignedTasksCount() {
        return assignedTasksCount;
    }

    public void setAssignedTasksCount(Integer assignedTasksCount) {
        this.assignedTasksCount = assignedTasksCount;
    }

    public Boolean getApprovedByAdmin() {
        return approvedByAdmin;
    }

    public void setApprovedByAdmin(Boolean approvedByAdmin) {
        this.approvedByAdmin = approvedByAdmin;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
