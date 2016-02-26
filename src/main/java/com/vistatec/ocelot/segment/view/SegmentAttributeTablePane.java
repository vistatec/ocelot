/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

public abstract class SegmentAttributeTablePane<T extends AbstractTableModel> extends JScrollPane implements OcelotEventQueueListener {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private T tableModel;
    private OcelotSegment selectedSegment;

    protected SegmentAttributeTablePane() {
        initializeTable();
    }

    private void initializeTable() {
        tableModel = createTableModel();
        table = buildTable(tableModel);
        table.setRowSorter(new TableRowSorter<T>(tableModel));
        setViewportView(table);
    }

    protected abstract T createTableModel();

    protected JTable buildTable(T model) {
        return new JTable(model);
    }

    protected T getTableModel() {
        return tableModel;
    }

    protected JTable getTable() {
        return table;
    }

    protected OcelotSegment getSelectedSegment() {
        return selectedSegment;
    }

    public void clearSelection() {
        getTable().clearSelection();
    }

    @Subscribe
    public void segmentSelected(SegmentSelectionEvent e) {
        segmentSelected(e.getSegment());
        getTableModel().fireTableDataChanged();
    }

    protected void segmentSelected(OcelotSegment seg) {
    	this.selectedSegment = seg;
    }

    @Subscribe
    public void fileOpened(OpenFileEvent e) {
        initializeTable();
    }
}
