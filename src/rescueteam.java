import java.util.ArrayList;
import java.util.List;

public class RescueTeam {
    private int teamId;
    private String teamName;
    private List<Volunteer> volunteers = new ArrayList<>();
    private boolean available = true;
    private String assignedRequestId;

    public RescueTeam(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public void addVolunteer(Volunteer volunteer) {
        if (volunteer != null) {
            volunteers.add(volunteer);
        }
    }

    public boolean assignToRequest(String requestId) {
        if (!available || requestId == null
                || requestId.trim().isEmpty()) {
            return false;
        }

        assignedRequestId = requestId;
        available = false;

        for (Volunteer volunteer : volunteers) {
            volunteer.setAvailable(false);
        }

        return true;
    }

    public void completeRequest() {
        if (available) {
            return;
        }

        assignedRequestId = null;
        available = true;

        for (Volunteer volunteer : volunteers) {
            volunteer.setAvailable(true);
        }
    }

    public void displayTeam() {
        System.out.println("Team ID: " + teamId);
        System.out.println("Team: " + teamName);
        System.out.println("Available: " + available);
        System.out.println("Request: "
                + (assignedRequestId == null
                ? "None" : assignedRequestId));

        for (Volunteer volunteer : volunteers) {
            System.out.println("Volunteer: " + volunteer.getName());
        }
    }
}