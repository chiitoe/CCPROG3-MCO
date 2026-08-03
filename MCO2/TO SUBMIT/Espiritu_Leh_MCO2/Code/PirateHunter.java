// ===== MODEL LAYER =====
/** PirateHunter
 * Purpose: Inherits from Character, holds all information for pirate hunter characters.
 */
public class PirateHunter extends Character {

    /** Combat styles offered in the GUI dropdown. */
    public static final String[] COMBAT_OPTIONS = {
            "None", "Swordsmanship", "Devil Fruit", "Haki",
            "Fish-man Karate", "Black Leg", "Rokushiki"
    };

    private String combatStyle;
    private int confirmedCaptures;

    /** CONSTRUCTOR
     * Purpose: Creates a pirate hunter.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param combatStyle falls back to "None" if null/blank
     * @param confirmedCaptures falls back to 0 if negative
     */
    public PirateHunter(String name, String alias, String origin, long wallet, String combatStyle, int confirmedCaptures){
        super(name, alias, origin, wallet);

        this.combatStyle = (combatStyle != null && !combatStyle.isBlank()) ? combatStyle : "None";
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures : 0;
    }


    /** LOAD CONSTRUCTOR
     * Purpose: Rebuilds a pirate hunter from a save file with its original ID preserved.
     * @param id the stored character ID
     */
    public PirateHunter(int id, String name, String alias, String origin, long wallet, String combatStyle, int confirmedCaptures){
        super(id, name, alias, origin, wallet);
        this.combatStyle = (combatStyle != null && !combatStyle.isBlank()) ? combatStyle : "None";
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures : 0;
    }

    // Getters
    /** @return this hunter's combat style */
    public String getCombatStyle(){ return this.combatStyle; }
    /** @return how many captures this hunter has confirmed */
    public int getConfirmedCaptures(){ return this.confirmedCaptures; }

    // Setters
    /**
     * Purpose: Changes this hunter's combat style.
     * @param combatStyle ignored if null/blank
     */
    public void setCombatStyle(String combatStyle){
        if(combatStyle != null && !combatStyle.isBlank()){
            this.combatStyle = combatStyle;
        }
    }

    /**
     * Purpose: Overwrites this hunter's capture tally.
     * @param confirmedCaptures falls back to 0 if negative
     */
    public void setConfirmedCaptures(int confirmedCaptures){
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures : 0;
    }

    /** Purpose: Adds one to this hunter's confirmed capture tally. */
    public void incrementConfirmedCaptures(){
        this.confirmedCaptures++;
    }

    /** Purpose: Prints style-specific flavour text for this hunter. */
    @Override
    public void performDuty(){
        switch(this.combatStyle){
            case "None" -> System.out.println("Eh? I don't know what to do");
            case "Swordsmanship" -> System.out.println("I'll cut you down!");
            case "Devil Fruit" -> System.out.println("This is insane, I love this!");
            case "Haki" -> System.out.println("Need to channel my haki. Hayah!");
            case "Fish-man Karate" -> System.out.println("Want some of my karate?");
            case "Black Leg" -> System.out.println("Time to kick my way through!");
            case "Rokushiki" -> System.out.println("Don't make me use all my six powers!");
            default -> System.out.println("Fighting with " + this.combatStyle + "...");
        }
    }
}
