package com.dms.service;

import com.dms.common.Manageable;
import com.dms.dto.ResourceAllocationDto;
import com.dms.dto.ResourceDto;

import java.util.List;

public interface ResourceService extends Manageable<ResourceDto, Long> {
    ResourceAllocationDto allocateResource(ResourceAllocationDto dto, String adminUsername);
    ResourceDto addStock(Long resourceId, int amount, String adminUsername);
    List<ResourceDto> getLowStockResources();
    List<ResourceAllocationDto> getAllocations();
    List<ResourceAllocationDto> getAllocationsForRequest(Long requestId);
}
