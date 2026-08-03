import java.util.ArrayList;

/** MarineCorps
 * Purpose: Manages a marine corps unit and its members.
 */
public class MarineCorps {

    private static int autoID = 1;   // Counter independent of Character's
    private final int corpsID;
    private String corpsName;
    private String baseLocation;
    private String corpsCommander;
    private int opFunds;
    private final ArrayList<Marine> members;

    /** CONSTRUCTOR
     * Purpose: Creates a new corps with a generated ID.
     * @param corpsName falls back to "Unnamed Corps" if null/blank
     * @param baseLocation falls back to "Unknown Base Location" if null/blank
     * @param corpsCommander falls back to "Unknown Commander" if null/blank
     * @param opFunds falls back to 0 if negative
     */
    public MarineCorps(String corpsName, String baseLocation, String corpsCommander, int opFunds){
        this.corpsID = autoID++;

        this.corpsName = (corpsName != null && !corpsName.isBlank()) ? corpsName : "Unnamed Corps";
        this.baseLocation = (baseLocation != null && !baseLocation.isBlank()) ? baseLocation : "Unknown Base Location";
        this.corpsCommander = (corpsCommander != null && !corpsCommander.isBlank()) ? corpsCommander : "Unknown Commander";
        this.members = new ArrayList<>();
        this.opFunds = (opFunds >= 0) ? opFunds : 0;
    }

    // Setters
    /**
     * Purpose: Renames the corps.
     * @param corpsName ignored if null/blank
     */
    public void setCorpsName(String corpsName){
        if(corpsName != null && !corpsName.isBlank()) this.corpsName = corpsName;
    }

    /**
     * Purpose: Relocates the corps' base.
     * @param baseLocation ignored if null/blank
     */
    public void setBaseLocation(String baseLocation){
        if(baseLocation != null && !baseLocation.isBlank()) this.baseLocation = baseLocation;
    }

    /**
     * Purpose: Changes the officer in command.
     * @param corpsCommander ignored if null/blank
     */
    public void setCorpsCommander(String corpsCommander){
        if(corpsCommander != null && !corpsCommander.isBlank()) this.corpsCommander = corpsCommander;
    }

    /**
     * Purpose: Overwrites the operational fund balance.
     * @param opFunds ignored if negative
     */
    public void setOpFunds(int opFunds){
        if(opFunds >= 0) this.opFunds = opFunds;
    }

    // Getters
    /** @return this corps' immutable auto-generated ID */
    public int getCorpsID(){ return this.corpsID; }
    /** @return this corps' name */
    public String getCorpsName(){ return this.corpsName; }
    /** @return where this corps is stationed */
    public String getBaseLocation(){ return this.baseLocation; }
    /** @return the officer commanding this corps */
    public String getCorpsCommander(){ return this.corpsCommander; }
    /** @return this corps' operational funds in Berries */
    public int getOpFunds(){ return this.opFunds; }
    /** @return a defensive copy of the member list */
    public ArrayList<Marine> getMembers(){
        return new ArrayList<>(this.members);
    }

    /**
     * Purpose: Recruits a marine into the corps.
     * @param marine rejected if null or already affiliated with a corps
     * @return true if recruited, false otherwise
     */
    public boolean recruitMarine(Marine marine){
        if(marine == null) return false;
        if(marine.getMarineCorps() != null){
            System.out.println("Error: " + marine.getAlias() + " is already in a corps.");
            return false;
        }
        members.add(marine);
        marine.assignCorps(this);
        return true;
    }

    /**
     * Purpose: Removes a member from the corps and clears their corps reference.
     * @param marine rejected if null or not a member
     * @return true if removed, false otherwise
     */
    public boolean goodbyeMember(Marine marine){
        if(marine == null || !members.contains(marine)) return false;
        members.remove(marine);
        marine.removeCorps();
        return true;
    }

    /** Purpose: Prints the full profile of every marine in this corps. */
    public void viewMembers(){
        System.out.println("Members of " + corpsName + ":");
        if(members.isEmpty()){
            System.out.println("  (no members)");
            return;
        }
        for(Marine marine : members){
            marine.displayProfile();
        }
    }

    /**
     * Purpose: Credits the corps' operational funds.
     * @param amount rejected if zero or negative
     * @return true if the funds were credited, false otherwise
     */
    public boolean addOpFunds(int amount){
        if(amount > 0){
            this.opFunds += amount;
            return true;
        }
        return false;
    }

    /** Purpose: Displays a summary of this corps' information. */
    public void displayMarineInfo(){
        System.out.println("=== Marine Corps: " + corpsName + " ===");
        System.out.println("Corps ID         : " + corpsID);
        System.out.println("Base Location    : " + baseLocation);
        System.out.println("Commander        : " + corpsCommander);
        System.out.println("Operational Funds: " + opFunds + " Berries");
        System.out.println("Unit Size        : " + members.size());
    }

    /**
     * Purpose: Builds a single-line, pipe-delimited record of this corps for file storage.
     * @return one CSV-style line, no trailing newline
     */
    public String toRecord(){
        return "CORPS|" + corpsID + "|" + corpsName + "|" + baseLocation + "|"
                + corpsCommander + "|" + members.size() + "|" + opFunds;
    }
}