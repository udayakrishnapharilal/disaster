package com.dms.service;

import com.dms.exception.ShelterFullException;
import com.dms.model.Shelter;
import com.dms.model.enums.ShelterStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shelter Domain Logic & Capacity Allocation Tests")
class ShelterServiceTest {

    private Shelter shelter;

    @BeforeEach
    void setUp() {
        shelter = new Shelter(
                "Community Relief Center",
                "123 Main St",
                "Metro City",
                28.6139,
                77.2090,
                100,
                "Coordinator Jane",
                "+1-555-0100",
                "Beds, Medical, Food"
        );
    }

    @Test
    @DisplayName("Should allocate capacity correctly when space is available")
    void testSuccessfulAllocation() {
        assertEquals(100, shelter.getRemainingCapacity());
        assertEquals(0, shelter.getCurrentOccupancy());

        shelter.allocate(40);

        assertEquals(40, shelter.getCurrentOccupancy());
        assertEquals(60, shelter.getRemainingCapacity());
        assertEquals(ShelterStatus.ACTIVE, shelter.getStatus());
    }

    @Test
    @DisplayName("Should throw ShelterFullException when requested allocation exceeds remaining beds")
    void testCapacityExceededThrowsException() {
        shelter.allocate(90);

        assertThrows(ShelterFullException.class, () -> {
            shelter.allocate(15); // Exceeds available 10 beds
        });
    }

    @Test
    @DisplayName("Should mark shelter status as FULL when reached max capacity")
    void testShelterStatusBecomesFull() {
        shelter.allocate(100);
        assertEquals(100, shelter.getCurrentOccupancy());
        assertEquals(0, shelter.getRemainingCapacity());
        assertEquals(ShelterStatus.FULL, shelter.getStatus());
    }

    @Test
    @DisplayName("Should release beds properly and reactivate status")
    void testReleaseBeds() {
        shelter.allocate(100);
        assertEquals(ShelterStatus.FULL, shelter.getStatus());

        shelter.release(30);
        assertEquals(70, shelter.getCurrentOccupancy());
        assertEquals(30, shelter.getRemainingCapacity());
        assertEquals(ShelterStatus.ACTIVE, shelter.getStatus());
    }
}
