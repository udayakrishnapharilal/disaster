package com.dms.model;

import com.dms.common.Auditable;
import com.dms.model.enums.DisasterSeverity;
import com.dms.model.enums.DisasterStatus;
import com.dms.model.enums.DisasterType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing an emergency disaster incident report.
 * Demonstrates OOP Encapsulation and Lifecycle state progression.
 */
@Entity
@Table(name = "disaster_reports")
public class DisasterReport implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull(message = "Disaster type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "disaster_type", nullable = false, length = 30)
    private DisasterType disasterType;

    @NotNull(message = "Severity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisasterSeverity severity = DisasterSeverity.MEDIUM;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisasterStatus status = DisasterStatus.REPORTED;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Location name is required")
    @Column(name = "location_name", nullable = false, length = 200)
    private String locationName;

    @Column(length = 100)
    private String city;

    @NotNull(message = "Latitude is required for interactive map plotting")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required for interactive map plotting")
    @Column(nullable = false)
    private Double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_by_user_id")
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_admin_id")
    private Administrator verifiedBy;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "casualties_count")
    private Integer casualtiesCount = 0;

    @Column(name = "affected_count")
    private Integer affectedCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DisasterReport() {
    }

    public DisasterReport(String title, DisasterType disasterType, DisasterSeverity severity,
                          String description, String locationName, String city,
                          Double latitude, Double longitude, User reportedBy, String photoUrl) {
        this.title = title;
        this.disasterType = disasterType;
        this.severity = severity;
        this.description = description;
        this.locationName = locationName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedBy = reportedBy;
        this.photoUrl = photoUrl;
        this.status = DisasterStatus.REPORTED;
        this.reportedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.reportedAt == null) {
            this.reportedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // OOP Domain methods
    public void verify(Administrator admin) {
        this.status = DisasterStatus.VERIFIED;
        this.verifiedBy = admin;
        this.verifiedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        this.status = DisasterStatus.IN_PROGRESS;
    }

    public void resolve() {
        this.status = DisasterStatus.RESOLVED;
    }

    public void reject() {
        this.status = DisasterStatus.REJECTED;
    }

    // Encapsulation: Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DisasterType getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(DisasterType disasterType) {
        this.disasterType = disasterType;
    }

    public DisasterSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DisasterSeverity severity) {
        this.severity = severity;
    }

    public DisasterStatus getStatus() {
        return status;
    }

    public void setStatus(DisasterStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Administrator getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(Administrator verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getCasualtiesCount() {
        return casualtiesCount;
    }

    public void setCasualtiesCount(Integer casualtiesCount) {
        this.casualtiesCount = casualtiesCount;
    }

    public Integer getAffectedCount() {
        return affectedCount;
    }

    public void setAffectedCount(Integer affectedCount) {
        this.affectedCount = affectedCount;
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
