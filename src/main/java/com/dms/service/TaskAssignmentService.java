package com.dms.service;

import com.dms.common.Manageable;
import com.dms.dto.TaskAssignmentDto;
import com.dms.model.enums.TaskStatus;

import java.util.List;

public interface TaskAssignmentService extends Manageable<TaskAssignmentDto, Long> {
    TaskAssignmentDto assignTaskToVolunteer(TaskAssignmentDto dto, String adminUsername);
    TaskAssignmentDto assignTaskToRescueTeam(TaskAssignmentDto dto, String adminUsername);
    TaskAssignmentDto updateTaskStatus(Long taskId, TaskStatus status, String notes);
    List<TaskAssignmentDto> getTasksForVolunteer(Long volunteerId);
    List<TaskAssignmentDto> getTasksForRescueTeam(Long teamId);
}
