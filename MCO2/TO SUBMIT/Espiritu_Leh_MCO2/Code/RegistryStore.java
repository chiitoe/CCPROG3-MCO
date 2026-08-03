// ===== MODEL LAYER =====

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** RegistryStore
 * Purpose: Basically Saves everything
 */
public final class RegistryStore {

    private static final String DIR = "registry_data";
    private static final String SAVE = "opums_save.txt";

    /** Purpose: Utility class - never instantiated. */
    private RegistryStore(){ }

    /**
     * Purpose: Resolves the save file, creating the data directory on first use.
     * @return the save file handle
     */
    private static File saveFile(){
        File dir = new File(DIR);
        if(!dir.exists()) dir.mkdirs();
        return new File(dir, SAVE);
    }

    /** @return true if a save file already exists on disk */
    public static boolean saveExists(){ return saveFile().exists(); }

    /**
     * Purpose: Strips pipe and newline characters so a value never breaks the format.
     * @param value the raw field value
     * @return a safe single-token value
     */
    private static String safe(String value){
        return value == null ? "" : value.replace("|", "/").replace("\n", " ").trim();
    }

    /**
     * Purpose: Writes the entire registry to the save file.
     * @param characters the roster
     * @param fruits the fruit registry
     * @param crews the pirate crews
     * @param corpsUnits the marine corps units
     * @throws DataIOException if the file cannot be written
     */
    public static void save(ArrayList<Character> characters, ArrayList<DevilFruit> fruits,
                            ArrayList<PirateCrew> crews, ArrayList<MarineCorps> corpsUnits)
            throws DataIOException {

        try(FileWriter w = new FileWriter(saveFile())){
            w.write("# OPUMS save file - do not hand-edit unless you know the format\n");

            w.write("[CHARACTERS]\n");
            for(Character c : characters){
                String base = safe(c.getName()) + "|" + safe(c.getAlias()) + "|" + safe(c.getOrigin())
                        + "|" + c.getWallet() + "|" + c.getStatus();
                switch (c) {
                    case Pirate p ->
                            w.write("Pirate|" + p.getCharacterID() + "|" + base + "|" + p.getBounty() + "|" + safe(p.getPirateRole()) + "\n");
                    case Marine m ->
                            w.write("Marine|" + m.getCharacterID() + "|" + base + "|" + m.getMarineRank() + "\n");
                    case PirateHunter h ->
                            w.write("PirateHunter|" + h.getCharacterID() + "|" + base + "|" + safe(h.getCombatStyle()) + "|" + h.getConfirmedCaptures() + "\n");
                    case Civilian civ ->
                            w.write("Civilian|" + civ.getCharacterID() + "|" + base + "|" + safe(civ.getProfession()) + "|" + safe(civ.getResidence()) + "\n");
                    default -> {
                    }
                }
            }

            w.write("[FRUITS]\n");
            for(DevilFruit f : fruits){
                StringBuilder hist = new StringBuilder();
                for(Character owner : f.getHistoricalOwners()){
                    if(!hist.isEmpty()) hist.append(",");
                    hist.append(owner.getCharacterID());
                }
                int ownerId = f.getCurrentOwner() != null ? f.getCurrentOwner().getCharacterID() : -1;
                w.write(f.getFruitID() + "|" + safe(f.getFruitName()) + "|" + f.getCategory() + "|"
                        + safe(f.getPrimaryAbility()) + "|" + ownerId + "|" + hist + "\n");
            }

            w.write("[CREWS]\n");
            for(PirateCrew c : crews){
                StringBuilder mem = new StringBuilder();
                for(Pirate p : c.getCrewMembers()){
                    if(!mem.isEmpty()) mem.append(",");
                    mem.append(p.getCharacterID());
                }
                int capId = c.getCaptain() != null ? c.getCaptain().getCharacterID() : -1;
                w.write(c.getCrewID() + "|" + safe(c.getCrewName()) + "|" + safe(c.getShipName()) + "|" + capId + "|" + mem + "\n");
            }

            w.write("[CORPS]\n");
            for(MarineCorps m : corpsUnits){
                StringBuilder mem = new StringBuilder();
                for(Marine mar : m.getMembers()){
                    if(!mem.isEmpty()) mem.append(",");
                    mem.append(mar.getCharacterID());
                }
                w.write(m.getCorpsID() + "|" + safe(m.getCorpsName()) + "|" + safe(m.getBaseLocation()) + "|"
                        + safe(m.getCorpsCommander()) + "|" + m.getOpFunds() + "|" + mem + "\n");
            }
        }
        catch(IOException e){
            throw new DataIOException("Could not write the save file", e);
        }
    }

