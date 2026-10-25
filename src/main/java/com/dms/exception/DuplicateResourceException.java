package com.dms.exception;

/**
 * Thrown when attempting to register duplicate unique entities (e.g., username, email, code).
 */
public class DuplicateResourceException extends DisasterManagementException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
