package com.dms.service.impl;

import com.dms.dto.TaskAssignmentDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.*;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.TaskStatus;
import com.dms.repository.*;
import com.dms.service.ActivityLogService;
import com.dms.service.TaskAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskAssignmentServiceImpl implements TaskAssignmentService {

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private DisasterReportRepository disasterReportRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public TaskAssignmentDto create(TaskAssignmentDto dto) {
        TaskAssignment task = new TaskAssignment();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : RequestPriority.HIGH);
        task.setStatus(TaskStatus.PENDING);

        if (dto.getAssignedVolunteerId() != null) {
            volunteerRepository.findById(dto.getAssignedVolunteerId()).ifPresent(task::setAssignedVolunteer);
        }
        if (dto.getAssignedRescueTeamId() != null) {
            rescueTeamRepository.findById(dto.getAssignedRescueTeamId()).ifPresent(task::setAssignedRescueTeam);
        }
        if (dto.getRelatedReportId() != null) {
            disasterReportRepository.findById(dto.getRelatedReportId()).ifPresent(task::setRelatedReport);
        }
        if (dto.getRelatedRequestId() != null) {
            emergencyRequestRepository.findById(dto.getRelatedRequestId()).ifPresent(task::setRelatedRequest);
        }

        TaskAssignment saved = taskAssignmentRepository.save(task);

        activityLogService.logActivity(
                "Admin",
                "ADMIN",
                "CREATE_TASK",
                "TaskAssignment",
                saved.getId(),
                "Created task: " + saved.getTitle()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public TaskAssignmentDto update(Long id, TaskAssignmentDto dto) {
        TaskAssignment task = taskAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskAssignment", "id", id));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setNotes(dto.getNotes());

        TaskAssignment updated = taskAssignmentRepository.save(task);
        return toDto(updated);
    }

    @Override
    public Optional<TaskAssignmentDto> getById(Long id) {
        return taskAssignmentRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<TaskAssignmentDto> getAll() {
        return taskAssignmentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaskAssignment task = taskAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskAssignment", "id", id));
        taskAssignmentRepository.delete(task);
    }

    @Override
    @Transactional
    public TaskAssignmentDto assignTaskToVolunteer(TaskAssignmentDto dto, String adminUsername) {
        Volunteer volunteer = volunteerRepository.findById(dto.getAssignedVolunteerId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", dto.getAssignedVolunteerId()));

        dto.setAssignedVolunteerName(volunteer.getFullName());
        TaskAssignmentDto created = create(dto);

        volunteer.setAssignedTasksCount(volunteer.getAssignedTasksCount() + 1);
        volunteerRepository.save(volunteer);

        return created;
    }

    @Override
    @Transactional
    public TaskAssignmentDto assignTaskToRescueTeam(TaskAssignmentDto dto, String adminUsername) {
        RescueTeam team = rescueTeamRepository.findById(dto.getAssignedRescueTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", dto.getAssignedRescueTeamId()));

        dto.setAssignedRescueTeamName(team.getTeamName());
        return create(dto);
    }

    @Override
    @Transactional
    public TaskAssignmentDto updateTaskStatus(Long taskId, TaskStatus status, String notes) {
        TaskAssignment task = taskAssignmentRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskAssignment", "id", taskId));

        if (status == TaskStatus.COMPLETED) {
            task.complete(notes);
            if (task.getAssignedVolunteer() != null) {
                Volunteer v = task.getAssignedVolunteer();
                v.setAssignedTasksCount(Math.max(0, v.getAssignedTasksCount() - 1));
                volunteerRepository.save(v);
            }
        } else if (status == TaskStatus.IN_PROGRESS) {
            task.start();
        } else if (status == TaskStatus.ACCEPTED) {
            task.accept();
        } else if (status == TaskStatus.CANCELLED) {
            task.cancel();
        } else {
            task.setStatus(status);
        }

        if (notes != null) {
            task.setNotes(notes);
        }

        TaskAssignment saved = taskAssignmentRepository.save(task);

        activityLogService.logActivity(
                "System",
                "OPERATIONAL",
                "UPDATE_TASK_STATUS",
                "TaskAssignment",
                saved.getId(),
                "Task #" + saved.getId() + " status updated to " + status
        );

        return toDto(saved);
    }

    @Override
    public List<TaskAssignmentDto> getTasksForVolunteer(Long volunteerId) {
        return taskAssignmentRepository.findByAssignedVolunteerId(volunteerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskAssignmentDto> getTasksForRescueTeam(Long teamId) {
        return taskAssignmentRepository.findByAssignedRescueTeamId(teamId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TaskAssignmentDto toDto(TaskAssignment t) {
        TaskAssignmentDto dto = new TaskAssignmentDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setPriority(t.getPriority());
        dto.setStatus(t.getStatus());
        dto.setAssignedDate(t.getAssignedDate());
        dto.setCompletedDate(t.getCompletedDate());
        dto.setNotes(t.getNotes());

        if (t.getAssignedVolunteer() != null) {
            dto.setAssignedVolunteerId(t.getAssignedVolunteer().getId());
            dto.setAssignedVolunteerName(t.getAssignedVolunteer().getFullName());
        }
        if (t.getAssignedRescueTeam() != null) {
            dto.setAssignedRescueTeamId(t.getAssignedRescueTeam().getId());
            dto.setAssignedRescueTeamName(t.getAssignedRescueTeam().getTeamName());
        }
        if (t.getRelatedReport() != null) {
            dto.setRelatedReportId(t.getRelatedReport().getId());
            dto.setRelatedReportTitle(t.getRelatedReport().getTitle());
        }
        if (t.getRelatedRequest() != null) {
            dto.setRelatedRequestId(t.getRelatedRequest().getId());
            dto.setRelatedRequestDescription(t.getRelatedRequest().getDescription());
        }
        return dto;
    }
}
