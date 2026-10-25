package com.dms.repository;

import com.dms.model.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VictimRepository extends JpaRepository<Victim, Long> {
    List<Victim> findByAllocatedShelterIsNull();
    List<Victim> findByAllocatedShelterId(Long shelterId);
}
