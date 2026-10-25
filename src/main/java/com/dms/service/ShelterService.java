package com.dms.service;

import com.dms.common.Manageable;
import com.dms.dto.ShelterAllocationDto;
import com.dms.dto.ShelterDto;

import java.util.List;

public interface ShelterService extends Manageable<ShelterDto, Long> {
    ShelterAllocationDto allocateVictims(ShelterAllocationDto dto, String adminUsername);
    ShelterAllocationDto dischargeVictims(Long allocationId, String adminUsername);
    List<ShelterDto> getAvailableShelters();
    List<ShelterAllocationDto> getAllocationsForShelter(Long shelterId);
    List<ShelterAllocationDto> getAllocationsForVictim(Long victimId);
}
