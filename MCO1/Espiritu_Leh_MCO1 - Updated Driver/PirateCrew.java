import java.util.ArrayList;

// Module 2 - Pirate Crew Class: Pirate Recruitment
public class PirateCrew {

    // Attributes
    private static int autoID = 1;   // Same auto-ID as Character
    private final int crewID;
    private String crewName;
    private String shipName;
    private Pirate captain;
    private ArrayList<Pirate> crewMembers;


    /* CONSTRUCTOR
        Purpose: Creates a new crew with a generated ID specific to it.
        @ crewName, shipName, captainName: fall back to defaults if null/blank.
    */
    public PirateCrew(String crewName, String shipName, Pirate captain){
        this.crewID = autoID++;
        this.crewName = (crewName != null && !crewName.isBlank()) ? crewName : "Unnamed crew";
        this.shipName = (shipName != null && !shipName.isBlank()) ? shipName : "Unnamed Ship";
        this.captain = captain; // A crew cannot be created without a captain
        this.crewMembers = new ArrayList<>();
    }

    // Setters and Getters
    public void setCrewName(String crewName) {
        if (crewName != null && !crewName.isBlank()) this.crewName = crewName;
    }
    public void setShipName(String shipName) {
        if (shipName != null && !shipName.isBlank()) this.shipName = shipName;
    }
    public boolean setCaptain(Pirate captain) {
        if (captain != null && captain.getPirateCrew() == this){
            // Demotes the current captain if there is one
            if (this.captain != null){this.captain.toggleCaptain(false);}

            // Promotes the new captain
            captain.toggleCaptain(true);

            // Sets new captain in this object
            this.captain = captain;
            return true;
        }
        return false;
    }

    public int getCrewID() { return crewID; }
    public String getCrewName() { return crewName; }
    public String getShipName() { return shipName; }
    public Pirate getCaptain() { return captain; }
    public ArrayList<Pirate> getCrewMembers() {
        // Creates and returns a copy of the list for data integrity
        return new ArrayList<>(this.crewMembers);
    }

     /* HELPER METHOD
        // Recruits a new pirate into the crew, rejected if already in a crew
    */
    public boolean recruitMember(Pirate pirate){
        if (pirate == null) return false;
        if (pirate.getPirateCrew() != null) {
            System.out.println("Error: " + pirate.getAlias() + " already in a crew.");
            return false;
        }
        crewMembers.add(pirate);
        pirate.assignCrew(this);
        return true;
    }
    /* HELPER METHOD
        // Removes a pirate from the crew
    */
    public boolean goodbyeMember(Pirate pirate) {
        if (pirate == null || !crewMembers.contains(pirate)) return false;
        crewMembers.remove(pirate);
        pirate.removeCrew();
        return true;
    }

    /* HELPER METHOD
        // Calculating the total bounty of the crew and returns the sum 
        // of the bounties of members that are FREE
    */
    public int getTotalBounty(){
        int total = 0;
        for (Pirate pirate : crewMembers){
            if (pirate.getStatus() == Status.FREE) {
                total += pirate.getBounty();
            }
        }
        return total;
    }
    public void viewCrew(){
        System.out.println("Members of " + crewName + ":");
        if(crewMembers.isEmpty()){
            System.out.println(" There are no members");
            return;
        }
        for (Pirate pirate : crewMembers){ pirate.displayProfile(); }
    }

    /* HELPER METHOD
        // Displays the summary of the information of the crews. 
    */
    public void displayPirateInfo() {
        System.out.println("=== Pirate Crew: " + crewName + " ===");
        System.out.println("Crew ID       : " + crewID);
        System.out.println("Ship's Name   : " + shipName);
        System.out.println("Captain       : " + captain.getName());
        System.out.println("Crew Size     : " + crewMembers.size());
        System.out.println("Total Bounty  : " + getTotalBounty() + " Berries");
    }
}

