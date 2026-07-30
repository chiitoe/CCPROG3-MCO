import java.util.ArrayList;

// Module 2 - Marine Corp Class: Member Recruitment
public class MarineCorps {

    private static int autoID = 1;
    private final int corpsID;
    private String corpsName;
    private String baseLocation;
    private String corpsCommander;
    private int opFunds;
    private ArrayList<Marine> members;

     /* CONSTRUCTOR
        Purpose: Creates a new corps with a specific ID.
        @ corpsName, baseLocation, corpsCommander: fall back to defaults if null/blank.
        @ operationalFunds: falls back to 0 if negative.
    */
    public MarineCorps(String corpsName, String baseLocation, String corpsCommander, int opFunds){
        this.corpsID = autoID++;

        this.corpsName = (corpsName != null && !corpsName.isBlank()) ? corpsName : "Unnamed corps";
        this.baseLocation = (baseLocation != null && !baseLocation.isBlank()) ? baseLocation : "Unknown Base Location";
        this.corpsCommander = (corpsCommander != null && !corpsCommander.isBlank()) ? corpsCommander : "Unknown Commander";
        this.members = new ArrayList<>();

        if (opFunds >= 0) {
            this.opFunds = opFunds;
        } else {
            this.opFunds = 0;
        }
    }

    // Setters and Getters
    public void setCorpsName(String corpsName) {
        if (corpsName != null && !corpsName.isBlank()) this.corpsName = corpsName;
    }
    public void setBaseLocation(String baseLocation) {
        if (baseLocation != null && !baseLocation.isBlank()) this.baseLocation = baseLocation;
    }
    public void setCorpsCommander(String corpsCommander) {
        if (corpsCommander != null && !corpsCommander.isBlank()) this.corpsCommander = corpsCommander;
    }
    public void setOpFunds(int opFunds){
        if(opFunds >=0) this.opFunds = opFunds;
    }

    public int getCorpsID() { return corpsID; }
    public String getCorpsName() { return corpsName; }
    public String getBaseLocation() { return baseLocation; }
    public String getCorpsCommander() { return corpsCommander; }
    public int getOpFunds() { return opFunds; }
    public ArrayList<Marine> getMembers() {
        // creates and returns a copy of the list for data integrity
        return new ArrayList<>(this.members);
    }

    /* HELPER METHOD
        // Recruiting a marine into the corps, returns if blank or is already in. 
    */
    public boolean recruitMarine(Marine marine){
        if (marine == null) return false;
        if (marine.getMarineCorps() != null) {
            System.out.println("Error: " + marine.getAlias() + " already in a crew.");
            return false;
        }
        members.add(marine);
        marine.assignCorps(this);
        return true;
    }
    /* HELPER METHOD
        // Removes a member from the corps, which also clears their data / reference.
    */ 
    public boolean goodbyeMember(Marine marine) {
        if (marine == null || !members.contains(marine)) return false;
        members.remove(marine);
        marine.removeCorps();
        return true;
    }

    // Viewing the number of members in a marine corps.
    public void viewMembers() {
        System.out.println("Members of " + corpsName + ":");
        if (members.isEmpty()) {
            System.out.println("  (no members)");
            return;
        }
        for (Marine marine : members) {
            marine.displayProfile();
        }
    }
    
    /* HELPER METHOD
        // Displays the information of the corps and the summary of the marine corps' group attributes.
    */
    public void displayMarineInfo() {
        System.out.println("=== Marine Corps: " + corpsName + " ===");
        System.out.println("Corps ID         : " + corpsID);
        System.out.println("Base Location    : " + baseLocation);
        System.out.println("Commander        : " + corpsCommander);
        System.out.println("Operational Funds: " + opFunds + " Berries");
        System.out.println("Unit Size        : " + members.size());
    }
}

