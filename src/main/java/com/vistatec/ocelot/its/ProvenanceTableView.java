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
import com.vistatec.ocelot.events.SegmentDeselectionEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentAttributeView;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS Provenance metadata.
 */
public class ProvenanceTableView extends JScrollPane {
    private static final long serialVersionUID = 1L;

    private SegmentAttributeView segAttrView;
    protected JTable provTable;
    private ProvTableModel provTableModel;
    private ListSelectionModel tableSelectionModel;
    private TableRowSorter<ProvTableModel> sort;

    public ProvenanceTableView(EventBus eventBus, SegmentAttributeView sav) {
        eventBus.register(this);
        segAttrView = sav;
    }

    @Subscribe
    public void setSegment(SegmentSelectionEvent e) {
        Segment seg = e.getSegment();
        setViewportView(null);
        provTableModel = new ProvTableModel();
        provTable = new JTable(provTableModel);

        tableSelectionModel = provTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new ProvSelectionHandler());

        List<Provenance> provData = seg.getProv();
        provTableModel.setRows(provData);

        sort = new TableRowSorter<ProvTableModel>(provTableModel);
        provTable.setRowSorter(sort);

        setViewportView(provTable);
    }

    @Subscribe
    public void clearSegment(SegmentDeselectionEvent e) {
        if (provTable != null) {
            provTable.clearSelection();
            provTableModel.deleteRows();
            provTable.setRowSorter(null);
        }
        setViewportView(null);
    }

    public void selectedProv() {
        int rowIndex = provTable.getSelectedRow();
        if (rowIndex >= 0) {
            segAttrView.setSelectedMetadata(provTableModel.rows.get(rowIndex));
        }
    }

    public void deselectProv() {
        if (provTable != null) {
            provTable.clearSelection();
        }
    }

    public class ProvTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        public static final int NUMCOLS = 6;
        public String [] colNames = {"Person", "Org", "Tool",
            "RevPerson", "RevOrg", "RevTool", "ProvRef"};
        private List<Provenance> rows;

        public void setRows(List<Provenance> attrs) {
            rows = attrs;
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
