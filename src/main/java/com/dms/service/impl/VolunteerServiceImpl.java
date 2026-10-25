package com.dms.service.impl;

import com.dms.dto.UserSummaryDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.EmergencyRequest;
import com.dms.model.Volunteer;
import com.dms.model.enums.RequestStatus;
import com.dms.repository.EmergencyRequestRepository;
import com.dms.repository.VolunteerRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ActivityLogService activityLogService;

    // OOP Polymorphic implementation of EmergencyDispatchable
    @Override
    @Transactional
    public String dispatch(Long volunteerId, Long requestId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", volunteerId));

        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        volunteer.setAvailabilityStatus("BUSY");
        volunteer.setAssignedTasksCount(volunteer.getAssignedTasksCount() + 1);
        volunteerRepository.save(volunteer);

        request.assignToVolunteer(volunteer);
        emergencyRequestRepository.save(request);

        activityLogService.logActivity(
                "System",
                "SYSTEM",
                "VOLUNTEER_DISPATCHED",
                "EmergencyRequest",
                request.getId(),
                "Dispatched volunteer " + volunteer.getFullName() + " to emergency request #" + request.getId()
        );

        return "Volunteer " + volunteer.getFullName() + " dispatched successfully to " + request.getLocationName();
    }

    @Override
    @Transactional
    public void updateOperationProgress(Long volunteerId, Long requestId, String progressNotes) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));
        request.setStatus(RequestStatus.IN_PROGRESS);
        if (progressNotes != null) {
            String current = request.getMedicalNotes() != null ? request.getMedicalNotes() + "\n" : "";
            request.setMedicalNotes(current + "Volunteer Update: " + progressNotes);
        }
        emergencyRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void completeOperation(Long volunteerId, Long requestId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", volunteerId));

        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyRequest", "id", requestId));

        volunteer.setAvailabilityStatus("AVAILABLE");
        volunteerRepository.save(volunteer);

        request.fulfill();
        emergencyRequestRepository.save(request);
    }

    @Override
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    @Override
    public Optional<Volunteer> getVolunteerById(Long id) {
        return volunteerRepository.findById(id);
    }

    @Override
    @Transactional
    public Volunteer approveVolunteer(Long id, String adminUsername) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", id));
        volunteer.setApprovedByAdmin(true);
        Volunteer saved = volunteerRepository.save(volunteer);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "APPROVE_VOLUNTEER",
                "Volunteer",
                saved.getId(),
                "Approved volunteer registration for: " + saved.getFullName()
        );

        return saved;
    }

    @Override
    @Transactional
    public Volunteer updateAvailability(Long id, String status) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", id));
        volunteer.setAvailabilityStatus(status.toUpperCase());
        return volunteerRepository.save(volunteer);
    }

    @Override
    public List<Volunteer> getAvailableVolunteers() {
        return volunteerRepository.findByAvailabilityStatus("AVAILABLE");
    }

    @Override
    public List<Volunteer> searchBySkill(String skill) {
        return volunteerRepository.findBySkillsContainingIgnoreCase(skill);
    }

    @Override
    public List<UserSummaryDto> getAllVolunteerSummaries() {
        return volunteerRepository.findAll().stream()
                .map(v -> new UserSummaryDto(
                        v.getId(),
                        v.getUsername(),
                        v.getEmail(),
                        v.getFullName(),
                        v.getPhone(),
                        v.getRole(),
                        v.getStatus(),
                        "Skills: " + v.getSkills() + " | Availability: " + v.getAvailabilityStatus() + " | Approved: " + v.getApprovedByAdmin()
                ))
                .collect(Collectors.toList());
    }
}
