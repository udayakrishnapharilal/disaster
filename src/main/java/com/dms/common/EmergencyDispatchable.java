package com.dms.common;

/**
 * Demonstrates OOP Abstraction and Polymorphism.
 * Implemented by service units that can respond to and be dispatched for emergency requests
 * (e.g., Rescue Teams and Volunteer Task forces).
 */
public interface EmergencyDispatchable {

    /**
     * Dispatch this unit to a specific emergency request.
     *
     * @param responderId ID of the responder unit (team or volunteer)
     * @param requestId   ID of the emergency request
     * @return Dispatch operation summary
     */
    String dispatch(Long responderId, Long requestId);

    /**
     * Report progress on an assigned operation.
     *
     * @param responderId ID of the responder
     * @param requestId   ID of the emergency request
     * @param progressNotes Status notes or progress update
     */
    void updateOperationProgress(Long responderId, Long requestId, String progressNotes);

    /**
     * Mark operation as completed.
     *
     * @param responderId ID of the responder
     * @param requestId   ID of the emergency request
     */
    void completeOperation(Long responderId, Long requestId);
}
