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

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ItsSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.QuickAddEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.SegmentTargetEnterEvent;
import com.vistatec.ocelot.events.SegmentTargetExitEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.SegmentViewColumn;

import static com.vistatec.ocelot.SegmentViewColumn.*;

import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import com.vistatec.ocelot.rules.DataCategoryFlagRenderer;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.SegmentSelector;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RuleListener;
import com.vistatec.ocelot.rules.StateQualifier;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener, OcelotEventQueueListener {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = Logger.getLogger(SegmentView.class);

    protected SegmentTableModel segmentTableModel;
    protected JTable sourceTargetTable;
    private TableColumnModel tableColumnModel;
    protected TableRowSorter<SegmentTableModel> sort;
    private boolean enabledTargetDiff = true;

    protected RuleConfiguration ruleConfig;
    private final OcelotEventQueue eventQueue;

    @Inject
    public SegmentView(OcelotEventQueue eventQueue, SegmentTableModel segmentTableModel,
            RuleConfiguration ruleConfig) throws IOException, InstantiationException,
            InstantiationException, IllegalAccessException {
        this.eventQueue = eventQueue;
        this.segmentTableModel = segmentTableModel;
        this.ruleConfig = ruleConfig;
        this.ruleConfig.addRuleListener(this);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
    }

    public final void initializeTable() {
        sourceTargetTable = new JTable(segmentTableModel);
        sourceTargetTable.getTableHeader().setReorderingAllowed(false);

        ListSelectionListener selectSegmentHandler = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                selectedSegment();
            }
        };
        ListSelectionModel tableSelectionModel = sourceTargetTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(selectSegmentHandler);

        sourceTargetTable.setDefaultRenderer(Integer.class, new IntegerRenderer());
        sourceTargetTable.setDefaultRenderer(ITSMetadata.class,
                new ITSMetadataRenderer());
        sourceTargetTable.setDefaultRenderer(SegmentVariant.class,
                new SegmentTextRenderer());

        // Install our custom edit behavior: hitting 'enter' anywhere inside the
        // row will open the target cell for editing.  Double-clicking will also
        // work, but this is handled separately (see SegmentEditor.isCellEditable()).
        InputMap inputMap = sourceTargetTable.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = sourceTargetTable.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "myStartEditing");
        final Action startEditAction = actionMap.get("startEditing");
        actionMap.put("myStartEditing", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceTargetTable.changeSelection(sourceTargetTable.getSelectedRow(),
                        segmentTableModel.getSegmentTargetColumnIndex(), false, false);
                startEditAction.actionPerformed(e);
                // Ensure the editor is focused.  XXX It's wrapped in a scrollpane, so we need
                // to drill down to get the SegmentTextCell instance.
                JScrollPane scrollPane = (JScrollPane)sourceTargetTable.getEditorComponent();
                scrollPane.getViewport().getView().requestFocus();
            }
        });

        tableColumnModel = sourceTargetTable.getColumnModel();
        configureColumns();
        tableColumnModel.getSelectionModel().addListSelectionListener(
                selectSegmentHandler);
        tableColumnModel.addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent tcme) {
                // Set the preferred column width when we add a column back
                // (eg, when we're changing column setup)
                int index = tcme.getToIndex();
                configureColumn(segmentTableModel.getColumn(index), index);
            }

            @Override
            public void columnRemoved(TableColumnModelEvent tcme) {}

            @Override
            public void columnMoved(TableColumnModelEvent tcme) {}

            @Override
            public void columnMarginChanged(ChangeEvent ce) {
                updateRowHeights();
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent lse) {}
        });

        addFilters();

        sourceTargetTable.addMouseListener(new SegmentPopupMenuListener());
        setViewportView(sourceTargetTable);
    }

    private void configureColumns() {
        for (SegmentViewColumn col : SegmentViewColumn.values()) {
            configureColumn(col, segmentTableModel.getIndexForColumn(col));
        }
    }

    private void configureColumn(SegmentViewColumn col, int index) {
        if (!segmentTableModel.isColumnEnabled(col)) {
            return;
        }
        switch (col) {
        case SegNum:
            tableColumnModel.getColumn(index).setMinWidth(20);
            tableColumnModel.getColumn(index).setPreferredWidth(25);
            tableColumnModel.getColumn(index).setMaxWidth(50);
            break;
        case Target:
            tableColumnModel.getColumn(index).setCellEditor(new SegmentEditor());
            break;
        case EditDistance:
            tableColumnModel.getColumn(index).setMinWidth(25);
            tableColumnModel.getColumn(index).setPreferredWidth(25);
            tableColumnModel.getColumn(index).setMaxWidth(40);
            break;
        case Flag1:
        case Flag2:
        case Flag3:
        case Flag4:
        case Flag5:
            tableColumnModel.getColumn(index).setMinWidth(15);
            tableColumnModel.getColumn(index).setPreferredWidth(20);
            tableColumnModel.getColumn(index).setMaxWidth(20);
            break;
        default:
            break;
        }
    }
    
    public SegmentTableModel getTableModel() {
        return segmentTableModel;
    }

    public void clearTable() {
        sourceTargetTable.clearSelection();
        sourceTargetTable.setRowSorter(null);
        setViewportView(null);
    }

    public void reloadTable() {
        clearTable();
        setViewportView(sourceTargetTable);
        addFilters();
        segmentTableModel.fireTableDataChanged();
        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segmentTableModel.getSegmentNumColumnIndex())
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + segmentTableModel.getRowCount()));
        updateRowHeights();
    }

    private void updateTableRow(int row) {
        segmentTableModel.fireTableRowsUpdated(row, row);
        updateRowHeights();
    }

    public void requestFocusTable() {
        sourceTargetTable.requestFocus();
    }

    public void addFilters() {
        sort = new TableRowSorter<SegmentTableModel>(segmentTableModel);
        sourceTargetTable.setRowSorter(sort);
        sort.setRowFilter(new RowFilter<SegmentTableModel, Integer>() {
            private SegmentSelector selector = new SegmentSelector(ruleConfig);
            @Override
            public boolean include(
                    RowFilter.Entry<? extends SegmentTableModel, ? extends Integer> entry) {
                return selector.matches(entry.getModel().getSegment(entry.getIdentifier()));
            }
        });
    }

    protected void updateRowHeights() {
        if (sourceTargetTable.getColumnModel().getColumnCount() != segmentTableModel.getColumnCount()) {
            // We haven't finished building the column model, so there's no point in calculating
            // the row height yet.
            return;
        }
        setViewportView(null);
        for (int viewRow = 0; viewRow < sort.getViewRowCount(); viewRow++) {
            int modelRow = sort.convertRowIndexToModel(viewRow);
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            int rowHeight = font.getHeight();
            rowHeight = getColumnHeight(SegNum, viewRow, "1", rowHeight);
            rowHeight = getColumnHeight(Source, viewRow,
                    segmentTableModel.getSegment(modelRow).getSource().getDisplayText(), rowHeight);
            rowHeight = getColumnHeight(Target, viewRow,
                    segmentTableModel.getSegment(modelRow).getTarget().getDisplayText(), rowHeight);
            rowHeight = getColumnHeight(Original, viewRow, getOriginalTargetText(modelRow), rowHeight);
            sourceTargetTable.setRowHeight(viewRow, rowHeight);
        }
        setViewportView(sourceTargetTable);
    }

    // TODO: move elsewhere
    private String getOriginalTargetText(int modelRow) {
        if (enabledTargetDiff) {
            List<String> textDiff = segmentTableModel.getSegment(modelRow).getTargetDiff();
            StringBuilder displayText = new StringBuilder();
            for (int i = 0; i < textDiff.size(); i += 2) {
                displayText.append(textDiff.get(i));
            }
            return displayText.toString();
        } else {
            return segmentTableModel.getSegment(modelRow).getOriginalTarget().getDisplayText();
        }
    }
    
    private int getColumnHeight(SegmentViewColumn colData, int viewRow, String text, int previousHeight) {
        if (!segmentTableModel.isColumnEnabled(colData)) {
            return previousHeight;
        }
        SegmentTextCell segmentCell = new SegmentTextCell();
        segmentCell.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        int col = segmentTableModel.getIndexForColumn(colData);
        int width = sourceTargetTable.getColumnModel().getColumn(col).getWidth();
        segmentCell.setText(text);
        // Need to set width to force text area to calculate a pref height
        segmentCell.setSize(new Dimension(width, sourceTargetTable.getRowHeight(viewRow)));
        return Math.max(previousHeight, segmentCell.getPreferredSize().height);
    }
    
    public OcelotSegment getSelectedSegment() {
        OcelotSegment selectedSeg = null;
        if (sourceTargetTable.getSelectedRow() >= 0) {
            selectedSeg = segmentTableModel.getSegment(sort.convertRowIndexToModel(
                sourceTargetTable.getSelectedRow()));
        }
        return selectedSeg;
    }

    public void selectedSegment() {
        OcelotSegment seg = getSelectedSegment();
        if (seg != null) {
            int colIndex = sourceTargetTable.getSelectedColumn();
            SegmentViewColumn col = segmentTableModel.getColumn(colIndex);
            if (col.isFlagColumn()) {
                ITSMetadata its = ruleConfig.getTopDataCategory(seg, col.getFlagIndex());
                if (its != null) {
                    eventQueue.post(new ItsSelectionEvent(its));
                }
            }
        }
        postSegmentSelection(seg);
    }

    public boolean getEnabledTargetDiff() {
        return enabledTargetDiff;
    }

    public void setEnabledTargetDiff(boolean enabled) {
        this.enabledTargetDiff = enabled;
        reloadTable();
    }

    @Subscribe
    public void addQuickAdd(QuickAddEvent event) {
        OcelotSegment seg = getSelectedSegment();
        if (seg != null && seg.isEditable()) {
            LanguageQualityIssue lqi = event.getQuickAdd().createLQI();
            seg.addLQI(lqi);
            notifyModifiedLQI(new LQIModificationEvent(lqi, seg));
        }
    }

    @Subscribe
    public void notifyModifiedLQI(LQIModificationEvent event) {
        int selectedRow = sourceTargetTable.getSelectedRow();
        updateTableRow(selectedRow);
        sourceTargetTable.setRowSelectionInterval(selectedRow, selectedRow);
        eventQueue.post(new LQISelectionEvent(event.getLQI()));
        postSegmentSelection(event.getSegment());
        requestFocusTable();
    }

    @Subscribe
    public void notifySegmentTargetReset(SegmentTargetResetEvent event) {
        segmentTableModel.fireTableDataChanged();
        updateRowHeights();
    }
    
    @Override
    public void enabledRule(String ruleLabel, boolean enabled) {
        reloadTable();
    }

    @Override
    public void setFilterMode(RuleConfiguration.FilterMode mode) {
        reloadTable();
    }

    @Override
    public void setStateQualifierMode(RuleConfiguration.StateQualifierMode mode) {
        reloadTable();
    }

    private void postSegmentSelection(OcelotSegment seg) {
        if (seg != null) {
            eventQueue.post(new SegmentSelectionEvent(seg));
        }
    }

    /**
     * TableCellRenderer for source/target text in the SegmentTableView.
     */
    public class SegmentTextRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
            boolean isSelected, boolean hasFocus, int row, int col) {
            SegmentTextCell renderTextPane = new SegmentTextCell();
            if (segmentTableModel.getRowCount() > row) {
                OcelotSegment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));
                SegmentVariant v = null;
                if (segmentTableModel.getSegmentSourceColumnIndex() == col) {
                    v = seg.getSource();
                } else if (segmentTableModel.getSegmentTargetColumnIndex() == col) {
                    v = seg.getTarget();
                } else if (segmentTableModel.getSegmentTargetOriginalColumnIndex() == col) {
                    if (!enabledTargetDiff) {
                        v = seg.getOriginalTarget();
                    }
                }
                if (v != null) {
                    renderTextPane.setVariant(v, false);
                } else {
                    renderTextPane.setTargetDiff(seg.getTargetDiff());
                }
                Color background = isSelected ?
                        seg.isEditable() ? jtable.getSelectionBackground() : Color.LIGHT_GRAY :
                        jtable.getBackground();

                Color foreground = seg.isEditable() ?
                        isSelected ? jtable.getSelectionForeground() : jtable.getForeground() :
                        Color.GRAY;

                renderTextPane.setBackground(background);
                renderTextPane.setForeground(foreground);
                renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            }

            return renderTextPane;
        }
    }

    static class ITSMetadataRenderer extends DataCategoryFlagRenderer {
        private static final long serialVersionUID = 1L;

        public ITSMetadataRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
            ITSMetadata its = (obj != null) ? ((ITSMetadata)obj) : NullITSMetadata.getInstance();
            DataCategoryFlag flag = its.getFlag();
            flag = (flag != null) ? flag : DataCategoryFlag.getDefault();
            return super.getTableCellRendererComponent(jtable, flag, isSelected, hasFocus, row, col);
        }
    }

    public class IntegerRenderer extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public IntegerRenderer() {
            setOpaque(true);
        }

        private Color getSegmentColor(OcelotSegment seg) {
            StateQualifier sq = seg.getStateQualifier();
            if (sq != null) {
                Color sqColor = ruleConfig.getStateQualifierColor(sq);
                if (sqColor == null) {
                    LOG.debug("No UI color for state-qualifier '" + sq + "'");
                }
                return sqColor;
            }
            return null;
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
            Integer segNum = (Integer) obj;
            OcelotSegment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));

            Color background = getSegmentColor(seg);
            background = background != null ? background :
                    isSelected ?
                        seg.isEditable() ? jtable.getSelectionBackground() : Color.LIGHT_GRAY
                        : jtable.getBackground();

            Color foreground = seg.isEditable()
                    ? isSelected ? jtable.getSelectionForeground() : jtable.getForeground()
                    : Color.GRAY;

            setHorizontalAlignment(JLabel.LEFT);
            setVerticalAlignment(JLabel.TOP);
            setBackground(background);
            setForeground(foreground);
            setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            if (segNum != null) {
                setText(segNum.toString());
            }
            return this;
        }
    }

    public class SegmentEditor extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 1L;

        protected SegmentTextCell editorComponent;
        protected SegmentCellEditorListener editListener;

        public SegmentEditor() {
            editListener = new SegmentCellEditorListener();
            addCellEditorListener(editListener);
        }

        @Override
        public Component getTableCellEditorComponent(JTable jtable, Object value,
            boolean isSelected, int row, int col) {
            OcelotSegment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));
            editListener.setBeginEdit(seg, seg.getTarget().getDisplayText());
            editorComponent = new SegmentTextCell(seg.getTarget().createCopy(), false);
            editorComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
            editorComponent.getActionMap().put("finish", new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
            adjustEditorInitialSize(jtable, row);
            return new JScrollPane(editorComponent);
        }

        public void adjustEditorInitialSize(JTable jtable, int row) {
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            jtable.setRowHeight(row, font.getHeight()*10);
        }

        @Override
        public Object getCellEditorValue() {
            return editorComponent.getVariant();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                // Override normal behavior and only allow double-click to edit the
                // cell
                return ((MouseEvent)anEvent).getClickCount() >= 2;
            }
            if (anEvent instanceof ActionEvent) {
                // I could further restrict this to "startEditing" event,
                // but I don't think it's necessary
                return true;
            }
            return false;
        }
    }

    public class SegmentCellEditorListener implements CellEditorListener {
        private OcelotSegment seg;

        public void setBeginEdit(OcelotSegment seg, String codedText) {
            this.seg = seg;
            eventQueue.post(new SegmentTargetEnterEvent(seg));
        }

        @Override
        public void editingStopped(ChangeEvent ce) {
            SegmentVariant updatedTarget = ((SegmentEditor)ce.getSource()).editorComponent.getVariant();
            int row = sourceTargetTable.getSelectedRow();
            eventQueue.post(new SegmentTargetExitEvent(seg));
            eventQueue.post(new SegmentTargetUpdateEvent(seg, updatedTarget));
            postSegmentSelection(seg);
            updateTableRow(row);
            // Restore row selection
            sourceTargetTable.setRowSelectionInterval(row, row);
        }

        @Override
        public void editingCanceled(ChangeEvent ce) {
            // Cancel not supported.
        }
    }

    public class SegmentPopupMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                displayContextMenu(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                displayContextMenu(e);
            }
        }

        public void displayContextMenu(MouseEvent e) {
            OcelotSegment seg = null;
            int r = sourceTargetTable.rowAtPoint(e.getPoint());
            if (r >= 0 && r < sourceTargetTable.getRowCount()) {
                sourceTargetTable.setRowSelectionInterval(r, r);
                seg = segmentTableModel.getSegment(r);
            }

            if (seg != null) {
                ContextMenu menu = new ContextMenu(seg, eventQueue);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
