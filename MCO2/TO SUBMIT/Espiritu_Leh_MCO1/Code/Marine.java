/** Marine
 * Purpose: Inherits from Character, holds all information for marine characters.
 */
public class Marine extends Character {

    private MarineRank marineRank;
    private MarineCorps marineCorps;

    /** CONSTRUCTOR
     * Purpose: Creates a member of the Marine Corps.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param marineRank falls back to ENSIGN if null
     */
    public Marine(String name, String alias, String origin, int wallet, MarineRank marineRank){
        super(name, alias, origin, wallet);

        this.marineRank = (marineRank != null) ? marineRank : MarineRank.ENSIGN;  // Starts at the bottom
        this.marineCorps = null;
    }

    // Getters
    /** @return this marine's current commissioned rank */
    public MarineRank getMarineRank(){ return this.marineRank; }
    /** @return the corps this marine serves in, or null if unassigned */
    public MarineCorps getMarineCorps(){ return this.marineCorps; }

    /**
     * Purpose: Promotes this marine to the next rank in the enum order.
     * @return true if promoted, false if already at the highest rank
     */
    public boolean promoteRank(){
        MarineRank[] allRanks = MarineRank.values();
        int index = this.marineRank.ordinal();      // Index of the current rank

        if(index < allRanks.length - 1){
            this.marineRank = allRanks[index + 1];
            return true;
        }
        return false;
    }

    /**
     * Purpose: Links this marine to a corps.
     * Contract: only MarineCorps.recruitMarine should call this, so the two-way link stays consistent.
     * @param marineCorps rejected if null or if this marine is already affiliated
     * @return true if the link was set, false otherwise
     */
    public boolean assignCorps(MarineCorps marineCorps){
        if(marineCorps != null && this.marineCorps == null){
            this.marineCorps = marineCorps;
            return true;
        }
        return false;
    }

    /** Purpose: Clears this marine's corps reference. */
    public void removeCorps(){
        this.marineCorps = null;
    }

    /** Purpose: Prints rank-specific flavor text for this marine. */
    @Override
    public void performDuty(){
        switch(this.marineRank){
            case ENSIGN -> System.out.println("Following a superior's orders.");
            case LIEUTENANT -> System.out.println("Fulfilling my duties now sir!");
            case COMMANDER -> System.out.println("What we having there mates?");
            case CAPTAIN -> System.out.println("About to have a coffee break in 5.");
            case COMMODORE -> System.out.println("What are you saying? I need to take over?");
            case REAR_ADMIRAL -> System.out.println("What am I even doing here, what a drag.");
            case VICE_ADMIRAL -> System.out.println("Time to lead a buster fleet call!");
            case ADMIRAL -> System.out.println("Six-Seven!");
            case FLEET_ADMIRAL -> System.out.println("Each one of you! Man up and take charge!");
            default -> System.out.println("Patrolling the seas...");
        }
    }
}