package com.vistatec.ocelot.segment;

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
    }

    @Subscribe
    public void fileOpened(OpenFileEvent e) {
        initializeTable();
    }
}
