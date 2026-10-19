public class Main {
    public static void main(String[] args) {

        // Register a volunteer
        Volunteer v1 = new Volunteer(
            1, "Anu", "9876543210", "First Aid"
        );

        System.out.println("=== VOLUNTEER ===");
        v1.displayDetails();

        // Create a rescue team and add the volunteer
        RescueTeam team = new RescueTeam(
            101, "Rescue Team A"
        );
        team.addVolunteer(v1);

        System.out.println("\n=== RESCUE TEAM ===");
        team.displayTeam();

        // Track disaster supplies
        Resource resources = new Resource();
        resources.addSupply("Food", 100);
        resources.addSupply("Water", 200);
        resources.addSupply("Medicines", 50);

        System.out.println("\n=== RESOURCES ===");
        resources.displayResources();

        // Assign the team to a rescue request
        System.out.println("\n=== ASSIGNMENT ===");
        boolean assigned =
            team.assignToRequest("REQ-001");

        System.out.println(
            "Team assigned successfully: " + assigned
        );
        team.displayTeam();
    }
}