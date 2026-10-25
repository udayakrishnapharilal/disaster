package com.dms.oop;

import com.dms.common.EmergencyDispatchable;
import com.dms.common.Manageable;
import com.dms.model.Administrator;
import com.dms.model.User;
import com.dms.model.Victim;
import com.dms.model.Volunteer;
import com.dms.model.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Object-Oriented Programming (OOP) Principles Verification Tests")
class OopPrinciplesTest {

    @Test
    @DisplayName("Verify Inheritance: Subclasses correctly inherit common User properties")
    void testInheritanceHierarchy() {
        Administrator admin = new Administrator("admin_user", "admin@test.com", "pass123", "Chief Admin", "+1-555-0001", "Crisis Management", 2);
        Victim victim = new Victim("victim_user", "victim@test.com", "pass123", "Victim One", "+1-555-0002", "Emergency Contact", 4, "Needs medication");
        Volunteer volunteer = new Volunteer("vol_user", "vol@test.com", "pass123", "Volunteer One", "+1-555-0003", "First Aid", 28.5, 77.2);

        // Verify that each subclass is an instance of the abstract User superclass
        assertTrue(admin instanceof User);
        assertTrue(victim instanceof User);
        assertTrue(volunteer instanceof User);

        // Inherited fields
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertEquals(UserRole.VICTIM, victim.getRole());
        assertEquals(UserRole.VOLUNTEER, volunteer.getRole());
    }

    @Test
    @DisplayName("Verify Polymorphism: Abstract methods behave differently across subclasses")
    void testPolymorphism() {
        List<User> users = new ArrayList<>();
        users.add(new Administrator("admin", "admin@dms.org", "pass", "Admin Vance", "+1", "Dept", 1));
        users.add(new Victim("victim", "victim@dms.org", "pass", "Victim Alice", "+1", "Contact", 2, "None"));
        users.add(new Volunteer("vol", "vol@dms.org", "pass", "Volunteer Bob", "+1", "Rescue", 28.0, 77.0));

        // Polymorphic invocation of getRoleDescription()
        for (User u : users) {
            assertNotNull(u.getRoleDescription());
            assertFalse(u.getRoleDescription().isBlank());
        }

        // Polymorphic action validation
        User adminUser = users.get(0);
        User victimUser = users.get(1);

        assertTrue(adminUser.canPerformAction("DELETE_DATABASE"));
        assertFalse(victimUser.canPerformAction("DELETE_DATABASE"));
        assertTrue(victimUser.canPerformAction("REPORT_DISASTER"));
    }

    @Test
    @DisplayName("Verify Abstraction: Interfaces define decoupled contracts")
    void testAbstractionContracts() {
        // Assert interface types exist and are contracts
        assertTrue(EmergencyDispatchable.class.isInterface());
        assertTrue(Manageable.class.isInterface());
    }

    @Test
    @DisplayName("Verify Encapsulation: Fields are private and accessed/validated via methods")
    void testEncapsulation() {
        Victim victim = new Victim();
        victim.setFamilyMembersCount(5);
        victim.setCurrentCondition("Critical");

        assertEquals(5, victim.getFamilyMembersCount());
        assertEquals("Critical", victim.getCurrentCondition());
    }
}
