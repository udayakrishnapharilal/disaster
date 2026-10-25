package com.dms.exception;

/**
 * Base custom unchecked exception for the Disaster Management System.
 * Demonstrates OOP Exception Handling and Class Hierarchy.
 */
public class DisasterManagementException extends RuntimeException {

    public DisasterManagementException(String message) {
        super(message);
    }

    public DisasterManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
