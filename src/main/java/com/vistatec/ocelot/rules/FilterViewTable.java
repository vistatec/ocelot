package com.vistatec.ocelot.rules;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.vistatec.ocelot.ui.OTable;
import com.vistatec.ocelot.ui.TableRowToggleMouseAdapter;

/**
 * Class that wraps a {@link JTable} that contains rule information
 * and checkboxes to select them.
 */
public abstract class FilterViewTable {
    private JTable table;
    private AbstractTableModel tableModel;
    private RuleConfiguration ruleConfig;
    private boolean allowSelection;

    public FilterViewTable(RuleConfiguration ruleConfig) {
        this.table = createFilterViewTable();
        this.ruleConfig = ruleConfig;
        this.allowSelection = getInitialAllowSelection();
    }

    public JTable getTable() {
        return table;
    }

    protected RuleConfiguration getConfig() {
        return ruleConfig;
    }

    protected abstract boolean getInitialAllowSelection(); 

    protected abstract AbstractTableModel getTableModel();

    protected JTable createFilterViewTable() {
        tableModel = getTableModel();
        JTable table = new OTable(tableModel);
        table.setTableHeader(null);
        table.setCellSelectionEnabled(false);
        table.setShowGrid(false);
        table.setDefaultRenderer(Boolean.class, new GreyableCheckboxRenderer());
        table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
        // Add a little little breathing room, particularly around the edge
        table.setRowHeight(table.getRowHeight() + 4);

        TableColumnModel columnModel = table.getColumnModel();
        // Hack - size the column to fit a checkbox exactly.  Otherwise
        // there's a rendering glitch where the checkbox can jump around
        // in the cell when clicked.
        JCheckBox cb = new JCheckBox();
        int cbWidth = cb.getPreferredSize().width + 4;
        columnModel.getColumn(0).setMinWidth(cbWidth);
        columnModel.getColumn(0).setPreferredWidth(cbWidth);
        columnModel.getColumn(0).setMaxWidth(cbWidth);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.getColumn(1).setPreferredWidth(325);
        columnModel.getColumn(1).setMaxWidth(325);
        columnModel.getColumn(2).setMinWidth(25);
        columnModel.getColumn(2).setPreferredWidth(25);
        columnModel.getColumn(2).setMaxWidth(25);

        table.addMouseListener(new TableRowToggleMouseAdapter() {
            @Override
            protected boolean acceptEvent(int row, int column) {
                return (allowSelection && column > 0);
            }
        });
        return table;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
        tableModel.fireTableDataChanged();
    }

    class GreyableCheckboxRenderer extends JCheckBox implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setEnabled(allowSelection);
            setHorizontalAlignment(JLabel.CENTER);
            if (value instanceof Boolean) {
                this.setSelected((Boolean)value);
            }
            return this;
        }
    }

    abstract class FilterTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return allowSelection && column == 0;
        }

        @Override
        public abstract int getRowCount();

        @Override
        public abstract Object getValueAt(int row, int column);

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return DataCategoryFlag.class;
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }
        
        @Override
        public abstract void setValueAt(Object obj, int row, int column);
    }
}
