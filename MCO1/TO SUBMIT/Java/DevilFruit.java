import java.util.ArrayList;

public class DevilFruit {

    private static int autoID = 1;  // Auto-generated id

    // Attributes
    private int fruitID;
    private String fruitName;
    private Category category;
    private String primaryAbility;
    private Character currentOwner;
    private ArrayList<Character> historicalOwners;

    /* CONSTRUCTOR
        Purpose: creates a new devil fruit with a generated ID.
        @ fruitName, category, primaryAbility: fall back to defaults if null/blank.
    */
    public DevilFruit(String fruitName, Category category, String primaryAbility){
        this.fruitID = autoID++;
        this.fruitName = (fruitName != null && !fruitName.isBlank()) ? fruitName : "Unknown";
        this.category = (category != null) ? category : Category.UNDETERMINED;
        this.primaryAbility = (primaryAbility != null && !primaryAbility.isBlank()) ? primaryAbility : "Unknown";
        this.currentOwner = null;
        this.historicalOwners = new java.util.ArrayList<>();
    }

    // Prints the profile of the devil fruit
    public void displayFruit(){
        System.out.println("=================================================="); //  50
        System.out.println("ID              : " + this.fruitID);
        System.out.println("Name            : " + this.fruitName);
        System.out.println("Category        : " + this.category);
        System.out.println("Primary Ability : " + this.primaryAbility);
        System.out.println("Current Owner   : " + ((currentOwner != null) ? currentOwner.getName():"None") );

        System.out.println("All Owners      :");
        if (historicalOwners.isEmpty()){
            System.out.println("None");
        }
        else{
            for (Character owner: historicalOwners){
                System.out.println(owner.getName());
            }
        }
    }

    // Getters
    public int getFruitID(){ return this.fruitID; }
    public String getFruitName(){ return this.fruitName; }
    public Category getCategory(){ return this.category; }
    public String getPrimaryAbility(){ return this.primaryAbility; }
    public Character getCurrentOwner(){ return this.currentOwner; }
    public ArrayList<Character> getHistoricalOwners(){
        // Creates and returns a new arrayList to retain immutability
        return new ArrayList<>(this.historicalOwners);
    }

    /* HELPER METHOD
        // Assigns a new devil fruit owner
        // Only runs if:
            the fruit has no current owner
            the new owner is NOT dead
            the new owner does not yet have a devil fruit
    */
    public boolean assignNewOwner(Character newOwner) {
        if (currentOwner == null && newOwner != null && newOwner.getStatus() != Status.DEAD && !newOwner.hasDevilFruit()) {
            this.currentOwner = newOwner;
            newOwner.setDevilFruitPower(this); // Passes the current fruit object so the character's devilFruitPower field points back to this fruit
            return true;
        }

        return false;
    }

    public void triggerReincarnation(){
        // Adds the current owner as a new entry in the fruit's historical owners list.
        this.historicalOwners.add(this.currentOwner);
        // Detaches the fruit's side of the connection
        this.currentOwner = null;
    }
}
