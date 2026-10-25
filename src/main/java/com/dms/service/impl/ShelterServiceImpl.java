package com.dms.service.impl;

import com.dms.dto.ShelterAllocationDto;
import com.dms.dto.ShelterDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.Shelter;
import com.dms.model.ShelterAllocation;
import com.dms.model.Victim;
import com.dms.model.enums.ShelterStatus;
import com.dms.repository.ShelterAllocationRepository;
import com.dms.repository.ShelterRepository;
import com.dms.repository.VictimRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShelterServiceImpl implements ShelterService {

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private ShelterAllocationRepository shelterAllocationRepository;

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public ShelterDto create(ShelterDto dto) {
        Shelter shelter = new Shelter();
        shelter.setName(dto.getName());
        shelter.setAddress(dto.getAddress());
        shelter.setCity(dto.getCity());
        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());
        shelter.setTotalCapacity(dto.getTotalCapacity());
        shelter.setCurrentOccupancy(0);
        shelter.setContactPerson(dto.getContactPerson());
        shelter.setContactPhone(dto.getContactPhone());
        shelter.setFacilities(dto.getFacilities());
        shelter.setStatus(dto.getStatus() != null ? dto.getStatus() : ShelterStatus.ACTIVE);

        Shelter saved = shelterRepository.save(shelter);

        activityLogService.logActivity(
                "Admin",
                "ADMIN",
                "CREATE_SHELTER",
                "Shelter",
                saved.getId(),
                "Registered new shelter: " + saved.getName() + " with capacity " + saved.getTotalCapacity()
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public ShelterDto update(Long id, ShelterDto dto) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", "id", id));

        shelter.setName(dto.getName());
        shelter.setAddress(dto.getAddress());
        shelter.setCity(dto.getCity());
        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());
        shelter.setTotalCapacity(dto.getTotalCapacity());
        shelter.setContactPerson(dto.getContactPerson());
        shelter.setContactPhone(dto.getContactPhone());
        shelter.setFacilities(dto.getFacilities());
        if (dto.getStatus() != null) {
            shelter.setStatus(dto.getStatus());
        }

        Shelter updated = shelterRepository.save(shelter);
        return toDto(updated);
    }

    @Override
    public Optional<ShelterDto> getById(Long id) {
        return shelterRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<ShelterDto> getAll() {
        return shelterRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", "id", id));
        shelterRepository.delete(shelter);
    }

    @Override
    @Transactional
    public ShelterAllocationDto allocateVictims(ShelterAllocationDto dto, String adminUsername) {
        Shelter shelter = shelterRepository.findById(dto.getShelterId())
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", "id", dto.getShelterId()));

        Victim victim = victimRepository.findById(dto.getVictimId())
                .orElseThrow(() -> new ResourceNotFoundException("Victim", "id", dto.getVictimId()));

        int count = dto.getPeopleCount() != null ? dto.getPeopleCount() : 1;

        // OOP Domain logic: allocates or throws ShelterFullException if capacity exceeded
        shelter.allocate(count);
        shelterRepository.save(shelter);

        victim.setAllocatedShelter(shelter);
        victimRepository.save(victim);

        ShelterAllocation allocation = new ShelterAllocation(shelter, victim, count, dto.getNotes());
        ShelterAllocation savedAllocation = shelterAllocationRepository.save(allocation);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "ALLOCATE_SHELTER",
                "ShelterAllocation",
                savedAllocation.getId(),
                "Allocated " + count + " victims (Lead: " + victim.getFullName() + ") to " + shelter.getName()
        );

        return toAllocationDto(savedAllocation);
    }

    @Override
    @Transactional
    public ShelterAllocationDto dischargeVictims(Long allocationId, String adminUsername) {
        ShelterAllocation allocation = shelterAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("ShelterAllocation", "id", allocationId));

        if (!"ALLOCATED".equals(allocation.getStatus())) {
            return toAllocationDto(allocation);
        }

        Shelter shelter = allocation.getShelter();
        // Domain logic: release beds
        shelter.release(allocation.getPeopleCount());
        shelterRepository.save(shelter);

        Victim victim = allocation.getVictim();
        victim.setAllocatedShelter(null);
        victimRepository.save(victim);

        allocation.setStatus("DISCHARGED");
        allocation.setDischargedAt(LocalDateTime.now());
        ShelterAllocation saved = shelterAllocationRepository.save(allocation);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "DISCHARGE_SHELTER",
                "ShelterAllocation",
                saved.getId(),
                "Discharged " + allocation.getPeopleCount() + " people from shelter " + shelter.getName()
        );

        return toAllocationDto(saved);
    }

    @Override
    public List<ShelterDto> getAvailableShelters() {
        return shelterRepository.findAvailableShelters().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShelterAllocationDto> getAllocationsForShelter(Long shelterId) {
        return shelterAllocationRepository.findByShelterId(shelterId).stream()
                .map(this::toAllocationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShelterAllocationDto> getAllocationsForVictim(Long victimId) {
        return shelterAllocationRepository.findByVictimId(victimId).stream()
                .map(this::toAllocationDto)
                .collect(Collectors.toList());
    }

    private ShelterDto toDto(Shelter s) {
        ShelterDto dto = new ShelterDto();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setAddress(s.getAddress());
        dto.setCity(s.getCity());
        dto.setLatitude(s.getLatitude());
        dto.setLongitude(s.getLongitude());
        dto.setTotalCapacity(s.getTotalCapacity());
        dto.setCurrentOccupancy(s.getCurrentOccupancy());
        dto.setRemainingCapacity(s.getRemainingCapacity());
        dto.setOccupancyRate(s.getOccupancyRate());
        dto.setContactPerson(s.getContactPerson());
        dto.setContactPhone(s.getContactPhone());
        dto.setFacilities(s.getFacilities());
        dto.setStatus(s.getStatus());
        return dto;
    }

    private ShelterAllocationDto toAllocationDto(ShelterAllocation a) {
        ShelterAllocationDto dto = new ShelterAllocationDto();
        dto.setId(a.getId());
        dto.setShelterId(a.getShelter().getId());
        dto.setShelterName(a.getShelter().getName());
        dto.setVictimId(a.getVictim().getId());
        dto.setVictimName(a.getVictim().getFullName());
        dto.setPeopleCount(a.getPeopleCount());
        dto.setAllocatedAt(a.getAllocatedAt());
        dto.setDischargedAt(a.getDischargedAt());
        dto.setStatus(a.getStatus());
        dto.setNotes(a.getNotes());
        return dto;
    }
}
