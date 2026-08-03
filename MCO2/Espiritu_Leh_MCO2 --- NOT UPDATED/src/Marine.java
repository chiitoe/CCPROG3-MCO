/** Marine
 * Purpose: Inherits from Character, hold all information for marine characters
 */
public class Marine extends Character { // Marine class derived from Character class

    // Child Attributes
    private MarineRank marineRank;  /** a marine's rank among its corps */
    private MarineCorps marineCorps;    /** the corps a marine is affiliated with */

    /** CONSTRUCTOR
     * Purpose: Creates a Marine.
     * @param name fall back to "Unknown" if null/blank.
     * @param alias fall back to "Unknown" if null/blank.
     * @param origin fall back to "Unknown" if null/blank.
     * @param wallet falls back to 0 if negative value.
     * @param marineRank falls back to ENSIGN if null
     */
    public Marine(String name, String alias, String origin, int wallet, MarineRank marineRank) {
        super(name, alias, origin, wallet);

        this.marineRank = (marineRank != null) ? marineRank:MarineRank.ENSIGN;  // Starts at the bottom if a rank is not given
        // Marines are created with no affiliations to a corps
    }

    /** Getters */
    public MarineRank getMarineRank(){ return this.marineRank; }
    public MarineCorps getMarineCorps(){ return this.marineCorps; }

    /** Helpers */

    /** Purpose: promotes a marine to the next rank
     * @return true if successful, false otherwise
     * */
    public boolean promoteRank(){
        MarineRank[] allRanks = MarineRank.values();
        int index = this.marineRank.ordinal();      // Holds the index of the current rank

        // Only runs when the current rank is NOT the HIGHEST rank
        if(index < allRanks.length - 1){
            this.marineRank = allRanks[index + 1];
            return true;
        }
        return false;
    }

    /** Purpose: assigns a marine to a corps
     * @param marineCorps rejected if null
     * @return true if successful, false otherwise
     * */
    public boolean assignCorps(MarineCorps marineCorps){

        // only runs if the input is valid
        if (marineCorps != null){
            this.marineCorps = marineCorps;
            return true;
        }
        return false;
    }

    /** Purpose: de-affiliates a marine from its corps */
    public void removeCorps(){
        this.marineCorps = null;
    }

    /** Purpose: unique flavour text for marine-related duties */
    @Override
    public void performDuty() {
        switch (this.marineRank){
            case ENSIGN -> System.out.println("Following a superior's orders...");
            case LIEUTENANT -> System.out.println("Fulfilling my duties...");
            case COMMANDER -> System.out.println("Overseeing low-rank officers...");
            case CAPTAIN -> System.out.println("Commanding a marine branch to capture pirates...");
            case COMMODORE -> System.out.println("Taking on authoritative duties...");
            case REAR_ADMIRAL -> System.out.println("Commanding low-ranked marines...");
            case VICE_ADMIRAL -> System.out.println("Leading a Buster Call fleet...");
            case ADMIRAL -> System.out.println("Serving as one of the Marine's three greatest powers...");
            case FLEET_ADMIRAL -> System.out.println("Overseeing marine operations...");
            default -> System.out.println("Patrolling the seas...");
        }
    }
}