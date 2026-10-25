package com.dms.repository;

import com.dms.model.Shelter;
import com.dms.model.enums.ShelterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    List<Shelter> findByStatus(ShelterStatus status);
    List<Shelter> findByCityIgnoreCase(String city);

    @Query("SELECT s FROM Shelter s WHERE s.totalCapacity > s.currentOccupancy AND s.status = 'ACTIVE'")
    List<Shelter> findAvailableShelters();

    @Query("SELECT SUM(s.totalCapacity) FROM Shelter s")
    Long sumTotalCapacity();

    @Query("SELECT SUM(s.currentOccupancy) FROM Shelter s")
    Long sumTotalOccupancy();
}
