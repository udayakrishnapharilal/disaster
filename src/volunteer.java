public class Volunteer {
    private int id;
    private String name;
    private String phone;
    private String skill;
    private boolean available;

    public Volunteer(int id, String name,
                     String phone, String skill) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.skill = skill;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void displayDetails() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);
        System.out.println("Skill: " + skill);
        System.out.println("Available: " + available);
    }
}