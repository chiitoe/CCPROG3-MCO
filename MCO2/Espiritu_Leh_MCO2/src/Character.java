/** Character
 * Purpose: Abstract template for characters.
 * Inherited by Pirate, Marine, PirateHunter, and Civilian
 */

public abstract class Character {
    
    private static int autoID = 1;  // Auto-generated id

    private final int characterID;  /** unique character ID */
    private String name;    /** character's name */
    private String alias;   /** character's chosen alias */
    private final String origin;    /** place of origin */
    private Status status;  /** Used an enum for status to prevent typos when calling */
    private DevilFruit devilFruitPower; /** current devil fruit power */
    private int wallet; /** collection of currency (berries) */

   /**
    * Purpose: Creates a character with an auto-generated ID.
    * @param name fall back to "Unknown" if null/blank.
    * @param alias fall back to "Unknown" if null/blank.
    * @param origin fall back to "Unknown" if null/blank.
    * @param wallet falls back to 0 if negative value.
    */
    protected Character(String name, String alias, String origin, int wallet){
        this.characterID = autoID++;        // returns value THEN increments - first character will have ID 1
        this.name = (name != null && !name.isBlank()) ? name:"Unknown";
        this.alias = (alias != null && !alias.isBlank()) ? alias:"Unknown";
        this.origin = (origin != null && !origin.isBlank()) ? origin:"Unknown";
        this.status = Status.FREE;
        this.devilFruitPower = null;
        this.wallet = (wallet >= 0) ? wallet:0;
    }

    /** Purpose: Prints out a character's profile
     * devil fruit falls back to "None" if the character currently owns no devil fruit
     */
    public void displayProfile(){
        System.out.println("=================================================="); //  50
        System.out.println("Character ID    : " + this.characterID);
        System.out.println("Name            : " + this.name);
        System.out.println("Alias           : " + this.alias);
        System.out.println("Origin          : " + this.origin);
        System.out.println("Status          : " + this.status);
        System.out.println("Devil Fruit     : " + (this.hasDevilFruit() ? this.devilFruitPower.getFruitName():"None"));
        System.out.println("Wallet          : " + this.wallet + " Berries");
    }

    /** Getters */
    public int getCharacterID(){ return this.characterID;}
    public String getName(){ return this.name; }
    public String getAlias(){ return this.alias; }
    public String getOrigin(){ return this.origin; }
    public Status getStatus(){ return this.status; }
    public DevilFruit getDevilFruitPower(){ return this.devilFruitPower; }
    public int getWallet(){ return this.wallet; }

    /** Setters */

    /** Purpose: Sets the name of a character
     * @param newName rejected if null or blank
     */
    public void setName(String newName){
        if(newName != null && !newName.isBlank()){
            this.name = newName;
        }
    }

    /** Purpose: Sets the name of a alias
     * @param newAlias rejected if null or blank
     */
    public void setAlias(String newAlias){
        if(newAlias != null && !newAlias.isBlank()){
            this.alias = newAlias;
        }
    }

    /** Purpose: Updates the character's status
     * @param newStatus triggers reinmcarnation if DEAD
     */
    public void setStatus(Status newStatus){
        this.status = newStatus;

        if(this.status == Status.DEAD && this.devilFruitPower != null){
            // Detaches in the fruit's side
            this.devilFruitPower.triggerReincarnation();
            // Detaches in the character's side
            this.devilFruitPower = null;
        }
    }

    /** Purpose: Adding money to a character's wallet
     * @param amount rejected if less than 0
     * @return true if successful, false otherwise
     */
    public boolean addWallet(int amount){
        if(amount > 0) {
            this.wallet += amount;
            return true;
        }
        else{
            return false;
        }
    }

    /** Purpose: Deducting money to a character's wallet
     * @param amount rejected if more than what's currently in the character's wallet
     * @return true if successful, false otherwise
     */
    public boolean deductWallet(int amount){
        if (hasEnoughMoney(amount)){
            this.wallet -= amount;
            return true;
        }
        else{
            return false;
        }
    }

    /** Purpose: Sets a character's devil fruit power
     * @param devilFruit rejected if null
     */
    public void setDevilFruitPower(DevilFruit devilFruit){
        if (devilFruit != null){
            this.devilFruitPower = devilFruit;
        }
    }

    /** Purpose: Clears a character's devil fruit power */
    public void clearDevilFruitPower() {
        this.devilFruitPower = null;
    }

    /** Helper Methods */

    /** Purpose: Abstract class for performDuty.
     *  Overridden in child classes
     */
    public abstract void performDuty();

    /** Purpose: Checks for if the wallet has enough money for a successful transaction
     * @param amount rejected if less than this.wallet
     * @return true if successful, false otherwise
     * */
    public boolean hasEnoughMoney(int amount){
        return this.wallet >= amount;
    }

    /** Purpose: Checks for a null devilFruitPower
     * @return true if successful, false otherwise
     */
    public boolean hasDevilFruit(){
        return devilFruitPower != null;
    }

}