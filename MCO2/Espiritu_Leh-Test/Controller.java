// ===== CONTROLLER LAYER =====
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/** RegistryController
 * Purpose: The Controller layer. It owns the model databases, attaches every
 * listener to the View, validates user input, and pushes refreshed data back.
 * No Swing component is built here and no business rule lives in the View.
 */
public class Controller {

    private final MainFrame view;
    private final CharacterDatabase charDB = new CharacterDatabase();
    private final AffiliationDatabase affDB = new AffiliationDatabase();
    private final DevilFruitDatabase fruitDB = new DevilFruitDatabase();
    private final BountyDatabase bountyDB = new BountyDatabase();

    /** CONSTRUCTOR
     * Purpose: Binds this controller to a view and wires up every control.
     * @param view the window to drive
     */
    public Controller(MainFrame view){
        this.view = view;
        attachNavigation();
        attachCharacterActions();
        attachGroupActions();
        attachFruitActions();
        attachCaptureActions();
        attachFileActions();
        attachExitPrompt();
        refreshAll();
    }

    // ---------- Wiring ----------

    /** Purpose: Points each navigation button at its screen and refreshes on arrival. */
    private void attachNavigation(){
        view.navCharacters.addActionListener(e -> { refreshAll(); view.show("characters"); });
        view.navGroups.addActionListener(e -> { refreshAll(); view.show("groups"); });
        view.navFruits.addActionListener(e -> { refreshAll(); view.show("fruits"); });
        view.navCaptures.addActionListener(e -> { refreshAll(); view.show("captures"); });
        view.navWanted.addActionListener(e -> { refreshWanted(); view.show("wanted"); });
        view.navFiles.addActionListener(e -> view.show("files"));
    }

    /** Purpose: Wires the character form, including the type-driven field relabelling. */
    private void attachCharacterActions(){
        view.charType.addActionListener(e -> updateCharacterForm());
        updateCharacterForm();

        view.charAdd.addActionListener(e -> addCharacter());
        view.charDelete.addActionListener(e -> deleteCharacter());
        view.charDuty.addActionListener(e -> performDuty());
        view.charPromote.addActionListener(e -> promoteMarine());
    }

    /** Purpose: Wires crew founding, corps commissioning, recruitment, and deletion. */
    private void attachGroupActions(){
        view.crewCreate.addActionListener(e -> createCrew());
        view.corpsCreate.addActionListener(e -> createCorps());
        view.groupRecruit.addActionListener(e -> recruitIntoGroup());
        view.groupMembers.addActionListener(e -> viewGroupMembers());
        view.groupDelete.addActionListener(e -> deleteGroup());
    }

    /** Purpose: Wires fruit cataloguing, assignment, and deletion. */
    private void attachFruitActions(){
        view.fruitAdd.addActionListener(e -> addFruit());
        view.fruitAssign.addActionListener(e -> assignFruit());
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
        view.fileLoad.addActionListener(e -> runFileTask(FileDirectory::readCaptureLog, "Capture log read."));
        view.fileInfo.addActionListener(e -> { echo(capture(FileDirectory::showFileInfo)); status("File information listed."); });
        view.fileArchive.addActionListener(e -> archiveFile());
        view.fileClear.addActionListener(e -> clearLog());
    }

