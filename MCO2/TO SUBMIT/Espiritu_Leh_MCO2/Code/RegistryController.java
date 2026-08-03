// ===== CONTROLLER =====
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/** RegistryController
 * Purpose: The Controller layer. It owns the model databases, attaches the
 * listener to the View, validates user input, and pushes refreshed data back.
 */
public class RegistryController {

    private final MainFrame view;
    private final CharacterDatabase charDB = new CharacterDatabase();
    private final AffiliationDatabase affDB = new AffiliationDatabase();
    private final DevilFruitDatabase fruitDB = new DevilFruitDatabase();
    private final BountyDatabase bountyDB = new BountyDatabase();

    /** CONSTRUCTOR
     * Purpose: Binds this controller to a view and wires up every control.
     * @param view the window to drive
     */
    public RegistryController(MainFrame view){
        this.view = view;
        attachNavigation();
        attachCharacterActions();
        attachGroupActions();
        attachFruitActions();
        attachCaptureActions();
        attachFileActions();
        attachExitPrompt();
        bootData();
        refreshAll();
    }

    /**
     * Purpose: Establishes the initial state. If a save file exists it is loaded from
     * disk otherwise a seed world is generated and immediately saved so the file exists from then on.
     */
    private void bootData(){
        try{
            if(RegistryStore.saveExists()){
                RegistryStore.load(charDB, fruitDB, affDB);
                view.log("Loaded previous save.");
            }
            else{
                DataSeeder.seed(charDB, affDB, fruitDB);
                RegistryStore.save(charDB.getCharacters(), fruitDB.getDevilFruits(),
                        affDB.getPirateCrews(), affDB.getMarineCorpsUnits());
                view.log("No save found — started with sample data.");
            }
        }
        catch(DataIOException e){
            view.log("Startup data problem: " + e.getMessage());
        }
    }

    // ---------- Wiring ----------

    /**
     * The card each menu option opens; CARD_FOR[i] serves option [i+1].
     * Several options share a screen (e.g. view and delete a character both
     * open the roster).
     */
    private static final String[] CARD_FOR = {
            "characters", "groups", "groups", "fruits", "fruits", "characters",
            "fruits", "groups", "characters", "fruits", "groups", "captures",
            "captures", "wanted", "files"
    };

    /** Purpose: Points each numbered menu button at its screen, refreshing each time. */
    private void attachNavigation(){
        for(int i = 0; i < view.menuButtons.length; i++){
            String card = CARD_FOR[i];
            String label = MainFrame.MENU_LABELS[i];
            view.menuButtons[i].addActionListener(e -> {
                refreshAll();
                view.log("Opening: " + label);
                view.show(card);
            });
        }
        view.menuExit.addActionListener(e -> promptExit());
    }

    /** Purpose: Wires the character form. */
    private void attachCharacterActions(){
        view.charType.addActionListener(e -> updateCharacterForm());
        updateCharacterForm();

        view.charAdd.addActionListener(e -> addCharacter());
        view.charDelete.addActionListener(e -> deleteCharacter());
        view.charDuty.addActionListener(e -> performDuty());
        view.charPromote.addActionListener(e -> promoteMarine());
        view.charProfile.addActionListener(e -> viewProfile());
        view.charEdit.addActionListener(e -> editCharacter());
    }

    /** Purpose: Wires crew founding, corps commissioning, recruitment, and management. */
    private void attachGroupActions(){
        view.crewCreate.addActionListener(e -> createCrew());
        view.corpsCreate.addActionListener(e -> createCorps());
        view.groupRecruit.addActionListener(e -> recruitIntoGroup());
        view.groupSetCaptain.addActionListener(e -> setCrewCaptain());
        view.groupDischarge.addActionListener(e -> dischargeMember());
        view.groupMembers.addActionListener(e -> viewGroupMembers());
        view.groupEdit.addActionListener(e -> editGroup());
        view.groupDelete.addActionListener(e -> deleteGroup());
    }

    /** Purpose: Wires fruit cataloging, assignment, details, and deletion. */
    private void attachFruitActions(){
        view.fruitAdd.addActionListener(e -> addFruit());
        view.fruitAssign.addActionListener(e -> assignFruit());
        view.fruitDetails.addActionListener(e -> viewFruitDetails());
        view.fruitDelete.addActionListener(e -> deleteFruit());
    }

    /** Purpose: Wires the bounty office and the leaderboard refresh. */
    private void attachCaptureActions(){
        view.captureRegister.addActionListener(e -> registerCapture());
        view.wantedRefresh.addActionListener(e -> refreshWanted());
    }

    /** Purpose: Wires every archives button to its FileManager operation. */
    private void attachFileActions(){
        view.fileSave.addActionListener(e -> saveAll(true));
        view.fileImport.addActionListener(e -> importSave());
        view.fileLoad.addActionListener(e -> runFileTask(FileManager::readCaptureLog, "Capture log read."));
        view.fileInfo.addActionListener(e -> { echo(capture(FileManager::showFileInfo)); status("File information listed."); });
        view.fileArchive.addActionListener(e -> archiveFile());
        view.fileSeed.addActionListener(e -> seedInitialData());
        view.fileClear.addActionListener(e -> clearLog());
    }

