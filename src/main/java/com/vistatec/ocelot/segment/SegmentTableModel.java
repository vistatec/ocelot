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
package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.rules.NullITSMetadata;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

/**
 * Table model representing the aligned source/target segments and the flags
 * shown in the SegmentView.
 */
public class SegmentTableModel extends AbstractTableModel {

    private SegmentController segmentController;
    private LinkedList<Segment> segments = new LinkedList<Segment>();
    protected HashMap<String, Integer> colNameToIndex;
    protected HashMap<Integer, String> colIndexToName;
    public static final int NUMFLAGS = 5;
    public static final int NONFLAGCOLS = 4;
    public static final String COLSEGNUM = "#";
    public static final String COLSEGSRC = "Source";
    public static final String COLSEGTGT = "Target";
    public static final String COLSEGTGTORI = "Target Original";

    public SegmentTableModel(SegmentController segController) {
        segmentController = segController;
        colNameToIndex = new HashMap<String, Integer>();
        colNameToIndex.put(COLSEGNUM, 0);
        colNameToIndex.put(COLSEGSRC, 1);
        colNameToIndex.put(COLSEGTGT, 2);
        colNameToIndex.put(COLSEGTGTORI, 3);
        colIndexToName = new HashMap<Integer, String>();
        for (String key : colNameToIndex.keySet()) {
            colIndexToName.put(colNameToIndex.get(key), key);
        }
    }

    @Override
    public String getColumnName(int col) {
        return col < NONFLAGCOLS ? colIndexToName.get(col) : "";
    }

    public int getColumnIndex(String col) {
        return colNameToIndex.get(col);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == getColumnIndex(COLSEGNUM)) {
            return Integer.class;
        }
        if (columnIndex == getColumnIndex(COLSEGSRC)
                || columnIndex == getColumnIndex(COLSEGTGT)
                || columnIndex == getColumnIndex(COLSEGTGTORI)) {
            return String.class;
        }
        return ITSMetadata.class;
    }

    @Override
    public int getRowCount() {
        return segments.size();
    }

    @Override
    public int getColumnCount() {
        return NONFLAGCOLS + NUMFLAGS;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == getColumnIndex(COLSEGNUM)) {
            return getSegment(row).getSegmentNumber();
        }
        if (col == getColumnIndex(COLSEGSRC)) {
            return getSegment(row).getSource().getCodedText();
        }
        if (col == getColumnIndex(COLSEGTGT)) {
            return getSegment(row).getTarget().getCodedText();
        }
        if (col == getColumnIndex(COLSEGTGTORI)) {
            return getSegment(row).getOriginalTarget().getCodedText();
        }
        Object ret = segmentController.getRuleConfig().getTopDataCategory(
                segments.get(row), col-NONFLAGCOLS);
        return ret != null ? ret : NullITSMetadata.getInstance();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == colNameToIndex.get(COLSEGTGT);
    }

    public void addSegment(Segment seg) {
        segments.add(seg);
    }

    public Segment getSegment(int row) {
        return segments.get(row);
    }

    protected void deleteSegments() {
        segments.clear();
        segmentController.notifyDeletedSegments();
    }
}
