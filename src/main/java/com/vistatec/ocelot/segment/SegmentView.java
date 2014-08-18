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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.AppConfig;
import com.vistatec.ocelot.events.ITSSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.its.ITSMetadata;
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
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
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

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = Logger.getLogger(SegmentView.class);

    protected SegmentTableModel segmentTableModel;
    protected JTable sourceTargetTable;
    private TableColumnModel tableColumnModel;
    protected TableRowSorter<SegmentTableModel> sort;
    private boolean enabledTargetDiff = true;

    protected RuleConfiguration ruleConfig;
    protected PluginManager pluginManager;
    private EventBus eventBus;

    public SegmentView(EventBus eventBus, SegmentTableModel segmentTableModel, AppConfig appConfig,
            RuleConfiguration ruleConfig, PluginManager pluginManager) throws IOException, 
                InstantiationException, InstantiationException, IllegalAccessException {
        this.eventBus = eventBus;
        this.segmentTableModel = segmentTableModel;
        this.ruleConfig = ruleConfig;
        this.ruleConfig.addRuleListener(this);
        this.pluginManager = pluginManager;
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
        eventBus.register(this);
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

        tableColumnModel = sourceTargetTable.getColumnModel();
        tableColumnModel.getSelectionModel().addListSelectionListener(
                selectSegmentHandler);
        tableColumnModel.getColumn(0).setMinWidth(20);
        tableColumnModel.getColumn(0).setPreferredWidth(25);
        tableColumnModel.getColumn(0).setMaxWidth(50);

        tableColumnModel.getColumn(segmentTableModel.getSegmentTargetColumnIndex())
                .setCellEditor(new SegmentEditor());
        int flagMinWidth = 15, flagPrefWidth = 20, flagMaxWidth = 20;
        for (int i = SegmentTableModel.NONFLAGCOLS;
             i < SegmentTableModel.NONFLAGCOLS+SegmentTableModel.NUMFLAGS; i++) {
            tableColumnModel.getColumn(i).setMinWidth(flagMinWidth);
            tableColumnModel.getColumn(i).setPreferredWidth(flagPrefWidth);
            tableColumnModel.getColumn(i).setMaxWidth(flagMaxWidth);
        }

        tableColumnModel.addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent tcme) {}

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
        setViewportView(null);

        SegmentTextCell segmentCell = new SegmentTextCell();
        segmentCell.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        for (int viewRow = 0; viewRow < sort.getViewRowCount(); viewRow++) {
            int modelRow = sort.convertRowIndexToModel(viewRow);
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            int rowHeight = font.getHeight();
            for (int col = 1; col < 4; col++) {
                int width = sourceTargetTable.getColumnModel().getColumn(col).getWidth();
                if (col == 1) {
                    String text = segmentTableModel.getSegment(modelRow).getSource().getDisplayText();
                    segmentCell.setText(text);
                } else if (col == 2) {
                    String text = segmentTableModel.getSegment(modelRow).getTarget().getDisplayText();
                    segmentCell.setText(text);
                } else if (col == 3) {
                    String text;
                    if (enabledTargetDiff) {
                        ArrayList<String> textDiff = segmentTableModel.getSegment(modelRow).getTargetDiff();
                        StringBuilder displayText = new StringBuilder();
                        for (int i = 0; i < textDiff.size(); i += 2) {
                            displayText.append(textDiff.get(i));
                        }
                        text = displayText.toString();
                    } else {
                        text = segmentTableModel.getSegment(modelRow).getOriginalTarget().getDisplayText();
                    }
                    segmentCell.setText(text.toString());
                }
                // Need to set width to force text area to calculate a pref height
                segmentCell.setSize(new Dimension(width, sourceTargetTable.getRowHeight(viewRow)));
                rowHeight = Math.max(rowHeight, segmentCell.getPreferredSize().height);
            }
            sourceTargetTable.setRowHeight(viewRow, rowHeight);
        }
        setViewportView(sourceTargetTable);
    }

    public Segment getSelectedSegment() {
        Segment selectedSeg = null;
        if (sourceTargetTable.getSelectedRow() >= 0) {
            selectedSeg = segmentTableModel.getSegment(sort.convertRowIndexToModel(
                sourceTargetTable.getSelectedRow()));
        }
        return selectedSeg;
    }

    public void selectedSegment() {
        Segment seg = getSelectedSegment();
        if (seg != null) {
            int colIndex = sourceTargetTable.getSelectedColumn();
            if (colIndex >= SegmentTableModel.NONFLAGCOLS) {
                int adjustedFlagIndex = colIndex - SegmentTableModel.NONFLAGCOLS;
                ITSMetadata its = ruleConfig.getTopDataCategory(seg, adjustedFlagIndex);
                if (its != null) {
                    eventBus.post(new ITSSelectionEvent(its));
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
    public void notifyModifiedLQI(LQIModificationEvent event) {
        int selectedRow = sourceTargetTable.getSelectedRow();
        updateTableRow(selectedRow);
        sourceTargetTable.setRowSelectionInterval(selectedRow, selectedRow);
        eventBus.post(new LQISelectionEvent(event.getLQI()));
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

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    private void postSegmentSelection(Segment seg) {
        if (seg != null) {
            eventBus.post(new SegmentSelectionEvent(seg));
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
                Segment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));
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
                        seg.isEditablePhase() ? jtable.getSelectionBackground() : Color.LIGHT_GRAY :
                        jtable.getBackground();

                Color foreground = seg.isEditablePhase() ?
                        isSelected ? jtable.getSelectionForeground() : jtable.getForeground() :
                        Color.GRAY;

                renderTextPane.setBackground(background);
                renderTextPane.setForeground(foreground);
                renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            }

            return renderTextPane;
        }
    }

    public class ITSMetadataRenderer extends DataCategoryFlagRenderer {
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

        private Color getSegmentColor(Segment seg) {
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
            Segment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));

            Color background = getSegmentColor(seg);
            background = background != null ? background :
                    isSelected ?
                        seg.isEditablePhase() ? jtable.getSelectionBackground() : Color.LIGHT_GRAY
                        : jtable.getBackground();

            Color foreground = seg.isEditablePhase()
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
            Segment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));
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
                return ((MouseEvent)anEvent).getClickCount() >= 2;
            }
            return true;
        }
    }

    public class SegmentCellEditorListener implements CellEditorListener {
        private Segment seg;

        public void setBeginEdit(Segment seg, String codedText) {
            this.seg = seg;
            pluginManager.notifySegmentTargetEnter(seg);
        }

        @Override
        public void editingStopped(ChangeEvent ce) {
            SegmentVariant updatedTarget = ((SegmentEditor)ce.getSource()).editorComponent.getVariant();
            int row = sourceTargetTable.getSelectedRow();
            pluginManager.notifySegmentTargetExit(seg);
            seg.updateTarget(updatedTarget);
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
            Segment seg = null;
            int r = sourceTargetTable.rowAtPoint(e.getPoint());
            if (r >= 0 && r < sourceTargetTable.getRowCount()) {
                sourceTargetTable.setRowSelectionInterval(r, r);
                seg = segmentTableModel.getSegment(r);
            }

            if (seg != null) {
                ContextMenu menu = new ContextMenu(seg);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
