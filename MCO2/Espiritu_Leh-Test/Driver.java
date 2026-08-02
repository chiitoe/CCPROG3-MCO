import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/** Driver
 * Purpose: Console front end for the Grand Line Registry.
 * All user input passes through readInt / readLine so a mistyped value never
 * crashes the program or the Scanner.
 */
public class Driver {

    private static final Scanner sc = new Scanner(System.in);
    private static final CharacterDatabase charDB = new CharacterDatabase();
    private static final AffiliationDatabase affDB = new AffiliationDatabase();
    private static final DevilFruitDatabase fruitDB = new DevilFruitDatabase();
    private static final BountyDatabase bountyDB = new BountyDatabase();

    // ---------- Input helpers ----------

    /**
     * Purpose: Reads a whole number, re-prompting until the user actually types one.
     * Catching InputMismatchException here is what keeps a stray letter from
     * throwing all the way out of main.
     * @param prompt the text shown before each attempt
     * @return the integer the user entered
     */
    private static int readInt(String prompt){
        while(true){
            System.out.print(prompt);
            try{
                int value = sc.nextInt();
                sc.nextLine();          // Consumes the leftover newline
                return value;
            }
            catch(InputMismatchException e){
                System.out.println("That is not a whole number. Try again.");
                sc.nextLine();          // Discards the bad token so we do not loop forever
            }
        }
    }

    /**
     * Purpose: Reads a line of text.
     * @param prompt the text shown before the input
     * @return the raw line entered by the user
     */
    private static String readLine(String prompt){
        System.out.print(prompt);
        return sc.nextLine();
    }

    /**
     * Purpose: Reads a yes/no answer.
     * @param prompt the question shown to the user
     * @return true if the user answered yes
     */
    private static boolean readYesNo(String prompt){
        String answer = readLine(prompt + " (y/n): ").trim().toLowerCase();
        return answer.startsWith("y");
    }

    // ---------- Lookup helpers ----------

    /**
     * Purpose: Collects every registered pirate.
     * @return a list of all pirates on the roster
     */
    private static ArrayList<Pirate> listPirates(){
        ArrayList<Pirate> result = new ArrayList<>();
        for(Character c : charDB.getCharacters()){
            if(c instanceof Pirate p) result.add(p);
        }
        return result;
    }

    /**
     * Purpose: Collects every registered marine.
     * @return a list of all marines on the roster
     */
    private static ArrayList<Marine> listMarines(){
        ArrayList<Marine> result = new ArrayList<>();
        for(Character c : charDB.getCharacters()){
            if(c instanceof Marine m) result.add(m);
        }
        return result;
    }

    /**
     * Purpose: Finds a character by ID but only accepts one of the expected subtype.
     * @param id the character ID to look up
     * @param type the required class, e.g. Pirate.class
     * @return the matching character, or null if absent or of the wrong type
     */
    private static Character findTypedCharacter(int id, Class<?> type){
        Character c = charDB.findCharacterByID(id);
        return (c != null && type.isInstance(c)) ? c : null;
    }

    /**
     * Purpose: Asks the user to nominate an unaffiliated pirate as founding captain.
     * @return the chosen pirate, or null if no valid choice was made
     */
    private static Pirate pickCaptain(){
        ArrayList<Pirate> available = new ArrayList<>();
        for(Pirate p : listPirates()){
            if(p.getPirateCrew() == null) available.add(p);
        }

        if(available.isEmpty()){
            System.out.println("No unaffiliated pirates exist - create one first.");
            return null;
        }

        System.out.println("Pick a captain (a crew cannot be founded without one):");
        for(Pirate p : available){
            System.out.println("  " + p.getCharacterID() + ". " + p.getName());
        }

        int id = readInt("Captain ID: ");
        Pirate chosen = (Pirate) findTypedCharacter(id, Pirate.class);

        if(chosen == null || chosen.getPirateCrew() != null){
            System.out.println("Not a valid, unaffiliated pirate ID.");
            return null;
        }
        return chosen;
    }

