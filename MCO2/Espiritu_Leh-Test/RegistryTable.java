// ===== VIEW LAYER =====
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/** RegistryTable
 * Purpose: A read-only results table with a live search box above it.
 * BONUS FEATURE 1 - instant filtering across every column as the user types.
 */
public final class RegistryTable extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;

    /**
     * Purpose: Builds the table, its sorter, and the search box.
     * @param columns the column headings
     */
    public RegistryTable(String[] columns){
        super(new BorderLayout(6, 6));
        setOpaque(false);

        this.tableModel = new DefaultTableModel(columns, 0){
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column){ return false; }
        };
        this.table = new JTable(tableModel);
        Theme.style(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        this.searchField = new JTextField();
        this.searchField.setFont(Theme.BODY);
        this.searchField.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e){ apply(); }
            @Override public void removeUpdate(DocumentEvent e){ apply(); }
            @Override public void changedUpdate(DocumentEvent e){ apply(); }

            /** Purpose: Rebuilds the row filter from the current search text. */
            private void apply(){
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.setOpaque(false);
        top.add(Theme.formLabel("Search:"), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Purpose: Replaces every row with a fresh data set.
     * @param rows the new rows, each an array matching the column count
     */
    public void setRows(List<Object[]> rows){
        tableModel.setRowCount(0);
        for(Object[] row : rows){
            tableModel.addRow(row);
        }
    }

    /**
     * Purpose: Reads the first column of the selected row, which always holds the ID.
     * @return the selected ID, or -1 if nothing is selected
     */
    public int getSelectedId(){
        int viewRow = table.getSelectedRow();
        if(viewRow < 0) return -1;
        Object value = tableModel.getValueAt(table.convertRowIndexToModel(viewRow), 0);
        return (value instanceof Integer i) ? i : Integer.parseInt(value.toString());
    }

    /** @return the underlying table, for callers needing selection events */
    public JTable getTable(){ return this.table; }
}