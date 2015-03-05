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
package com.vistatec.ocelot.its.view;

import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.events.ProvenanceSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.view.SegmentAttributeTablePane;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * Table View for displaying segment ITS Provenance metadata.
 */
public class ProvenanceTableView extends SegmentAttributeTablePane<ProvenanceTableView.ProvTableModel> implements OcelotEventQueueListener {
    private static final long serialVersionUID = 1L;

    private final OcelotEventQueue eventQueue;

    public ProvenanceTableView(OcelotEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    protected ProvTableModel createTableModel() {
        return new ProvTableModel();
    }

    @Override
    protected JTable buildTable(ProvTableModel model) {
        JTable table = super.buildTable(model);
        ListSelectionModel tableSelectionModel = table.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new ProvSelectionHandler());
        return table;
    }

    @Override
    protected void segmentSelected(OcelotSegment seg) {
        List<Provenance> provData = seg.getProvenance();
        getTableModel().setRows(provData);
    }

    private void selectedProv() {
        int rowIndex = getTable().getSelectedRow();
        if (rowIndex >= 0) {
            eventQueue.post(new ProvenanceSelectionEvent(getTableModel().rows.get(rowIndex)));
        }
    }

    static class ProvTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        public static final int NUMCOLS = 6;
        public String [] colNames = {"Person", "Org", "Tool",
            "RevPerson", "RevOrg", "RevTool", "ProvRef"};
        private List<Provenance> rows = new ArrayList<Provenance>();

        public void setRows(List<Provenance> attrs) {
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
                    tableCell = rows.get(row).getPerson();
                    break;

                case 1:
                    tableCell = rows.get(row).getOrg();
                    break;

                case 2:
                    tableCell = rows.get(row).getTool();
                    break;

                case 3:
                    tableCell = rows.get(row).getRevPerson();
                    break;

                case 4:
                    tableCell = rows.get(row).getRevOrg();
                    break;

                case 5:
                    tableCell = rows.get(row).getRevTool();
                    break;

                case 6:
                    tableCell = rows.get(row).getProvRef();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: "+col);
            }
            return tableCell;
        }
    }

    public class ProvSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedProv();
        }
    }
}
