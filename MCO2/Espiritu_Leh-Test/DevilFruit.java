// ===== MODEL LAYER =====
import java.util.ArrayList;

/** DevilFruit
 * Purpose: Holds information for each devil fruit.
 */
public class DevilFruit {

    private static int autoID = 1;  // Auto-generated id

    private final int fruitID;
    private String fruitName;
    private Category category;
    private String primaryAbility;
    private Character currentOwner;
    private final ArrayList<Character> historicalOwners;

    /** CONSTRUCTOR
     * Purpose: Creates a new devil fruit with a generated ID.
     * @param fruitName falls back to "Unknown" if null/blank
     * @param category falls back to UNDETERMINED if null
     * @param primaryAbility falls back to "Unknown" if null/blank
     */
    public DevilFruit(String fruitName, Category category, String primaryAbility){
        this.fruitID = autoID++;
        this.fruitName = (fruitName != null && !fruitName.isBlank()) ? fruitName : "Unknown";
        this.category = (category != null) ? category : Category.UNDETERMINED;
        this.primaryAbility = (primaryAbility != null && !primaryAbility.isBlank()) ? primaryAbility : "Unknown";
        this.currentOwner = null;
        this.historicalOwners = new ArrayList<>();
    }

    /** LOAD CONSTRUCTOR
     * Purpose: Rebuilds a fruit from persisted data with its original ID preserved.
     * @param id the stored fruit ID
     */
    public DevilFruit(int id, String fruitName, Category category, String primaryAbility){
        this.fruitID = id;
        if(id >= autoID) autoID = id + 1;
        this.fruitName = (fruitName != null && !fruitName.isBlank()) ? fruitName : "Unknown";
        this.category = (category != null) ? category : Category.UNDETERMINED;
        this.primaryAbility = (primaryAbility != null && !primaryAbility.isBlank()) ? primaryAbility : "Unknown";
        this.currentOwner = null;
        this.historicalOwners = new ArrayList<>();
    }

    /**
     * Purpose: Restores the current owner during a load, wiring both sides of the link
     * without the validation assignNewOwner enforces for live assignments.
     * @param owner the character to restore as current holder
     */
    public void restoreCurrentOwner(Character owner){
        if(owner != null){
            this.currentOwner = owner;
            owner.setDevilFruitPower(this);
        }
    }

    /**
     * Purpose: Appends a past owner during a load.
     * @param owner the historical holder to record
     */
    public void restoreHistoricalOwner(Character owner){
        if(owner != null) this.historicalOwners.add(owner);
    }

    /** Purpose: Prints out the information of a fruit, including its ownership history. */
    public void displayFruit(){
        System.out.println("=================================================="); //  50
        System.out.println("ID              : " + this.fruitID);
        System.out.println("Name            : " + this.fruitName);
        System.out.println("Category        : " + this.category);
        System.out.println("Primary Ability : " + this.primaryAbility);
        System.out.println("Current Owner   : " + ((currentOwner != null) ? currentOwner.getName() : "None"));

        System.out.println("Past Owners     :");
        if(historicalOwners.isEmpty()){
            System.out.println("  (none)");
        }
        else{
            for(Character owner : historicalOwners){
                System.out.println("  " + owner.getName());
            }
        }
    }

    // Getters
    /** @return this fruit's immutable auto-generated ID */
    public int getFruitID(){ return this.fruitID; }
    /** @return this fruit's name */
    public String getFruitName(){ return this.fruitName; }
    /** @return this fruit's category */
    public Category getCategory(){ return this.category; }
    /** @return this fruit's primary ability */
    public String getPrimaryAbility(){ return this.primaryAbility; }
    /** @return the character currently holding this fruit, or null if unowned */
    public Character getCurrentOwner(){ return this.currentOwner; }
    /** @return a defensive copy of the list of past owners */
    public ArrayList<Character> getHistoricalOwners(){
        return new ArrayList<>(this.historicalOwners);
    }

    // Setters
    /**
     * Purpose: Renames this fruit.
     * @param fruitName ignored if null/blank
     */
    public void setFruitName(String fruitName){
        if(fruitName != null && !fruitName.isBlank()) this.fruitName = fruitName;
    }

    /**
     * Purpose: Reclassifies this fruit.
     * @param category ignored if null
     */
    public void setCategory(Category category){
        if(category != null) this.category = category;
    }

    /**
     * Purpose: Changes the described primary ability.
     * @param primaryAbility ignored if null/blank
     */
    public void setPrimaryAbility(String primaryAbility){
        if(primaryAbility != null && !primaryAbility.isBlank()) this.primaryAbility = primaryAbility;
    }

    /**
     * Purpose: Assigns this fruit to a new owner, setting both sides of the link.
     * @param newOwner rejected if null, dead, or already holding a devil fruit
     * @return true if the assignment succeeded, false otherwise
     */
    public boolean assignNewOwner(Character newOwner){
        if(currentOwner == null && newOwner != null
                && newOwner.getStatus() != Status.DEAD && !newOwner.hasDevilFruit()){

            // Passes this fruit object so the character's field points back here
            if(newOwner.setDevilFruitPower(this)){
                this.currentOwner = newOwner;
                return true;
            }
        }
        return false;
    }

    /**
     * Purpose: Reincarnates the fruit when its holder dies, archiving them as a past owner.
     * Safe to call when the fruit is unowned - it simply does nothing.
     */
    public void triggerReincarnation(){
        if(this.currentOwner != null){
            this.historicalOwners.add(this.currentOwner);
            this.currentOwner = null;
        }
    }

    /**
     * Purpose: Builds a single-line, pipe-delimited record of this fruit for file storage.
     * @return one CSV-style line, no trailing newline
     */
    public String toRecord(){
        return "FRUIT|" + fruitID + "|" + fruitName + "|" + category + "|" + primaryAbility + "|"
                + ((currentOwner != null) ? currentOwner.getName() : "None") + "|"
                + historicalOwners.size();
    }
}
