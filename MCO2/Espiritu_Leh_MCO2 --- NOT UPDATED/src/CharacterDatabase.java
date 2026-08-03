import java.util.ArrayList;

// This is where the character data is managed
public class CharacterDatabase {

    private final ArrayList<Character> characters;

    
    /* CONSTRUCTOR
        Purpose: sets up empty group lists.
    */
    public CharacterDatabase() {
        characters = new ArrayList<>();
    }

    /* HELPER FUNCTION
       // Creates and registers a new Pirate and then returns it
    */
    public Pirate createPirate(String name, String alias, String origin, int wallet, int bounty, String pirateRole){
        Pirate pirate = new Pirate(name, alias, origin, wallet, bounty, pirateRole);
        characters.add(pirate);

        System.out.println("Created Pirate: " + pirate.getName());
        return pirate;
    }
    /* HELPER FUNCTION
       // Creates and registers a new Pirate and then returns it
    */
    public Marine createMarine(String name, String alias, String origin, int wallet, MarineRank marineRank){
        Marine marine = new Marine(name, alias, origin, wallet, marineRank);
        characters.add(marine);

        System.out.println("Created Marine: " + marine.getName());
        return marine;
    }
    /* HELPER FUNCTION
       // Creates and registers a new PirateHunter and then returns it
    */
    public PirateHunter createPirateHunter(String name, String alias, String origin, int wallet, String combatStyle, int confirmedCaptures){
        PirateHunter pirateHunter = new PirateHunter(name, alias, origin, wallet, combatStyle, confirmedCaptures);
        characters.add(pirateHunter);

        System.out.println("Created Pirate Hunter: " + pirateHunter.getName());
        return pirateHunter;
    }
    /* HELPER FUNCTION
       // Creates and registers a new Civilian and then returns it
    */
    public Civilian createCivilian(String name, String alias, String origin, int wallet, String profession, String residence){
        Civilian civilian = new Civilian(name, alias, origin, wallet, profession, residence);
        characters.add(civilian);

        System.out.println("Created Civilian: " + civilian.getName());
        return civilian;
    }

    // Delete a character
    public boolean deleteCharacter(int id){
        Character character = findCharacterByID(id);
        if(character != null){
            characters.remove(character);
            return true;
        }
        return false;
    }

    // View all characters
    public void displayAllCharacters() {
        System.out.println("=== Characters ===");
        for (Character c : characters) {
            System.out.println("- [" + c.getCharacterID() + "] " + c.getName());
        }
    }

    /* HELPER METHOD
       // Finds the character id and returns it
    */
    public Character findCharacterByID(int id){
        for(Character c: characters){
            if(c.getCharacterID() == id){
                return c;
            }
        }
        return null;
    }
}