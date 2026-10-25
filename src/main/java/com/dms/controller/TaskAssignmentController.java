package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.TaskAssignmentDto;
import com.dms.model.enums.TaskStatus;
import com.dms.service.TaskAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskAssignmentController {

    @Autowired
    private TaskAssignmentService taskAssignmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskAssignmentDto>>> getAllTasks() {
        return ResponseEntity.ok(ApiResponse.success(taskAssignmentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskAssignmentDto>> getTaskById(@PathVariable Long id) {
        return taskAssignmentService.getById(id)
                .map(t -> ResponseEntity.ok(ApiResponse.success(t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Task not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskAssignmentDto>> createTask(@Valid @RequestBody TaskAssignmentDto dto) {
        TaskAssignmentDto created = taskAssignmentService.create(dto);
        return new ResponseEntity<>(ApiResponse.success("Task created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskAssignmentDto>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskAssignmentDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Task updated", taskAssignmentService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskAssignmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskAssignmentDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success("Task status updated", taskAssignmentService.updateTaskStatus(id, status, notes)));
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<ApiResponse<List<TaskAssignmentDto>>> getVolunteerTasks(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(ApiResponse.success(taskAssignmentService.getTasksForVolunteer(volunteerId)));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<ApiResponse<List<TaskAssignmentDto>>> getTeamTasks(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.success(taskAssignmentService.getTasksForRescueTeam(teamId)));
    }
}
