package com.dms.exception;

/**
 * Thrown when an attempt is made to allocate victims to a shelter that has reached full capacity.
 */
public class ShelterFullException extends DisasterManagementException {
    private final Long shelterId;
    private final int requestedCapacity;
    private final int availableCapacity;

    public ShelterFullException(Long shelterId, int requestedCapacity, int availableCapacity) {
        super(String.format("Shelter with ID %d is at maximum capacity. Requested: %d, Available: %d",
                shelterId, requestedCapacity, availableCapacity));
        this.shelterId = shelterId;
        this.requestedCapacity = requestedCapacity;
        this.availableCapacity = availableCapacity;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public int getRequestedCapacity() {
        return requestedCapacity;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }
}
