// ===== VIEW LAYER =====
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;

/** Theme
 * Purpose: Central palette and component factory so every screen shares it
 * One Piece inspired look.
 */
public final class Theme {

    /** Purpose: Utility class - never instantiated. */
    private Theme(){ }

    public static final Color SEA = new Color(11, 37, 69);
    public static final Color DEEP = new Color(20, 55, 95);
    public static final Color GOLD = new Color(232, 181, 71);
    public static final Color RED = new Color(176, 58, 46);
    public static final Color PARCHMENT = new Color(247, 239, 219);
    public static final Color INK = new Color(58, 42, 26);

    public static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    public static final Font HEADING = new Font("Serif", Font.BOLD, 17);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font BUTTON = new Font("SansSerif", Font.BOLD, 13);

    /**
     * Purpose: Builds a themed button.
     * @param text the button caption
     * @param background the fill color
     * @return a styled, centered button.
     */
    public static JButton button(String text, Color background){
        JButton b = new JButton(text);
        b.setFont(BUTTON);
        b.setBackground(background);
        b.setForeground(background == GOLD ? INK : Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD.darker(), 1),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    /**
     * Purpose: Builds a gold button.
     * @param text the button caption
     * @return a button in the  accent color
     */
    public static JButton gold(String text){ return button(text, GOLD); }

    /**
     * Purpose: Builds a red button.
     * @param text the button caption
     * @return a styled button in the warning color
     */
    public static JButton danger(String text){ return button(text, RED); }

    /**
     * Purpose: Builds a label rendered in gold on the dark background.
     * @param text the caption
     * @param font the font to apply
     * @return the styled label
     */
    public static JLabel label(String text, Font font){
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(GOLD);
        return l;
    }

    /**
     * Purpose: Builds a label rendered in ink for use on parchment forms.
     * @param text the caption
     * @return the styled label
     */
    public static JLabel formLabel(String text){
        JLabel l = new JLabel(text);
        l.setFont(BODY);
        l.setForeground(INK);
        return l;
    }

    /**
     * Purpose: Builds a parchment panel with a gold titled border.
     * @param title the border caption
     * @return the styled panel, using BorderLayout by default
     */
    public static JPanel card(String title){
        JPanel p = new JPanel(new java.awt.BorderLayout(8, 8));
        p.setBackground(PARCHMENT);
        p.setBorder(titledBorder(title));
        return p;
    }

    /**
     * Purpose: Builds the gold titled border shared by every card.
     * @param title the border caption
     * @return the border
     */
    public static Border titledBorder(String title){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(GOLD.darker(), 2), title,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP, HEADING, INK),
                BorderFactory.createEmptyBorder(6, 8, 8, 8));
    }

    /**
     * Purpose: Applies the shared look to a results table.
     * @param table the table to style
     */
    public static void style(JTable table){
        table.setFont(BODY);
        table.setRowHeight(23);
        table.setBackground(PARCHMENT);
        table.setForeground(INK);
        table.setGridColor(new Color(205, 190, 160));
        table.setSelectionBackground(GOLD);
        table.setSelectionForeground(INK);
        table.getTableHeader().setFont(BUTTON);
        table.getTableHeader().setBackground(DEEP);
        table.getTableHeader().setForeground(GOLD);
        table.setPreferredScrollableViewportSize(new Dimension(700, 220));
    }
}
