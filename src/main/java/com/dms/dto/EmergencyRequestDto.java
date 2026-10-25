package com.dms.dto;

import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EmergencyRequestDto {
    private Long id;

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    @NotNull(message = "Priority is required")
    private RequestPriority priority = RequestPriority.HIGH;

    private RequestStatus status = RequestStatus.PENDING;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull
    @Min(1)
    private Integer numberOfPeople = 1;

    @NotBlank(message = "Location name is required")
    private String locationName;

    private String city;
    private Double latitude;
    private Double longitude;

    private Long requesterId;
    private String requesterName;
    private String requesterPhone;

    private Long assignedRescueTeamId;
    private String assignedRescueTeamName;

    private Long assignedVolunteerId;
    private String assignedVolunteerName;

    private String contactPhone;
    private String medicalNotes;

    private LocalDateTime requestedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime fulfilledAt;

    public EmergencyRequestDto() {
    }

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

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhone() {
        return requesterPhone;
    }

    public void setRequesterPhone(String requesterPhone) {
        this.requesterPhone = requesterPhone;
    }

    public Long getAssignedRescueTeamId() {
        return assignedRescueTeamId;
    }

    public void setAssignedRescueTeamId(Long assignedRescueTeamId) {
        this.assignedRescueTeamId = assignedRescueTeamId;
    }

    public String getAssignedRescueTeamName() {
        return assignedRescueTeamName;
    }

    public void setAssignedRescueTeamName(String assignedRescueTeamName) {
        this.assignedRescueTeamName = assignedRescueTeamName;
    }

    public Long getAssignedVolunteerId() {
        return assignedVolunteerId;
    }

    public void setAssignedVolunteerId(Long assignedVolunteerId) {
        this.assignedVolunteerId = assignedVolunteerId;
    }

    public String getAssignedVolunteerName() {
        return assignedVolunteerName;
    }

    public void setAssignedVolunteerName(String assignedVolunteerName) {
        this.assignedVolunteerName = assignedVolunteerName;
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
}
