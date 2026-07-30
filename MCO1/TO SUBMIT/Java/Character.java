// Module 1 setups what is needed for other child classes
public abstract class Character {
    
    private static int autoID = 1;  // Auto-generated id

    private final int characterID;
    private String name;
    private String alias;
    private final String origin;
    private Status status;  // Used an enum for status to prevent typos when calling
    private DevilFruit devilFruitPower;
    private int wallet;

   /* CONSTRUCTOR
        Purpose: Creates a character with an auto-generated ID.
        // name, alias, origin: fall back to "Unknown" if null/blank.
        // wallet: falls back to 0 if negative value.
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

    // Prints the character profile
    public void displayProfile(){
        System.out.println("=================================================="); //  50
        System.out.println("ID              : " + this.characterID);
        System.out.println("Name            : " + this.name);
        System.out.println("Alias           : " + this.alias);
        System.out.println("Origin          : " + this.origin);
        System.out.println("Status          : " + this.status);
        System.out.println("Devil Fruit     : " + (this.hasDevilFruit() ? this.devilFruitPower:"None"));
        System.out.println("Wallet          : " + this.wallet + " Berries");
    }

    // Getters
    public int getCharacterID(){ return this.characterID;}
    public String getName(){ return this.name; }
    public String getAlias(){ return this.alias; }
    public String getOrigin(){ return this.origin; }
    public Status getStatus(){ return this.status; }
    public DevilFruit getDevilFruitPower(){ return this.devilFruitPower; }
    public int getWallet(){ return this.wallet; }

    // SETTERS
    public void setName(String newName){
        if(newName != null && !newName.isBlank()){
            this.name = newName;
        }
    }
    public void setAlias(String newAlias){
        if(newAlias != null && !newAlias.isBlank()){
            this.alias = newAlias;
        }
    }
    public void setStatus(Status newStatus){
        this.status = newStatus;

        if(this.status == Status.DEAD && this.devilFruitPower != null){
            // Detaches in the fruit's side
            this.devilFruitPower.triggerReincarnation();
            // Detaches in the character's side
            this.devilFruitPower = null;
        }
    }

    // Adding money to wallet
    public boolean addWallet(int amount){
        if(amount > 0) {
            this.wallet += amount;
            return true;
        }
        else{
            return false;
        }
    }
    // Deducting money from wallet
    public boolean deductWallet(int amount){
        if (hasEnoughMoney(amount)){
            this.wallet -= amount;
            return true;
        }
        else{
            return false;
        }
    }
    
    /* HELPER METHOD
        // Ensures validation rules in contact with DevilFruit.assignNewOwner.
    */
    public void setDevilFruitPower(DevilFruit devilFruit){
        if (devilFruit != null){
            this.devilFruitPower = devilFruit;
        }
    }

    /* HELPER METHOD
    // Clears this character's devil fruit reference without changing status.
    // Needed when a fruit is deleted from DevilFruitDatabase while still owned -
    // setStatus(DEAD) is too heavy-handed just to detach a fruit.
*/
    public void clearDevilFruitPower(){
        this.devilFruitPower = null;
    }


    /* HELPER METHODS
        // Sub-class behavior for a character type
        // Wallet balance
        // Devil Fruit Power
    */
    public abstract void performDuty();
    public boolean hasEnoughMoney(int amount){
        return this.wallet >= amount;
    }
    public boolean hasDevilFruit(){
        return devilFruitPower != null;
    }
}



