package com.dms.service;

import com.dms.common.Manageable;
import com.dms.dto.DisasterReportDto;
import com.dms.model.enums.DisasterSeverity;
import com.dms.model.enums.DisasterStatus;
import com.dms.model.enums.DisasterType;

import java.util.List;

public interface DisasterReportService extends Manageable<DisasterReportDto, Long> {
    DisasterReportDto verifyReport(Long reportId, String adminUsername);
    DisasterReportDto updateStatus(Long reportId, DisasterStatus status, String username);
    List<DisasterReportDto> searchReports(DisasterType type, DisasterStatus status, DisasterSeverity severity, String query);
    List<DisasterReportDto> getReportsByReporter(Long userId);
}
