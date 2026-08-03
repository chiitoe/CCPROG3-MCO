import java.util.ArrayList;

/** PirateCrew
 * Purpose: Manages a pirate crew and its members.
 */
public final class PirateCrew {

    private static int autoID = 1;   // Counter independent of Character's
    private final int crewID;
    private String crewName;
    private String shipName;
    private Pirate captain;
    private final ArrayList<Pirate> crewMembers;

    /** CONSTRUCTOR
     * Purpose: Creates a new crew with a generated ID and enrols its captain.
     * @param crewName falls back to "Unnamed Crew" if null/blank
     * @param shipName falls back to "Unnamed Ship" if null/blank
     * @param captain the founding captain
     * @throws IllegalArgumentException if captain is null or already sails with another crew
     */
    public PirateCrew(String crewName, String shipName, Pirate captain){
        if(captain == null){
            throw new IllegalArgumentException("A crew cannot be founded without a captain.");
        }
        if(captain.getPirateCrew() != null){
            throw new IllegalArgumentException(captain.getName() + " already sails with another crew.");
        }

        this.crewID = autoID++;
        this.crewName = (crewName != null && !crewName.isBlank()) ? crewName : "Unnamed Crew";
        this.shipName = (shipName != null && !shipName.isBlank()) ? shipName : "Unnamed Ship";
        this.crewMembers = new ArrayList<>();

        // Enrol the captain properly so both sides of the link agree
        this.crewMembers.add(captain);
        captain.assignCrew(this);
        captain.toggleCaptain(true);
        this.captain = captain;
    }

    // Setters
    /**
     * Purpose: Renames the crew.
     * @param crewName ignored if null/blank
     */
    public void setCrewName(String crewName){
        if(crewName != null && !crewName.isBlank()) this.crewName = crewName;
    }

    /**
     * Purpose: Renames the ship.
     * @param shipName ignored if null/blank
     */
    public void setShipName(String shipName){
        if(shipName != null && !shipName.isBlank()) this.shipName = shipName;
    }

    /**
     * Purpose: Hands captaincy to another member, demoting the incumbent.
     * @param captain rejected if null or not already a member of this crew
     * @return true if the handover succeeded, false otherwise
     */
    public boolean setCaptain(Pirate captain){
        if(captain != null && crewMembers.contains(captain)){
            if(this.captain != null){ this.captain.toggleCaptain(false); }

            captain.toggleCaptain(true);
            this.captain = captain;
            return true;
        }
        return false;
    }

    // Getters
    /** @return this crew's immutable auto-generated ID */
    public int getCrewID(){ return this.crewID; }
    /** @return this crew's name */
    public String getCrewName(){ return this.crewName; }
    /** @return this crew's ship name */
    public String getShipName(){ return this.shipName; }
    /** @return the current captain, or null if the seat is vacant */
    public Pirate getCaptain(){ return this.captain; }
    /** @return a defensive copy of the member list */
    public ArrayList<Pirate> getCrewMembers(){
        return new ArrayList<>(this.crewMembers);
    }

    /**
     * Purpose: Recruits a new pirate into the crew.
     * @param pirate rejected if null or already affiliated with a crew
     * @return true if recruited, false otherwise
     */
    public boolean recruitMember(Pirate pirate){
        if(pirate == null) return false;
        if(pirate.getPirateCrew() != null){
            System.out.println("Error: " + pirate.getAlias() + " is already in a crew.");
            return false;
        }
        crewMembers.add(pirate);
        pirate.assignCrew(this);
        return true;
    }

    /**
     * Purpose: Removes a pirate from the crew and clears their crew reference.
     * Vacates the captain's seat if the departing pirate held it.
     * @param pirate rejected if null or not a member
     * @return true if removed, false otherwise
     */
    public boolean goodbyeMember(Pirate pirate){
        if(pirate == null || !crewMembers.contains(pirate)) return false;

        if(pirate == this.captain){ this.captain = null; }
        crewMembers.remove(pirate);
        pirate.removeCrew();
        return true;
    }

    /**
     * Purpose: Sums the bounties of members whose status is FREE.
     * Captured and dead members contribute nothing, which is how bounty
     * deduction after a capture is handled implicitly.
     * @return the crew's combined active bounty in Berries
     */
    public int getTotalBounty(){
        int total = 0;
        for(Pirate pirate : crewMembers){
            if(pirate.getStatus() == Status.FREE){
                total += pirate.getBounty();
            }
        }
        return total;
    }

    /** Purpose: Prints the full profile of every crew member. */
    public void viewCrew(){
        System.out.println("Members of " + crewName + ":");
        if(crewMembers.isEmpty()){
            System.out.println("  (no members)");
            return;
        }
        for(Pirate pirate : crewMembers){ pirate.displayProfile(); }
    }

    /** Purpose: Displays a summary of this crew's information. */
    public void displayPirateInfo(){
        System.out.println("=== Pirate Crew: " + crewName + " ===");
        System.out.println("Crew ID       : " + crewID);
        System.out.println("Ship's Name   : " + shipName);
        System.out.println("Captain       : " + ((captain != null) ? captain.getName() : "Vacant"));
        System.out.println("Crew Size     : " + crewMembers.size());
        System.out.println("Total Bounty  : " + getTotalBounty() + " Berries");
    }

    /**
     * Purpose: Builds a single-line, pipe-delimited record of this crew for file storage.
     * @return one CSV-style line, no trailing newline
     */
    public String toRecord(){
        return "CREW|" + crewID + "|" + crewName + "|" + shipName + "|"
                + ((captain != null) ? captain.getName() : "Vacant") + "|"
                + crewMembers.size() + "|" + getTotalBounty();
    }
}