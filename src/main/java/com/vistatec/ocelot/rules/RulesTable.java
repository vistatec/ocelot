package com.vistatec.ocelot.rules;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.vistatec.ocelot.rules.NullITSMetadata.NullDataCategoryFlag;

/**
 * Class that wraps a {@link JTable} that contains rule information
 * and checkboxes to select them.
 */
public class RulesTable {
    // XXX I may need this thing to be an instance that exposes the state.
    // (checkboxes)
    public static JTable createRulesTable(RuleConfiguration ruleConfig) {
        JTable table = new JTable(new TableModel(ruleConfig));
        table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(100);
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(0).setMaxWidth(500);
        columnModel.getColumn(1).setMinWidth(15);
        columnModel.getColumn(1).setPreferredWidth(15);
        columnModel.getColumn(1).setMaxWidth(15);
        return table;
    }

    static class TableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private RuleConfiguration ruleConfig;

        TableModel(RuleConfiguration ruleConfig) {
            this.ruleConfig = ruleConfig;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return ruleConfig.getRules().size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Rule rule = ruleConfig.getRules().get(rowIndex);
            switch (columnIndex) {
            case 0:
                return rule.getLabel();
            case 1:
                DataCategoryFlag flag = rule.getFlag();
                return (flag != null) ? flag : NullDataCategoryFlag.getInstance();
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return DataCategoryFlag.class;
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }
        
    }

}