    // ---------- [1] Characters ----------

    /** Purpose: Menu action - creates a character of the chosen type. */
    private static void addCharacter(){
        System.out.println("\n-- Add a Character --");
        System.out.println("[1] Pirate");
        System.out.println("[2] Marine");
        System.out.println("[3] Civilian");
        System.out.println("[4] Pirate Hunter");
        int choose = readInt("Type: ");

        if(choose < 1 || choose > 4){
            System.out.println("Invalid type.");
            return;
        }

        String name = readLine("Name: ");
        String alias = readLine("Alias: ");
        String origin = readLine("Origin: ");
        int wallet = readInt("Wallet: ");

        switch(choose){
            case 1 -> {
                int bounty = readInt("Bounty: ");
                String role = readLine("Pirate Role: ");
                Pirate p = charDB.createPirate(name, alias, origin, wallet, bounty, role);
                System.out.println("Assigned ID: " + p.getCharacterID());
            }
            case 2 -> {
                MarineRank rank = pickMarineRank();
                Marine m = charDB.createMarine(name, alias, origin, wallet, rank);
                System.out.println("Assigned ID: " + m.getCharacterID());
            }
            case 3 -> {
                String profession = readLine("Profession: ");
                String residence = readLine("Residence: ");
                Civilian c = charDB.createCivilian(name, alias, origin, wallet, profession, residence);
                System.out.println("Assigned ID: " + c.getCharacterID());
            }
            case 4 -> {
                String style = readLine("Combat Style: ");
                int captures = readInt("Confirmed Captures: ");
                PirateHunter h = charDB.createPirateHunter(name, alias, origin, wallet, style, captures);
                System.out.println("Assigned ID: " + h.getCharacterID());
            }
        }
    }

    /**
     * Purpose: Prompts for a commissioned rank, listing the enum so the menu can never
     * drift out of sync with MarineRank.
     * @return the chosen rank, or null to let the Marine constructor default to ENSIGN
     */
    private static MarineRank pickMarineRank(){
        MarineRank[] ranks = MarineRank.values();

        System.out.println("\n-- Marine Ranks --");
        for(int i = 0; i < ranks.length; i++){
            System.out.println("[" + (i + 1) + "] " + ranks[i]);
        }

        int choose = readInt("Choose a rank: ");
        if(choose < 1 || choose > ranks.length){
            System.out.println("Invalid choice - defaulting to ENSIGN.");
            return null;
        }

        System.out.println("RANK: " + ranks[choose - 1]);
        return ranks[choose - 1];
    }

    // ---------- [2] Groups ----------

