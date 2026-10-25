package com.dms.repository;

import com.dms.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    List<Volunteer> findByApprovedByAdminTrue();
    List<Volunteer> findByApprovedByAdminFalse();
    List<Volunteer> findByAvailabilityStatus(String availabilityStatus);
    long countByAvailabilityStatus(String availabilityStatus);
    List<Volunteer> findBySkillsContainingIgnoreCase(String skill);
}
