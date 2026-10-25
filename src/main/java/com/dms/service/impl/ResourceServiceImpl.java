package com.dms.service.impl;

import com.dms.dto.ResourceAllocationDto;
import com.dms.dto.ResourceDto;
import com.dms.exception.ResourceNotFoundException;
import com.dms.model.EmergencyRequest;
import com.dms.model.Resource;
import com.dms.model.ResourceAllocation;
import com.dms.model.Shelter;
import com.dms.repository.EmergencyRequestRepository;
import com.dms.repository.ResourceAllocationRepository;
import com.dms.repository.ResourceRepository;
import com.dms.repository.ShelterRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public ResourceDto create(ResourceDto dto) {
        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setType(dto.getType());
        resource.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);
        resource.setUnit(dto.getUnit());
        resource.setStorageLocation(dto.getStorageLocation());
        resource.setWarehouseCode(dto.getWarehouseCode());
        resource.setMinThreshold(dto.getMinThreshold() != null ? dto.getMinThreshold() : 50);
        resource.updateStatus();

        Resource saved = resourceRepository.save(resource);

        activityLogService.logActivity(
                "Admin",
                "ADMIN",
                "CREATE_RESOURCE",
                "Resource",
                saved.getId(),
                "Registered new relief inventory: " + saved.getName() + " (" + saved.getQuantity() + " " + saved.getUnit() + ")"
        );

        return toDto(saved);
    }

    @Override
    @Transactional
    public ResourceDto update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        resource.setName(dto.getName());
        resource.setType(dto.getType());
        resource.setQuantity(dto.getQuantity());
        resource.setUnit(dto.getUnit());
        resource.setStorageLocation(dto.getStorageLocation());
        resource.setWarehouseCode(dto.getWarehouseCode());
        resource.setMinThreshold(dto.getMinThreshold());
        resource.updateStatus();

        Resource updated = resourceRepository.save(resource);
        return toDto(updated);
    }

    @Override
    public Optional<ResourceDto> getById(Long id) {
        return resourceRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<ResourceDto> getAll() {
        return resourceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));
        resourceRepository.delete(resource);
    }

    @Override
    @Transactional
    public ResourceAllocationDto allocateResource(ResourceAllocationDto dto, String adminUsername) {
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", dto.getResourceId()));

        int qty = dto.getQuantityAllocated() != null ? dto.getQuantityAllocated() : 1;

        // OOP Domain Logic: deducts or throws InsufficientResourceException
        resource.deductStock(qty);
        resourceRepository.save(resource);

        EmergencyRequest emergencyRequest = null;
        if (dto.getEmergencyRequestId() != null) {
            emergencyRequest = emergencyRequestRepository.findById(dto.getEmergencyRequestId()).orElse(null);
        }

        Shelter shelter = null;
        if (dto.getShelterId() != null) {
            shelter = shelterRepository.findById(dto.getShelterId()).orElse(null);
        }

        ResourceAllocation allocation = new ResourceAllocation(
                resource,
                emergencyRequest,
                shelter,
                qty,
                adminUsername != null ? adminUsername : "Admin",
                dto.getRemarks()
        );

        ResourceAllocation saved = resourceAllocationRepository.save(allocation);

        String destination = shelter != null ? "Shelter: " + shelter.getName() :
                (emergencyRequest != null ? "Request #" + emergencyRequest.getId() : "General Relief");

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "ALLOCATE_RESOURCE",
                "ResourceAllocation",
                saved.getId(),
                "Distributed " + qty + " " + resource.getUnit() + " of " + resource.getName() + " to " + destination
        );

        return toAllocationDto(saved);
    }

    @Override
    @Transactional
    public ResourceDto addStock(Long resourceId, int amount, String adminUsername) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));

        resource.addStock(amount);
        Resource saved = resourceRepository.save(resource);

        activityLogService.logActivity(
                adminUsername != null ? adminUsername : "Admin",
                "ADMIN",
                "RESTOCK_RESOURCE",
                "Resource",
                saved.getId(),
                "Restocked " + amount + " " + resource.getUnit() + " of " + resource.getName()
        );

        return toDto(saved);
    }

    @Override
    public List<ResourceDto> getLowStockResources() {
        return resourceRepository.findLowStockResources().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceAllocationDto> getAllocations() {
        return resourceAllocationRepository.findAll().stream()
                .map(this::toAllocationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceAllocationDto> getAllocationsForRequest(Long requestId) {
        return resourceAllocationRepository.findByEmergencyRequestId(requestId).stream()
                .map(this::toAllocationDto)
                .collect(Collectors.toList());
    }

    private ResourceDto toDto(Resource r) {
        ResourceDto dto = new ResourceDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setType(r.getType());
        dto.setQuantity(r.getQuantity());
        dto.setUnit(r.getUnit());
        dto.setStorageLocation(r.getStorageLocation());
        dto.setWarehouseCode(r.getWarehouseCode());
        dto.setMinThreshold(r.getMinThreshold());
        dto.setStatus(r.getStatus());
        return dto;
    }

    private ResourceAllocationDto toAllocationDto(ResourceAllocation a) {
        ResourceAllocationDto dto = new ResourceAllocationDto();
        dto.setId(a.getId());
        dto.setResourceId(a.getResource().getId());
        dto.setResourceName(a.getResource().getName());
        if (a.getEmergencyRequest() != null) {
            dto.setEmergencyRequestId(a.getEmergencyRequest().getId());
        }
        if (a.getShelter() != null) {
            dto.setShelterId(a.getShelter().getId());
            dto.setShelterName(a.getShelter().getName());
        }
        dto.setQuantityAllocated(a.getQuantityAllocated());
        dto.setAllocatedBy(a.getAllocatedBy());
        dto.setAllocatedAt(a.getAllocatedAt());
        dto.setStatus(a.getStatus());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}
