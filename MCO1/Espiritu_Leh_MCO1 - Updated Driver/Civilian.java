/**
 * Module 1 setups a character not affiliated with Pirate Crew or Marine Corps
 */
public class Civilian extends Character {

    /**
     * Child attributes
     */
    private String profession;
    private String residence;

    /**
     * CONSTRUCTOR
     * Purpose: Creates a civilian.
     * @ profession: Falls back to "Unemployed" if null/blank.
     * @ residence: Falls back to "Homeless" if null/blank.
     */
    public Civilian(String name, String alias, String origin, int wallet, String profession, String residence) {
        super(name, alias, origin, wallet);

        this.profession = (profession != null && !profession.isBlank()) ? profession:"Unemployed";
        this.residence = (residence != null && !residence.isBlank()) ? residence:"Homeless";
    }

    /**
     * Getters
     */
    public String getProfession(){ return this.profession; }
    public String getResidence(){ return this.residence; }

    /**
     * Setters
     */
    public void setProfession(String profession){
        if (profession != null && !profession.isBlank()){
            this.profession = profession;
        }
    }
    public void setResidence(String residence){
        if (residence != null && !residence.isBlank()){
            this.residence = residence;
        }
    }

    /**
     * Switch Case according to civilian types
     */
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
