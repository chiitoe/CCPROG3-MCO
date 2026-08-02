/** Civilian
 * Purpose: Inherits from Character, holds all information for civilian characters.
 */
public class Civilian extends Character {

    private String profession;
    private String residence;

    /** CONSTRUCTOR
     * Purpose: Creates a civilian.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param profession falls back to "Unemployed" if null/blank
     * @param residence falls back to "Homeless" if null/blank
     */
    public Civilian(String name, String alias, String origin, int wallet, String profession, String residence){
        super(name, alias, origin, wallet);

        this.profession = (profession != null && !profession.isBlank()) ? profession : "Unemployed";
        this.residence = (residence != null && !residence.isBlank()) ? residence : "Homeless";
    }

    // Getters
    /** @return this civilian's profession */
    public String getProfession(){ return this.profession; }
    /** @return this civilian's place of residence */
    public String getResidence(){ return this.residence; }

    // Setters
    /**
     * Purpose: Changes this civilian's profession.
     * @param profession ignored if null/blank
     */
    public void setProfession(String profession){
        if(profession != null && !profession.isBlank()){
            this.profession = profession;
        }
    }

    /**
     * Purpose: Changes this civilian's residence.
     * @param residence ignored if null/blank
     */
    public void setResidence(String residence){
        if(residence != null && !residence.isBlank()){
            this.residence = residence;
        }
    }

    /** Purpose: Prints profession-specific  text for this civilian. */
    @Override
    public void performDuty(){
        switch(this.profession){
            case "Unemployed" -> System.out.println("I am doing nothing heh.");
            case "Shipwright" -> System.out.println("Time to build and repair the ship.");
            case "Bartender" -> System.out.println("Any drinks you want?");
            case "Scholar" -> System.out.println("What is it that you wanna know?");
            case "Merchant" -> System.out.println("Fancy anything?");
            case "Fisherman" -> System.out.println("Imma catch some fish!");
            case "Blacksmith" -> System.out.println("Want some weapons?");
            case "Farmer" -> System.out.println("Growing some crops out here.");
            case "Hunter" -> System.out.println("Need to chase someone down?");
            case "Doctor" -> System.out.println("Healing for everyone!");
            case "Innkeeper" -> System.out.println("Some place to stay in? Yes?");
            case "Tailor" -> System.out.println("Your clothes have some holes in them, need help?");
            default -> System.out.println("Working as the town's " + this.profession + "...");
        }
    }
}