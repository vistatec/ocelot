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
package com.vistatec.ocelot.its;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.events.LQIDeselectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentAttributeTablePane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS metadata.
 */
public class LanguageQualityIssueTableView extends 
            SegmentAttributeTablePane<LanguageQualityIssueTableView.LQITableModel> {
    private static final long serialVersionUID = 1L;

    public LanguageQualityIssueTableView(EventBus eventBus) {
        super(eventBus);
        addMouseListener(new LQIPopupMenuListener());
    }

    @Override
    protected LQITableModel createTableModel() {
        return new LQITableModel();
    }

    @Override
    protected JTable buildTable(LQITableModel model) {
        JTable table = super.buildTable(model);
        ListSelectionModel tableSelectionModel = table.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new LQISelectionHandler());
        table.setRowSorter(new TableRowSorter<LQITableModel>(model));
        table.addMouseListener(new LQIPopupMenuListener());
        return table;
    }

    @Subscribe
    public void handleLQIUpdate(LQIModificationEvent e) {
        getTableModel().fireTableDataChanged();
    }

    private void selectedLQI() {
        int rowIndex = getTable().getSelectedRow();
        if (rowIndex >= 0) {
            ITSMetadata selected = getTableModel().getRow(rowIndex);
            getEventBus().post(new LQISelectionEvent((LanguageQualityIssue)selected));
        }
    }

    @Override
    protected void segmentSelected(Segment seg) {
        List<LanguageQualityIssue> lqiData = seg.getLQI();
        getTableModel().setRows(lqiData);
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        getEventBus().post(new LQIDeselectionEvent());
    }

    static class LQITableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        public static final int NUMCOLS = 3;
        public String[] colNames = {"Type", "Severity", "Comment"};
        private List<LanguageQualityIssue> rows = new ArrayList<LanguageQualityIssue>();

        public LanguageQualityIssue getRow(int row) {
            return rows.get(row);
        }

        public void setRows(List<LanguageQualityIssue> attrs) {
            rows.clear();
            rows.addAll(attrs);
        }

        public void deleteRows() {
            rows.clear();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return NUMCOLS;
        }

        @Override
        public String getColumnName(int col) {
            return col < NUMCOLS ? colNames[col] : "";
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object tableCell;
            switch (col) {
                case 0:
                    tableCell = rows.get(row).getType();
                    break;

                case 1:
                    tableCell = rows.get(row).getSeverity();
                    break;
                case 2:
                    tableCell = rows.get(row).getComment();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: " + col);
            }
            return tableCell;
        }
    }

    public class LQISelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedLQI();
        }
    }

    public class LQIPopupMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            lqiPopup(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            lqiPopup(e);
        }
        void lqiPopup(MouseEvent e) {
            LanguageQualityIssue selectedLQI = null;
            if (getTable() != null) {
                int r = getTable().rowAtPoint(e.getPoint());
                if (r >= 0 && r < getTable().getRowCount()) {
                    getTable().setRowSelectionInterval(r, r);
                    selectedLQI = getTableModel().getRow(r);
                }
            }
            if (e.isPopupTrigger() && getSelectedSegment() != null) {
                ContextMenu menu = selectedLQI == null ?
                        new ContextMenu(getSelectedSegment()) :
                        new ContextMenu(getSelectedSegment(), selectedLQI);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}