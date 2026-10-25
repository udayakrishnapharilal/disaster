package com.dms.service.impl;

import com.dms.exception.ResourceNotFoundException;
import com.dms.model.EmergencyRequest;
import com.dms.model.RescueTeam;
import com.dms.model.enums.RequestStatus;
import com.dms.repository.EmergencyRequestRepository;
import com.dms.repository.RescueTeamRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.RescueTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RescueTeamServiceImpl implements RescueTeamService {

    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ActivityLogService activityLogService;

    // OOP Polymorphic implementation of EmergencyDispatchable
    @Override
    @Transactional
    public String dispatch(Long responderId, Long requestId) {
        RescueTeam team = rescueTeamRepository.findById(responderId)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", responderId));

        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        team.markDispatched();
        rescueTeamRepository.save(team);

        request.assignToRescueTeam(team);
        emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                "System",
                "SYSTEM",
                "TEAM_DISPATCHED",
                "EmergencyRequest",
                request.getId(),
                "Dispatched rescue team '" + team.getTeamName() + "' to emergency request #" + request.getId()
        );

        return "Rescue Team '" + team.getTeamName() + "' dispatched to location: " + request.getLocationName();
    }

    @Override
    @Transactional
    public void updateOperationProgress(Long responderId, Long requestId, String progressNotes) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));
        request.setStatus(RequestStatus.IN_PROGRESS);
        if (progressNotes != null) {
            String current = request.getMedicalNotes() != null ? request.getMedicalNotes() + "\n" : "";
            request.setMedicalNotes(current + "Rescue Team Update: " + progressNotes);
        }
        emergencyRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void completeOperation(Long responderId, Long requestId) {
        RescueTeam team = rescueTeamRepository.findById(responderId)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", responderId));

        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        team.markAvailable();
        rescueTeamRepository.save(team);

        request.fulfill();
        emergencyRequestRepository.save(request);
    }

    @Override
    @Transactional
    public RescueTeam create(RescueTeam entity) {
        return rescueTeamRepository.save(entity);
    }

    @Override
    @Transactional
    public RescueTeam update(Long id, RescueTeam entity) {
        RescueTeam team = rescueTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", id));

        team.setTeamName(entity.getTeamName());
        team.setLeaderName(entity.getLeaderName());
        team.setContactNumber(entity.getContactNumber());
        team.setSpecialization(entity.getSpecialization());
        team.setMemberCount(entity.getMemberCount());
        team.setCurrentLocation(entity.getCurrentLocation());
        team.setLatitude(entity.getLatitude());
        team.setLongitude(entity.getLongitude());
        if (entity.getStatus() != null) {
            team.setStatus(entity.getStatus());
        }

        return rescueTeamRepository.save(team);
    }

    @Override
    public Optional<RescueTeam> getById(Long id) {
        return rescueTeamRepository.findById(id);
    }

    @Override
    public List<RescueTeam> getAll() {
        return rescueTeamRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RescueTeam team = rescueTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", id));
        rescueTeamRepository.delete(team);
    }

    @Override
    public List<RescueTeam> getAvailableTeams() {
        return rescueTeamRepository.findByStatus("AVAILABLE");
    }

    @Override
    @Transactional
    public RescueTeam updateStatus(Long id, String status) {
        RescueTeam team = rescueTeamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RescueTeam", "id", id));
        team.setStatus(status.toUpperCase());
        return rescueTeamRepository.save(team);
    }

    @Override
    public List<RescueTeam> searchBySpecialization(String specialization) {
        return rescueTeamRepository.findBySpecializationContainingIgnoreCase(specialization);
    }
}
