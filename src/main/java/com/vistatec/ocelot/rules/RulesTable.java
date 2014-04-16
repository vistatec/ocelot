package com.vistatec.ocelot.rules;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.vistatec.ocelot.rules.NullITSMetadata.NullDataCategoryFlag;

/**
 * Class that wraps a {@link JTable} that contains rule information
 * and checkboxes to select them.
 */
public class RulesTable {
    private JTable table;
    private boolean allowEnableDisable;
    private TableModel tableModel;
    private RuleConfiguration ruleConfig;

    public RulesTable(RuleConfiguration ruleConfig) {
        this.table = createRulesTable();
        this.ruleConfig = ruleConfig;
        // XXX Ugly
        this.allowEnableDisable = 
                !ruleConfig.getAllSegments() &&
                !ruleConfig.getAllMetadataSegments();
    }

    public JTable getTable() {
        return table;
    }

    private JTable createRulesTable() {
        tableModel = new TableModel();
        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Boolean.class, new GreyableCheckboxRenderer());
        table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
        TableColumnModel columnModel = table.getColumnModel();
        // Hack - size the column to fit a checkbox exactly.  Otherwise
        // there's a rendering glitch where the checkbox can jump around
        // in the cell when clicked.
        JCheckBox cb = new JCheckBox();
        int cbWidth = cb.getPreferredSize().width;
        columnModel.getColumn(0).setMinWidth(cbWidth);
        columnModel.getColumn(0).setPreferredWidth(cbWidth);
        columnModel.getColumn(0).setMaxWidth(cbWidth);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(1).setMaxWidth(500);
        columnModel.getColumn(2).setMinWidth(25);
        columnModel.getColumn(2).setPreferredWidth(25);
        columnModel.getColumn(2).setMaxWidth(25);
        return table;
    }

    public boolean getAllowEnableDisable() {
        return allowEnableDisable;
    }

    public void setAllowEnableDisable(boolean allowEnableDisable) {
        this.allowEnableDisable = allowEnableDisable;
        tableModel.fireTableDataChanged();
    }

    class GreyableCheckboxRenderer extends JCheckBox implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setEnabled(allowEnableDisable);
            if (value instanceof Boolean) {
                this.setSelected((Boolean)value);
            }
            return this;
        }
    }

    class TableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return allowEnableDisable && column == 0;
        }

        @Override
        public int getRowCount() {
            return ruleConfig.getRules().size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            Rule rule = ruleConfig.getRules().get(row);
            switch (column) {
            case 0:
                return rule.getEnabled();
            case 1:
                return rule.getLabel();
            case 2:
                DataCategoryFlag flag = rule.getFlag();
                return (flag != null) ? flag : NullDataCategoryFlag.getInstance();
            }
            throw new IllegalArgumentException("Invalid column " + column);
        }

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
        public void setValueAt(Object obj, int row, int column) {
            if (obj instanceof Boolean && column == 0) {
                Rule rule = ruleConfig.getRules().get(row);
                rule.setEnabled((Boolean)obj);
                fireTableCellUpdated(row, column);
            }
        }
    }
}
