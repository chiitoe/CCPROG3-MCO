// ===== VIEW LAYER =====

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** MainFrame
 * Purpose: The whole View layer, basically like the driver but now with GUI
 */
public final class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JTextArea shipLog = new JTextArea(6, 80);

    /** Captions for the numbered menu, in order. Index 0 is option [1]. */
    public static final String[] MENU_LABELS = {
            "Register a Character", "Create an Affiliation Group", "Assign a Character to a Group",
            "Catalogue a Devil Fruit", "Assign a Devil Fruit", "View All Characters",
            "View All Devil Fruits", "View All Groups", "Delete a Character",
            "Delete a Devil Fruit", "Delete a Group", "Register a Capture",
            "View Capture History", "Most Wanted Board", "File Operations (Archives)"
    };

    public final JButton[] menuButtons = new JButton[MENU_LABELS.length];
    public final JButton menuExit = Theme.danger("Abandon Ship (Exit)");

    // ----- Character screen -----
    /** Character input for all fields. */
    public final JComboBox<String> charType = new JComboBox<>(new String[]{"Pirate", "Marine", "Civilian", "Pirate Hunter"});
    public final JTextField charName = new JTextField(14);
    public final JTextField charAlias = new JTextField(14);
    public final JTextField charOrigin = new JTextField(14);
    public final JTextField charWallet = new JTextField(14);
    public final JTextField charExtra1 = new JTextField(14);
    public final JTextField charExtra2 = new JTextField(14);
    public final JLabel charExtra1Label = Theme.formLabel("Bounty:");
    public final JLabel charExtra2Label = Theme.formLabel("Role:");
    public final JComboBox<MarineRank> charRank = new JComboBox<>(MarineRank.values());
    public final JLabel charRankLabel = Theme.formLabel("Rank:");
    public final JButton charAdd = Theme.gold("Recruit");
    public final JButton charDelete = Theme.danger("Strike From Record");
    public final JButton charDuty = Theme.gold("Perform Duty");
    public final JButton charPromote = Theme.gold("Promote Marine");
    public final JComboBox<String> charSlot1Combo = new JComboBox<>();
    public final JComboBox<String> charSlot2Combo = new JComboBox<>();
    public final JButton charProfile = Theme.gold("View Profile");
    public final JButton charEdit = Theme.gold("Edit Selected");
    public final RegistryTable charTable =
            new RegistryTable(new String[]{"ID", "Type", "Name", "Alias", "Origin", "Status", "Affiliation", "Devil Fruit", "Wallet"});

    // ----- Group screen -----
    /** Input for the crew and other fields. */
    public final JTextField crewName = new JTextField(12);
    public final JTextField shipName = new JTextField(12);
    public final JComboBox<String> crewCaptain = new JComboBox<>();
    public final JButton crewCreate = Theme.gold("Set Sail");
    public final JTextField corpsName = new JTextField(12);
    public final JTextField corpsBase = new JTextField(12);
    public final JTextField corpsCommander = new JTextField(12);
    public final JTextField corpsFunds = new JTextField(12);
    public final JButton corpsCreate = Theme.gold("Commission Unit");
    public final JComboBox<String> recruitTarget = new JComboBox<>();
    public final JButton groupRecruit = Theme.gold("Recruit Into Selected");
    public final JButton groupMembers = Theme.gold("View Members");
    public final JButton groupSetCaptain = Theme.gold("Make Captain");
    public final JButton groupDischarge = Theme.gold("Discharge Member");
    public final JButton groupEdit = Theme.gold("Edit Selected");
    public final JButton groupDelete = Theme.danger("Disband");
    public final RegistryTable groupTable =
            new RegistryTable(new String[]{"ID", "Kind", "Name", "Ship / Base", "Leader", "Size", "Bounty / Funds"});

    // ----- Devil fruit screen -----
    /** Input for devil fruit and other fields. */
    public final JTextField fruitName = new JTextField(14);
    public final JComboBox<Category> fruitCategory = new JComboBox<>(Category.values());
    public final JTextField fruitAbility = new JTextField(14);
    public final JButton fruitAdd = Theme.gold("Catalogue Fruit");
    public final JComboBox<String> fruitEater = new JComboBox<>();
    public final JButton fruitAssign = Theme.gold("Feed To Character");
    public final JButton fruitDetails = Theme.gold("View Details");
    public final JButton fruitDelete = Theme.danger("Destroy Record");
    public final RegistryTable fruitTable =
            new RegistryTable(new String[]{"ID", "Name", "Category", "Ability", "Current Owner", "Past Owners"});

    // ----- Capture screen -----
    public final JComboBox<String> captureTarget = new JComboBox<>();
    public final JComboBox<String> captureCaptor = new JComboBox<>();
    public final JCheckBox captureDead = new JCheckBox("Target was killed");
    public final JButton captureRegister = Theme.gold("Claim Bounty");
    public final RegistryTable captureTable =
            new RegistryTable(new String[]{"ID", "Captured", "Captor", "Status", "Berries Claimed"});

    // ----- Most Wanted screen -----
    /** The leaderboard table. */
    public final RegistryTable wantedTable =
            new RegistryTable(new String[]{"Rank", "Name", "Alias", "Crew", "Bounty", "Status"});
    /** Recomputes the leaderboard. */
    public final JButton wantedRefresh = Theme.gold("Refresh Rankings");

    // ----- Archives screen -----
    public final JButton fileSave = Theme.gold("Save All To Disk");
    public final JButton fileLoad = Theme.gold("Read Capture Log");
    public final JButton fileInfo = Theme.gold("File Information");
    public final JButton fileArchive = Theme.gold("Archive A File");
    public final JButton fileClear = Theme.danger("Delete Capture Log");
    public final JTextArea fileOutput = new JTextArea(10, 50);
    public final JButton fileSeed = Theme.gold("Load Sample Data");
    public final JButton fileImport = Theme.gold("Import Save File");

    /** CONSTRUCTOR
     * Purpose: Assembles the window: the menu, every action screen, and the ship's log.
     */
    public MainFrame(){
        super("Registry for OPUMS");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);   // Controller runs the exit prompt

        content.setBackground(Theme.SEA);
        content.add(buildMenu(), "menu");
        content.add(wrap("[1/6/9]  Crew Roster", buildCharacterBody(), bar(charAdd, charProfile, charEdit, charDuty, charPromote, charDelete), charTable), "characters");
        content.add(buildGroupCard(), "groups");
        content.add(wrap("[4/7/10]  Devil Fruit Vault", buildFruitBody(), bar(fruitAdd, fruitAssign, fruitDetails, fruitDelete), fruitTable), "fruits");
        content.add(wrap("[12/13]  Bounty Office", buildCaptureBody(), bar(captureRegister), captureTable), "captures");
        content.add(wrap("[14]  Most Wanted", buildWantedBody(), bar(wantedRefresh), wantedTable), "wanted");
        content.add(buildFileCard(), "files");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.SEA);
        root.add(content, BorderLayout.CENTER);
        root.add(buildLog(), BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(1000, 760));
        pack();
        setLocationRelativeTo(null);
    }

    // ---------- Main menu ----------

    /**
     * Purpose: Builds the title screen with the numbered menu buttons.
     * @return the assembled menu card
     */
    private JPanel buildMenu(){
        JLabel title = Theme.label("ONE PIECE UNIFIED MANAGEMENT SYSTEM", Theme.TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(24, 0, 2, 0));

        JLabel motto = Theme.label("The One Piece is Real!", Theme.BODY);
        motto.setHorizontalAlignment(SwingConstants.CENTER);
        motto.setForeground(Theme.PARCHMENT);

        // Two centred columns of numbered options, like a game's main menu
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 8));
        grid.setOpaque(false);
        for(int i = 0; i < MENU_LABELS.length; i++){
            menuButtons[i] = Theme.gold("[" + (i + 1) + "]  " + MENU_LABELS[i]);
            menuButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            grid.add(menuButtons[i]);
        }

        JPanel gridWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridWrap.setOpaque(false);
        gridWrap.add(grid);

        JPanel exitBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        exitBar.setOpaque(false);
        exitBar.add(menuExit);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setOpaque(false);
        centre.add(Box.createVerticalStrut(6));
        centre.add(gridWrap);
        centre.add(exitBar);

        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(Theme.SEA);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SEA);
        header.add(title, BorderLayout.NORTH);
        header.add(motto, BorderLayout.SOUTH);
        menu.add(header, BorderLayout.NORTH);
        menu.add(centre, BorderLayout.CENTER);
        return menu;
    }

    // ---------- Shared scaffolding ----------

    /**
     * Purpose: Builds the persistent ship's-log console shown beneath every screen.
     * @return the assembled log panel
     */
    private JPanel buildLog(){
        shipLog.setEditable(false);
        shipLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        shipLog.setBackground(new Color(6, 22, 42));
        shipLog.setForeground(new Color(120, 230, 160));   // Terminal green on deep navy
        shipLog.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        shipLog.setText(">> Ship's log ready. Welcome!.");

        JScrollPane scroll = new JScrollPane(shipLog);
        scroll.setBorder(Theme.titledBorder("Ship's Log"));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.SEA);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /**
     * Purpose: Builds the Back-to-menu bar that tops every action screen.
     * @param title the screen's heading
     * @return the header panel, with its own wired Back button
     */
    private JPanel topBar(String title){
        JButton back = Theme.gold("←  Back to Menu");
        back.addActionListener(e -> show("menu"));   // Pure UI navigation

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(back);

        JLabel heading = Theme.label(title, Theme.HEADING);
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SEA);
        bar.add(left, BorderLayout.WEST);
        bar.add(heading, BorderLayout.CENTER);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 6, 6, 6));
        return bar;
    }

    /**
     * Purpose: Wraps a form and a table into the standard action-screen layout.
     * @param title the screen heading, shown next to the Back button
     * @param formBody the input area
     * @param buttons the centred action bar
     * @param table the results table
     * @return the assembled screen
     */
    private JPanel wrap(String title, JPanel formBody, JPanel buttons, RegistryTable table){
        JPanel top = Theme.card("Actions");
        top.add(formBody, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        JPanel bottom = Theme.card("Records");
        bottom.add(table, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(Theme.SEA);
        body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        body.add(top, BorderLayout.NORTH);
        body.add(bottom, BorderLayout.CENTER);

        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Theme.SEA);
        screen.add(topBar(title), BorderLayout.NORTH);
        screen.add(body, BorderLayout.CENTER);
        return screen;
    }

    /**
     * Purpose: Lays out a two-column label/field form.
     * @param rows alternating label and component
     * @return the assembled form panel
     */
    private JPanel form(Component... rows){
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 6));
        p.setOpaque(false);
        for(Component c : rows){ p.add(c); }
        return p;
    }

    /**
     * Purpose: Lays out a centred row of buttons.
     * @param buttons the buttons to place
     * @return the assembled button bar
     */
    private JPanel bar(JButton... buttons){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setOpaque(false);
        for(JButton b : buttons){ p.add(b); }
        return p;
    }

    // ---------- Screen bodies ----------

    /** CardLayout wrappers so each variable slot can show a text field or a dropdown. */
    private final java.awt.CardLayout slot1Cards = new java.awt.CardLayout();
    private final java.awt.CardLayout slot2Cards = new java.awt.CardLayout();
    private final JPanel slot1 = new JPanel(slot1Cards);
    private final JPanel slot2 = new JPanel(slot2Cards);

    private JPanel buildCharacterBody(){
        slot1.setOpaque(false);
        slot1.add(charExtra1, "text");
        slot1.add(charSlot1Combo, "combo");
        slot2.setOpaque(false);
        slot2.add(charExtra2, "text");
        slot2.add(charSlot2Combo, "combo");

        JPanel body = form(
                Theme.formLabel("Type:"), charType,
                Theme.formLabel("Name:"), charName,
                Theme.formLabel("Alias:"), charAlias,
                Theme.formLabel("Origin:"), charOrigin,
                Theme.formLabel("Wallet (Berries):"), charWallet,
                charExtra1Label, slot1,
                charExtra2Label, slot2,
                charRankLabel, charRank);
        charRankLabel.setVisible(false);
        charRank.setVisible(false);
        return body;
    }

    /**
     * Purpose: Chooses whether a variable slot shows its text field or its dropdown.
     * @param which 1 for the first slot, 2 for the second
     * @param combo true to show the dropdown, false for the text field
     */
    public void setSlotMode(int which, boolean combo){
        if(which == 1) slot1Cards.show(slot1, combo ? "combo" : "text");
        else slot2Cards.show(slot2, combo ? "combo" : "text");
    }

    /**
     * Purpose: Builds the devil fruit form body.
     * @return the assembled form
     */
    private JPanel buildFruitBody(){
        return form(
                Theme.formLabel("Fruit Name:"), fruitName,
                Theme.formLabel("Category:"), fruitCategory,
                Theme.formLabel("Primary Ability:"), fruitAbility,
                Theme.formLabel("Feed to:"), fruitEater);
    }

    /**
     * Purpose: Builds the capture form body.
     * @return the assembled form
     */
    private JPanel buildCaptureBody(){
        captureDead.setOpaque(false);
        captureDead.setFont(Theme.BODY);
        captureDead.setForeground(Theme.INK);
        return form(
                Theme.formLabel("Target Pirate:"), captureTarget,
                Theme.formLabel("Captor:"), captureCaptor,
                Theme.formLabel("Outcome:"), captureDead);
    }

    /**
     * Purpose: Builds the Most Wanted note body.
     * @return the assembled body
     */
    private JPanel buildWantedBody(){
        JPanel note = new JPanel(new FlowLayout(FlowLayout.CENTER));
        note.setOpaque(false);
        note.add(Theme.formLabel("Living, uncaptured pirates ranked by active bounty."));
        return note;
    }

    /**
     * Purpose: Builds the combined crews-and-corps screen (options 2, 3, 8, 11).
     * @return the assembled screen
     */
    private JPanel buildGroupCard(){
        JPanel crew = Theme.card("Found a Pirate Crew");
        crew.add(form(Theme.formLabel("Crew Name:"), crewName,
                Theme.formLabel("Ship Name:"), shipName,
                Theme.formLabel("Captain:"), crewCaptain), BorderLayout.CENTER);
        crew.add(bar(crewCreate), BorderLayout.SOUTH);

        JPanel corps = Theme.card("Commission a Marine Corps");
        corps.add(form(Theme.formLabel("Corps Name:"), corpsName,
                Theme.formLabel("Base Location:"), corpsBase,
                Theme.formLabel("Commander:"), corpsCommander,
                Theme.formLabel("Operational Funds:"), corpsFunds), BorderLayout.CENTER);
        corps.add(bar(corpsCreate), BorderLayout.SOUTH);

        JPanel top = new JPanel(new GridLayout(1, 2, 12, 0));
        top.setBackground(Theme.SEA);
        top.add(crew);
        top.add(corps);

        JPanel recruit = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        recruit.setOpaque(false);
        recruit.add(Theme.formLabel("Character:"));
        recruit.add(recruitTarget);
        recruit.add(groupRecruit);
        recruit.add(groupSetCaptain);
        recruit.add(groupDischarge);
        recruit.add(groupMembers);
        recruit.add(groupEdit);
        recruit.add(groupDelete);

        JPanel bottom = Theme.card("Registered Groups");
        bottom.add(groupTable, BorderLayout.CENTER);
        bottom.add(recruit, BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(Theme.SEA);
        body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        body.add(top, BorderLayout.NORTH);
        body.add(bottom, BorderLayout.CENTER);

        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Theme.SEA);
        screen.add(topBar("[2/3/8/11]  Crews & Corps"), BorderLayout.NORTH);
        screen.add(body, BorderLayout.CENTER);
        return screen;
    }

    /**
     * Purpose: Builds the archives (file handling) screen.
     * @return the assembled screen
     */
    private JPanel buildFileCard(){
        fileOutput.setEditable(false);
        fileOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fileOutput.setBackground(Theme.PARCHMENT);
        fileOutput.setForeground(Theme.INK);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        for(JButton b : new JButton[]{fileSave, fileImport, fileLoad, fileInfo, fileArchive, fileSeed, fileClear}){
            buttons.add(b);
            buttons.add(Box.createVerticalStrut(8));
        }

        JPanel card = Theme.card("Archives");
        card.add(buttons, BorderLayout.WEST);
        card.add(new JScrollPane(fileOutput), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.SEA);
        body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        body.add(card, BorderLayout.CENTER);

        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Theme.SEA);
        screen.add(topBar("[15]  Archives"), BorderLayout.NORTH);
        screen.add(body, BorderLayout.CENTER);
        return screen;
    }

    // ---------- Public View API ----------

    /**
     * Purpose: Brings one card to the front.
     * @param name the card name, e.g. "menu" or "characters"
     */
    public void show(String name){ cards.show(content, name); }

    /**
     * Purpose: Appends a timestamped line to the ship's log and scrolls to it.
     * @param message the text to record
     */
    public void log(String message){
        shipLog.append("\n>> " + message);
        shipLog.setCaretPosition(shipLog.getDocument().getLength());
    }

    /**
     * Purpose: Appends a block of text to the archives output area.
     * @param text the text to append
     */
    public void appendFileOutput(String text){
        fileOutput.append(text + "\n");
        fileOutput.setCaretPosition(fileOutput.getDocument().getLength());
    }

    /** Purpose: Shows or hides both variable-field slots at once (hidden entirely for types like Marine that don't use them). */
    public void setVariableSlotsVisible(boolean visible){
        slot1.setVisible(visible);
        slot2.setVisible(visible);
    }
}
