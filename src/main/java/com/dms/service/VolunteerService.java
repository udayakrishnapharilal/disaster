package com.dms.service;

import com.dms.common.EmergencyDispatchable;
import com.dms.dto.UserSummaryDto;
import com.dms.model.Volunteer;

import java.util.List;
import java.util.Optional;

public interface VolunteerService extends EmergencyDispatchable {
    List<Volunteer> getAllVolunteers();
    Optional<Volunteer> getVolunteerById(Long id);
    Volunteer approveVolunteer(Long id, String adminUsername);
    Volunteer updateAvailability(Long id, String status);
    List<Volunteer> getAvailableVolunteers();
    List<Volunteer> searchBySkill(String skill);
    List<UserSummaryDto> getAllVolunteerSummaries();
}
