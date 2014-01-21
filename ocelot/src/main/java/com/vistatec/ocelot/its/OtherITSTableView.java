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

import com.vistatec.ocelot.segment.Segment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table view for displaying simple key-value pair ITS metadata on a segment.
 */
public class OtherITSTableView extends JScrollPane {
    private Logger LOG = LoggerFactory.getLogger(OtherITSTableView.class);
    protected JTable itsTable;
    private OtherITSTableModel itsTableModel;

    public OtherITSTableView() {}

    public JTable getTable() {
        return this.itsTable;
    }

    public OtherITSTableModel getTableModel() {
        return this.itsTableModel;
    }

    public void setSegment(Segment seg) {
        setViewportView(null);
        this.itsTableModel = new OtherITSTableModel();
        this.itsTable = new JTable(this.itsTableModel);

        List<OtherITSMetadata> itsMetadata = seg.getOtherITSMetadata();
        this.itsTableModel.setRows(itsMetadata);
        setViewportView(this.itsTable);
    }

    public void clearSegment() {
        clearTableSelection();
        deleteTableValues();
        setViewportView(null);
    }

    public void clearTableSelection() {
        if (getTable() != null) {
            getTable().clearSelection();
        }
    }

    public void deleteTableValues() {
        if (getTableModel() != null) {
            getTableModel().deleteRows();
        }
    }

    public class OtherITSTableModel extends AbstractTableModel {
        public static final int NUMCOLS = 2;
        public String[] colNames = {"Name", "Value"};
        private List<OtherITSMetadata> rows = new ArrayList<OtherITSMetadata>();

        public void setRows(List<OtherITSMetadata> attrs) {
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
            OtherITSMetadata itsRow = rows.get(row);
            Object ret = "";
            if (itsRow != null) {
                switch (col) {
                    case 0:
                        ret = itsRow.getType().getName();
                        break;

                    case 1:
                        ret = itsRow.getValue();
                        break;

                    default:
                        throw new IllegalArgumentException("Incorrect number of columns: "+col);
                }
            } else {
                LOG.warn("Invalid value for ITS table view ("+row+", "+col+")");
            }
            return ret;
        }
    }
}
