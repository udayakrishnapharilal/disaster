package com.dms.model;

import com.dms.model.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Demonstrates OOP Inheritance and Polymorphism.
 * Administrator subclass specializing User with administrative authority,
 * department, and clearance level.
 */
@Entity
@Table(name = "administrators")
public class Administrator extends User {

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "admin_level")
    private Integer adminLevel = 1; // 1 = standard admin, 2 = super admin

    public Administrator() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Administrator(String username, String email, String password, String fullName, String phone, String department, Integer adminLevel) {
        super(username, email, password, fullName, phone, UserRole.ADMIN);
        this.department = department;
        this.adminLevel = adminLevel != null ? adminLevel : 1;
    }

    // OOP Polymorphism: Overridden abstract methods from User
    @Override
    public String getRoleDescription() {
        return "System Administrator with full operational clearance over disasters, resources, teams, and shelters.";
    }

    @Override
    public boolean canPerformAction(String action) {
        // Admins can execute all administrative actions
        return true;
    }

    // Encapsulation: Specific Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(Integer adminLevel) {
        this.adminLevel = adminLevel;
    }
}
