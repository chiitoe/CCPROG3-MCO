/** PirateHunter
 * Purpose: Inherits from Character, hold all information for pirate hunter characters
 */
public class PirateHunter extends Character {

    // Child attributes
    private String combatStyle;
    private int confirmedCaptures;

    /** CONSTRUCTOR
     * Purpose: Creates a pirate.
     * @param name fall back to "Unknown" if null/blank.
     * @param alias fall back to "Unknown" if null/blank.
     * @param origin fall back to "Unknown" if null/blank.
     * @param wallet falls back to 0 if negative value.
     * @param combatStyle falls back to "None" if null/blank
     * @param confirmedCaptures falls back to 0 if negative
     */
    public PirateHunter(String name, String alias, String origin, int wallet, String combatStyle, int confirmedCaptures) {
        super(name, alias, origin, wallet);

        this.combatStyle = (combatStyle != null && !combatStyle.isBlank()) ? combatStyle:"None";
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures : 0;
    }

    /** Getters */
    public String getCombatStyle(){ return this.combatStyle; }
    public int getConfirmedCaptures(){ return this.confirmedCaptures; }

    /** Setters */

    /** Purpose: sets a hunter's combat style
     * @param combatStyle rejected if blank or null
     */
    public void setCombatStyle(String combatStyle){
        if (combatStyle != null && !combatStyle.isBlank()){
            this.combatStyle = combatStyle;
        }
    }

    /** Purpose: replaces/reassignes a hunter's confirmed captures
     * @param confirmedCaptures falls back to 0 if negative
     * @return true if successful, false otherwise
     * */
    public void setConfirmedCaptures(int confirmedCaptures){
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures:0;
    }

    public void incrementConfirmedCaptures(){
        this.confirmedCaptures++;
    }

    /** Purpose: unique flavour text for pirate-related duties */
    @Override
    public void performDuty() {
        switch(this.combatStyle){
            case "None" -> System.out.println("Fighting...");
            case "Swordsmanship" -> System.out.println("Cutting down enemies with precision...");
            case "Devil Fruit" -> System.out.println("Consuming fruit power...");
            case "Haki" -> System.out.println("Channeling Haki to overpower my enemies...");
            case "Fish-man Karate" -> System.out.println("Striking with enhanced Fish-man Karate...");
            case "Black Leg" -> System.out.println("Kicking my way through life...");
            case "Rokushiki" -> System.out.println("Unleashing one of the six powers...");
            default -> System.out.println("Fighting with " + this.combatStyle + "...");
        }
    }
}