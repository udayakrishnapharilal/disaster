package com.dms.controller;

import com.dms.service.ReportGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
public class AnalyticsReportController {

    @Autowired
    private ReportGeneratorService reportGeneratorService;

    @GetMapping("/export/{type}")
    public ResponseEntity<InputStreamResource> exportCsv(@PathVariable String type) {
        ByteArrayInputStream in;
        String filename;

        switch (type.toLowerCase()) {
            case "disasters" -> {
                in = reportGeneratorService.exportDisastersCsv();
                filename = "disaster_reports.csv";
            }
            case "requests" -> {
                in = reportGeneratorService.exportEmergencyRequestsCsv();
                filename = "emergency_requests.csv";
            }
            case "shelters" -> {
                in = reportGeneratorService.exportSheltersCsv();
                filename = "shelter_occupancy.csv";
            }
            case "resources" -> {
                in = reportGeneratorService.exportResourcesCsv();
                filename = "relief_resources.csv";
            }
            default -> throw new IllegalArgumentException("Unknown report export type: " + type);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}
