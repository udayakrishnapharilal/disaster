package com.dms.service;

import com.dms.common.EmergencyDispatchable;
import com.dms.common.Manageable;
import com.dms.model.RescueTeam;

import java.util.List;

public interface RescueTeamService extends Manageable<RescueTeam, Long>, EmergencyDispatchable {
    List<RescueTeam> getAvailableTeams();
    RescueTeam updateStatus(Long id, String status);
    List<RescueTeam> searchBySpecialization(String specialization);
}
