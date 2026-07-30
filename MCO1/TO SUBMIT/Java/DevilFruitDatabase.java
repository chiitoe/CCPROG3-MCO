import java.util.ArrayList;

public class DevilFruitDatabase {
    private final ArrayList<DevilFruit> devilFruits;
    
    /* CONSTRUCTOR
        Purpose: Sets up empty group lists.
    */
    public DevilFruitDatabase(){
        devilFruits = new ArrayList<>();
    }

    /* HELPER METHOD
        // Creates and registers a new DevilFruit and returns it.
    */
    public DevilFruit createDevilFruit(String fruitName, Category category, String primaryAbility) {
        DevilFruit fruit = new DevilFruit(fruitName, category, primaryAbility);
        devilFruits.add(fruit);
        System.out.println("Created Fruit: " + fruit.getFruitName());
        return fruit;
    }

    // View all fruits
    public void viewAllFruits() {
        System.out.println("=== Devil Fruits ===");
        for (DevilFruit fruit : devilFruits) {
            fruit.displayFruit();
        }
    }

    // Find fruit via unique id number
    public DevilFruit findFruitById(int id){
        for (DevilFruit f : devilFruits){
            if (f.getFruitID() == id) return f;
        }
        return null;
    }

    public ArrayList<DevilFruit> getDevilFruits(){
        // Creates and returns a copy of the list for data integrity
        return new ArrayList<>(this.devilFruits);
    }

    public boolean deleteDevilFruit(int id){
        DevilFruit fruit = findFruitById(id);
        if (fruit == null) return false;

        if (fruit.getCurrentOwner() != null){
            fruit.getCurrentOwner().clearDevilFruitPower();
        }
        devilFruits.remove(fruit);
        return true;
    }

}
