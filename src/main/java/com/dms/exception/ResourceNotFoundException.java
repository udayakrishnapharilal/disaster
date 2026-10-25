package com.dms.exception;

/**
 * Thrown when an entity with a specific ID is not found in the database.
 */
public class ResourceNotFoundException extends DisasterManagementException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
