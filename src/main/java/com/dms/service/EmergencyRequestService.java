package com.dms.service;

import com.dms.common.Manageable;
import com.dms.dto.EmergencyRequestDto;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;

import java.util.List;

public interface EmergencyRequestService extends Manageable<EmergencyRequestDto, Long> {
    EmergencyRequestDto assignRescueTeam(Long requestId, Long rescueTeamId, String adminUsername);
    EmergencyRequestDto assignVolunteer(Long requestId, Long volunteerId, String adminUsername);
    EmergencyRequestDto updateStatus(Long requestId, RequestStatus status, String username);
    List<EmergencyRequestDto> searchRequests(RequestType type, RequestStatus status, RequestPriority priority, String query);
    List<EmergencyRequestDto> getRequestsByRequester(Long userId);
    List<EmergencyRequestDto> getRequestsByVolunteer(Long volunteerId);
    List<EmergencyRequestDto> getRequestsByRescueTeam(Long rescueTeamId);
}
