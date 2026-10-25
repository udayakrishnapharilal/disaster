package com.dms.service.impl;

import com.dms.dto.EmergencyRequestDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.EmergencyRequest;
import com.dms.model.RescueTeam;
import com.dms.model.User;
import com.dms.model.Volunteer;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;
import com.dms.repository.EmergencyRequestRepository;
import com.dms.repository.RescueTeamRepository;
import com.dms.repository.UserRepository;
import com.dms.repository.VolunteerRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.EmergencyRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmergencyRequestServiceImpl implements EmergencyRequestService {

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public EmergencyRequestDto create(EmergencyRequestDto dto) {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestType(dto.getRequestType());
        request.setPriority(dto.getPriority() != null ? dto.getPriority() : RequestPriority.HIGH);
        request.setStatus(RequestStatus.PENDING);
        request.setDescription(dto.getDescription());
        request.setNumberOfPeople(dto.getNumberOfPeople() != null ? dto.getNumberOfPeople() : 1);
        request.setLocationName(dto.getLocationName());
        request.setCity(dto.getCity());
        request.setLatitude(dto.getLatitude() != null ? dto.getLatitude() : 0.0);
        request.setLongitude(dto.getLongitude() != null ? dto.getLongitude() : 0.0);
        request.setContactPhone(dto.getContactPhone());
        request.setMedicalNotes(dto.getMedicalNotes());

        if (dto.getRequesterId() != null) {
            userRepository.findById(dto.getRequesterId()).ifPresent(request::setRequester);
        }

        EmergencyRequest saved = emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                saved.getRequester() != null ? saved.getRequester().getUsername() : "Victim",
                "VICTIM",
                "CREATE_EMERGENCY_REQUEST",
                "EmergencyRequest",
                saved.getId(),
                "New emergency request for " + saved.getRequestType() + " at " + saved.getLocationName()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public EmergencyRequestDto update(Long id, EmergencyRequestDto dto) {
        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", id));

        request.setRequestType(dto.getRequestType());
        request.setPriority(dto.getPriority());
        request.setDescription(dto.getDescription());
        request.setNumberOfPeople(dto.getNumberOfPeople());
        request.setLocationName(dto.getLocationName());
        request.setCity(dto.getCity());
        request.setContactPhone(dto.getContactPhone());
        request.setMedicalNotes(dto.getMedicalNotes());

        EmergencyRequest updated = emergencyRequestRepository.save(request);
        return toDto(updated);
    }

    @Override
    public Optional<EmergencyRequestDto> getById(Long id) {
        return emergencyRequestRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<EmergencyRequestDto> getAll() {
        return emergencyRequestRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", id));
        emergencyRequestRepository.delete(request);
    }

    @Override
    @Transactional
    public EmergencyRequestDto assignRescueTeam(Long requestId, Long rescueTeamId, String adminUsername) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        RescueTeam team = rescueTeamRepository.findById(rescueTeamId)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", rescueTeamId));

        request.assignToRescueTeam(team);
        team.markDispatched();
        rescueTeamRepository.save(team);

        EmergencyRequest saved = emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "ASSIGN_RESCUE_TEAM",
                "EmergencyRequest",
                saved.getId(),
                "Assigned rescue team '" + team.getTeamName() + "' to request #" + saved.getId()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public EmergencyRequestDto assignVolunteer(Long requestId, Long volunteerId, String adminUsername) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", volunteerId));

        request.assignToVolunteer(volunteer);
        volunteer.setAssignedTasksCount(volunteer.getAssignedTasksCount() + 1);
        volunteer.setAvailabilityStatus("BUSY");
        volunteerRepository.save(volunteer);

        EmergencyRequest saved = emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "ASSIGN_VOLUNTEER",
                "EmergencyRequest",
                saved.getId(),
                "Assigned volunteer '" + volunteer.getFullName() + "' to request #" + saved.getId()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public EmergencyRequestDto updateStatus(Long requestId, RequestStatus status, String username) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        if (status == RequestStatus.FULFILLED) {
            request.fulfill();
            // Release assigned resources/teams if applicable
            if (request.getAssignedRescueTeam() != null) {
                request.getAssignedRescueTeam().markAvailable();
                rescueTeamRepository.save(request.getAssignedRescueTeam());
            }
            if (request.getAssignedVolunteer() != null) {
                request.getAssignedVolunteer().setAvailabilityStatus("AVAILABLE");
                volunteerRepository.save(request.getAssignedVolunteer());
            }
        } else if (status == RequestStatus.CANCELLED) {
            request.cancel();
        } else if (status == RequestStatus.IN_PROGRESS) {
            request.markInProgress();
        } else {
            request.setStatus(status);
        }

        EmergencyRequest saved = emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                username != null ? username : "System",
                "USER",
                "UPDATE_REQUEST_STATUS",
                "EmergencyRequest",
                saved.getId(),
                "Emergency request #" + saved.getId() + " status updated to " + status
        );

        return toDto(saved);
    }

    @Override
    public List<EmergencyRequestDto> searchRequests(RequestType type, RequestStatus status, RequestPriority priority, String query) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return emergencyRequestRepository.searchRequests(type, status, priority, cleanQuery).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequestDto> getRequestsByRequester(Long userId) {
        return emergencyRequestRepository.findByRequesterId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequestDto> getRequestsByVolunteer(Long volunteerId) {
        return emergencyRequestRepository.findByAssignedVolunteerId(volunteerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequestDto> getRequestsByRescueTeam(Long rescueTeamId) {
        return emergencyRequestRepository.findByAssignedRescueTeamId(rescueTeamId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EmergencyRequestDto toDto(EmergencyRequest req) {
        EmergencyRequestDto dto = new EmergencyRequestDto();
        dto.setId(req.getId());
        dto.setRequestType(req.getRequestType());
        dto.setPriority(req.getPriority());
        dto.setStatus(req.getStatus());
        dto.setDescription(req.getDescription());
        dto.setNumberOfPeople(req.getNumberOfPeople());
        dto.setLocationName(req.getLocationName());
        dto.setCity(req.getCity());
        dto.setLatitude(req.getLatitude());
        dto.setLongitude(req.getLongitude());
        dto.setContactPhone(req.getContactPhone());
        dto.setMedicalNotes(req.getMedicalNotes());
        dto.setRequestedAt(req.getRequestedAt());
        dto.setAssignedAt(req.getAssignedAt());
        dto.setFulfilledAt(req.getFulfilledAt());

        if (req.getRequester() != null) {
            dto.setRequesterId(req.getRequester().getId());
            dto.setRequesterName(req.getRequester().getFullName());
            dto.setRequesterPhone(req.getRequester().getPhone());
        }
        if (req.getAssignedRescueTeam() != null) {
            dto.setAssignedRescueTeamId(req.getAssignedRescueTeam().getId());
            dto.setAssignedRescueTeamName(req.getAssignedRescueTeam().getTeamName());
        }
        if (req.getAssignedVolunteer() != null) {
            dto.setAssignedVolunteerId(req.getAssignedVolunteer().getId());
            dto.setAssignedVolunteerName(req.getAssignedVolunteer().getFullName());
        }
        return dto;
    }
}
