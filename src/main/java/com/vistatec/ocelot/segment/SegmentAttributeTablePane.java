package com.vistatec.ocelot.segment;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;

public abstract class SegmentAttributeTablePane<T extends AbstractTableModel> extends JScrollPane {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private T tableModel;
    private Segment selectedSegment;
    private EventBus eventBus;

    protected SegmentAttributeTablePane(EventBus eventBus) {
        initializeTable();
        this.eventBus = eventBus;
        eventBus.register(this);
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

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected Segment getSelectedSegment() {
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

    protected void segmentSelected(Segment seg) {
    }

    @Subscribe
    public void fileOpened(OpenFileEvent e) {
        initializeTable();
    }
}
