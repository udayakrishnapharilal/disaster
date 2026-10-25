package com.dms.exception;

/**
 * Thrown when a user attempts an action not permitted by their role or state.
 */
public class UnauthorizedOperationException extends DisasterManagementException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
