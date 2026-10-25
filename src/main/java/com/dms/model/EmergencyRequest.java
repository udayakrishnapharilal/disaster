package com.dms.model;

import com.dms.common.Auditable;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing an emergency relief or rescue request submitted by a victim or citizen.
 * Demonstrates OOP Encapsulation and Polymorphic assignment to responders.
 */
@Entity
@Table(name = "emergency_requests")
public class EmergencyRequest implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Request type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 30)
    private RequestType requestType;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestPriority priority = RequestPriority.HIGH;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(value = 1, message = "At least 1 person must be specified")
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople = 1;

    @NotBlank(message = "Location name is required")
    @Column(name = "location_name", nullable = false, length = 200)
    private String locationName;

    @Column(length = 100)
    private String city;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_rescue_team_id")
    private RescueTeam assignedRescueTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_volunteer_id")
    private Volunteer assignedVolunteer;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public EmergencyRequest() {
    }

    public EmergencyRequest(RequestType requestType, RequestPriority priority, String description,
                            Integer numberOfPeople, String locationName, String city,
                            Double latitude, Double longitude, User requester, String contactPhone) {
        this.requestType = requestType;
        this.priority = priority;
        this.description = description;
        this.numberOfPeople = numberOfPeople != null ? numberOfPeople : 1;
        this.locationName = locationName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.requester = requester;
        this.contactPhone = contactPhone;
        this.status = RequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // OOP Domain methods
    public void assignToRescueTeam(RescueTeam team) {
        this.assignedRescueTeam = team;
        this.status = RequestStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public void assignToVolunteer(Volunteer volunteer) {
        this.assignedVolunteer = volunteer;
        this.status = RequestStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        this.status = RequestStatus.IN_PROGRESS;
    }

    public void fulfill() {
        this.status = RequestStatus.FULFILLED;
        this.fulfilledAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = RequestStatus.CANCELLED;
    }

    // Encapsulation
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        this.priority = priority;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public RescueTeam getAssignedRescueTeam() {
        return assignedRescueTeam;
    }

    public void setAssignedRescueTeam(RescueTeam assignedRescueTeam) {
        this.assignedRescueTeam = assignedRescueTeam;
    }

    public Volunteer getAssignedVolunteer() {
        return assignedVolunteer;
    }

    public void setAssignedVolunteer(Volunteer assignedVolunteer) {
        this.assignedVolunteer = assignedVolunteer;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public void setFulfilledAt(LocalDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
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
