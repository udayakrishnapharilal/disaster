package com.dms.repository;

import com.dms.model.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {
    List<ResourceAllocation> findByResourceId(Long resourceId);
    List<ResourceAllocation> findByEmergencyRequestId(Long emergencyRequestId);
    List<ResourceAllocation> findByShelterId(Long shelterId);
}
