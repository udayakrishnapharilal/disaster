package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.EmergencyRequestDto;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;
import com.dms.service.EmergencyRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class EmergencyRequestController {

    @Autowired
    private EmergencyRequestService emergencyRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmergencyRequestDto>>> getAllRequests() {
        return ResponseEntity.ok(ApiResponse.success(emergencyRequestService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> getRequestById(@PathVariable Long id) {
        return emergencyRequestService.getById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Request not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> createRequest(@Valid @RequestBody EmergencyRequestDto dto) {
        EmergencyRequestDto created = emergencyRequestService.create(dto);
        return new ResponseEntity<>(ApiResponse.success("Emergency request submitted successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody EmergencyRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Emergency request updated", emergencyRequestService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        emergencyRequestService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Emergency request deleted", null));
    }

    @PostMapping("/{id}/assign-team")
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> assignRescueTeam(
            @PathVariable Long id,
            @RequestParam Long rescueTeamId,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        EmergencyRequestDto assigned = emergencyRequestService.assignRescueTeam(id, rescueTeamId, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Rescue team assigned successfully", assigned));
    }

    @PostMapping("/{id}/assign-volunteer")
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> assignVolunteer(
            @PathVariable Long id,
            @RequestParam Long volunteerId,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        EmergencyRequestDto assigned = emergencyRequestService.assignVolunteer(id, volunteerId, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Volunteer assigned successfully", assigned));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EmergencyRequestDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "user";
        EmergencyRequestDto updated = emergencyRequestService.updateStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Request status updated to " + status, updated));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmergencyRequestDto>>> searchRequests(
            @RequestParam(required = false) RequestType type,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestPriority priority,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(emergencyRequestService.searchRequests(type, status, priority, query)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<EmergencyRequestDto>>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(emergencyRequestService.getRequestsByRequester(userId)));
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<ApiResponse<List<EmergencyRequestDto>>> getVolunteerRequests(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(ApiResponse.success(emergencyRequestService.getRequestsByVolunteer(volunteerId)));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<ApiResponse<List<EmergencyRequestDto>>> getTeamRequests(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.success(emergencyRequestService.getRequestsByRescueTeam(teamId)));
    }
}
