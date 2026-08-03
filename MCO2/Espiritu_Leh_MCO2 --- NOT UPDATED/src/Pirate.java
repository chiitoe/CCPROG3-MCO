/** Pirate
 * Purpose: Inherits from Character, hold all information for pirate characters
 */

public class Pirate extends Character { // Pirate Class derived from Character Class

    // Child attributes
    private int bounty; /** a pirate's individual bounty for thier life */
    private String pirateRole;  /** role within the crew */
    private boolean isCaptain;  /** toggled to determine if the pirate is the captain of their crew */
    private PirateCrew pirateCrew;  /** the crew the pirate is affiliated with */

    /** CONSTRUCTOR
     * Purpose: Creates a pirate.
     * @param name fall back to "Unknown" if null/blank.
     * @param alias fall back to "Unknown" if null/blank.
     * @param origin fall back to "Unknown" if null/blank.
     * @param wallet falls back to 0 if negative value.
     * @param bounty Value of the bounty of the pirate.
     * @param pirateRole Assings a pirate role.
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

    /** Getters */
    public int getBounty(){ return this.bounty; }
    public PirateCrew getPirateCrew(){ return this.pirateCrew; }
    public String getPirateRole(){ return this.pirateRole; }
    public boolean isCaptain(){ return this.isCaptain; }

    /** Setters */

    /** Purpose: sets the bounty of a pirate
     * @param bounty rejected if less than 0
     * */
    public void setBounty (int bounty){
        if (bounty >= 0){
            this.bounty = bounty;
        }
    }

    /** Purpose: assign a pirate to a crew
     * @param pirateCrew rejected if null
     * @return true if successful, false otherwise
     * */
    public boolean assignCrew(PirateCrew pirateCrew){
        if(pirateCrew != null){
            this.pirateCrew = pirateCrew;
            return true;
        }
        return false;
    }

    /** Purpose:removes a pirate from a crew */
    public void removeCrew(){ this.pirateCrew = null; }

    /** Purpose: sets a pirate's role within the crew
     * @param newRole
     * */
    public void setPirateRole(String newRole){
        this.pirateRole = (newRole != null && !newRole.isBlank()) ? newRole:"Unassigned";
    }

    /** Purpose: toggles the captain attribute in a pirate character and updates their role as needed
     * @param state true indicates a toggle on, false indicates a toggle off
     * @return true if successful, false otherwise
     * */
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

    /** Purpose: unique flavour text for pirate-related duties */
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