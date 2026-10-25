package com.dms.repository;

import com.dms.model.DisasterReport;
import com.dms.model.enums.DisasterSeverity;
import com.dms.model.enums.DisasterStatus;
import com.dms.model.enums.DisasterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisasterReportRepository extends JpaRepository<DisasterReport, Long> {
    List<DisasterReport> findByStatus(DisasterStatus status);
    List<DisasterReport> findByDisasterType(DisasterType disasterType);
    List<DisasterReport> findBySeverity(DisasterSeverity severity);
    List<DisasterReport> findByReportedById(Long userId);

    @Query("SELECT r FROM DisasterReport r WHERE " +
           "(:type IS NULL OR r.disasterType = :type) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:severity IS NULL OR r.severity = :severity) AND " +
           "(:query IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.locationName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<DisasterReport> searchReports(
            @Param("type") DisasterType type,
            @Param("status") DisasterStatus status,
            @Param("severity") DisasterSeverity severity,
            @Param("query") String query
    );

    @Query("SELECT r.disasterType, COUNT(r) FROM DisasterReport r GROUP BY r.disasterType")
    List<Object[]> countReportsByType();
}
