package com.dms.service.impl;

import com.dms.dto.DisasterReportDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.Administrator;
import com.dms.model.DisasterReport;
import com.dms.model.User;
import com.dms.model.enums.DisasterSeverity;
import com.dms.model.enums.DisasterStatus;
import com.dms.model.enums.DisasterType;
import com.dms.repository.AdministratorRepository;
import com.dms.repository.DisasterReportRepository;
import com.dms.repository.UserRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.DisasterReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisasterReportServiceImpl implements DisasterReportService {

    @Autowired
    private DisasterReportRepository disasterReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public DisasterReportDto create(DisasterReportDto dto) {
        DisasterReport report = new DisasterReport();
        report.setTitle(dto.getTitle());
        report.setDisasterType(dto.getDisasterType());
        report.setSeverity(dto.getSeverity() != null ? dto.getSeverity() : DisasterSeverity.MEDIUM);
        report.setStatus(DisasterStatus.REPORTED);
        report.setDescription(dto.getDescription());
        report.setLocationName(dto.getLocationName());
        report.setCity(dto.getCity());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setPhotoUrl(dto.getPhotoUrl());
        report.setCasualtiesCount(dto.getCasualtiesCount() != null ? dto.getCasualtiesCount() : 0);
        report.setAffectedCount(dto.getAffectedCount() != null ? dto.getAffectedCount() : 0);

        if (dto.getReportedById() != null) {
            userRepository.findById(dto.getReportedById()).ifPresent(report::setReportedBy);
        }

        DisasterReport saved = disasterReportRepository.save(report);

        activityLogService.logActivity(
                saved.getReportedBy() != null ? saved.getReportedBy().getUsername() : "Anonymous",
                "USER",
                "REPORT_DISASTER",
                "DisasterReport",
                saved.getId(),
                "Reported new incident: " + saved.getTitle() + " (" + saved.getDisasterType() + ")"
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public DisasterReportDto update(Long id, DisasterReportDto dto) {
        DisasterReport report = disasterReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterReport", "id", id));

        report.setTitle(dto.getTitle());
        report.setDisasterType(dto.getDisasterType());
        report.setSeverity(dto.getSeverity());
        report.setDescription(dto.getDescription());
        report.setLocationName(dto.getLocationName());
        report.setCity(dto.getCity());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setPhotoUrl(dto.getPhotoUrl());
        report.setCasualtiesCount(dto.getCasualtiesCount());
        report.setAffectedCount(dto.getAffectedCount());

        DisasterReport updated = disasterReportRepository.save(report);
        return toDto(updated);
    }

    @Override
    public Optional<DisasterReportDto> getById(Long id) {
        return disasterReportRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<DisasterReportDto> getAll() {
        return disasterReportRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DisasterReport report = disasterReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterReport", "id", id));
        disasterReportRepository.delete(report);
    }

    @Override
    @Transactional
    public DisasterReportDto verifyReport(Long reportId, String adminUsername) {
        DisasterReport report = disasterReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterReport", "id", reportId));

        User adminUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", adminUsername));

        Administrator admin = null;
        if (adminUser instanceof Administrator) {
            admin = (Administrator) adminUser;
        } else {
            // Check in administrator repo
            admin = administratorRepository.findById(adminUser.getId()).orElse(null);
        }

        // Domain method call: encapsulation & status change
        report.verify(admin);
        DisasterReport saved = disasterReportRepository.save(report);

        activityLogService.logActivity(
                adminUsername,
                "ADMIN",
                "VERIFY_REPORT",
                "DisasterReport",
                saved.getId(),
                "Verified disaster report #" + saved.getId() + " - " + saved.getTitle()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public DisasterReportDto updateStatus(Long reportId, DisasterStatus status, String username) {
        DisasterReport report = disasterReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterReport", "id", reportId));

        report.setStatus(status);
        DisasterReport saved = disasterReportRepository.save(report);

        activityLogService.logActivity(
                username != null ? username : "System",
                "ADMIN",
                "UPDATE_REPORT_STATUS",
                "DisasterReport",
                saved.getId(),
                "Changed status of report #" + saved.getId() + " to " + status
        );

        return toDto(saved);
    }

    @Override
    public List<DisasterReportDto> searchReports(DisasterType type, DisasterStatus status, DisasterSeverity severity, String query) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return disasterReportRepository.searchReports(type, status, severity, cleanQuery).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisasterReportDto> getReportsByReporter(Long userId) {
        return disasterReportRepository.findByReportedById(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DisasterReportDto toDto(DisasterReport report) {
        DisasterReportDto dto = new DisasterReportDto();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setDisasterType(report.getDisasterType());
        dto.setSeverity(report.getSeverity());
        dto.setStatus(report.getStatus());
        dto.setDescription(report.getDescription());
        dto.setLocationName(report.getLocationName());
        dto.setCity(report.getCity());
        dto.setLatitude(report.getLatitude());
        dto.setLongitude(report.getLongitude());
        dto.setPhotoUrl(report.getPhotoUrl());
        dto.setCasualtiesCount(report.getCasualtiesCount());
        dto.setAffectedCount(report.getAffectedCount());
        dto.setReportedAt(report.getReportedAt());
        dto.setVerifiedAt(report.getVerifiedAt());

        if (report.getReportedBy() != null) {
            dto.setReportedById(report.getReportedBy().getId());
            dto.setReportedByName(report.getReportedBy().getFullName());
        }
        if (report.getVerifiedBy() != null) {
            dto.setVerifiedById(report.getVerifiedBy().getId());
            dto.setVerifiedByName(report.getVerifiedBy().getFullName());
        }
        return dto;
    }
}
