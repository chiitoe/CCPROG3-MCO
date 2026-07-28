/** Devil fruit
 * Purpose: holds information for each devil fruit
 */

import java.util.ArrayList;

public class DevilFruit {

    private static int autoID = 1;  /** Auto-generated id */

    // Attributes
    private int fruitID;    /** a fruit's unique ID */
    private String fruitName;   /** a fruit's name*/
    private Category category;  /** the type of devil fruit it is */
    private String primaryAbility;  /** description of the fruit's primary ability */
    private Character currentOwner; /** current holder of the fruit */
    private ArrayList<Character> historicalOwners;  /** list of all characters who have held the fruit before */

    /** CONSTRUCTOR
     * Purpose: initializes a new devil fruit
     * @param fruitName falls back to "Unknown" if null/blank
     * @param category falls back to "Undetermined" if null
     * @param primaryAbility falls back to "Unknown" if null/blank
     */
    public DevilFruit(String fruitName, Category category, String primaryAbility){
        this.fruitID = autoID++;
        this.fruitName = (fruitName != null && !fruitName.isBlank()) ? fruitName : "Unknown";
        this.category = (category != null) ? category : Category.UNDETERMINED;
        this.primaryAbility = (primaryAbility != null && !primaryAbility.isBlank()) ? primaryAbility : "Unknown";
        this.currentOwner = null;
        this.historicalOwners = new java.util.ArrayList<>();
    }

    /** Purpose: Prints out the information of a fruit
     */
    public void displayFruit(){
        System.out.println("=================================================="); //  50
        System.out.println("Fruit ID        : " + this.fruitID);
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

    /** Getters */
    public int getFruitID(){ return this.fruitID; }
    public String getFruitName(){ return this.fruitName; }
    public Category getCategory(){ return this.category; }
    public String getPrimaryAbility(){ return this.primaryAbility; }
    public Character getCurrentOwner(){ return this.currentOwner; }
    public ArrayList<Character> getHistoricalOwners(){
        // Creates and returns a new arrayList to retain immutability
        return new ArrayList<>(this.historicalOwners);
    }

    /** Helpers */

    /** Purpose: assigns a fruit to a new owner
     * @param newOwner rejected if null, dead, or if they already have a devil fruit
     * @return true if successful, false otherwise
     * */
    public boolean assignNewOwner(Character newOwner) {
        if (currentOwner == null && newOwner != null && newOwner.getStatus() != Status.DEAD && !newOwner.hasDevilFruit()) {
            this.currentOwner = newOwner;
            newOwner.setDevilFruitPower(this); // Psses the current fruit object so the character's devilFruitPower field points back to this fruit
            return true;
        }

        return false;
    }

    /** Purpose: triggers the automatic reincarnation of a fruit when a character that holds it is pronounced dead */
    public void triggerReincarnation(){
        // Adds the current owner as a new entry in the fruit's historical owners list.
        this.historicalOwners.add(this.currentOwner);
        // Detaches the fruit's side of the connection
        this.currentOwner = null;
    }
}