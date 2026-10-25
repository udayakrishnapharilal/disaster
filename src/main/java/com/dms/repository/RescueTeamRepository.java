package com.dms.repository;

import com.dms.model.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueTeamRepository extends JpaRepository<RescueTeam, Long> {
    Optional<RescueTeam> findByTeamName(String teamName);
    List<RescueTeam> findByStatus(String status);
    long countByStatus(String status);
    List<RescueTeam> findBySpecializationContainingIgnoreCase(String specialization);
}
