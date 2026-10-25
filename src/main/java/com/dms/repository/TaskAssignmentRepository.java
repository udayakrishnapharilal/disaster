package com.dms.repository;

import com.dms.model.TaskAssignment;
import com.dms.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByAssignedVolunteerId(Long volunteerId);
    List<TaskAssignment> findByAssignedRescueTeamId(Long rescueTeamId);
    List<TaskAssignment> findByStatus(TaskStatus status);
}
