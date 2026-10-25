package com.dms.model.enums;

public enum UserRole {
    ADMIN("Administrator"),
    VICTIM("Victim / General User"),
    VOLUNTEER("Volunteer"),
    RESCUE_TEAM("Rescue Team");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
