// ===== VIEW LAYER =====
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
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
 * Purpose: The whole View layer - every screen, control, and table the user sees.
 * It holds no business logic: it only exposes its components so the Controller
 * can attach behavior and push data into it.
 */
public final class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel statusBar = new JLabel("Ready. Welcome to the Grand Line.", SwingConstants.CENTER);

    // ----- Navigation -----
    /** Nav buttons, in display order. */
    public final JButton navCharacters = Theme.gold("Crew Roster");
    /** Nav button for the affiliations screen. */
    public final JButton navGroups = Theme.gold("Crews & Corps");
    /** Nav button for the devil fruit screen. */
    public final JButton navFruits = Theme.gold("Devil Fruits");
    /** Nav button for the capture screen. */
    public final JButton navCaptures = Theme.gold("Bounty Office");
    /** Nav button for the leaderboard screen. */
    public final JButton navWanted = Theme.gold("Most Wanted");
    /** Nav button that populates the registry with sample starting data. */
    public final JButton fileSeed = Theme.gold("Load Sample Data");
    /** Nav button for the file screen. */
    public final JButton navFiles = Theme.gold("Archives");

    // ----- Character screen -----
    /** Character type selector, which relabels the two variable fields. */
    public final JComboBox<String> charType = new JComboBox<>(new String[]{"Pirate", "Marine", "Civilian", "Pirate Hunter"});
    /** Character name input. */
    public final JTextField charName = new JTextField(14);
    /** Character alias input. */
    public final JTextField charAlias = new JTextField(14);
    /** Character origin input. */
    public final JTextField charOrigin = new JTextField(14);
    /** Character starting wallet input. */
    public final JTextField charWallet = new JTextField(14);
    /** First type-specific input (bounty / profession / combat style). */
    public final JTextField charExtra1 = new JTextField(14);
    /** Second type-specific input (role / residence / captures). */
    public final JTextField charExtra2 = new JTextField(14);
    /** Caption for the first type-specific input. */
    public final JLabel charExtra1Label = Theme.formLabel("Bounty:");
    /** Caption for the second type-specific input. */
    public final JLabel charExtra2Label = Theme.formLabel("Role:");
    /** Rank selector, shown only while Marine is the selected type. */
    public final JComboBox<MarineRank> charRank = new JComboBox<>(MarineRank.values());
    /** Caption for the rank selector. */
    public final JLabel charRankLabel = Theme.formLabel("Rank:");
    /** Registers the character described by the form. */
    public final JButton charAdd = Theme.gold("Recruit");
    /** Deletes the selected character. */
    public final JButton charDelete = Theme.danger("Strike From Record");
    /** Prints the selected character's duty line. */
    public final JButton charDuty = Theme.gold("Perform Duty");
    /** Promotes the selected marine one rank. */
    public final JButton charPromote = Theme.gold("Promote Marine");
    /** The character roster table. */
    public final RegistryTable charTable =
            new RegistryTable(new String[]{"ID", "Type", "Name", "Alias", "Origin", "Status", "Affiliation", "Devil Fruit", "Wallet"});

    // ----- Group screen -----
    /** Crew name input. */
    public final JTextField crewName = new JTextField(12);
    /** Ship name input. */
    public final JTextField shipName = new JTextField(12);
    /** Founding captain selector, listing unaffiliated pirates only. */
    public final JComboBox<String> crewCaptain = new JComboBox<>();
    /** Founds a new crew. */
    public final JButton crewCreate = Theme.gold("Set Sail");
    /** Corps name input. */
    public final JTextField corpsName = new JTextField(12);
    /** Corps base location input. */
    public final JTextField corpsBase = new JTextField(12);
    /** Corps commander input. */
    public final JTextField corpsCommander = new JTextField(12);
    /** Corps starting operational funds input. */
    public final JTextField corpsFunds = new JTextField(12);
    /** Establishes a new corps. */
    public final JButton corpsCreate = Theme.gold("Commission Unit");
    /** Character to be recruited into the selected group. */
    public final JComboBox<String> recruitTarget = new JComboBox<>();
    /** Recruits the chosen character into the selected group. */
    public final JButton groupRecruit = Theme.gold("Recruit Into Selected");
    /** Lists the members of the selected group. */
    public final JButton groupMembers = Theme.gold("View Members");
    /** Disbands the selected group. */
    public final JButton groupDelete = Theme.danger("Disband");
    /** The combined crew and corps table. */
    public final RegistryTable groupTable =
            new RegistryTable(new String[]{"ID", "Kind", "Name", "Ship / Base", "Leader", "Size", "Bounty / Funds"});

    // ----- Devil fruit screen -----
    /** Fruit name input. */
    public final JTextField fruitName = new JTextField(14);
    /** Fruit category selector. */
    public final JComboBox<Category> fruitCategory = new JComboBox<>(Category.values());
    /** Fruit primary ability input. */
    public final JTextField fruitAbility = new JTextField(14);
    /** Registers a new fruit. */
    public final JButton fruitAdd = Theme.gold("Catalogue Fruit");
    /** Character who will eat the selected fruit. */
    public final JComboBox<String> fruitEater = new JComboBox<>();
    /** Feeds the selected fruit to the chosen character. */
    public final JButton fruitAssign = Theme.gold("Feed To Character");
    /** Destroys the selected fruit record. */
    public final JButton fruitDelete = Theme.danger("Destroy Record");
    /** The fruit registry table. */
    public final RegistryTable fruitTable =
            new RegistryTable(new String[]{"ID", "Name", "Category", "Ability", "Current Owner", "Past Owners"});

    // ----- Capture screen -----
    /** The pirate being hunted. */
    public final JComboBox<String> captureTarget = new JComboBox<>();
    /** The character claiming the bounty. */
    public final JComboBox<String> captureCaptor = new JComboBox<>();
    /** Ticked when the target was killed rather than taken alive. */
    public final JCheckBox captureDead = new JCheckBox("Target was killed");
    /** Files the capture. */
    public final JButton captureRegister = Theme.gold("Claim Bounty");
    /** The capture log table. */
    public final RegistryTable captureTable =
            new RegistryTable(new String[]{"ID", "Captured", "Captor", "Status", "Berries Claimed"});

    // ----- Most Wanted screen -----
    /** The leaderboard table. */
    public final RegistryTable wantedTable =
            new RegistryTable(new String[]{"Rank", "Name", "Alias", "Crew", "Bounty", "Status"});
    /** Recomputes the leaderboard. */
    public final JButton wantedRefresh = Theme.gold("Refresh Rankings");

    // ----- Archives screen -----
    /** Writes every register to disk. */
    public final JButton fileSave = Theme.gold("Save All To Disk");
    /** Reads the capture log back from disk. */
    public final JButton fileLoad = Theme.gold("Read Capture Log");
    /** Shows filesystem metadata for the data folder. */
    public final JButton fileInfo = Theme.gold("File Information");
    /** Makes a byte-for-byte archive copy of a saved file. */
    public final JButton fileArchive = Theme.gold("Archive A File");
    /** Deletes the capture log. */
    public final JButton fileClear = Theme.danger("Delete Capture Log");
    /** Where file operation output is echoed. */
    public final JTextArea fileOutput = new JTextArea(12, 60);

    /** CONSTRUCTOR
     * Purpose: Assembles the window, its banner, its centred navigation, and every screen.
     */
    public MainFrame(){
        super("Grand Line Registry");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);   // Controller handles the exit prompt
        setMinimumSize(new Dimension(1000, 720));

        content.setBackground(Theme.SEA);
        content.add(buildCharacterScreen(), "characters");
        content.add(buildGroupScreen(), "groups");
        content.add(buildFruitScreen(), "fruits");
        content.add(buildCaptureScreen(), "captures");
        content.add(buildWantedScreen(), "wanted");
        content.add(buildFileScreen(), "files");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.SEA);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        statusBar.setFont(Theme.BODY);
        statusBar.setForeground(Theme.PARCHMENT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Purpose: Builds the gold banner and the centred navigation strip beneath it.
     * @return the assembled header
     */
    private JPanel buildHeader(){
        JLabel title = Theme.label("\u2620  GRAND LINE REGISTRY  \u2620", Theme.TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));

        // FlowLayout.CENTER keeps the nav buttons centred at every window width
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        nav.setBackground(Theme.SEA);
        for(JButton b : new JButton[]{navCharacters, navGroups, navFruits, navCaptures, navWanted, navFiles}){
            nav.add(b);
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SEA);
        header.add(title, BorderLayout.NORTH);
        header.add(nav, BorderLayout.SOUTH);
        return header;
    }

    /**
     * Purpose: Lays out a two-column label/field form.
     * @param rows alternating label and component, e.g. {label, field, label, field}
     * @return the assembled form panel
     */
    private JPanel form(Component... rows){
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 6));
        p.setOpaque(false);
        for(Component c : rows){ p.add(c); }
        return p;
    }

    /**
     * Purpose: Lays out a row of buttons, centred.
     * @param buttons the buttons to place
     * @return the assembled button bar
     */
    private JPanel bar(JButton... buttons){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setOpaque(false);
        for(JButton b : buttons){ p.add(b); }
        return p;
    }

    /**
     * Purpose: Wraps a form and a table into the standard screen layout.
     * @param formTitle caption for the input card
     * @param formBody the input card contents
     * @param buttons the centred action bar
     * @param table the results table
     * @return the assembled screen
     */
    private JPanel screen(String formTitle, JPanel formBody, JPanel buttons, RegistryTable table){
        JPanel top = Theme.card(formTitle);
        top.add(formBody, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        JPanel bottom = Theme.card("Records");
        bottom.add(table, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(Theme.SEA);
        wrap.setBorder(BorderFactory.createEmptyBorder(4, 14, 14, 14));
        wrap.add(top, BorderLayout.NORTH);
        wrap.add(bottom, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Purpose: Builds the character roster screen.
     * @return the assembled screen
     */
    private JPanel buildCharacterScreen(){
        JPanel body = form(
                Theme.formLabel("Type:"), charType,
                Theme.formLabel("Name:"), charName,
                Theme.formLabel("Alias:"), charAlias,
                Theme.formLabel("Origin:"), charOrigin,
                Theme.formLabel("Wallet (Berries):"), charWallet,
                charExtra1Label, charExtra1,
                charExtra2Label, charExtra2,
                charRankLabel, charRank);
        charRankLabel.setVisible(false);
        charRank.setVisible(false);
        return screen("Register a Character", body,
                bar(charAdd, charDuty, charPromote, charDelete), charTable);
    }

    /**
     * Purpose: Builds the crews and corps screen.
     * @return the assembled screen
     */
    private JPanel buildGroupScreen(){
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
        recruit.add(groupMembers);
        recruit.add(groupDelete);

        JPanel bottom = Theme.card("Registered Groups");
        bottom.add(groupTable, BorderLayout.CENTER);
        bottom.add(recruit, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(Theme.SEA);
        wrap.setBorder(BorderFactory.createEmptyBorder(4, 14, 14, 14));
        wrap.add(top, BorderLayout.NORTH);
        wrap.add(bottom, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Purpose: Builds the devil fruit screen.
     * @return the assembled screen
     */
    private JPanel buildFruitScreen(){
        JPanel body = form(
                Theme.formLabel("Fruit Name:"), fruitName,
                Theme.formLabel("Category:"), fruitCategory,
                Theme.formLabel("Primary Ability:"), fruitAbility,
                Theme.formLabel("Feed to:"), fruitEater);
        return screen("Catalogue a Devil Fruit", body,
                bar(fruitAdd, fruitAssign, fruitDelete), fruitTable);
    }

    /**
     * Purpose: Builds the bounty office screen.
     * @return the assembled screen
     */
    private JPanel buildCaptureScreen(){
        captureDead.setOpaque(false);
        captureDead.setFont(Theme.BODY);
        captureDead.setForeground(Theme.INK);

        JPanel body = form(
                Theme.formLabel("Target Pirate:"), captureTarget,
                Theme.formLabel("Captor:"), captureCaptor,
                Theme.formLabel("Outcome:"), captureDead);
        return screen("Register a Capture", body, bar(captureRegister), captureTable);
    }

    /**
     * Purpose: Builds the Most Wanted leaderboard screen.
     * @return the assembled screen
     */
    private JPanel buildWantedScreen(){
        JPanel note = new JPanel(new FlowLayout(FlowLayout.CENTER));
        note.setOpaque(false);
        note.add(Theme.formLabel("Living, uncaptured pirates ranked by active bounty."));

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(note, BorderLayout.CENTER);
        return screen("Most Wanted", body, bar(wantedRefresh), wantedTable);
    }

    /**
     * Purpose: Builds the archives (file handling) screen.
     * @return the assembled screen
     */
    private JPanel buildFileScreen(){
        fileOutput.setEditable(false);
        fileOutput.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        fileOutput.setBackground(Theme.PARCHMENT);
        fileOutput.setForeground(Theme.INK);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        for(JButton b : new JButton[]{fileSave, fileLoad, fileInfo, fileArchive, fileSeed, fileClear}){
            buttons.add(b);
            buttons.add(Box.createVerticalStrut(8));
        }

        JPanel card = Theme.card("Archives");
        card.add(buttons, BorderLayout.WEST);
        card.add(new JScrollPane(fileOutput), BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Theme.SEA);
        wrap.setBorder(BorderFactory.createEmptyBorder(4, 14, 14, 14));
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Purpose: Brings one screen to the front.
     * @param name the card name, e.g. "characters"
     */
    public void show(String name){ cards.show(content, name); }

    /**
     * Purpose: Writes a one-line message to the status bar.
     * @param message the text to display
     */
    public void setStatus(String message){ statusBar.setText(message); }

    /**
     * Purpose: Appends a block of text to the archives output area.
     * @param text the text to append
     */
    public void appendFileOutput(String text){
        fileOutput.append(text + "\n");
        fileOutput.setCaretPosition(fileOutput.getDocument().getLength());
    }
}