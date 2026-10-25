package com.dms.model;

import com.dms.model.enums.UserRole;
import jakarta.persistence.*;

/**
 * Demonstrates OOP Inheritance and Polymorphism.
 * Victim subclass representing impacted individuals or general citizens
 * reporting disasters and requesting emergency aid.
 */
@Entity
@Table(name = "victims")
public class Victim extends User {

    @Column(name = "current_condition", length = 100)
    private String currentCondition = "Safe"; // Safe, Injured, Critical, Displaced

    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    @Column(name = "medical_needs", columnDefinition = "TEXT")
    private String medicalNeeds;

    @Column(name = "family_members_count")
    private Integer familyMembersCount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_shelter_id")
    private Shelter allocatedShelter;

    public Victim() {
        super();
        setRole(UserRole.VICTIM);
    }

    public Victim(String username, String email, String password, String fullName, String phone,
                  String emergencyContact, Integer familyMembersCount, String medicalNeeds) {
        super(username, email, password, fullName, phone, UserRole.VICTIM);
        this.emergencyContact = emergencyContact;
        this.familyMembersCount = familyMembersCount != null ? familyMembersCount : 1;
        this.medicalNeeds = medicalNeeds;
    }

    // OOP Polymorphism: Overridden abstract methods from User
    @Override
    public String getRoleDescription() {
        return "Disaster victim or general citizen who can report incidents and request emergency relief/shelter.";
    }

    @Override
    public boolean canPerformAction(String action) {
        if (action == null) return false;
        return switch (action.toUpperCase()) {
            case "REPORT_DISASTER", "REQUEST_EMERGENCY_AID", "VIEW_SHELTERS", "VIEW_RESOURCES", "VIEW_OWN_REQUESTS" -> true;
            default -> false;
        };
    }

    // Encapsulation
    public String getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(String currentCondition) {
        this.currentCondition = currentCondition;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalNeeds() {
        return medicalNeeds;
    }

    public void setMedicalNeeds(String medicalNeeds) {
        this.medicalNeeds = medicalNeeds;
    }

    public Integer getFamilyMembersCount() {
        return familyMembersCount;
    }

    public void setFamilyMembersCount(Integer familyMembersCount) {
        this.familyMembersCount = familyMembersCount;
    }

    public Shelter getAllocatedShelter() {
        return allocatedShelter;
    }

    public void setAllocatedShelter(Shelter allocatedShelter) {
        this.allocatedShelter = allocatedShelter;
    }
}
