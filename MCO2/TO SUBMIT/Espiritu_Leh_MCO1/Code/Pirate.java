/** Pirate
 * Purpose: Inherits from Character, holds all information for pirate characters.
 */
public class Pirate extends Character {

    private int bounty;
    private String pirateRole;
    private boolean isCaptain;
    private PirateCrew pirateCrew;

    /** CONSTRUCTOR
     * Purpose: Creates a pirate.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param bounty falls back to 0 if negative
     * @param pirateRole falls back to "Unassigned" if null/blank
     */
    public Pirate(String name, String alias, String origin, int wallet, int bounty, String pirateRole){
        super(name, alias, origin, wallet);

        this.pirateRole = (pirateRole != null && !pirateRole.isBlank()) ? pirateRole : "Unassigned";
        this.isCaptain = false;
        this.pirateCrew = null;
        this.bounty = (bounty >= 0) ? bounty : 0;
    }

    // Getters
    /** @return this pirate's current bounty in Berries */
    public int getBounty(){ return this.bounty; }
    /** @return the crew this pirate belongs to, or null if unaffiliated */
    public PirateCrew getPirateCrew(){ return this.pirateCrew; }
    /** @return this pirate's role aboard the ship */
    public String getPirateRole(){ return this.pirateRole; }
    /** @return true if this pirate captains their crew */
    public boolean isCaptain(){ return this.isCaptain; }

    // Setters
    /**
     * Purpose: Updates this pirate's bounty.
     * @param bounty ignored if negative
     */
    public void setBounty(int bounty){
        if(bounty >= 0){
            this.bounty = bounty;
        }
    }

    /**
     * Purpose: Links this pirate to a crew.
     * Contract: only PirateCrew.recruitMember should call this, so the two-way link stays consistent.
     * @param pirateCrew rejected if null or if this pirate is already affiliated
     * @return true if the link was set, false otherwise
     */
    public boolean assignCrew(PirateCrew pirateCrew){
        if(pirateCrew != null && this.pirateCrew == null){
            this.pirateCrew = pirateCrew;
            return true;
        }
        return false;
    }

    /** Purpose: Clears this pirate's crew reference and any captaincy that came with it. */
    public void removeCrew(){
        this.isCaptain = false;
        this.pirateCrew = null;
    }

    /**
     * Purpose: Changes this pirate's shipboard role.
     * @param newRole falls back to "Unassigned" if null/blank
     */
    public void setPirateRole(String newRole){
        this.pirateRole = (newRole != null && !newRole.isBlank()) ? newRole : "Unassigned";
    }

    /**
     * Purpose: Promotes or demotes this pirate as captain of their crew.
     * @param state true to become captain, false to step down
     * @return true if the toggle applied, false if this pirate has no crew
     */
    public boolean toggleCaptain(boolean state){
        if(this.pirateCrew == null){
            return false;
        }

        this.isCaptain = state;
        this.pirateRole = state ? "Captain" : "Unassigned";
        return true;
    }

    /** Purpose: Prints role-specific text for this pirate. */
    @Override
    public void performDuty(){
        switch(this.pirateRole){
            case "Unassigned" -> System.out.println("What are we doing here?");
            case "Captain" -> System.out.println("Let's get sailing!");
            case "Navigator" -> System.out.println("Onwards! Follow the map.");
            case "Cook" -> System.out.println("Some delicious stuff I'm cooking here.");
            case "Doctor" -> System.out.println("Need healing?");
            case "Janitor" -> System.out.println("*Whistling* Just cleaning my stuff");
            default -> System.out.println("Performing my " + this.pirateRole + " duties...");
        }
    }
}