// ===== MODEL LAYER =====
/** Character
 * Purpose: Abstract template for characters.
 * Inherited by Pirate, Marine, PirateHunter, and Civilian.
 */
public abstract class Character {

    private static int autoID = 1;  // Auto-generated id

    private final int characterID;
    private String name;
    private String alias;
    private final String origin;
    private Status status;  // Enum prevents typos when comparing/assigning
    private DevilFruit devilFruitPower;
    private long wallet;

    /** CONSTRUCTOR
     * Purpose: Creates a character with an auto-generated ID.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     */
    protected Character(String name, String alias, String origin, long wallet){
        this.characterID = autoID++;        // Returns value THEN increments - first character gets ID 1
        this.name = (name != null && !name.isBlank()) ? name : "Unknown";
        this.alias = (alias != null && !alias.isBlank()) ? alias : "Unknown";
        this.origin = (origin != null && !origin.isBlank()) ? origin : "Unknown";
        this.status = Status.FREE;
        this.devilFruitPower = null;
        this.wallet = (wallet >= 0) ? wallet : 0;
    }


    /** LOAD CONSTRUCTOR
     * Purpose: Rebuilds a character from persisted data, preserving its original ID and
     * keeping the shared auto-ID counter ahead of every ID seen so far.
     * @param id the stored character ID
     * @param name stored name
     * @param alias stored alias
     * @param origin stored origin
     * @param wallet stored wallet (clamped to >= 0)
     */
    protected Character(int id, String name, String alias, String origin, long wallet){
        this.characterID = id;
        if(id >= autoID) autoID = id + 1;
        this.name = (name != null && !name.isBlank()) ? name : "Unknown";
        this.alias = (alias != null && !alias.isBlank()) ? alias : "Unknown";
        this.origin = (origin != null && !origin.isBlank()) ? origin : "Unknown";
        this.status = Status.FREE;
        this.devilFruitPower = null;
        this.wallet = (wallet >= 0) ? wallet : 0;
    }

    /**
     * Purpose: Prints out a character's profile.
     * The devil fruit line falls back to "None" when the character owns no fruit.
     */
    public void displayProfile(){
        System.out.println("=================================================="); //  50
        System.out.println("ID              : " + this.characterID);
        System.out.println("Name            : " + this.name);
        System.out.println("Alias           : " + this.alias);
        System.out.println("Origin          : " + this.origin);
        System.out.println("Status          : " + this.status);
        System.out.println("Devil Fruit     : " + (this.hasDevilFruit() ? this.devilFruitPower.getFruitName() : "None"));
        System.out.println("Wallet          : " + this.wallet + " Berries");
    }

    /**
     * Purpose: Builds a single-line, pipe-delimited record of this character for file storage.
     * @return one CSV-style line, no trailing newline
     */
    public String toRecord(){
        return this.getClass().getSimpleName() + "|" + this.characterID + "|" + this.name + "|"
                + this.alias + "|" + this.origin + "|" + this.status + "|"
                + (this.hasDevilFruit() ? this.devilFruitPower.getFruitName() : "None") + "|" + this.wallet;
    }

    // Getters
    /** @return this character's immutable auto-generated ID */
    public int getCharacterID(){ return this.characterID; }
    /** @return this character's name */
    public String getName(){ return this.name; }
    /** @return this character's alias */
    public String getAlias(){ return this.alias; }
    /** @return this character's place of origin */
    public String getOrigin(){ return this.origin; }
    /** @return this character's current status */
    public Status getStatus(){ return this.status; }
    /** @return the devil fruit this character owns, or null if none */
    public DevilFruit getDevilFruitPower(){ return this.devilFruitPower; }
    /** @return this character's current wallet balance in Berries */
    public long getWallet(){ return this.wallet; }

    // Setters
    /**
     * Purpose: Renames this character.
     * @param newName ignored if null/blank
     */
    public void setName(String newName){
        if(newName != null && !newName.isBlank()){
            this.name = newName;
        }
    }

    /**
     * Purpose: Changes this character's alias.
     * @param newAlias ignored if null/blank
     */
    public void setAlias(String newAlias){
        if(newAlias != null && !newAlias.isBlank()){
            this.alias = newAlias;
        }
    }

    /**
     * Purpose: Updates the character's status.
     * @param newStatus ignored if null; triggers devil fruit reincarnation when DEAD
     */
    public void setStatus(Status newStatus){
        if(newStatus == null) return;
        this.status = newStatus;

        if(this.status == Status.DEAD && this.devilFruitPower != null){
            this.devilFruitPower.triggerReincarnation();    // Detaches on the fruit's side
            this.devilFruitPower = null;                    // Detaches on the character's side
        }
    }

    /**
     * Purpose: Adds money to this character's wallet.
     * @param amount rejected if zero or negative
     * @return true if the wallet was credited, false otherwise
     */
    public boolean addWallet(long amount){
        if(amount > 0){
            this.wallet += amount;
            return true;
        }
        return false;
    }

    /**
     * Purpose: Removes money from this character's wallet.
     * @param amount rejected if it exceeds the current balance
     * @return true if the wallet was debited, false otherwise
     */
    public boolean deductWallet(long amount){
        if(hasEnoughMoney(amount)){
            this.wallet -= amount;
            return true;
        }
        return false;
    }

    /**
     * Purpose: Links a devil fruit to this character.
     * Package-level contract: only DevilFruit.assignNewOwner should call this, so
     * the two-way link stays consistent.
     * @param devilFruit rejected if null or if this character already owns a fruit
     * @return true if the link was set, false otherwise
     */
    public boolean setDevilFruitPower(DevilFruit devilFruit){
        if(devilFruit != null && this.devilFruitPower == null){
            this.devilFruitPower = devilFruit;
            return true;
        }
        return false;
    }

    /**
     * Purpose: Clears this character's devil fruit reference without changing status.
     * Needed when a fruit is deleted from DevilFruitDatabase while still owned -
     * setStatus(DEAD) is too heavy-handed just to detach a fruit.
     */
    public void clearDevilFruitPower(){
        this.devilFruitPower = null;
    }

    /**
     * Purpose: Sub-class specific behaviour, printed as flavour text.
     * Implemented individually by Pirate, Marine, PirateHunter, and Civilian.
     */
    public abstract void performDuty();

    /**
     * Purpose: Checks whether the wallet can cover a given cost.
     * @param amount the cost to test against the balance
     * @return true if the balance is greater than or equal to the amount
     */
    public boolean hasEnoughMoney(long amount){
        return this.wallet >= amount;
    }

    /**
     * Purpose: Checks whether this character currently owns a devil fruit.
     * @return true if a fruit is attached, false otherwise
     */
    public boolean hasDevilFruit(){
        return this.devilFruitPower != null;
    }
}