    /**
     * Purpose: Loads the entire registry from the save file into the given databases,
     * rebuilding every object with its original ID and re-attaching all references.
     * @param charDB the character database to fill
     * @param fruitDB the fruit database to fill
     * @param affDB the affiliation database to fill
     * @throws DataIOException if the file exists but cannot be read
     */
    public static void load(CharacterDatabase charDB, DevilFruitDatabase fruitDB, AffiliationDatabase affDB)
            throws DataIOException {

        File file = saveFile();
        if(!file.exists()) return;

        Map<Integer, Character> byId = new HashMap<>();

        try(Scanner sc = new Scanner(file)){
            String section = "";
            while(sc.hasNextLine()){
                String line = sc.nextLine().trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                if(line.startsWith("[")){ section = line; continue; }

                String[] f = line.split("\\|", -1);
                switch(section){
                    case "[CHARACTERS]" -> {
                        Character c = parseCharacter(f);
                        if(c != null){
                            charDB.addLoaded(c);
                            byId.put(c.getCharacterID(), c);
                            applyStatus(c, f);
                        }
                    }
                    case "[FRUITS]" -> parseFruit(f, fruitDB, byId);
                    case "[CREWS]" -> parseCrew(f, affDB, byId);
                    case "[CORPS]" -> parseCorps(f, affDB, byId);
                    default -> { /* ignore unknown sections */ }
                }
            }
        }
        catch(IOException e){
            throw new DataIOException("Could not read the save file", e);
        }
    }

    /**
     * Purpose: Rebuilds one character from a split line (status applied separately).
     * @param f the split fields
     * @return the character, or null if the type tag is not known
     */
    private static Character parseCharacter(String[] f){
        String type = f[0];
        int id = Integer.parseInt(f[1]);
        String name = f[2], alias = f[3], origin = f[4];
        long wallet = Long.parseLong(f[5]);
        return switch(type){
            case "Pirate" -> new Pirate(id, name, alias, origin, wallet, Long.parseLong(f[7]), f[8]);
            case "Marine" -> new Marine(id, name, alias, origin, wallet, MarineRank.valueOf(f[7]));
            case "PirateHunter" -> new PirateHunter(id, name, alias, origin, wallet, f[7], Integer.parseInt(f[8]));
            case "Civilian" -> new Civilian(id, name, alias, origin, wallet, f[7], f[8]);
            default -> null;
        };
    }

    /**
     * Purpose: Restores a loaded character's status. DEAD is skipped because a dead
     * character never owns a fruit or crew slot in a valid save, and setStatus(DEAD)
     * here would run reincarnation logic against not-yet-attached references.
     * @param c the character to update
     * @param f the split fields (status at index 6)
     */
    private static void applyStatus(Character c, String[] f){
        Status status = Status.valueOf(f[6]);
        if(status != Status.FREE) c.setStatus(status);
    }

    /**
     * Purpose: Rebuilds one fruit and re-attaches its current and historical owners.
     * @param f the split fields
     * @param fruitDB the fruit database to fill
     * @param byId the id-to-character lookup built from the characters section
     */
    private static void parseFruit(String[] f, DevilFruitDatabase fruitDB, Map<Integer, Character> byId){
        DevilFruit fruit = new DevilFruit(Integer.parseInt(f[0]), f[1], Category.valueOf(f[2]), f[3]);
        int ownerId = Integer.parseInt(f[4]);
        if(ownerId >= 0 && byId.containsKey(ownerId)){
            fruit.restoreCurrentOwner(byId.get(ownerId));
        }
        if(f.length > 5 && !f[5].isBlank()){
            for(String hid : f[5].split(",")){
                Character past = byId.get(Integer.parseInt(hid.trim()));
                fruit.restoreHistoricalOwner(past);
            }
        }
        fruitDB.addLoaded(fruit);
    }

    /**
     * Purpose: Rebuilds one crew, re-recruits its members, and restores its captain.
     * @param f the split fields
     * @param affDB the affiliation database to fill
     * @param byId the id-to-character lookup
     */
    private static void parseCrew(String[] f, AffiliationDatabase affDB, Map<Integer, Character> byId){
        PirateCrew crew = new PirateCrew(Integer.parseInt(f[0]), f[1], f[2]);
        int captainId = Integer.parseInt(f[3]);
        if(f.length > 4 && !f[4].isBlank()){
            for(String mid : f[4].split(",")){
                Character c = byId.get(Integer.parseInt(mid.trim()));
                if(c instanceof Pirate p) crew.recruitMember(p);
            }
        }
        if(captainId >= 0 && byId.get(captainId) instanceof Pirate cap){
            crew.setCaptain(cap);
        }
        affDB.addLoadedCrew(crew);
    }

    /**
     * Purpose: Rebuilds one corps and re-recruits its members.
     * @param f the split fields
     * @param affDB the affiliation database to fill
     * @param byId the id-to-character lookup
     */
    private static void parseCorps(String[] f, AffiliationDatabase affDB, Map<Integer, Character> byId){
        MarineCorps corps = new MarineCorps(Integer.parseInt(f[0]), f[1], f[2], f[3], Long.parseLong(f[4]));
        if(f.length > 5 && !f[5].isBlank()){
            for(String mid : f[5].split(",")){
                Character c = byId.get(Integer.parseInt(mid.trim()));
                if(c instanceof Marine m) corps.recruitMarine(m);
            }
        }
        affDB.addLoadedCorps(corps);
    }
}
