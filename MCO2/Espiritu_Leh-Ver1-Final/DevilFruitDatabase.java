// ===== MODEL LAYER =====
import java.util.ArrayList;

/** DevilFruitDatabase
 * Purpose: Where devil fruit data is created, searched, and deleted.
 */
public class DevilFruitDatabase {

    private final ArrayList<DevilFruit> devilFruits;

    /** CONSTRUCTOR
     * Purpose: Sets up an empty fruit registry.
     */
    public DevilFruitDatabase(){
        devilFruits = new ArrayList<>();
    }

    /**
     * Purpose: Creates and registers a new devil fruit.
     * @param fruitName falls back to "Unknown" if null/blank
     * @param category falls back to UNDETERMINED if null
     * @param primaryAbility falls back to "Unknown" if null/blank
     * @return the newly registered fruit
     */
    public DevilFruit createDevilFruit(String fruitName, Category category, String primaryAbility){
        DevilFruit fruit = new DevilFruit(fruitName, category, primaryAbility);
        devilFruits.add(fruit);
        System.out.println("Created Fruit: " + fruit.getFruitName());
        return fruit;
    }

    /** Purpose: Prints the details of every registered fruit. */
    public void viewAllFruits(){
        System.out.println("=== Devil Fruits ===");
        if(devilFruits.isEmpty()){
            System.out.println("  (no fruits registered)");
            return;
        }
        for(DevilFruit fruit : devilFruits){
            fruit.displayFruit();
        }
    }

    /**
     * Purpose: Finds a devil fruit by ID.
     * @param id the fruit ID to search for
     * @return the matching fruit, or null if none exists
     */
    public DevilFruit findFruitById(int id){
        for(DevilFruit f : devilFruits){
            if(f.getFruitID() == id) return f;
        }
        return null;
    }

    /**
     * Purpose: Inserts a fruit rebuilt from disk, keeping its original ID (no printing).
     * @param f the loaded fruit
     */
    public void addLoaded(DevilFruit f){ if(f != null) devilFruits.add(f); }

    /** @return a defensive copy of the fruit registry */
    public ArrayList<DevilFruit> getDevilFruits(){
        return new ArrayList<>(this.devilFruits);
    }

    /**
     * Purpose: Removes a fruit from the registry, clearing its owner's reference first.
     * @param id the fruit ID to delete
     * @return true if a fruit was found and deleted, false otherwise
     */
    public boolean deleteDevilFruit(int id){
        DevilFruit fruit = findFruitById(id);
        if(fruit == null) return false;

        if(fruit.getCurrentOwner() != null){
            fruit.getCurrentOwner().clearDevilFruitPower();
        }
        devilFruits.remove(fruit);
        return true;
    }
}
