package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.DisasterReportDto;
import com.dms.model.enums.DisasterSeverity;
import com.dms.model.enums.DisasterStatus;
import com.dms.model.enums.DisasterType;
import com.dms.service.DisasterReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disasters")
public class DisasterReportController {

    @Autowired
    private DisasterReportService disasterReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DisasterReportDto>>> getAllDisasters() {
        return ResponseEntity.ok(ApiResponse.success(disasterReportService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DisasterReportDto>> getDisasterById(@PathVariable Long id) {
        return disasterReportService.getById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Disaster report not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DisasterReportDto>> createDisaster(
            @Valid @RequestBody DisasterReportDto dto) {
        DisasterReportDto created = disasterReportService.create(dto);
        return new ResponseEntity<>(ApiResponse.success("Disaster reported successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DisasterReportDto>> updateDisaster(
            @PathVariable Long id,
            @Valid @RequestBody DisasterReportDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Disaster updated successfully", disasterReportService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDisaster(@PathVariable Long id) {
        disasterReportService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Disaster report deleted successfully", null));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<DisasterReportDto>> verifyDisaster(
            @PathVariable Long id,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        DisasterReportDto verified = disasterReportService.verifyReport(id, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Disaster report verified successfully", verified));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DisasterReportDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam DisasterStatus status,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "admin";
        DisasterReportDto updated = disasterReportService.updateStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Disaster report status updated", updated));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DisasterReportDto>>> searchDisasters(
            @RequestParam(required = false) DisasterType type,
            @RequestParam(required = false) DisasterStatus status,
            @RequestParam(required = false) DisasterSeverity severity,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(disasterReportService.searchReports(type, status, severity, query)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DisasterReportDto>>> getUserDisasterReports(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(disasterReportService.getReportsByReporter(userId)));
    }
}
