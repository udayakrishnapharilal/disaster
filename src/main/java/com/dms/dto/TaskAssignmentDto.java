package com.dms.dto;

import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TaskAssignmentDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Long assignedVolunteerId;
    private String assignedVolunteerName;

    private Long assignedRescueTeamId;
    private String assignedRescueTeamName;

    private Long relatedReportId;
    private String relatedReportTitle;

    private Long relatedRequestId;
    private String relatedRequestDescription;

    @NotNull
    private RequestPriority priority = RequestPriority.HIGH;

    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime assignedDate;
    private LocalDateTime completedDate;
    private String notes;

    public TaskAssignmentDto() {
    }

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

    public Long getRelatedReportId() {
        return relatedReportId;
    }

    public void setRelatedReportId(Long relatedReportId) {
        this.relatedReportId = relatedReportId;
    }

    public String getRelatedReportTitle() {
        return relatedReportTitle;
    }

    public void setRelatedReportTitle(String relatedReportTitle) {
        this.relatedReportTitle = relatedReportTitle;
    }

    public Long getRelatedRequestId() {
        return relatedRequestId;
    }

    public void setRelatedRequestId(Long relatedRequestId) {
        this.relatedRequestId = relatedRequestId;
    }

    public String getRelatedRequestDescription() {
        return relatedRequestDescription;
    }

    public void setRelatedRequestDescription(String relatedRequestDescription) {
        this.relatedRequestDescription = relatedRequestDescription;
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
}
