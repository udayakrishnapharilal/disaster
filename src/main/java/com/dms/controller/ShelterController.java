package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.ShelterAllocationDto;
import com.dms.dto.ShelterDto;
import com.dms.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShelterDto>>> getAllShelters() {
        return ResponseEntity.ok(ApiResponse.success(shelterService.getAll()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ShelterDto>>> getAvailableShelters() {
        return ResponseEntity.ok(ApiResponse.success(shelterService.getAvailableShelters()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShelterDto>> getShelterById(@PathVariable Long id) {
        return shelterService.getById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Shelter not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShelterDto>> createShelter(@Valid @RequestBody ShelterDto dto) {
        ShelterDto created = shelterService.create(dto);
        return new ResponseEntity<>(ApiResponse.success("Shelter registered successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShelterDto>> updateShelter(
            @PathVariable Long id,
            @Valid @RequestBody ShelterDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Shelter updated", shelterService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShelter(@PathVariable Long id) {
        shelterService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Shelter deleted", null));
    }

    @PostMapping("/allocate")
    public ResponseEntity<ApiResponse<ShelterAllocationDto>> allocateVictims(
            @Valid @RequestBody ShelterAllocationDto dto,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        ShelterAllocationDto allocation = shelterService.allocateVictims(dto, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Victims allocated to shelter successfully", allocation));
    }

    @PostMapping("/discharge/{allocationId}")
    public ResponseEntity<ApiResponse<ShelterAllocationDto>> dischargeVictims(
            @PathVariable Long allocationId,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        ShelterAllocationDto discharged = shelterService.dischargeVictims(allocationId, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Victims discharged and shelter beds released", discharged));
    }

    @GetMapping("/{id}/allocations")
    public ResponseEntity<ApiResponse<List<ShelterAllocationDto>>> getShelterAllocations(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(shelterService.getAllocationsForShelter(id)));
    }

    @GetMapping("/victim/{victimId}/allocations")
    public ResponseEntity<ApiResponse<List<ShelterAllocationDto>>> getVictimAllocations(@PathVariable Long victimId) {
        return ResponseEntity.ok(ApiResponse.success(shelterService.getAllocationsForVictim(victimId)));
    }
}
