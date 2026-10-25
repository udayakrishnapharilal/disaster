package com.dms.controller;

import com.dms.common.ApiResponse;
import com.dms.dto.ResourceAllocationDto;
import com.dms.dto.ResourceDto;
import com.dms.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceDto>>> getAllResources() {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getAll()));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ResourceDto>>> getLowStockResources() {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getLowStockResources()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceDto>> getResourceById(@PathVariable Long id) {
        return resourceService.getById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.success(r)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Resource not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResourceDto>> createResource(@Valid @RequestBody ResourceDto dto) {
        ResourceDto created = resourceService.create(dto);
        return new ResponseEntity<>(ApiResponse.success("Resource created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceDto>> updateResource(@PathVariable Long id, @Valid @RequestBody ResourceDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Resource updated", resourceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted", null));
    }

    @PostMapping("/allocate")
    public ResponseEntity<ApiResponse<ResourceAllocationDto>> allocateResource(
            @Valid @RequestBody ResourceAllocationDto dto,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        ResourceAllocationDto allocated = resourceService.allocateResource(dto, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Resource allocated successfully", allocated));
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<ApiResponse<ResourceDto>> restockResource(
            @PathVariable Long id,
            @RequestParam int amount,
            Authentication authentication) {
        String adminUsername = authentication != null ? authentication.getName() : "admin";
        ResourceDto restocked = resourceService.addStock(id, amount, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Resource restocked successfully", restocked));
    }

    @GetMapping("/allocations")
    public ResponseEntity<ApiResponse<List<ResourceAllocationDto>>> getAllocations() {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getAllocations()));
    }

    @GetMapping("/request/{requestId}/allocations")
    public ResponseEntity<ApiResponse<List<ResourceAllocationDto>>> getRequestAllocations(@PathVariable Long requestId) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getAllocationsForRequest(requestId)));
    }
}
