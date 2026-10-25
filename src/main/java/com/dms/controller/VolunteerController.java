package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.UserSummaryDto;
import com.dms.model.Volunteer;
import com.dms.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Volunteer>>> getAllVolunteers() {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getAllVolunteers()));
    }

    @GetMapping("/summaries")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getVolunteerSummaries() {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getAllVolunteerSummaries()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Volunteer>>> getAvailableVolunteers() {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getAvailableVolunteers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Volunteer>> getVolunteerById(@PathVariable Long id) {
        return volunteerService.getVolunteerById(id)
                .map(v -> ResponseEntity.ok(ApiResponse.success(v)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Volunteer not found")));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Volunteer>> approveVolunteer(@PathVariable Long id, Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        return ResponseEntity.ok(ApiResponse.success("Volunteer approved", volunteerService.approveVolunteer(id, adminUsername)));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Volunteer>> updateAvailability(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Availability updated", volunteerService.updateAvailability(id, status)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Volunteer>>> searchBySkill(@RequestParam String skill) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.searchBySkill(skill)));
    }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<ApiResponse<String>> dispatchVolunteer(@PathVariable Long id, @RequestParam Long requestId) {
        String result = volunteerService.dispatch(id, requestId);
        return ResponseEntity.ok(ApiResponse.success(result, result));
    }
}
