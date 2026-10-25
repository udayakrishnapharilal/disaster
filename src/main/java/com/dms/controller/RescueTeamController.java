package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.model.RescueTeam;
import com.dms.service.RescueTeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rescue-teams")
public class RescueTeamController {

    @Autowired
    private RescueTeamService rescueTeamService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RescueTeam>>> getAllTeams() {
        return ResponseEntity.ok(ApiResponse.success(rescueTeamService.getAll()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RescueTeam>>> getAvailableTeams() {
        return ResponseEntity.ok(ApiResponse.success(rescueTeamService.getAvailableTeams()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RescueTeam>> getTeamById(@PathVariable Long id) {
        return rescueTeamService.getById(id)
                .map(t -> ResponseEntity.ok(ApiResponse.success(t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Rescue team not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RescueTeam>> createTeam(@Valid @RequestBody RescueTeam team) {
        RescueTeam created = rescueTeamService.create(team);
        return new ResponseEntity<>(ApiResponse.success("Rescue team created", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RescueTeam>> updateTeam(@PathVariable Long id, @Valid @RequestBody RescueTeam team) {
        return ResponseEntity.ok(ApiResponse.success("Rescue team updated", rescueTeamService.update(id, team)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long id) {
        rescueTeamService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Rescue team deleted", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RescueTeam>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Rescue team status updated", rescueTeamService.updateStatus(id, status)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RescueTeam>>> searchBySpecialization(@RequestParam String specialization) {
        return ResponseEntity.ok(ApiResponse.success(rescueTeamService.searchBySpecialization(specialization)));
    }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<ApiResponse<String>> dispatchTeam(@PathVariable Long id, @RequestParam Long requestId) {
        String result = rescueTeamService.dispatch(id, requestId);
        return ResponseEntity.ok(ApiResponse.success(result, result));
    }
}
