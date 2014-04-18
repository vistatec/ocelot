package com.vistatec.ocelot.rules;

import java.util.Arrays;

import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;

public class StateQualifierTable extends FilterViewTable {

    public StateQualifierTable(RuleConfiguration ruleConfig) {
        super(ruleConfig);
    }

    @Override
    protected boolean getInitialAllowSelection() {
        return (getConfig().getStateQualifierMode() == StateQualifierMode.SELECTED_STATES);
    }

    @Override
    protected TableModel getTableModel() {
        return new TableModel();
    }

    class TableModel extends FilterTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getRowCount() {
            return StateQualifier.values().length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            StateQualifier state = getStateForRow(row);
            switch (column) {
            case 0:
                return getConfig().getStateQualifierEnabled(state);
            case 1:
                return state.getName();
            case 2:
                DataCategoryFlag flag = new DataCategoryFlag(); 
                flag.setFill(getConfig().getStateQualifierColor(state));
                flag.setText(String.valueOf(row + 1));
                return flag;
            }
            throw new IllegalArgumentException("Invalid column " + column);
        }

        @Override
        public void setValueAt(Object obj, int row, int column) {
            if (obj instanceof Boolean && column == 0) {
                StateQualifier state = getStateForRow(row);
                getConfig().setStateQualifierEnabled(state, (Boolean)obj);
                fireTableCellUpdated(row, column);
            }
        }

        private StateQualifier getStateForRow(int row) {
            return Arrays.asList(StateQualifier.values()).get(row);
        }
    }
}
