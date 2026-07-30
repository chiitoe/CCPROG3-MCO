public class Pirate extends Character { // Pirate Class derived from Character Class

    // Child attributes
    private int bounty;
    private String pirateRole;
    private boolean isCaptain;
    private PirateCrew pirateCrew;

    /* CONSTRUCTOR
        Purpose: Creates a pirate.
        @ bounty: Value of the bounty of the pirate.
        @ piraterole: Assings a pirate role.
    */
    public Pirate(String name, String alias, String origin, int wallet, int bounty, String pirateRole) {
        super(name, alias, origin, wallet);

        this.pirateRole = (pirateRole != null && !pirateRole.isBlank()) ? pirateRole : "Unassigned";
        this.isCaptain = false;

        if (bounty >= 0) {
            this.bounty = bounty;
        } else {
            this.bounty = 0;
        }
    }

    // Getters
    public int getBounty(){ return this.bounty; }
    public PirateCrew getPirateCrew(){ return this.pirateCrew; }
    public String getPirateRole(){ return this.pirateRole; }
    public boolean isCaptain(){ return this.isCaptain; }

    // Setters
    public void setBounty (int bounty){
        if (bounty >= 0){
            this.bounty = bounty;
        }
    }
    public boolean assignCrew(PirateCrew pirateCrew){
        if(pirateCrew != null){
            this.pirateCrew = pirateCrew;
            return true;
        }
        return false;
    }

    public void removeCrew(){ this.pirateCrew = null; }

    public void setPirateRole(String newRole){
        this.pirateRole = (newRole != null && !newRole.isBlank()) ? newRole:"Unassigned";
    }

    // Parameter: true == become captain; false == demote from captain
    public boolean toggleCaptain(boolean state){
        // Toggle on
        if (this.pirateCrew != null && state == true){
            this.isCaptain = true;
            this.pirateRole = "Captain";
            return true;
        }

        // Toggle off
        else if (this.pirateCrew != null && state == false){
            this.isCaptain = false;
            this.pirateRole = "Unassigned";
            return true;
        }

        return false;
    }

    @Override
    public void performDuty() {
        switch(this.pirateRole){
            case "Unassigned" -> System.out.println("I AM READY!");
            case "Captain" -> System.out.println("LET'S GET THIS SHIP SAILING!");
            case "Navigator" -> System.out.println("ONWARDS TO THE LAND!");
            case "Cook" -> System.out.println("MMMMM! THIS IS DELICIOUS!");
            case "Doctor" -> System.out.println("WHO NEEDS HEALING?");
            case "Janitor" -> System.out.println("THIS PLACE NEEDS CLEANING.");
            default -> System.out.println("Performing my " + this.pirateRole + " duties...");
        }
    }
}
