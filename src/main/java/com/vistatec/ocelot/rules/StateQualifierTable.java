/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
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
