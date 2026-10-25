package com.dms.repository;

import com.dms.model.EmergencyRequest;
import com.dms.model.enums.RequestPriority;
import com.dms.model.enums.RequestStatus;
import com.dms.model.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findByStatus(RequestStatus status);
    List<EmergencyRequest> findByPriority(RequestPriority priority);
    List<EmergencyRequest> findByRequestType(RequestType requestType);
    List<EmergencyRequest> findByRequesterId(Long userId);
    List<EmergencyRequest> findByAssignedVolunteerId(Long volunteerId);
    List<EmergencyRequest> findByAssignedRescueTeamId(Long rescueTeamId);

    @Query("SELECT req FROM EmergencyRequest req WHERE " +
           "(:type IS NULL OR req.requestType = :type) AND " +
           "(:status IS NULL OR req.status = :status) AND " +
           "(:priority IS NULL OR req.priority = :priority) AND " +
           "(:query IS NULL OR LOWER(req.locationName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(req.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<EmergencyRequest> searchRequests(
            @Param("type") RequestType type,
            @Param("status") RequestStatus status,
            @Param("priority") RequestPriority priority,
            @Param("query") String query
    );

    @Query("SELECT r.status, COUNT(r) FROM EmergencyRequest r GROUP BY r.status")
    List<Object[]> countRequestsByStatus();

    long countByStatus(RequestStatus status);
}
