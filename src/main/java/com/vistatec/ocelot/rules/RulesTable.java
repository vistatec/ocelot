package com.vistatec.ocelot.rules;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.vistatec.ocelot.rules.NullITSMetadata.NullDataCategoryFlag;
import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;

/**
 * Class that wraps a {@link JTable} that contains rule information
 * and checkboxes to select them.
 */
public class RulesTable extends FilterViewTable {
    public RulesTable(RuleConfiguration ruleConfig) {
        super(ruleConfig);
    }

    @Override
    protected boolean getInitialAllowSelection() {
        return (getConfig().getFilterMode() == FilterMode.SELECTED_SEGMENTS);
    }

    @Override
    protected TableModel getTableModel() {
        return new TableModel();
    }

    class TableModel extends FilterTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getRowCount() {
            return getConfig().getRules().size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            Rule rule = getConfig().getRules().get(row);
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
        public void setValueAt(Object obj, int row, int column) {
            if (obj instanceof Boolean && column == 0) {
                Rule rule = getConfig().getRules().get(row);
                getConfig().enableRule(rule, (Boolean)obj);
                fireTableCellUpdated(row, column);
            }
        }
    }
}
