/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.view;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.RuleConfiguration;

import static com.vistatec.ocelot.SegmentViewColumn.*;

import com.vistatec.ocelot.services.SegmentService;

import javax.swing.table.AbstractTableModel;

import com.google.inject.Inject;

/**
 * Table model that repackages SegmentController and rule data for use
 * in a SegmentView.
 */
public class SegmentTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private RuleConfiguration ruleConfig;
    protected EnumMap<SegmentViewColumn, Boolean> enabledColumns =
            new EnumMap<SegmentViewColumn, Boolean>(SegmentViewColumn.class);

    private final SegmentService segmentService;
    private final JsonConfigService configService;

    @Inject
    public SegmentTableModel(SegmentService segmentService,
                             RuleConfiguration ruleConfig, JsonConfigService configService) {
        this.segmentService = segmentService;
        this.ruleConfig = ruleConfig;
        this.configService = configService;
        for (SegmentViewColumn c : SegmentViewColumn.values()) {
            enabledColumns.put(c, configService.isColumnEnabled(c));
        }
    }

    public boolean isColumnEnabled(SegmentViewColumn column) {
        return enabledColumns.get(column);
    }

    public void setColumnEnabled(SegmentViewColumn column, boolean enabled) {
        enabledColumns.put(column, enabled);
    }

    public Map<SegmentViewColumn, Boolean> getColumnEnabledStates() {
        return Collections.unmodifiableMap(enabledColumns);
    }

    @Override
    public String getColumnName(int col) {
        return getColumn(col).getName();
    }

    int getSegmentNumColumnIndex() {
        return SegNum.ordinal();
    }

   public int getSegmentSourceColumnIndex() {
        return Source.ordinal();
    }

    public int getSegmentTargetColumnIndex() {
        return Target.ordinal();
    }

    int getSegmentTargetOriginalColumnIndex() {
        return Original.ordinal();
    }
    
    int getNotesColumnIndex(){
    	return Notes.ordinal();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getColumn(columnIndex).getDatatype();
    }

    @Override
    public int getRowCount() {
        return segmentService.getNumSegments();
    }

    /**
     * Return the number of visible columns.
     */
    @Override
    public int getColumnCount() {
        int count = 0;
        for (boolean b : enabledColumns.values()) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return the column in a given column index.
     * @param index
     * @return
     */
    public SegmentViewColumn getColumn(int index) {
        int count = 0;
        for (SegmentViewColumn column : SegmentViewColumn.values()) {
            if (enabledColumns.get(column)) {
                if (count == index) {
                    return column;
                }
                count++;
            }
        }
        return null;
    }

    /**
     * Return the display index of this column, or -1 if the column
     * is currently hidden.
     * @param column
     * @return column index, or -1 if the column is hidden
     */
    public int getIndexForColumn(SegmentViewColumn column) {
        int index = 0;
        if (!enabledColumns.get(column)) {
            return -1;
        }
        for (SegmentViewColumn col : SegmentViewColumn.values()) {
            if (col.equals(column)) {
                return index;
            }
            if (enabledColumns.get(col)) {
                index++;
            }
        }
        return -1; // should never happen
    }

    @Override
    public Object getValueAt(int row, int col) {
        SegmentViewColumn column = getColumn(col);

        switch (column) {
        case SegNum:
            return getSegment(row).getSegmentNumber();
        case Source:
            return getSegment(row).getSource();
        case Target:
            return getSegment(row).getTarget();
        case Original:
            return getSegment(row).getOriginalTarget();
        case EditDistance:
            return getSegment(row).getEditDistance();
        case Notes:
        	return getSegment(row).getNotes();
        default: // flag cases
            Object ret = ruleConfig.getTopDataCategory(
                    getSegment(row), column.getFlagIndex());
            return ret != null ? ret : NullITSMetadata.getInstance();
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    	if(rowIndex < segmentService.getNumSegments()){
//    		SegmentViewColumn column = getColumn(columnIndex);
//    		if(column.equals(Notes)){
//    			getSegment(rowIndex).getNotes().editNote((String)aValue, String.valueOf(getSegment(rowIndex).getSegmentNumber()));
//    		} else {
    			super.setValueAt(aValue, rowIndex, columnIndex);
//    		}
    	}
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return ((col == getSegmentTargetColumnIndex() || col == getSegmentSourceColumnIndex()) && segmentService.getSegment(row).isTranslatable() )  || col == getNotesColumnIndex();
    }

    public OcelotSegment getSegment(int row) {
        return segmentService.getSegment(row);
    }
    
	public int getModelIndexForSegment(OcelotSegment segment) {

		return getModelIndexBySegmentNumber(segment.getSegmentNumber());
	}

	public int getModelIndexBySegmentNumber(int segmentNumber) {

		int index = -1;
		for (int i = 0; i < segmentService.getNumSegments(); i++) {
			if (segmentService.getSegment(i).getSegmentNumber() == segmentNumber) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
    public void saveColumnConfiguration() throws TransferException {
		
		configService.saveColumnConfiguration(enabledColumns);
	}
}
