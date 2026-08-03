// ===== MODEL LAYER =====
import java.util.ArrayList;

/** CharacterDatabase
 * Purpose: Where character data is created, searched, and deleted.
 */
public class CharacterDatabase {

    private final ArrayList<Character> characters;

    /** CONSTRUCTOR
     * Purpose: Sets up an empty character roster.
     */
    public CharacterDatabase(){
        characters = new ArrayList<>();
    }

    /**
     * Purpose: Creates and registers a new pirate.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param bounty falls back to 0 if negative
     * @param pirateRole falls back to "Unassigned" if null/blank
     * @return the newly registered pirate
     */
    public Pirate createPirate(String name, String alias, String origin, long wallet, long bounty, String pirateRole){
        Pirate pirate = new Pirate(name, alias, origin, wallet, bounty, pirateRole);
        characters.add(pirate);

        System.out.println("Created Pirate: " + pirate.getName());
        return pirate;
    }

    /**
     * Purpose: Creates and registers a new marine.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param marineRank falls back to ENSIGN if null
     * @return the newly registered marine
     */
    public Marine createMarine(String name, String alias, String origin, long wallet, MarineRank marineRank){
        Marine marine = new Marine(name, alias, origin, wallet, marineRank);
        characters.add(marine);

        System.out.println("Created Marine: " + marine.getName());
        return marine;
    }

    /**
     * Purpose: Creates and registers a new pirate hunter.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param combatStyle falls back to "None" if null/blank
     * @param confirmedCaptures falls back to 0 if negative
     * @return the newly registered pirate hunter
     */
    public PirateHunter createPirateHunter(String name, String alias, String origin, long wallet,
                                           String combatStyle, int confirmedCaptures){
        PirateHunter pirateHunter = new PirateHunter(name, alias, origin, wallet, combatStyle, confirmedCaptures);
        characters.add(pirateHunter);

        System.out.println("Created Pirate Hunter: " + pirateHunter.getName());
        return pirateHunter;
    }

    /**
     * Purpose: Creates and registers a new civilian.
     * @param name falls back to "Unknown" if null/blank
     * @param alias falls back to "Unknown" if null/blank
     * @param origin falls back to "Unknown" if null/blank
     * @param wallet falls back to 0 if negative
     * @param profession falls back to "Unemployed" if null/blank
     * @param residence falls back to "Homeless" if null/blank
     * @return the newly registered civilian
     */
    public Civilian createCivilian(String name, String alias, String origin, long wallet,
                                   String profession, String residence){
        Civilian civilian = new Civilian(name, alias, origin, wallet, profession, residence);
        characters.add(civilian);

        System.out.println("Created Civilian: " + civilian.getName());
        return civilian;
    }

    /**
     * Purpose: Removes a character from the roster, first severing every link that
     * would otherwise dangle: crew or corps membership, captaincy, and devil fruit.
     * @param id the character ID to delete
     * @return true if a character was found and deleted, false otherwise
     */
    public boolean deleteCharacter(int id){
        Character character = findCharacterByID(id);
        if(character == null) return false;

        // Sever affiliation - goodbyeMember also vacates the captain's seat if needed
        if(character instanceof Pirate pirate && pirate.getPirateCrew() != null){
            pirate.getPirateCrew().goodbyeMember(pirate);
        }
        if(character instanceof Marine marine && marine.getMarineCorps() != null){
            marine.getMarineCorps().goodbyeMember(marine);
        }

        // Release the devil fruit back into the world
        if(character.hasDevilFruit()){
            character.getDevilFruitPower().triggerReincarnation();
            character.clearDevilFruitPower();
        }

        characters.remove(character);
        return true;
    }

    /** Purpose: Prints the full profile of every registered character. */
    public void displayAllCharacters(){
        System.out.println("=== Characters ===");
        if(characters.isEmpty()){
            System.out.println("  (no characters registered)");
            return;
        }
        for(Character c : characters){
            c.displayProfile();
        }
    }

    /**
     * Purpose: Finds a character by ID.
     * @param id the character ID to search for
     * @return the matching character, or null if none exists
     */
    public Character findCharacterByID(int id){
        for(Character c : characters){
            if(c.getCharacterID() == id){
                return c;
            }
        }
        return null;
    }

    /**
     * Purpose: Inserts a character rebuilt from disk, keeping its original ID (no printing).
     * @param c the loaded character
     */
    public void addLoaded(Character c){ if(c != null) characters.add(c); }

    /** @return a defensive copy of the character roster */
    public ArrayList<Character> getCharacters(){ return new ArrayList<>(this.characters); }
}
