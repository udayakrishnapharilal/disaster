package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.DashboardStatsDto;
import com.dms.model.ActivityLog;
import com.dms.service.ActivityLogService;
import com.dms.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getRecentActivities(
            @RequestParam(defaultValue = "15") int limit) {
        return ResponseEntity.ok(ApiResponse.success(activityLogService.getRecentActivities(limit)));
    }
}
