// ===== MODEL LAYER =====

/** SeedData
 * Purpose: Builds the pre-populated baseline the demonstration flows expect, so the
 * very first boot has a rich, connected state to load from. It runs only when no
 * save file exists yet; after the first save the file drives startup instead.
 */
public final class SeedData {

    /** Purpose: Utility class - never instantiated. */
    private SeedData(){ }

    /**
     * Purpose: Populates the three databases with a starter world:
     * a Marine corps with two marines, the Straw Hat crew with a fruit-owning captain,
     * a free pirate, a pirate hunter, two civilians, and one owned + one unowned fruit.
     * @param charDB the character database to fill
     * @param fruitDB the fruit database to fill
     * @param affDB the affiliation database to fill
     */
    public static void populate(CharacterDatabase charDB, DevilFruitDatabase fruitDB, AffiliationDatabase affDB){
        // --- Marines + a corps ---
        Marine smoker = charDB.createMarine("Smoker", "White Hunter", "Grand Line", 8000, MarineRank.COMMODORE);
        Marine tashigi = charDB.createMarine("Tashigi", "Ensign", "Grand Line", 3000, MarineRank.CAPTAIN);
        MarineCorps g5 = affDB.createMarineCorps("G-5 Base", "New World", "Smoker", 100000);
        g5.recruitMarine(smoker);
        g5.recruitMarine(tashigi);

        // --- Straw Hat crew ---
        Pirate luffy = charDB.createPirate("Monkey D. Luffy", "Straw Hat", "East Blue", 5000, 3000000000L, "Unassigned");
        Pirate zoro = charDB.createPirate("Roronoa Zoro", "Pirate Hunter Zoro", "East Blue", 2000, 1111000000, "Unassigned");
        Pirate nami = charDB.createPirate("Nami", "Cat Burglar", "East Blue", 4000, 366000000, "Navigator");
        Pirate sanji = charDB.createPirate("Sanji", "Black Leg", "North Blue", 3000, 1032000000, "Cook");
        PirateCrew strawHats = affDB.createPirateCrew("Straw Hat Pirates", "Thousand Sunny", luffy);
        strawHats.recruitMember(zoro);
        strawHats.recruitMember(nami);
        strawHats.recruitMember(sanji);

        // --- A free (unaffiliated) pirate, available for crew assignment ---
        charDB.createPirate("Buggy", "The Clown", "Grand Line", 1500, 3189000000L, "Unassigned");

        // --- A pirate hunter ---
        charDB.createPirateHunter("Ryuma", "Sword God", "Wano", 2500, "Swordsmanship", 3);

        // --- Civilians ---
        charDB.createCivilian("Kaya", "Village Heiress", "Syrup Village", 1200, "Doctor", "Syrup Village");
        charDB.createCivilian("Tom", "Master Shipwright", "Water 7", 900, "Shipwright", "Water 7");

        // --- Devil fruits: one owned by the captain, one left unowned ---
        DevilFruit gomu = fruitDB.createDevilFruit("Gomu Gomu no Mi", Category.MYTHICAL, "Rubber-body Nika transformation");
        gomu.assignNewOwner(luffy);
        fruitDB.createDevilFruit("Hana Hana no Mi", Category.PARAMECIA, "Sprout body parts anywhere");
    }
}
