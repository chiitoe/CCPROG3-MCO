import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    private static Scanner sc = new Scanner(System.in);
    private static CharacterDatabase charDB = new CharacterDatabase();
    private static AffiliationDatabase affDB = new AffiliationDatabase();
    private static DevilFruitDatabase fruitDB = new DevilFruitDatabase();

    // Helpers used by [1], [2], [3]
    private static ArrayList<Pirate> listPirates() {
        ArrayList<Pirate> result = new ArrayList<>();
        for (Character c : charDB.getCharacters()) {
            if (c instanceof Pirate p) result.add(p);
        }
        return result;
    }

    private static ArrayList<Marine> listMarines() {
        ArrayList<Marine> result = new ArrayList<>();
        for (Character c : charDB.getCharacters()) {
            if (c instanceof Marine m) result.add(m);
        }
        return result;
    }

    private static Character findTypedCharacter(int id, Class<?> type) {
        Character c = charDB.findCharacterByID(id);
        return (c != null && type.isInstance(c)) ? c : null;
    }

    private static Pirate pickCaptain() {
        ArrayList<Pirate> pirates = listPirates();
        if (pirates.isEmpty()) {
            System.out.println("No pirates exist yet - creating crew with no captain.");
            return null;
        }
        System.out.println("Pick a captain (existing pirate ID), or 0 to skip:");
        for (Pirate p : pirates) System.out.println("  " + p.getCharacterID() + ". " + p.getName());
        int id = sc.nextInt();
        sc.nextLine();
        if (id == 0) return null;
        Pirate chosen = (Pirate) findTypedCharacter(id, Pirate.class);
        if (chosen == null) System.out.println("Not a valid pirate ID - creating crew with no captain.");
        return chosen;
    }

    // [1]
    private static void addCharacter() {
        System.out.println("\n-- Add a Character --");
        System.out.println("[1] Pirate ");
        System.out.println("[2] Marine ");
        System.out.println("[3] Civilian ");
        System.out.println("[2] Hunter ");
        System.out.print("Type: ");
        int choose = sc.nextInt();
        sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Alias: ");
        String alias = sc.nextLine();
        System.out.print("Origin: ");
        String origin = sc.nextLine();
        System.out.print("Wallet: ");
        int wallet = sc.nextInt();
        sc.nextLine();

        switch (choose) {
            case 1 -> {
                System.out.print("Bounty: ");
                int bounty = sc.nextInt();
                sc.nextLine();
                System.out.print("Pirate Role: ");
                String role = sc.nextLine();
                Pirate p = charDB.createPirate(name, alias, origin, wallet, bounty, role);
                System.out.println("Assigned ID: " + p.getCharacterID());
            }
            case 2 -> {
                MarineRank rank = pickMarineRank();
                Marine m = charDB.createMarine(name, alias, origin, wallet, rank);
                System.out.println("Assigned ID: " + m.getCharacterID());
            }
            case 3 -> {
                System.out.print("Profession: ");
                String profession = sc.nextLine();
                System.out.print("Residence: ");
                String residence = sc.nextLine();
                Civilian c = charDB.createCivilian(name, alias, origin, wallet, profession, residence);
                System.out.println("Assigned ID: " + c.getCharacterID());
            }
            case 4 -> {
                System.out.print("Combat Style: ");
                String style = sc.nextLine();
                System.out.print("Confirmed Captures: ");
                int captures = sc.nextInt();
                sc.nextLine();

                PirateHunter h = charDB.createPirateHunter(name, alias, origin, wallet, style, captures);
                System.out.println("Assigned ID: " + h.getCharacterID());
            }
            default -> System.out.println("Invalid type.");
        }
    }
    // Helper for [1]
    private static MarineRank pickMarineRank() {

        System.out.println("\n=== MENU ===");
        System.out.println("[1] ENSIGN ");
        System.out.println("[2] LIEUTENANT ");
        System.out.println("[3] COMMANDER ");
        System.out.println("[4] CAPTAIN ");
        System.out.println("[5] REAR ADMIRAL ");
        System.out.println("[6] VICE ADMIRAL ");
        System.out.println("[7] ADMIRAL ");
        System.out.println("[8] FLEET ADMIRAL ");
        System.out.print("Choose an option: ");
        int choose = sc.nextInt();
        MarineRank rank = null;
        switch (choose) {
            case 1 -> { System.out.println("RANK: ENSIGN "); rank = MarineRank.ENSIGN; }
            case 2 -> { System.out.println("RANK: LIEUTENANT "); rank = MarineRank.LIEUTENANT; }
            case 3 -> { System.out.println("RANK: COMMANDER "); rank = MarineRank.COMMANDER; }
            case 4 -> { System.out.println("RANK: CAPTAIN "); rank = MarineRank.CAPTAIN; }
            case 5 -> { System.out.println("RANK: REAR ADMIRAL "); rank = MarineRank.REAR_ADMIRAL; }
            case 6 -> { System.out.println("RANK: VICE ADMIRAL "); rank = MarineRank.VICE_ADMIRAL; }
            case 7 -> { System.out.println("RANK: ADMIRAL "); rank = MarineRank.ADMIRAL; }
            case 8 -> { System.out.println("RANK: FLEET ADMIRAL "); rank = MarineRank.FLEET_ADMIRAL; }
            default -> System.out.println("Invalid choice.");

        }
        return rank;
    }

    // [2]
    private static void createGroup(){
        System.out.println("\n-- Create an Affiliation Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        System.out.print("Choose: ");
        int choose = sc.nextInt();
        sc.nextLine();

        switch (choose) {
            case 1 -> {
                System.out.print("Crew Name: ");
                String crewName = sc.nextLine();
                System.out.print("Ship Name: ");
                String shipName = sc.nextLine();
                Pirate captain = pickCaptain();
                PirateCrew crew = affDB.createPirateCrew(crewName, shipName, captain);
                System.out.println("Assigned Crew ID: " + crew.getCrewID());
            }
            case 2 -> {
                System.out.print("Corps Name: ");
                String corpsName = sc.nextLine();
                System.out.print("Base Location: ");
                String base = sc.nextLine();
                System.out.print("Corps Commander: ");
                String commander = sc.nextLine();
                System.out.print("Operational Funds: ");
                int funds = sc.nextInt();
                sc.nextLine();
                MarineCorps corps = affDB.createMarineCorps(corpsName, base, commander, funds);
                System.out.println("Assigned Corps ID: " + corps.getCorpsID());
            }
            default -> System.out.println("Invalid choice.");

        }

    }

    //[3]
    private static void assignToGrp(){
        System.out.println("\n-- Assign a Character to a Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        System.out.print("Type: ");
        int type = sc.nextInt();
        sc.nextLine();

        switch (type){
            case 1 -> {
                listPirates().forEach(p -> System.out.println("  " + p.getCharacterID() + ". " + p.getName()
                        + (p.getPirateCrew() != null ? " (already in " + p.getPirateCrew().getCrewName() + ")" : "")));
                System.out.print("Pirate ID: ");
                Pirate p = (Pirate) findTypedCharacter(sc.nextInt(), Pirate.class);
                if (p == null) { System.out.println("Invalid pirate ID."); return;}

                affDB.getPirateCrews().forEach(c -> System.out.println("  " + c.getCrewID() + ". " + c.getCrewName()));
                System.out.print("Crew ID: ");
                PirateCrew crew = affDB.findPirateCrewById(sc.nextInt());
                if (crew == null) { System.out.println("Invalid crew ID."); return; }

                boolean ok = crew.recruitMember(p);
                System.out.println(ok ? "Recruited successfully." : "Recruitment failed (already affiliated).");

        }
            case 2 -> {
                listMarines().forEach(m -> System.out.println("  " + m.getCharacterID() + ". " + m.getName()
                        + (m.getMarineCorps() != null ? " (already in " + m.getMarineCorps().getCorpsName() + ")" : "")));
                System.out.print("Marine ID: ");
                Marine m = (Marine) findTypedCharacter(sc.nextInt(), Marine.class);
                if (m == null) { System.out.println("Invalid marine ID."); return; }

                affDB.getMarineCorpsUnits().forEach(c -> System.out.println("  " + c.getCorpsID() + ". " + c.getCorpsName()));
                System.out.print("Corps ID: ");
                MarineCorps corps = affDB.findMarineCorpsById(sc.nextInt());
                if (corps == null) { System.out.println("Invalid corps ID."); return; }

                boolean ok = corps.recruitMarine(m);
                System.out.println(ok ? "Recruited successfully." : "Recruitment failed (already affiliated).");

            }
            default -> System.out.println("Invalid choice.");
        }

    }

    // [4]
    private static void createDevilFruit() {
        System.out.println("\n-- Create a Devil Fruit --");
        System.out.print("Fruit Name: ");
        String name = sc.nextLine();
        Category cat = pickCategory();
        System.out.print("Primary Ability: ");
        String ability = sc.nextLine();
        DevilFruit fruit = fruitDB.createDevilFruit(name, cat, ability);
        System.out.println("Assigned Fruit ID: " + fruit.getFruitID());
    }
    // Helper for [4]
    private static Category pickCategory() {

        System.out.println("\n=== MENU ===");
        System.out.println("[1] PARAMECIA ");
        System.out.println("[2] ZOAN ");
        System.out.println("[3] LOGIA ");
        System.out.println("[4] UNDETERMINED ");
        System.out.print("Choose an option: ");
        int choose = sc.nextInt();
        sc.nextLine();
        Category cat = null;
        switch (choose) {
            case 1 -> { System.out.println("CATEGORY: PARAMECIA "); cat = Category.PARAMECIA; }
            case 2 -> { System.out.println("CATEGORY: ZOAN "); cat = Category.ZOAN; }
            case 3 -> { System.out.println("CATEGORY: LOGIA "); cat = Category.LOGIA; }
            case 4 -> { System.out.println("CATEGORY: UNDETERMINED "); cat = Category.UNDETERMINED; }
            default -> System.out.println("Invalid choice.");

        }
        return cat;
    }

    // [5]
    private static void assignDevilFruit() {
        System.out.println("\n-- Assign a Devil Fruit to a Character --");
        ArrayList<DevilFruit> fruits = fruitDB.getDevilFruits();
        if (fruits.isEmpty()) { System.out.println("No devil fruits exist yet."); return; }
        fruits.forEach(f -> System.out.println("  " + f.getFruitID() + ". " + f.getFruitName()
                + (f.getCurrentOwner() != null ? " [owned by " + f.getCurrentOwner().getName() + "]" : " [unowned]")));
        System.out.print("Fruit ID: ");
        DevilFruit fruit = fruitDB.findFruitById(sc.nextInt());
        sc.nextLine();
        if (fruit == null) { System.out.println("Invalid fruit ID."); return; }

        charDB.displayAllCharacters();
        System.out.print("Character ID: ");
        Character c = charDB.findCharacterByID(sc.nextInt());
        sc.nextLine();
        if (c == null) { System.out.println("Invalid character ID."); return; }

        boolean ok = fruit.assignNewOwner(c);
        System.out.println(ok ? "Fruit assigned successfully." : "Assignment failed (fruit or character already has one).");
    }

    // [9]
    private static void deleteCharacter() {
        System.out.println("\n-- Delete a Character --");
        charDB.displayAllCharacters();
        System.out.print("Character ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();
        Character c = charDB.findCharacterByID(id);
        if (c == null) { System.out.println("No character with that ID."); return; }

        if (c instanceof Pirate p && p.getPirateCrew() != null) {
            p.getPirateCrew().goodbyeMember(p);
        }
        if (c instanceof Marine m && m.getMarineCorps() != null) {
            m.getMarineCorps().goodbyeMember(m);
        }
        if (c.hasDevilFruit()) {
            c.getDevilFruitPower().triggerReincarnation();
            c.clearDevilFruitPower();
        }

        boolean ok = charDB.deleteCharacter(id);
        System.out.println(ok ? "Character deleted." : "Deletion failed.");
    }

    // [10]
    private static void deleteDevilFruit() {
        System.out.println("\n-- Delete a Devil Fruit --");
        fruitDB.getDevilFruits().forEach(f -> System.out.println("  " + f.getFruitID() + ". " + f.getFruitName()));
        System.out.print("Fruit ID to delete: ");
        boolean ok = fruitDB.deleteDevilFruit(sc.nextInt());
        sc.nextLine();
        System.out.println(ok ? "Fruit deleted (owner's reference cleared, if any)." : "Deletion failed.");
    }

    // [11]
    private static void deleteGroup() {
        System.out.println("\n-- Delete a Group --");
        System.out.println("[1] Pirate Crew");
        System.out.println("[2] Marine Corps");
        System.out.print("Type: ");
        int type = sc.nextInt();
        sc.nextLine();

        switch (type) {
            case 1 -> {
                affDB.getPirateCrews().forEach(c -> System.out.println("  " + c.getCrewID() + ". " + c.getCrewName()));
                System.out.print("Crew ID to delete: ");
                boolean ok = affDB.deletePirateCrew(sc.nextInt());
                sc.nextLine();
                System.out.println(ok ? "Crew deleted (members freed for reassignment)." : "Deletion failed.");
            }
            case 2 -> {
                affDB.getMarineCorpsUnits().forEach(c -> System.out.println("  " + c.getCorpsID() + ". " + c.getCorpsName()));
                System.out.print("Corps ID to delete: ");
                boolean ok = affDB.deleteMarineCorps(sc.nextInt());
                sc.nextLine();
                System.out.println(ok ? "Corps deleted (members freed for reassignment)." : "Deletion failed.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU ===");
            System.out.println("[1] Create a Character");
            System.out.println("[2] Choose an Affiliation Group");
            System.out.println("[3] Add a Character to a Crew ");
            System.out.println("[4] Create a Devil Fruit ");
            System.out.println("[5] Assign a Devil Fruit to a Character ");
            System.out.println("[6] View All Characters ");
            System.out.println("[7] View All Devil Fruits");
            System.out.println("[8] View All Groups");
            System.out.println("[9] Delete a Character");
            System.out.println("[10] Delete a Devil Fruit");
            System.out.println("[11] Delete a Group");
            System.out.println("[12] Exit");
            System.out.print("Choose an option: ");
            int choose = sc.nextInt();
            sc.nextLine();
            switch (choose) {
                case 1 -> addCharacter();
                case 2 -> createGroup();
                case 3 -> assignToGrp();
                case 4 -> createDevilFruit();
                case 5 -> assignDevilFruit();
                case 6 -> charDB.displayAllCharacters();
                case 7 -> fruitDB.viewAllFruits();
                case 8 -> affDB.viewGroups();
                case 9 -> deleteCharacter();
                case 10 -> deleteGroup();
                case 11 -> deleteDevilFruit();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("See you again!");
    }
}