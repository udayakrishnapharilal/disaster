import java.util.HashMap;
import java.util.Map;

public class Resource {
    private Map<String, Integer> supplies = new HashMap<>();

    public void addSupply(String type, int quantity) {
        if (type == null || type.trim().isEmpty()
                || quantity <= 0) {
            System.out.println("Invalid supply details.");
            return;
        }

        supplies.put(type,
                supplies.getOrDefault(type, 0) + quantity);
    }

    public boolean useSupply(String type, int quantity) {
        if (type == null || quantity <= 0) {
            return false;
        }

        int stock = supplies.getOrDefault(type, 0);

        if (stock < quantity) {
            System.out.println("Insufficient " + type);
            return false;
        }

        supplies.put(type, stock - quantity);
        return true;
    }

    public void displayResources() {
        System.out.println("--- Available Supplies ---");

        for (String type : supplies.keySet()) {
            System.out.println(type + ": " + supplies.get(type));
        }
    }
}