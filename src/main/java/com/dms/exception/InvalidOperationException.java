package com.dms.exception;

/**
 * Thrown when an operation is invalid for current entity lifecycle state.
 */
public class InvalidOperationException extends DisasterManagementException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