    /** Purpose: Menu action - founds a pirate crew or establishes a marine corps. */
    private static void createGroup(){
        System.out.println("\n-- Create an Affiliation Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        int choose = readInt("Choose: ");

        switch(choose){
            case 1 -> {
                String crewName = readLine("Crew Name: ");
                String shipName = readLine("Ship Name: ");
                Pirate captain = pickCaptain();

                if(captain == null){
                    System.out.println("Crew creation cancelled.");
                    return;
                }

                // The constructor throws if the captain is somehow still invalid
                try{
                    PirateCrew crew = affDB.createPirateCrew(crewName, shipName, captain);
                    System.out.println("Assigned Crew ID: " + crew.getCrewID());
                }
                catch(IllegalArgumentException e){
                    System.out.println("Crew creation failed: " + e.getMessage());
                }
            }
            case 2 -> {
                String corpsName = readLine("Corps Name: ");
                String base = readLine("Base Location: ");
                String commander = readLine("Corps Commander: ");
                int funds = readInt("Operational Funds: ");
                MarineCorps corps = affDB.createMarineCorps(corpsName, base, commander, funds);
                System.out.println("Assigned Corps ID: " + corps.getCorpsID());
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    /** Purpose: Menu action - assigns an existing character to an existing group. */
    private static void assignToGroup(){
        System.out.println("\n-- Assign a Character to a Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        int type = readInt("Type: ");

        switch(type){
            case 1 -> {
                if(affDB.getPirateCrews().isEmpty()){
                    System.out.println("No crews exist yet.");
                    return;
                }
                for(Pirate p : listPirates()){
                    System.out.println("  " + p.getCharacterID() + ". " + p.getName()
                            + (p.getPirateCrew() != null ? " (already in " + p.getPirateCrew().getCrewName() + ")" : ""));
                }
                Pirate p = (Pirate) findTypedCharacter(readInt("Pirate ID: "), Pirate.class);
                if(p == null){ System.out.println("Invalid pirate ID."); return; }

                for(PirateCrew c : affDB.getPirateCrews()){
                    System.out.println("  " + c.getCrewID() + ". " + c.getCrewName());
                }
                PirateCrew crew = affDB.findPirateCrewById(readInt("Crew ID: "));
                if(crew == null){ System.out.println("Invalid crew ID."); return; }

                boolean ok = crew.recruitMember(p);
                System.out.println(ok ? "Recruited successfully." : "Recruitment failed (already affiliated).");
            }
            case 2 -> {
                if(affDB.getMarineCorpsUnits().isEmpty()){
                    System.out.println("No corps exist yet.");
                    return;
                }
                for(Marine m : listMarines()){
                    System.out.println("  " + m.getCharacterID() + ". " + m.getName()
                            + (m.getMarineCorps() != null ? " (already in " + m.getMarineCorps().getCorpsName() + ")" : ""));
                }
                Marine m = (Marine) findTypedCharacter(readInt("Marine ID: "), Marine.class);
                if(m == null){ System.out.println("Invalid marine ID."); return; }

                for(MarineCorps c : affDB.getMarineCorpsUnits()){
                    System.out.println("  " + c.getCorpsID() + ". " + c.getCorpsName());
                }
                MarineCorps corps = affDB.findMarineCorpsById(readInt("Corps ID: "));
                if(corps == null){ System.out.println("Invalid corps ID."); return; }

                boolean ok = corps.recruitMarine(m);
                System.out.println(ok ? "Recruited successfully." : "Recruitment failed (already affiliated).");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ---------- [4] & [5] Devil fruits ----------

    /** Purpose: Menu action - registers a new devil fruit. */
    private static void createDevilFruit(){
        System.out.println("\n-- Create a Devil Fruit --");
        String name = readLine("Fruit Name: ");
        Category cat = pickCategory();
        String ability = readLine("Primary Ability: ");
        DevilFruit fruit = fruitDB.createDevilFruit(name, cat, ability);
        System.out.println("Assigned Fruit ID: " + fruit.getFruitID());
    }

    /**
     * Purpose: Prompts for a fruit category, listing the enum so the menu stays in sync.
     * @return the chosen category, or null to let the constructor default to UNDETERMINED
     */
    private static Category pickCategory(){
        Category[] categories = Category.values();

        System.out.println("\n-- Fruit Categories --");
        for(int i = 0; i < categories.length; i++){
            System.out.println("[" + (i + 1) + "] " + categories[i]);
        }

        int choose = readInt("Choose a category: ");
        if(choose < 1 || choose > categories.length){
            System.out.println("Invalid choice - defaulting to UNDETERMINED.");
            return null;
        }

        System.out.println("CATEGORY: " + categories[choose - 1]);
        return categories[choose - 1];
    }

    /** Purpose: Menu action - hands an unowned fruit to a living character. */
    private static void assignDevilFruit(){
        System.out.println("\n-- Assign a Devil Fruit to a Character --");
        ArrayList<DevilFruit> fruits = fruitDB.getDevilFruits();
        if(fruits.isEmpty()){ System.out.println("No devil fruits exist yet."); return; }

        for(DevilFruit f : fruits){
            System.out.println("  " + f.getFruitID() + ". " + f.getFruitName()
                    + (f.getCurrentOwner() != null ? " [owned by " + f.getCurrentOwner().getName() + "]" : " [unowned]"));
        }
        DevilFruit fruit = fruitDB.findFruitById(readInt("Fruit ID: "));
        if(fruit == null){ System.out.println("Invalid fruit ID."); return; }

        charDB.displayAllCharacters();
        Character c = charDB.findCharacterByID(readInt("Character ID: "));
        if(c == null){ System.out.println("Invalid character ID."); return; }

        boolean ok = fruit.assignNewOwner(c);
        System.out.println(ok ? "Fruit assigned successfully."
                : "Assignment failed (the fruit is taken, or the character is dead or already powered).");
    }

    // ---------- [9] [10] [11] Deletion ----------

    /** Purpose: Menu action - deletes a character and severs all their links. */
    private static void deleteCharacter(){
        System.out.println("\n-- Delete a Character --");
        charDB.displayAllCharacters();
        int id = readInt("Character ID to delete: ");

        boolean ok = charDB.deleteCharacter(id);
        System.out.println(ok ? "Character deleted (affiliations and fruit released)."
                : "No character with that ID.");
    }

    /** Purpose: Menu action - deletes a devil fruit. */
    private static void deleteDevilFruit(){
        System.out.println("\n-- Delete a Devil Fruit --");
        for(DevilFruit f : fruitDB.getDevilFruits()){
            System.out.println("  " + f.getFruitID() + ". " + f.getFruitName());
        }
        boolean ok = fruitDB.deleteDevilFruit(readInt("Fruit ID to delete: "));
        System.out.println(ok ? "Fruit deleted (owner's reference cleared, if any)." : "Deletion failed.");
    }

    /** Purpose: Menu action - disbands a crew or dissolves a corps. */
    private static void deleteGroup(){
        System.out.println("\n-- Delete a Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        int type = readInt("Type: ");

        switch(type){
            case 1 -> {
                for(PirateCrew c : affDB.getPirateCrews()){
                    System.out.println("  " + c.getCrewID() + ". " + c.getCrewName());
                }
                boolean ok = affDB.deletePirateCrew(readInt("Crew ID to delete: "));
                System.out.println(ok ? "Crew disbanded (members freed for reassignment)." : "Deletion failed.");
            }
            case 2 -> {
                for(MarineCorps c : affDB.getMarineCorpsUnits()){
                    System.out.println("  " + c.getCorpsID() + ". " + c.getCorpsName());
                }
                boolean ok = affDB.deleteMarineCorps(readInt("Corps ID to delete: "));
                System.out.println(ok ? "Corps dissolved (members freed for reassignment)." : "Deletion failed.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ---------- [12] Captures ----------

    /** Purpose: Menu action - registers a capture and appends it to the on-disk log. */
    private static void registerCapture(){
        System.out.println("\n-- Register a Capture --");
        ArrayList<Pirate> pirates = listPirates();
        if(pirates.isEmpty()){ System.out.println("No pirates exist yet."); return; }

        for(Pirate p : pirates){
            System.out.println("  " + p.getCharacterID() + ". " + p.getName()
                    + " (" + p.getStatus() + ", " + p.getBounty() + " Berries)");
        }
        Pirate target = (Pirate) findTypedCharacter(readInt("Pirate to capture (ID): "), Pirate.class);
        if(target == null){ System.out.println("Invalid pirate ID."); return; }

        charDB.displayAllCharacters();
        Character captor = charDB.findCharacterByID(readInt("Captor (ID) - cannot be a Pirate: "));

        System.out.println("[1] Dead  [2] Alive/Captured");
        boolean isDead = (readInt("Result: ") == 1);

        try{
            CaptureRecord record = bountyDB.registerCapture(target, captor, isDead);
            System.out.println("Capture registered successfully.");
            record.displayCaptureRecord();

            FileDirectory.appendCapture(record);      // Persist immediately so the log survives a crash
            System.out.println("Capture written to the log file.");
        }
        catch(InvalidCaptorException e){
            System.out.println("Capture rejected: " + e.getMessage());
        }
        catch(IllegalArgumentException e){
            System.out.println("Capture rejected: " + e.getMessage());
        }
        catch(DataIOException e){
            // The capture itself succeeded; only the disk write failed
            System.out.println("Capture recorded in memory, but the log could not be written: " + e.getMessage());
        }
    }

    // ---------- [14] File handling ----------

    /** Purpose: Menu action - the file-handling sub-menu. */
    private static void fileMenu(){
        System.out.println("\n-- File Operations --");
        System.out.println("[1] Save everything to disk");
        System.out.println("[2] Read the capture log from disk");
        System.out.println("[3] Show file information");
        System.out.println("[4] Archive a saved file (byte-for-byte copy)");
        System.out.println("[5] Delete the capture log");
        int choose = readInt("Choose: ");

        try{
            switch(choose){
                case 1 -> {
                    FileDirectory.saveCharacters(charDB.getCharacters());
                    FileDirectory.saveDevilFruits(fruitDB.getDevilFruits());
                    FileDirectory.saveAffiliations(affDB.getPirateCrews(), affDB.getMarineCorpsUnits());
                    System.out.println("All registry data saved.");
                }
                case 2 -> FileDirectory.readCaptureLog();
                case 3 -> FileDirectory.showFileInfo();
                case 4 -> {
                    String fileName = readLine("File to archive (e.g. characters.txt): ");
                    FileDirectory.archiveFile(fileName);
                }
                case 5 -> {
                    if(readYesNo("Permanently delete the capture log?")){
                        boolean ok = FileDirectory.clearCaptureLog();
                        System.out.println(ok ? "Capture log deleted." : "Deletion failed - the log may not exist.");
                    }
                    else{
                        System.out.println("Cancelled.");
                    }
                }
                default -> System.out.println("Invalid choice.");
            }
        }
        catch(DataIOException e){
            System.out.println("File operation failed: " + e.getMessage());
            if(e.getCause() != null){
                System.out.println("Underlying cause: " + e.getCause());
            }
        }
    }

    /**
     * Purpose: Entry point - runs the main menu loop until the user exits.
     * @param args unused
     */
    public static void main(String[] args){
        boolean running = true;

        while(running){
            System.out.println("\n=== GRAND LINE REGISTRY ===");
            System.out.println("[1]  Create a Character");
            System.out.println("[2]  Create an Affiliation Group");
            System.out.println("[3]  Assign a Character to a Group");
            System.out.println("[4]  Create a Devil Fruit");
            System.out.println("[5]  Assign a Devil Fruit to a Character");
            System.out.println("[6]  View All Characters");
            System.out.println("[7]  View All Devil Fruits");
            System.out.println("[8]  View All Groups");
            System.out.println("[9]  Delete a Character");
            System.out.println("[10] Delete a Devil Fruit");
            System.out.println("[11] Delete a Group");
            System.out.println("[12] Register a Capture");
            System.out.println("[13] View Capture History");
            System.out.println("[14] File Handling");
            System.out.println("[15] Exit");

            int choose = readInt("Choose an option: ");

            switch(choose){
                case 1 -> addCharacter();
                case 2 -> createGroup();
                case 3 -> assignToGroup();
                case 4 -> createDevilFruit();
                case 5 -> assignDevilFruit();
                case 6 -> charDB.displayAllCharacters();
                case 7 -> fruitDB.viewAllFruits();
                case 8 -> affDB.viewGroups();
                case 9 -> deleteCharacter();
                case 10 -> deleteDevilFruit();
                case 11 -> deleteGroup();
                case 12 -> registerCapture();
                case 13 -> bountyDB.viewAllCaptures();
                case 14 -> fileMenu();
                case 15 -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }

        System.out.println("See you again!");
        sc.close();
    }
}