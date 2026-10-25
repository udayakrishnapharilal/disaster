package com.dms.service;

import com.dms.exception.InsufficientResourceException;
import com.dms.model.Resource;
import com.dms.model.enums.ResourceType;
import com.dms.model.enums.ResourceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Resource Inventory & Stock Protection Tests")
class ResourceServiceTest {

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = new Resource(
                "Drinking Water Packets",
                ResourceType.WATER,
                500,
                ResourceUnit.PACKETS,
                "Warehouse Bay 2",
                "WH-02",
                50
        );
    }

    @Test
    @DisplayName("Should deduct stock accurately when stock is sufficient")
    void testSuccessfulStockDeduction() {
        assertEquals(500, resource.getQuantity());

        resource.deductStock(150);

        assertEquals(350, resource.getQuantity());
        assertEquals("IN_STOCK", resource.getStatus());
        assertFalse(resource.isLowStock());
    }

    @Test
    @DisplayName("Should throw InsufficientResourceException when requested amount exceeds current inventory")
    void testOverAllocationThrowsException() {
        assertThrows(InsufficientResourceException.class, () -> {
            resource.deductStock(550);
        });
    }

    @Test
    @DisplayName("Should transition status to LOW_STOCK when quantity falls below threshold")
    void testLowStockStatusTransition() {
        resource.deductStock(460); // Leaves 40, threshold is 50

        assertEquals(40, resource.getQuantity());
        assertEquals("LOW_STOCK", resource.getStatus());
        assertTrue(resource.isLowStock());
    }

    @Test
    @DisplayName("Should transition status to OUT_OF_STOCK when inventory drops to zero")
    void testOutOfStockStatusTransition() {
        resource.deductStock(500);

        assertEquals(0, resource.getQuantity());
        assertEquals("OUT_OF_STOCK", resource.getStatus());
    }
}
