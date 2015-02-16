package com.vistatec.ocelot.segment;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.SegmentDeselectionEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;

public abstract class SegmentAttributeTablePane extends JScrollPane {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private AbstractTableModel tableModel;
    private Segment selectedSegment;
    private EventBus eventBus;

    protected SegmentAttributeTablePane(EventBus eventBus) {
        this.tableModel = buildTableModelForSegment(null);
        this.table = new JTable(tableModel);
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    protected abstract AbstractTableModel buildTableModelForSegment(Segment segment);

    protected JTable buildTable(TableModel model) {
        return new JTable(model);
    }

    protected AbstractTableModel getTableModel() {
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
        Segment seg = e.getSegment();
        setViewportView(null);
        this.tableModel = buildTableModelForSegment(seg);
        this.table = buildTable(tableModel);
        setViewportView(this.table);
    }

    @Subscribe
    public void segmentDeselected(SegmentDeselectionEvent e) {
        clearSelection();
        // TODO
        //tableModel.deleteRows();
        table.setRowSorter(null);
        setViewportView(null);
    }

}