    /**
     * Purpose: Intercepts the window close so unsaved work can be written first.
     * BONUS FEATURE 3 - the registry offers to persist itself on exit.
     */
    private void attachExitPrompt(){
        view.addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){
                int answer = JOptionPane.showConfirmDialog(view,
                        "Save the registry to disk before leaving?", "Leaving the Grand Line",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                if(answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION) return;
                if(answer == JOptionPane.YES_OPTION && !saveAll(false)) return;   // Stay open if saving failed

                view.dispose();
                System.exit(0);
            }
        });
    }

    // ---------- Character actions ----------

    /** Purpose: Relabels the two variable inputs to match the selected character type. */
    private void updateCharacterForm(){
        String type = String.valueOf(view.charType.getSelectedItem());
        boolean marine = "Marine".equals(type);

        view.charRank.setVisible(marine);
        view.charRankLabel.setVisible(marine);
        view.charExtra1.setVisible(!marine);
        view.charExtra1Label.setVisible(!marine);
        view.charExtra2.setVisible(!marine);
        view.charExtra2Label.setVisible(!marine);

        switch(type){
            case "Pirate" -> setExtraLabels("Bounty:", "Pirate Role:");
            case "Civilian" -> setExtraLabels("Profession:", "Residence:");
            case "Pirate Hunter" -> setExtraLabels("Combat Style:", "Confirmed Captures:");
            default -> setExtraLabels("", "");
        }
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

        Integer wallet = parseNonNegative(view.charWallet, "Wallet");
        if(wallet == null) return;

        String alias = view.charAlias.getText().trim();
        String origin = view.charOrigin.getText().trim();
        String type = String.valueOf(view.charType.getSelectedItem());

        switch(type){
            case "Pirate" -> {
                Integer bounty = parseNonNegative(view.charExtra1, "Bounty");
                if(bounty == null) return;
                charDB.createPirate(name, alias, origin, wallet, bounty, view.charExtra2.getText().trim());
            }
            case "Marine" -> charDB.createMarine(name, alias, origin, wallet, (MarineRank) view.charRank.getSelectedItem());
            case "Civilian" -> charDB.createCivilian(name, alias, origin, wallet,
                    view.charExtra1.getText().trim(), view.charExtra2.getText().trim());
            default -> {
                Integer captures = parseNonNegative(view.charExtra2, "Confirmed Captures");
                if(captures == null) return;
                charDB.createPirateHunter(name, alias, origin, wallet, view.charExtra1.getText().trim(), captures);
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
     * BONUS FEATURE 4 - flavour text pulled straight from the polymorphic performDuty.
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
     * BONUS FEATURE 5 - exercises the rank ladder from the model.
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

    /** Purpose: Founds a crew under the selected unaffiliated pirate. */
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
        Integer funds = parseNonNegative(view.corpsFunds, "Operational Funds");
        if(funds == null) return;

        affDB.createMarineCorps(view.corpsName.getText().trim(), view.corpsBase.getText().trim(),
                view.corpsCommander.getText().trim(), funds);
        clear(view.corpsName, view.corpsBase, view.corpsCommander, view.corpsFunds);
        refreshAll();
        status("A new marine corps was commissioned.");
    }

    /** Purpose: Recruits the chosen character into the selected group, matching kinds. */
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

        JOptionPane.showMessageDialog(view, sb.length() == 0 ? "No members yet." : sb.toString(),
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

    /** Purpose: Catalogues a new devil fruit. */
    private void addFruit(){
        String name = view.fruitName.getText().trim();
        if(name.isEmpty()){
            warn("A fruit needs a name.");
            return;
        }

        fruitDB.createDevilFruit(name, (Category) view.fruitCategory.getSelectedItem(), view.fruitAbility.getText().trim());
        clear(view.fruitName, view.fruitAbility);
        refreshAll();
        status(name + " was added to the catalogue.");
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

    /** Purpose: Registers a capture, reporting every rejection reason back to the user. */
    private void registerCapture(){
        Character target = fromCombo(view.captureTarget);
        Character captor = fromCombo(view.captureCaptor);

        if(!(target instanceof Pirate pirate)){
            warn("Select a free pirate as the target.");
            return;
        }

        try{
            CaptureRecord record = bountyDB.registerCapture(pirate, captor, view.captureDead.isSelected());
            FileDirectory.appendCapture(record);      // Log survives across runs
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
            FileDirectory.saveCharacters(charDB.getCharacters());
            FileDirectory.saveDevilFruits(fruitDB.getDevilFruits());
            FileDirectory.saveAffiliations(affDB.getPirateCrews(), affDB.getMarineCorpsUnits());
            view.appendFileOutput("Saved characters, devil fruits, and affiliations.");
            if(announce) status("Registry saved to disk.");
            return true;
        }
        catch(DataIOException e){
            warn("Save failed: " + e.getMessage());
            return false;
        }
    }

    /** Purpose: Prompts for a file name and makes a byte-for-byte archive copy of it. */
    private void archiveFile(){
        String name = JOptionPane.showInputDialog(view, "File to archive:", "characters.txt");
        if(name == null || name.isBlank()) return;
        runFileTask(() -> FileDirectory.archiveFile(name.trim()), "Archive complete.");
    }

    /** Purpose: Deletes the capture log after confirmation. */
    private void clearLog(){
        if(!confirm("Permanently delete the capture log file?")) return;

        boolean ok = FileDirectory.clearCaptureLog();
        view.appendFileOutput(ok ? "Capture log deleted." : "Deletion failed - the log may not exist.");
        status(ok ? "Capture log deleted." : "No capture log to delete.");
    }

    /**
     * Purpose: Runs a file operation, echoing its console output and any failure.
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

    /** Purpose: A file operation that may fail with an error. */
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
     * BONUS FEATURE 2 - free pirates ranked by active bounty, highest first.
     */
    private void refreshWanted(){
        List<Pirate> wanted = new ArrayList<>();
        for(Character c : charDB.getCharacters()){
            if(c instanceof Pirate p && p.getStatus() == Status.FREE && p.getBounty() > 0){
                wanted.add(p);
            }
        }
        wanted.sort(Comparator.comparingInt(Pirate::getBounty).reversed());

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
        List<String> freePirates = new ArrayList<>();
        List<String> captors = new ArrayList<>();

        for(Character c : charDB.getCharacters()){
            String entry = c.getCharacterID() + " - " + c.getName();
            everyone.add(entry);

            if(c instanceof Pirate p){
                if(p.getPirateCrew() == null) captains.add(entry);
                if(p.getStatus() == Status.FREE) freePirates.add(entry);
            }
            else{
                captors.add(entry);     // A pirate may never claim a bounty
            }
        }

        fill(view.crewCaptain, captains);
        fill(view.recruitTarget, everyone);
        fill(view.fruitEater, everyone);
        fill(view.captureTarget, freePirates);
        fill(view.captureCaptor, captors);
    }

    // ---------- Small helpers ----------

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
     * Purpose: Parses a field that must hold a non-negative whole number.
     * @param field the input to read
     * @param label the field name used in the error message
     * @return the parsed value, or null if the field was invalid
     */
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
    private void status(String message){ view.setStatus(message); }

    /**
     * Purpose: Application entry point - builds the view and hands it to a controller.
     * @param args unused
     */
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            new Controller(frame);
            frame.setVisible(true);
            frame.show("characters");
        });
    }
}