package com.dms.repository;

import com.dms.model.Resource;
import com.dms.model.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByType(ResourceType type);
    List<Resource> findByStatus(String status);

    @Query("SELECT r FROM Resource r WHERE r.quantity <= r.minThreshold")
    List<Resource> findLowStockResources();

    @Query("SELECT r.type, SUM(r.quantity) FROM Resource r GROUP BY r.type")
    List<Object[]> sumQuantitiesByType();

    @Query("SELECT SUM(r.quantity) FROM Resource r")
    Long sumTotalInventoryQuantity();
}
