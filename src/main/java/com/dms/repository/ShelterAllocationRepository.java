package com.dms.repository;

import com.dms.model.ShelterAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterAllocationRepository extends JpaRepository<ShelterAllocation, Long> {
    List<ShelterAllocation> findByShelterId(Long shelterId);
    List<ShelterAllocation> findByVictimId(Long victimId);
    List<ShelterAllocation> findByStatus(String status);
}
