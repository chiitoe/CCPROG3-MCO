import java.util.ArrayList;

// This manages every Pirate Crew or Marine Corp. 
public class AffiliationDatabase {

    // Attributes
    private final ArrayList<PirateCrew> pirateCrews;
    private final ArrayList<MarineCorps> marineCorps;

    /* CONSTRUCTOR
        Purpose: Sets up empty group lists.
    */
    public AffiliationDatabase(){
        pirateCrews = new ArrayList<>();
        marineCorps = new ArrayList<>();
    }

    /* HELPER METHOD
        // Builds and creates a new PirateCrew which then returns it.
    */
    public PirateCrew createPirateCrew(String crewName, String shipName, Pirate captain) {
        PirateCrew crew = new PirateCrew(crewName, shipName, captain);
        pirateCrews.add(crew);
        System.out.println("Created Pirate Crew: " + crew.getCrewName());
        return crew;
    }
    /* HELPER METHOD
        // Builds and creates a new MarineCorpus unit which then returns it.
    */
    public MarineCorps createMarineCorps(String corpsName, String baseLocation, String corpsCommander, int operationalFunds) {
        MarineCorps corps = new MarineCorps(corpsName, baseLocation, corpsCommander, operationalFunds);
        marineCorps.add(corps);
        System.out.println("Created Marine Corps: " + corps.getCorpsName());
        return corps;
    }
    
    // Lists every crew annd corps by their ID and name.
    public void viewGroups() {
        System.out.println("=== Pirate Crews ===");
        for (PirateCrew c : pirateCrews) {
            c.displayPirateInfo();
        }
        System.out.println("=== Marine Corps Units ===");
        for (MarineCorps m : marineCorps) {
            m.displayMarineInfo();
        }
    }

    // Getters
    public ArrayList<PirateCrew> getPirateCrews() { return new ArrayList<>(this.pirateCrews); }
    public ArrayList<MarineCorps> getMarineCorpsUnits() { return new ArrayList<>(this.marineCorps); }

    /* HELPER METHOD
        // Looks up a pirate crew by its ID and returns the match.
    */
    public PirateCrew findPirateCrewById(int id) {
        for (PirateCrew c : pirateCrews) {
            if (c.getCrewID() == id) return c;
        }
        return null;
    }

    /* HELPER METHOD
        // Looks up a marine corps unit by its ID and returns the match.
    */
    public MarineCorps findMarineCorpsById(int id) {
        for (MarineCorps m : marineCorps) {
            if (m.getCorpsID() == id) return m;
        }
        return null;
    }

    /* HELPER METHOD
        // Deletes a pirate crew by ID. Clears every member's crew reference
        // Returns true if successful, false otherwise (ID not found).
    */
    public boolean deletePirateCrew(int id) {
        PirateCrew crew = findPirateCrewById(id);
        if (crew == null) return false;

        for (Pirate member : crew.getCrewMembers()) {
            if (member.isCaptain()) member.toggleCaptain(false);
            member.removeCrew();
        }
        pirateCrews.remove(crew);
        return true;
    }

    /* HELPER METHOD
        // Deletes a marine corps unit by ID. Clears every member's corps
        // Returns true if successful, false otherwise (ID not found).
    */
    public boolean deleteMarineCorps(int id) {
        MarineCorps corps = findMarineCorpsById(id);
        if (corps == null) return false;

        for (Marine member : corps.getMembers()) {
            member.removeCorps();
        }
        marineCorps.remove(corps);
        return true;
    }

}
