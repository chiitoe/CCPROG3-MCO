// Module 2 setup a character not affiliated with a Pirate Crew or Marine Corp 
public class PirateHunter extends Character {

    // Child attributes
    private String combatStyle;
    private int confirmedCaptures;

    /* CONSTRUCTOR
        Purpose: Creates a pirate hunter.
        @ combatStyle: falls back to "None" if null/blank.
        @ confirmedCaptures: falls back to 0 if negative.
    */
    public PirateHunter(String name, String alias, String origin, int wallet, String combatStyle, int confirmedCaptures) {
        super(name, alias, origin, wallet);

        this.combatStyle = (combatStyle != null && !combatStyle.isBlank()) ? combatStyle:"None";
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures : 0;
    }

    // Getters
    public String getCombatStyle(){ return this.combatStyle; }
    public int getConfirmedCaptures(){ return this.confirmedCaptures; }

    // Setters
    public void setCombatStyle(String combatStyle){
        if (combatStyle != null && !combatStyle.isBlank()){
            this.combatStyle = combatStyle;
        }
    }
    public void setConfirmedCaptures(int confirmedCaptures){
        this.confirmedCaptures = (confirmedCaptures >= 0) ? confirmedCaptures:0;
    }

    // Switch case according to Pirate Hunter type
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
