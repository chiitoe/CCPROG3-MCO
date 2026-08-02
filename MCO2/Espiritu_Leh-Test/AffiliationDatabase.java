import java.util.ArrayList;

/** AffiliationDatabase
 * Purpose: Manages all pirate crews and marine corps units.
 */
public class AffiliationDatabase {

    private final ArrayList<PirateCrew> pirateCrews;
    private final ArrayList<MarineCorps> marineCorps;

    /** CONSTRUCTOR
     * Purpose: Sets up empty group lists.
     */
    public AffiliationDatabase(){
        pirateCrews = new ArrayList<>();
        marineCorps = new ArrayList<>();
    }

    /**
     * Purpose: Founds a new pirate crew under the given captain.
     * @param crewName falls back to "Unnamed Crew" if null/blank
     * @param shipName falls back to "Unnamed Ship" if null/blank
     * @param captain the founding captain, who is enrolled automatically
     * @return the newly founded crew
     * @throws IllegalArgumentException if the captain is null or already in a crew
     */
    public PirateCrew createPirateCrew(String crewName, String shipName, Pirate captain){
        PirateCrew crew = new PirateCrew(crewName, shipName, captain);
        pirateCrews.add(crew);
        System.out.println("Created Pirate Crew: " + crew.getCrewName());
        return crew;
    }

    /**
     * Purpose: Establishes a new marine corps unit.
     * @param corpsName falls back to "Unnamed Corps" if null/blank
     * @param baseLocation falls back to "Unknown Base Location" if null/blank
     * @param corpsCommander falls back to "Unknown Commander" if null/blank
     * @param operationalFunds falls back to 0 if negative
     * @return the newly established corps
     */
    public MarineCorps createMarineCorps(String corpsName, String baseLocation,
                                         String corpsCommander, int operationalFunds){
        MarineCorps corps = new MarineCorps(corpsName, baseLocation, corpsCommander, operationalFunds);
        marineCorps.add(corps);
        System.out.println("Created Marine Corps: " + corps.getCorpsName());
        return corps;
    }

    /** Purpose: Prints a summary of every crew and corps on file. */
    public void viewGroups(){
        System.out.println("=== Pirate Crews ===");
        if(pirateCrews.isEmpty()) System.out.println("  (no crews registered)");
        for(PirateCrew c : pirateCrews){
            c.displayPirateInfo();
        }

        System.out.println("=== Marine Corps Units ===");
        if(marineCorps.isEmpty()) System.out.println("  (no corps registered)");
        for(MarineCorps m : marineCorps){
            m.displayMarineInfo();
        }
    }

    // Getters
    public ArrayList<PirateCrew> getPirateCrews(){ return new ArrayList<>(this.pirateCrews); }
    public ArrayList<MarineCorps> getMarineCorpsUnits(){ return new ArrayList<>(this.marineCorps); }

    /**
     * Purpose: Finds a pirate crew by ID.
     * @param id the crew ID to search for
     * @return the matching crew, or null if none exists
     */
    public PirateCrew findPirateCrewById(int id){
        for(PirateCrew c : pirateCrews){
            if(c.getCrewID() == id) return c;
        }
        return null;
    }

    /**
     * Purpose: Finds a marine corps unit by ID.
     * @param id the corps ID to search for
     * @return the matching corps, or null if none exists
     */
    public MarineCorps findMarineCorpsById(int id){
        for(MarineCorps m : marineCorps){
            if(m.getCorpsID() == id) return m;
        }
        return null;
    }

    /**
     * Purpose: Disbands a pirate crew, clearing every member's crew reference.
     * @param id the crew ID to delete
     * @return true if a crew was found and disbanded, false otherwise
     */
    public boolean deletePirateCrew(int id){
        PirateCrew crew = findPirateCrewById(id);
        if(crew == null) return false;

        for(Pirate member : crew.getCrewMembers()){
            member.removeCrew();    // Also clears captaincy
        }
        pirateCrews.remove(crew);
        return true;
    }

    /**
     * Purpose: Dissolves a marine corps unit, clearing every member's corps reference.
     * @param id the corps ID to delete
     * @return true if a corps was found and dissolved, false otherwise
     */
    public boolean deleteMarineCorps(int id){
        MarineCorps corps = findMarineCorpsById(id);
        if(corps == null) return false;

        for(Marine member : corps.getMembers()){
            member.removeCorps();
        }
        marineCorps.remove(corps);
        return true;
    }
}