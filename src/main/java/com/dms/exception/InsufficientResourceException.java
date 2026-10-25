package com.dms.exception;

/**
 * Thrown when an allocation exceeds current relief stock.
 */
public class InsufficientResourceException extends DisasterManagementException {
    private final Long resourceId;
    private final int requestedAmount;
    private final int currentStock;

    public InsufficientResourceException(Long resourceId, int requestedAmount, int currentStock) {
        super(String.format("Insufficient stock for resource ID %d. Requested: %d, Currently in Stock: %d",
                resourceId, requestedAmount, currentStock));
        this.resourceId = resourceId;
        this.requestedAmount = requestedAmount;
        this.currentStock = currentStock;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public int getRequestedAmount() {
        return requestedAmount;
    }

    public int getCurrentStock() {
        return currentStock;
    }
}