    /**
     * Purpose: Intercepts the window close so unsaved work can be written first.
     * BONUS FEATURE 3 - the registry offers to persist itself on exit.
     */
    private void attachExitPrompt(){
        view.addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){ promptExit(); }
        });
    }

    /**
     * Purpose: Runs the save-on-exit prompt, shared by the window's close box and the
     * Exit menu button. Offers to persist until it is canceled and exits if saving fails.
     */
    private void promptExit(){
        int answer = JOptionPane.showConfirmDialog(view,
                "Save the registry to disk before leaving?", "Leaving OPUMS",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION) return;
        if(answer == JOptionPane.YES_OPTION && !saveAll(false)) return;   // Stay open if saving failed

        view.dispose();
        System.exit(0);
    }

    // ---------- Character actions ----------

    /**
     * Purpose: Relabels the two variable inputs for the selected type and, per the
     * requirement, presents every role-type field as a dropdown: Pirate role,
     * Civilian profession, and Pirate Hunter combat style. The numeric/free-text
     * fields (bounty, residence, captures) stay as text; Marine rank stays its enum
     * dropdown.
     */
    private void updateCharacterForm(){
        String type = String.valueOf(view.charType.getSelectedItem());
        boolean marine = "Marine".equals(type);

        view.charRank.setVisible(marine);
        view.charRankLabel.setVisible(marine);
        view.charExtra1Label.setVisible(!marine);
        view.charExtra2Label.setVisible(!marine);
        view.setVariableSlotsVisible(!marine);

        switch(type){
            case "Pirate" -> {
                setExtraLabels("Bounty:", "Pirate Role:");
                view.setSlotMode(1, false);                     // bounty = text
                setCombo(view.charSlot2Combo, Pirate.ROLE_OPTIONS);
                view.setSlotMode(2, true);                      // role = dropdown
            }
            case "Civilian" -> {
                setExtraLabels("Profession:", "Residence:");
                setCombo(view.charSlot1Combo, Civilian.PROFESSION_OPTIONS);
                view.setSlotMode(1, true);                      // profession = dropdown
                view.setSlotMode(2, false);                     // residence = text
            }
            case "Pirate Hunter" -> {
                setExtraLabels("Combat Style:", "Confirmed Captures:");
                setCombo(view.charSlot1Combo, PirateHunter.COMBAT_OPTIONS);
                view.setSlotMode(1, true);                      // combat style = dropdown
                view.setSlotMode(2, false);                     // captures = text
            }
            default -> setExtraLabels("", "");
        }
    }

    /**
     * Purpose: Replaces a dropdown's options.
     * @param combo the dropdown to refill
     * @param options the values to offer
     */
    private void setCombo(JComboBox<String> combo, String[] options){
        combo.removeAllItems();
        for(String o : options){ combo.addItem(o); }
    }

    /**
     * Purpose: Applies captions to the two variable inputs.
     * @param first caption for the first field
     * @param second caption for the second field
     */
    private void setExtraLabels(String first, String second){
        view.charExtra1Label.setText(first);
        view.charExtra2Label.setText(second);
    }

    /** Purpose: Validates the character form and registers the new character. */
    private void addCharacter(){
        String name = view.charName.getText().trim();
        if(name.isEmpty()){
            warn("A character needs a name.");
            return;
        }

        Long wallet = parseMoney(view.charWallet, "Wallet");
        if(wallet == null) return;

        String alias = view.charAlias.getText().trim();
        String origin = view.charOrigin.getText().trim();
        String type = String.valueOf(view.charType.getSelectedItem());

        switch(type){
            case "Pirate" -> {
                Long bounty = parseMoney(view.charExtra1, "Bounty");
                if(bounty == null) return;
                charDB.createPirate(name, alias, origin, wallet, bounty, comboText(view.charSlot2Combo));
            }
            case "Marine" -> charDB.createMarine(name, alias, origin, wallet, (MarineRank) view.charRank.getSelectedItem());
            case "Civilian" -> charDB.createCivilian(name, alias, origin, wallet,
                    comboText(view.charSlot1Combo), view.charExtra2.getText().trim());
            default -> {
                Integer captures = parseNonNegative(view.charExtra2, "Confirmed Captures");
                if(captures == null) return;
                charDB.createPirateHunter(name, alias, origin, wallet, comboText(view.charSlot1Combo), captures);
            }
        }

        clear(view.charName, view.charAlias, view.charOrigin, view.charWallet, view.charExtra1, view.charExtra2);
        refreshAll();
        status(name + " joined the registry.");
    }

    /** Purpose: Deletes the selected character after confirmation. */
    private void deleteCharacter(){
        Character c = selectedCharacter();
        if(c == null) return;

        if(confirm("Strike " + c.getName() + " from the record?")){
            charDB.deleteCharacter(c.getCharacterID());
            refreshAll();
            status(c.getName() + " was struck from the record.");
        }
    }

    /**
     * Purpose: Shows the selected character's duty line in a dialog.
     *  Text pulled straight from the performDuty.
     */
    private void performDuty(){
        Character c = selectedCharacter();
        if(c == null) return;

        String line = capture(c::performDuty).trim();
        JOptionPane.showMessageDialog(view, c.getName() + ":\n\n\"" + line + "\"",
                "On Duty", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Purpose: Promotes the selected marine one rank.
     *
     */
    private void promoteMarine(){
        Character c = selectedCharacter();
        if(c == null) return;

        if(!(c instanceof Marine marine)){
            warn(c.getName() + " is not a marine.");
            return;
        }
        if(marine.promoteRank()){
            refreshAll();
            status(marine.getName() + " was promoted to " + marine.getMarineRank() + ".");
        }
        else{
            warn(marine.getName() + " already holds the highest rank.");
        }
    }

    // ---------- Group actions ----------

    /** Purpose: Found a crew under the selected unaffiliated pirate. */
    private void createCrew(){
        Character captain = fromCombo(view.crewCaptain);
        if(!(captain instanceof Pirate pirate)){
            warn("Select an unaffiliated pirate to captain the crew.");
            return;
        }

        try{
            affDB.createPirateCrew(view.crewName.getText().trim(), view.shipName.getText().trim(), pirate);
            clear(view.crewName, view.shipName);
            refreshAll();
            status(pirate.getName() + " set sail with a new crew.");
        }
        catch(IllegalArgumentException e){
            warn(e.getMessage());
        }
    }

    /** Purpose: Commissions a new marine corps unit. */
    private void createCorps(){
        Long funds = parseMoney(view.corpsFunds, "Operational Funds");
        if(funds == null) return;

        affDB.createMarineCorps(view.corpsName.getText().trim(), view.corpsBase.getText().trim(),
                view.corpsCommander.getText().trim(), funds);
        clear(view.corpsName, view.corpsBase, view.corpsCommander, view.corpsFunds);
        refreshAll();
        status("A new marine corps was commissioned.");
    }

    /** Purpose: Recruits the chosen character into the selected group. */
    private void recruitIntoGroup(){
        int row = view.groupTable.getTable().getSelectedRow();
        if(row < 0){
            warn("Select a crew or corps in the table first.");
            return;
        }

        int id = view.groupTable.getSelectedId();
        Character c = fromCombo(view.recruitTarget);
        if(c == null){
            warn("Select a character to recruit.");
            return;
        }

        PirateCrew crew = affDB.findPirateCrewById(id);
        MarineCorps corps = affDB.findMarineCorpsById(id);
        String kind = String.valueOf(view.groupTable.getTable().getValueAt(row, 1));

        boolean ok;
        if("Crew".equals(kind) && crew != null && c instanceof Pirate p){ ok = crew.recruitMember(p); }
        else if("Corps".equals(kind) && corps != null && c instanceof Marine m){ ok = corps.recruitMarine(m); }
        else{
            warn("A pirate joins a crew and a marine joins a corps - those do not match.");
            return;
        }

        refreshAll();
        if(ok) status(c.getName() + " was recruited.");
        else warn(c.getName() + " is already affiliated.");
    }

    /** Purpose: Lists the members of the selected group in a dialog. */
    private void viewGroupMembers(){
        int row = view.groupTable.getTable().getSelectedRow();
        if(row < 0){
            warn("Select a group first.");
            return;
        }

        int id = view.groupTable.getSelectedId();
        String kind = String.valueOf(view.groupTable.getTable().getValueAt(row, 1));
        StringBuilder sb = new StringBuilder();

        if("Crew".equals(kind)){
            PirateCrew crew = affDB.findPirateCrewById(id);
            if(crew != null){
                for(Pirate p : crew.getCrewMembers()){
                    sb.append(p.getName()).append(" - ").append(p.getPirateRole())
                      .append(" (").append(p.getBounty()).append(" Berries)\n");
                }
            }
        }
        else{
            MarineCorps corps = affDB.findMarineCorpsById(id);
            if(corps != null){
                for(Marine m : corps.getMembers()){
                    sb.append(m.getName()).append(" - ").append(m.getMarineRank()).append("\n");
                }
            }
        }

        JOptionPane.showMessageDialog(view, sb.isEmpty() ? "No members yet." : sb.toString(),
                "Members", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Purpose: Disbands the selected crew or corps after confirmation. */
    private void deleteGroup(){
        int row = view.groupTable.getTable().getSelectedRow();
        if(row < 0){
            warn("Select a group first.");
            return;
        }
        if(!confirm("Disband the selected group?")) return;

        int id = view.groupTable.getSelectedId();
        String kind = String.valueOf(view.groupTable.getTable().getValueAt(row, 1));
        boolean ok = "Crew".equals(kind) ? affDB.deletePirateCrew(id) : affDB.deleteMarineCorps(id);

        refreshAll();
        status(ok ? "The group was disbanded; members are free to reaffiliate." : "Nothing was disbanded.");
    }

    // ---------- Devil fruit actions ----------

    /** Purpose: Catalogs a new devil fruit. */
    private void addFruit(){
        String name = view.fruitName.getText().trim();
        if(name.isEmpty()){
            warn("A fruit needs a name.");
            return;
        }

        fruitDB.createDevilFruit(name, (Category) view.fruitCategory.getSelectedItem(), view.fruitAbility.getText().trim());
        clear(view.fruitName, view.fruitAbility);
        refreshAll();
        status(name + " was added to the catalog.");
    }

    /** Purpose: Feeds the selected fruit to the chosen character. */
    private void assignFruit(){
        int id = view.fruitTable.getSelectedId();
        if(id < 0){
            warn("Select a fruit in the table first.");
            return;
        }

        DevilFruit fruit = fruitDB.findFruitById(id);
        Character eater = fromCombo(view.fruitEater);
        if(fruit == null || eater == null){
            warn("Select both a fruit and a character.");
            return;
        }

        boolean ok = fruit.assignNewOwner(eater);
        refreshAll();
        if(ok) status(eater.getName() + " ate the " + fruit.getFruitName() + ".");
        else warn("That fruit is taken, or the character is dead or already powered.");
    }

    /** Purpose: Deletes the selected fruit record after confirmation. */
    private void deleteFruit(){
        int id = view.fruitTable.getSelectedId();
        if(id < 0){
            warn("Select a fruit first.");
            return;
        }
        if(!confirm("Destroy this fruit record?")) return;

        fruitDB.deleteDevilFruit(id);
        refreshAll();
        status("The fruit record was destroyed.");
    }

    // ---------- Capture actions ----------

    /** Purpose: Registers a capture. */
    private void registerCapture(){
        Character target = fromCombo(view.captureTarget);
        Character captor = fromCombo(view.captureCaptor);

        if(!(target instanceof Pirate pirate)){
            warn("Select a free pirate as the target.");
            return;
        }

        try{
            CaptureRecord record = bountyDB.registerCapture(pirate, captor, view.captureDead.isSelected());
            FileManager.appendCapture(record);      // Log survives across runs
            refreshAll();
            status("Capture #" + record.getCaptureId() + " filed - "
                    + record.getBountyClaimed() + " Berries paid to " + record.getCaptor().getName() + ".");
        }
        catch(InvalidCaptorException | IllegalArgumentException e){
            warn("Capture rejected: " + e.getMessage());
        }
        catch(DataIOException e){
            refreshAll();
            warn("Capture recorded, but the log could not be written: " + e.getMessage());
        }
    }

    // ---------- File actions ----------

    /**
     * Purpose: Writes every register to disk.
     * @param announce true to update the status bar on success
     * @return true if all three files were written
     */
    private boolean saveAll(boolean announce){
        try{
            FileManager.saveCharacters(charDB.getCharacters());
            FileManager.saveDevilFruits(fruitDB.getDevilFruits());
            FileManager.saveAffiliations(affDB.getPirateCrews(), affDB.getMarineCorpsUnits());
            view.appendFileOutput("Saved characters, devil fruits, and affiliations.");
            if(announce) status("Registry saved to disk.");
            return true;
        }
        catch(DataIOException e){
            warn("Save failed: " + e.getMessage());
            return false;
        }
    }

    /** Purpose: Prompts for a file name and makes archive copy of it. */
    private void archiveFile(){
        String name = JOptionPane.showInputDialog(view, "File to archive:", "characters.txt");
        if(name == null || name.isBlank()) return;
        runFileTask(() -> FileManager.archiveFile(name.trim()), "Archive complete.");
    }

    /** Purpose: Deletes the capture log after confirmation. */
    private void clearLog(){
        if(!confirm("Permanently delete the capture log file?")) return;

        boolean ok = FileManager.clearCaptureLog();
        view.appendFileOutput(ok ? "Capture log deleted." : "Deletion failed - the log may not exist.");
        status(ok ? "Capture log deleted." : "No capture log to delete.");
    }

    /**
     * Purpose: Wipes the running registry and reloads it from the save file on disk.
     * Destructive - confirms with the user first since any unsaved work is lost.
     */
    private void importSave(){
        if(!RegistryStore.saveExists()){
            warn("No save file found on disk to import.");
            return;
        }
        if(!confirm("This replaces everything currently loaded with the saved file. Continue?")){
            return;
        }

        // Clears current dbs to properly import saved data.
        charDB.clear();
        fruitDB.clear();
        affDB.clear();

        try{
            RegistryStore.load(charDB, fruitDB, affDB);
            refreshAll();
            status("Save file imported.");
        }
        catch(DataIOException e){
            status("Import failed: " + e.getMessage());
        }
    }

    /**
     * Purpose: Runs a file operation.
     * @param task the operation to run
     * @param successMessage the status message shown when it succeeds
     */
    private void runFileTask(FileTask task, String successMessage){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try{
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            task.run();
            status(successMessage);
        }
        catch(DataIOException e){
            warn(e.getMessage());
        }
        finally{
            System.setOut(original);
            echo(buffer.toString(StandardCharsets.UTF_8));
        }
    }

    @FunctionalInterface
    private interface FileTask {
        /**
         * Purpose: Performs the operation.
         * @throws DataIOException if the disk operation fails
         */
        void run() throws DataIOException;
    }

    // ---------- Refresh ----------

    /** Purpose: Rebuilds every table and dropdown from the current model state. */
    private void refreshAll(){
        refreshCharacters();
        refreshGroups();
        refreshFruits();
        refreshCaptures();
        refreshWanted();
        refreshCombos();
    }

    /** Purpose: Rebuilds the character roster table. */
    private void refreshCharacters(){
        List<Object[]> rows = new ArrayList<>();
        for(Character c : charDB.getCharacters()){
            rows.add(new Object[]{c.getCharacterID(), c.getClass().getSimpleName(), c.getName(), c.getAlias(),
                    c.getOrigin(), c.getStatus(), affiliationOf(c),
                    c.hasDevilFruit() ? c.getDevilFruitPower().getFruitName() : "None", c.getWallet()});
        }
        view.charTable.setRows(rows);
    }

    /** Purpose: Rebuilds the combined crew and corps table. */
    private void refreshGroups(){
        List<Object[]> rows = new ArrayList<>();
        for(PirateCrew c : affDB.getPirateCrews()){
            rows.add(new Object[]{c.getCrewID(), "Crew", c.getCrewName(), c.getShipName(),
                    c.getCaptain() != null ? c.getCaptain().getName() : "Vacant",
                    c.getCrewMembers().size(), c.getTotalBounty()});
        }
        for(MarineCorps m : affDB.getMarineCorpsUnits()){
            rows.add(new Object[]{m.getCorpsID(), "Corps", m.getCorpsName(), m.getBaseLocation(),
                    m.getCorpsCommander(), m.getMembers().size(), m.getOpFunds()});
        }
        view.groupTable.setRows(rows);
    }

    /** Purpose: Rebuilds the devil fruit table. */
    private void refreshFruits(){
        List<Object[]> rows = new ArrayList<>();
        for(DevilFruit f : fruitDB.getDevilFruits()){
            rows.add(new Object[]{f.getFruitID(), f.getFruitName(), f.getCategory(), f.getPrimaryAbility(),
                    f.getCurrentOwner() != null ? f.getCurrentOwner().getName() : "Unowned",
                    f.getHistoricalOwners().size()});
        }
        view.fruitTable.setRows(rows);
    }

    /** Purpose: Rebuilds the capture log table. */
    private void refreshCaptures(){
        List<Object[]> rows = new ArrayList<>();
        for(CaptureRecord r : bountyDB.getCaptureRecords()){
            rows.add(new Object[]{r.getCaptureId(), r.getCapturedPirate().getName(), r.getCaptor().getName(),
                    r.getStatus(), r.getBountyClaimed()});
        }
        view.captureTable.setRows(rows);
    }

    /**
     * Purpose: Rebuilds the Most Wanted leaderboard.
     * BONUS free pirates ranked by active bounty, highest first.
     */
    private void refreshWanted(){
        List<Pirate> wanted = new ArrayList<>();
        for(Character c : charDB.getCharacters()){
            if(c instanceof Pirate p && p.getStatus() == Status.FREE && p.getBounty() > 0){
                wanted.add(p);
            }
        }
        wanted.sort(Comparator.comparingLong(Pirate::getBounty).reversed());

        List<Object[]> rows = new ArrayList<>();
        int rank = 1;
        for(Pirate p : wanted){
            rows.add(new Object[]{rank++, p.getName(), p.getAlias(),
                    p.getPirateCrew() != null ? p.getPirateCrew().getCrewName() : "Unaffiliated",
                    p.getBounty(), p.getStatus()});
        }
        view.wantedTable.setRows(rows);
    }

    /** Purpose: Repopulates every dropdown so it only offers currently valid choices. */
    private void refreshCombos(){
        List<String> captains = new ArrayList<>();
        List<String> everyone = new ArrayList<>();
        List<String> captors = new ArrayList<>();

        for(Character c : charDB.getCharacters()){
            String entry = c.getCharacterID() + " - " + c.getName();
            everyone.add(entry);

            if(c instanceof Pirate p){
                if(p.getPirateCrew() == null) captains.add(entry);   // eligible founding captains
            }
            else{
                captors.add(entry);     // A pirate may never claim a bounty
            }
        }

        fill(view.crewCaptain, captains);
        fill(view.recruitTarget, everyone);
        fill(view.fruitEater, everyone);
        // Target lists everyone (not just free pirates) so the structural blocks
        // - capturing a non-pirate, or an already-captured pirate - can be shown.
        fill(view.captureTarget, everyone);
        fill(view.captureCaptor, captors);
    }

    // ---------- Profile / edit / group management ----------

    /**
     * Purpose: Shows a category-adaptive profile of the selected character, so the
     * Dynamic Profile Display reveals the fields specific to each type.
     */
    private void viewProfile(){
        Character c = selectedCharacter();
        if(c == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("ID       : ").append(c.getCharacterID()).append("\n");
        sb.append("Name     : ").append(c.getName()).append("\n");
        sb.append("Alias    : ").append(c.getAlias()).append("\n");
        sb.append("Origin   : ").append(c.getOrigin()).append("\n");
        sb.append("Status   : ").append(c.getStatus()).append("\n");
        sb.append("Wallet   : ").append(c.getWallet()).append(" Berries\n");
        sb.append("Fruit    : ").append(c.hasDevilFruit() ? c.getDevilFruitPower().getFruitName() : "None").append("\n");
        sb.append("--- ").append(c.getClass().getSimpleName()).append(" details ---\n");

        switch (c) {
            case Pirate p -> {
                sb.append("Bounty   : ").append(p.getBounty()).append(" Berries\n");
                sb.append("Role     : ").append(p.getPirateRole()).append("\n");
                sb.append("Captain  : ").append(p.isCaptain() ? "Yes" : "No").append("\n");
                sb.append("Crew     : ").append(p.getPirateCrew() != null ? p.getPirateCrew().getCrewName() : "None");
            }
            case Marine m -> {
                sb.append("Rank     : ").append(m.getMarineRank()).append("\n");
                sb.append("Corps    : ").append(m.getMarineCorps() != null ? m.getMarineCorps().getCorpsName() : "None");
            }
            case PirateHunter h -> {
                sb.append("Combat   : ").append(h.getCombatStyle()).append("\n");
                sb.append("Captures : ").append(h.getConfirmedCaptures());
            }
            case Civilian civ -> {
                sb.append("Profession: ").append(civ.getProfession()).append("\n");
                sb.append("Residence : ").append(civ.getResidence());
            }
            default -> {
            }
        }

        JOptionPane.showMessageDialog(view, sb.toString(), "Profile: " + c.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Purpose: Edits the selected character. Alias is editable for all; each subtype
     * exposes its own attributes (bounty/role, rank, combat/captures, profession/residence).
     * Negative bounty and blank alias are rejected, demonstrating the validation block.
     */
    private void editCharacter(){
        Character c = selectedCharacter();
        if(c == null) return;

        JTextField alias = new JTextField(c.getAlias());
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Alias:")); panel.add(alias);

        JTextField bounty = null; JComboBox<String> role = null;
        JComboBox<MarineRank> rank = null;
        JComboBox<String> style = null; JTextField captures = null;
        JComboBox<String> profession = null; JTextField residence = null;

        switch (c) {
            case Pirate p -> {
                bounty = new JTextField(String.valueOf(p.getBounty()));
                role = new JComboBox<>(Pirate.ROLE_OPTIONS);
                role.setSelectedItem(p.getPirateRole());
                panel.add(new JLabel("Bounty:"));
                panel.add(bounty);
                panel.add(new JLabel("Role:"));
                panel.add(role);
            }
            case Marine m -> {
                rank = new JComboBox<>(MarineRank.values());
                rank.setSelectedItem(m.getMarineRank());
                panel.add(new JLabel("Rank:"));
                panel.add(rank);
            }
            case PirateHunter h -> {
                style = new JComboBox<>(PirateHunter.COMBAT_OPTIONS);
                style.setSelectedItem(h.getCombatStyle());
                captures = new JTextField(String.valueOf(h.getConfirmedCaptures()));
                panel.add(new JLabel("Combat Style:"));
                panel.add(style);
                panel.add(new JLabel("Confirmed Captures:"));
                panel.add(captures);
            }
            case Civilian civ -> {
                profession = new JComboBox<>(Civilian.PROFESSION_OPTIONS);
                profession.setSelectedItem(civ.getProfession());
                residence = new JTextField(civ.getResidence());
                panel.add(new JLabel("Profession:"));
                panel.add(profession);
                panel.add(new JLabel("Residence:"));
                panel.add(residence);
            }
            default -> {
            }
        }

        if(JOptionPane.showConfirmDialog(view, panel, "Edit " + c.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        if(alias.getText().isBlank()){ warn("Alias cannot be blank - no changes were saved."); return; }
        c.setAlias(alias.getText().trim());

        switch (c) {
            case Pirate p -> {
                try {
                    long b = Long.parseLong(bounty.getText().trim());
                    if (b < 0) {
                        warn("Bounty cannot be negative - bounty left unchanged.");
                    } else p.setBounty(b);
                } catch (NumberFormatException e) {
                    warn("Bounty must be a whole number - bounty left unchanged.");
                }
                p.setPirateRole(String.valueOf(role.getSelectedItem()));
            }
            case Marine m -> m.setMarineRank((MarineRank) rank.getSelectedItem());
            case PirateHunter h -> {
                h.setCombatStyle(String.valueOf(style.getSelectedItem()));
                try {
                    h.setConfirmedCaptures(Integer.parseInt(captures.getText().trim()));
                } catch (NumberFormatException e) { /* leave unchanged */ }
            }
            case Civilian civ -> {
                civ.setProfession(String.valueOf(profession.getSelectedItem()));
                civ.setResidence(residence.getText().trim());
            }
            default -> {
            }
        }

        refreshAll();
        status(c.getName() + "'s record was updated.");
    }

    /**
     * Purpose: Promotes the character chosen in the recruit dropdown to captain of the
     * crew selected in the table. The outgoing captain reverts to a standard role.
     */
    private void setCrewCaptain(){
        int row = view.groupTable.getTable().getSelectedRow();
        if(row < 0 || !"Crew".equals(view.groupTable.getTable().getValueAt(row, 1))){
            warn("Select a crew in the table first.");
            return;
        }
        PirateCrew crew = affDB.findPirateCrewById(view.groupTable.getSelectedId());
        Character c = fromCombo(view.recruitTarget);
        if(crew == null || !(c instanceof Pirate p)){
            warn("Choose a pirate from the Character dropdown to make captain.");
            return;
        }
        if(crew.setCaptain(p)){
            refreshAll();
            status(p.getName() + " is now captain of " + crew.getCrewName() + ".");
        }
        else{
            warn(p.getName() + " must be a member of that crew first.");
        }
    }

    /**
     * Purpose: Discharges the character chosen in the recruit dropdown from whichever
     * crew or corps they currently belong to.
     */
    private void dischargeMember(){
        Character c = fromCombo(view.recruitTarget);
        if(c == null){ warn("Choose a character from the dropdown to discharge."); return; }

        boolean ok = false;
        if(c instanceof Pirate p && p.getPirateCrew() != null){
            ok = p.getPirateCrew().goodbyeMember(p);
        }
        else if(c instanceof Marine m && m.getMarineCorps() != null){
            ok = m.getMarineCorps().goodbyeMember(m);
        }

        refreshAll();
        status(ok ? c.getName() + " was discharged." : c.getName() + " is not currently affiliated.");
    }

    /**
     * Purpose: Edits the selected group: a crew's name/ship, or a corps' name, base,
     * commander, and operational funds (the Flow 2 fund allocation).
     */
    private void editGroup(){
        int row = view.groupTable.getTable().getSelectedRow();
        if(row < 0){ warn("Select a group in the table first."); return; }

        int id = view.groupTable.getSelectedId();
        String kind = String.valueOf(view.groupTable.getTable().getValueAt(row, 1));
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));

        if("Crew".equals(kind)){
            PirateCrew crew = affDB.findPirateCrewById(id);
            if(crew == null) return;
            JTextField cn = new JTextField(crew.getCrewName());
            JTextField sn = new JTextField(crew.getShipName());
            panel.add(new JLabel("Crew Name:")); panel.add(cn);
            panel.add(new JLabel("Ship Name:")); panel.add(sn);
            if(JOptionPane.showConfirmDialog(view, panel, "Edit Crew",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            crew.setCrewName(cn.getText().trim());
            crew.setShipName(sn.getText().trim());
        }
        else{
            MarineCorps corps = affDB.findMarineCorpsById(id);
            if(corps == null) return;
            JTextField cn = new JTextField(corps.getCorpsName());
            JTextField base = new JTextField(corps.getBaseLocation());
            JTextField cmd = new JTextField(corps.getCorpsCommander());
            JTextField funds = new JTextField(String.valueOf(corps.getOpFunds()));
            panel.add(new JLabel("Corps Name:")); panel.add(cn);
            panel.add(new JLabel("Base Location:")); panel.add(base);
            panel.add(new JLabel("Commander:")); panel.add(cmd);
            panel.add(new JLabel("Operational Funds:")); panel.add(funds);
            if(JOptionPane.showConfirmDialog(view, panel, "Edit Corps",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            corps.setCorpsName(cn.getText().trim());
            corps.setBaseLocation(base.getText().trim());
            corps.setCorpsCommander(cmd.getText().trim());
            try{
                long f = Long.parseLong(funds.getText().trim());
                if(f < 0) warn("Funds cannot be negative - left unchanged."); else corps.setOpFunds(f);
            }
            catch(NumberFormatException e){ warn("Funds must be a whole number - left unchanged."); }
        }

        refreshAll();
        status("Group updated.");
    }

    /**
     * Purpose: Shows a fruit's full details, including current and historical owners,
     * so the Reincarnation Trigger (past owners populated, current owner null) is visible.
     */
    private void viewFruitDetails(){
        int id = view.fruitTable.getSelectedId();
        if(id < 0){ warn("Select a fruit in the table first."); return; }

        DevilFruit f = fruitDB.findFruitById(id);
        if(f == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("ID       : ").append(f.getFruitID()).append("\n");
        sb.append("Name     : ").append(f.getFruitName()).append("\n");
        sb.append("Category : ").append(f.getCategory()).append("\n");
        sb.append("Ability  : ").append(f.getPrimaryAbility()).append("\n");
        sb.append("Current  : ").append(f.getCurrentOwner() != null ? f.getCurrentOwner().getName() : "None (available)").append("\n");
        sb.append("Past Owners:\n");
        if(f.getHistoricalOwners().isEmpty()) sb.append("  (none)");
        else for(Character o : f.getHistoricalOwners()) sb.append("  - ").append(o.getName()).append("\n");

        JOptionPane.showMessageDialog(view, sb.toString(), "Fruit: " + f.getFruitName(), JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Small helpers ----------

    /**
     * Purpose: Reads a dropdown's current text safely.
     * @param combo the dropdown to read
     * @return the selected item's text, or "" if nothing is selected
     */
    private String comboText(JComboBox<String> combo){
        Object item = combo.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    /**
     * Purpose: Replaces a dropdown's contents while keeping the previous choice if still valid.
     * @param combo the dropdown to refill
     * @param entries the new entries
     */
    private void fill(JComboBox<String> combo, List<String> entries){
        Object previous = combo.getSelectedItem();
        combo.removeAllItems();
        for(String entry : entries){ combo.addItem(entry); }
        if(previous != null && entries.contains(previous.toString())){
            combo.setSelectedItem(previous);
        }
    }

    /**
     * Purpose: Resolves the character behind a dropdown entry of the form "id - name".
     * @param combo the dropdown to read
     * @return the matching character, or null if nothing is selected
     */
    private Character fromCombo(JComboBox<String> combo){
        Object item = combo.getSelectedItem();
        if(item == null) return null;
        return charDB.findCharacterByID(Integer.parseInt(item.toString().split(" - ")[0]));
    }

    /**
     * Purpose: Reads the character highlighted in the roster table.
     * @return the selected character, or null after warning the user
     */
    private Character selectedCharacter(){
        int id = view.charTable.getSelectedId();
        if(id < 0){
            warn("Select a character in the table first.");
            return null;
        }
        return charDB.findCharacterByID(id);
    }

    /**
     * Purpose: Describes a character's crew or corps for the roster table.
     * @param c the character to describe
     * @return the group name, a captain marker, or "-"
     */
    private String affiliationOf(Character c){
        if(c instanceof Pirate p && p.getPirateCrew() != null){
            return p.getPirateCrew().getCrewName() + (p.isCaptain() ? " (Captain)" : "");
        }
        if(c instanceof Marine m && m.getMarineCorps() != null){
            return m.getMarineCorps().getCorpsName();
        }
        if(c instanceof PirateHunter h){
            return h.getConfirmedCaptures() + " captures";
        }
        if(c instanceof Civilian civ){
            return civ.getProfession();
        }
        return "-";
    }

    /**
     * Purpose: Parses a field that must hold a non-negative amount of money.
     * @param field the input to read
     * @param label the field name used in the error message
     * @return the parsed value, or null if the field was invalid
     */
    private Long parseMoney(JTextField field, String label){
        String text = field.getText().trim();
        if(text.isEmpty()) return 0L;
        try{
            long value = Long.parseLong(text);
            if(value < 0){ warn(label + " cannot be negative."); return null; }
            return value;
        }
        catch(NumberFormatException e){
            warn(label + " must be a whole number.");
            return null;
        }
    }

    private Integer parseNonNegative(JTextField field, String label){
        String text = field.getText().trim();
        if(text.isEmpty()) return 0;

        try{
            int value = Integer.parseInt(text);
            if(value < 0){
                warn(label + " cannot be negative.");
                return null;
            }
            return value;
        }
        catch(NumberFormatException e){
            warn(label + " must be a whole number.");
            return null;
        }
    }

    /**
     * Purpose: Runs a model method that reports through System.out and captures its text.
     * Lets the GUI reuse the model's existing display logic without duplicating it.
     * @param task the model call to run
     * @return everything the task printed
     */
    private String capture(Runnable task){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try{
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            task.run();
        }
        finally{
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * Purpose: Appends non-empty text to the archives output area.
     * @param text the text to append
     */
    private void echo(String text){
        if(text != null && !text.isBlank()) view.appendFileOutput(text.strip());
    }

    /**
     * Purpose: Clears a batch of text fields.
     * @param fields the fields to blank
     */
    private void clear(JTextField... fields){
        for(JTextField f : fields){ f.setText(""); }
    }

    /**
     * Purpose: Shows a warning dialog and mirrors it in the status bar.
     * @param message the text to show
     */
    private void warn(String message){
        JOptionPane.showMessageDialog(view, message, "Hold On", JOptionPane.WARNING_MESSAGE);
        status(message);
    }

    /**
     * Purpose: Asks the user to confirm a destructive action.
     * @param message the question to ask
     * @return true if the user agreed
     */
    private boolean confirm(String message){
        return JOptionPane.showConfirmDialog(view, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Purpose: Writes a message to the status bar.
     * @param message the text to show
     */
    private void status(String message){ view.log(message); }

    /**
     * Purpose: Application entry point - builds the view and hands it to a controller.
     * @param args unused
     */
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            new RegistryController(frame);
            frame.setVisible(true);
            frame.show("menu");
        });
    }

    /** Purpose: Seeds sample data into the running registry on demand. */
    private void seedInitialData(){
        DataSeeder.seed(charDB, affDB, fruitDB);
        refreshAll();
        status("Sample data loaded.");
    }
}
