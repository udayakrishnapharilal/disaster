package com.dms.model;

import com.dms.common.Auditable;
import com.dms.exception.ShelterFullException;
import com.dms.model.enums.ShelterStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing emergency relief shelters.
 * Demonstrates OOP Encapsulation with synchronized capacity tracking and custom exception throwing.
 */
@Entity
@Table(name = "shelters")
public class Shelter implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shelter name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @NotNull(message = "Latitude is required for map plotting")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required for map plotting")
    @Column(nullable = false)
    private Double longitude;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be at least 1")
    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @NotNull
    @Min(value = 0, message = "Current occupancy cannot be negative")
    @Column(name = "current_occupancy", nullable = false)
    private Integer currentOccupancy = 0;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities; // e.g., "Medical Ward, Hot Meals, Clean Water, Sanitation, Generator"

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShelterStatus status = ShelterStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Shelter() {
    }

    public Shelter(String name, String address, String city, Double latitude, Double longitude,
                   Integer totalCapacity, String contactPerson, String contactPhone, String facilities) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCapacity = totalCapacity;
        this.currentOccupancy = 0;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.facilities = facilities;
        this.status = ShelterStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // OOP Domain Logic & Invariant Protection
    public synchronized void allocate(int personCount) {
        if (personCount <= 0) {
            throw new IllegalArgumentException("Person count to allocate must be positive.");
        }
        int available = getRemainingCapacity();
        if (personCount > available) {
            throw new ShelterFullException(this.id, personCount, available);
        }
        this.currentOccupancy += personCount;
        if (this.currentOccupancy >= this.totalCapacity) {
            this.status = ShelterStatus.FULL;
        }
    }

    public synchronized void release(int personCount) {
        if (personCount <= 0) {
            throw new IllegalArgumentException("Person count to release must be positive.");
        }
        this.currentOccupancy = Math.max(0, this.currentOccupancy - personCount);
        if (this.currentOccupancy < this.totalCapacity && this.status == ShelterStatus.FULL) {
            this.status = ShelterStatus.ACTIVE;
        }
    }

    public int getRemainingCapacity() {
        return Math.max(0, this.totalCapacity - this.currentOccupancy);
    }

    public double getOccupancyRate() {
        if (this.totalCapacity == null || this.totalCapacity == 0) return 0.0;
        return ((double) this.currentOccupancy / this.totalCapacity) * 100.0;
    }

    // Encapsulation: Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public ShelterStatus getStatus() {
        return status;
    }

    public void setStatus(ShelterStatus status) {
        this.status = status;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
