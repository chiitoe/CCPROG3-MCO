/** Civilian
 * Purpose: Inherits from Character, hold all information for civilian characters
 */
public class Civilian extends Character {

    // Child attributes
    private String profession;  /** a civilian's profession */
    private String residence;   /** a civilian's residence */

    /** CONSTRUCTOR
     * Purpose: Creates a pirate.
     * @param name fall back to "Unknown" if null/blank.
     * @param alias fall back to "Unknown" if null/blank.
     * @param origin fall back to "Unknown" if null/blank.
     * @param wallet falls back to 0 if negative value.
     * @param profession falls back to "Unemployed" if empty/null.
     * @param residence falls back to "Homeless" if  if empty/null.
     */
    public Civilian(String name, String alias, String origin, int wallet, String profession, String residence) {
        super(name, alias, origin, wallet);

        this.profession = (profession != null && !profession.isBlank()) ? profession:"Unemployed";
        this.residence = (residence != null && !residence.isBlank()) ? residence:"Homeless";
    }

    /** Getters */
    public String getProfession(){ return this.profession; }
    public String getResidence(){ return this.residence; }

    /** Setters */

    /** Purpose: sets/replaces a civilian's profession
     * @param profession rejected if null or blank
     */
    public void setProfession(String profession){
        if (profession != null && !profession.isBlank()){
            this.profession = profession;
        }
    }

    /** Purpose: sets/replaces a civilian's residence
     * @param residence rejected if null or blank
     */
    public void setResidence(String residence){
        if (residence != null && !residence.isBlank()){
            this.residence = residence;
        }
    }

    /** Purpose: unique flavour text for pirate-related duties */
    @Override
    public void performDuty() {
        switch(this.profession){
            case "Unemployed" -> System.out.println("Enjoying my day to day...");
            case "Shipwright" -> System.out.println("Building and repairing ships...");
            case "Bartender" -> System.out.println("Serving drinks at the local tavern...");
            case "Scholar" -> System.out.println("Studying the world's history and lore...");
            case "Merchant" -> System.out.println("Buying and selling goods at the market...");
            case "Fisherman" -> System.out.println("Casting nets and reeling in catch...");
            case "Blacksmith" -> System.out.println("Forging tools and weapons...");
            case "Farmer" -> System.out.println("Tending to the crops...");
            case "Hunter" -> System.out.println("Chasing through the forests and fields...");
            case "Doctor" -> System.out.println("Treating the sick and injured...");
            case "Innkeeper" -> System.out.println("Managing the local inn...");
            case "Tailor" -> System.out.println("Mending and making clothes...");
            default -> System.out.println("Working as the town's " + this.profession + "...");
        }
    }
}