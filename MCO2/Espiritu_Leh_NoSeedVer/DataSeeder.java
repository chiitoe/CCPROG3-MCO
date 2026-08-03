/** DataSeeder
 * Purpose: Populates a fresh registry with a starting set of sample data,
 * so both the console (Driver) and GUI (Controller) entry points can seed
 * identical data without duplicating the same character list twice.
 */
public final class DataSeeder {

    /** Purpose: Utility class. */
    private DataSeeder(){ }

    /**
     * Purpose: Creates a small cast of characters, groups, and a devil fruit,
     * registering them into the databases passed in.
     * @param charDB the character registry to populate
     * @param affDB the affiliation registry to populate
     * @param fruitDB the devil fruit registry to populate
     */
    public static void seed(CharacterDatabase charDB, AffiliationDatabase affDB, DevilFruitDatabase fruitDB){
        Pirate luffy = charDB.createPirate("Monkey D. Luffy", "Straw Hat", "East Blue", 100, 300000000, "Captain");
        Pirate zoro  = charDB.createPirate("Roronoa Zoro", "Pirate Hunter", "East Blue", 200, 111000000, "Combatant");
        Pirate nami  = charDB.createPirate("Nami", "Cat Burglar", "Cocoyasi Village", 300, 66000000, "Navigator");
        Pirate buggy = charDB.createPirate("Buggy", "Clown", "East Blue", 50, 15000000, "Captain");

        Marine smoker = charDB.createMarine("Smoker", "White Hunter", "Loguetown", 10000, MarineRank.VICE_ADMIRAL);
        Marine tashigi = charDB.createMarine("Tashigi", "Tashigi", "Loguetown", 3000, MarineRank.ENSIGN);

        Civilian makino = charDB.createCivilian("Makino", "Makino", "Windmill Village", 1000, "Bartender", "Windmill Village");
        Civilian yasopp = charDB.createCivilian("Yasopp", "Yasopp", "Syrup Village", 800, "Sniper", "Syrup Village");

        PirateHunter johnny = charDB.createPirateHunter("Johnny", "Johnny", "East Blue", 500, "Swordsmanship", 3);

        PirateCrew strawHats = affDB.createPirateCrew("Straw Hat Pirates", "Going Merry", luffy);
        strawHats.recruitMember(zoro);
        strawHats.recruitMember(nami);

        MarineCorps marineG5 = affDB.createMarineCorps("Marine G-5", "New World", "Doberman", 500000);
        marineG5.recruitMarine(smoker);

        DevilFruit gomuGomu = fruitDB.createDevilFruit("Gomu Gomu no Mi", Category.PARAMECIA, "Rubber body");
        gomuGomu.assignNewOwner(luffy);
    }
}