package com.dms.model;

import com.dms.common.Auditable;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing emergency operational task assignments for Volunteers and Rescue Teams.
 * Demonstrates OOP Composition and Task Lifecycle progression.
 */
@Entity
@Table(name = "task_assignments")
public class TaskAssignment implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Task description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_volunteer_id")
    private Volunteer assignedVolunteer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_rescue_team_id")
    private RescueTeam assignedRescueTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disaster_report_id")
    private DisasterReport relatedReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emergency_request_id")
    private EmergencyRequest relatedRequest;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestPriority priority = RequestPriority.HIGH;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TaskAssignment() {
    }

    public TaskAssignment(String title, String description, Volunteer assignedVolunteer,
                          RescueTeam assignedRescueTeam, DisasterReport relatedReport,
                          EmergencyRequest relatedRequest, RequestPriority priority) {
        this.title = title;
        this.description = description;
        this.assignedVolunteer = assignedVolunteer;
        this.assignedRescueTeam = assignedRescueTeam;
        this.relatedReport = relatedReport;
        this.relatedRequest = relatedRequest;
        this.priority = priority != null ? priority : RequestPriority.HIGH;
        this.status = TaskStatus.PENDING;
        this.assignedDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.assignedDate == null) {
            this.assignedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle methods
    public void accept() {
        this.status = TaskStatus.ACCEPTED;
    }

    public void start() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void complete(String finalNotes) {
        this.status = TaskStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
        if (finalNotes != null) {
            this.notes = finalNotes;
        }
    }

    public void cancel() {
        this.status = TaskStatus.CANCELLED;
    }

    // Encapsulation
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Volunteer getAssignedVolunteer() {
        return assignedVolunteer;
    }

    public void setAssignedVolunteer(Volunteer assignedVolunteer) {
        this.assignedVolunteer = assignedVolunteer;
    }

    public RescueTeam getAssignedRescueTeam() {
        return assignedRescueTeam;
    }

    public void setAssignedRescueTeam(RescueTeam assignedRescueTeam) {
        this.assignedRescueTeam = assignedRescueTeam;
    }

    public DisasterReport getRelatedReport() {
        return relatedReport;
    }

    public void setRelatedReport(DisasterReport relatedReport) {
        this.relatedReport = relatedReport;
    }

    public EmergencyRequest getRelatedRequest() {
        return relatedRequest;
    }

    public void setRelatedRequest(EmergencyRequest relatedRequest) {
        this.relatedRequest = relatedRequest;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
