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

import com.vistatec.ocelot.config.AppConfig;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import com.vistatec.ocelot.rules.DataCategoryFlagRenderer;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.SegmentSelector;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RuleListener;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.editdistance.EditDistance;

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

import net.sf.okapi.common.resource.TextContainer;

import org.apache.log4j.Logger;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener {
    private static Logger LOG = Logger.getLogger(SegmentView.class);

    protected SegmentController segmentController;
    protected JTable sourceTargetTable;
    private ListSelectionModel tableSelectionModel;
    private SegmentAttributeView attrView;
    private TableColumnModel tableColumnModel;
    protected TableRowSorter sort;

    protected RuleConfiguration ruleConfig;
    protected PluginManager pluginManager;

    public SegmentView(SegmentAttributeView attr, SegmentController segController,
            AppConfig appConfig, RuleConfiguration ruleConfig) throws IOException, 
                InstantiationException, InstantiationException, IllegalAccessException {
        attrView = attr;
        segmentController = segController;
        this.ruleConfig = ruleConfig;
        this.ruleConfig.addRuleListener(this);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
        pluginManager = new PluginManager(appConfig);
        pluginManager.discover(pluginManager.getPluginDir());
    }

    public final void initializeTable() {
        sourceTargetTable = new JTable(segmentController.getSegmentTableModel());
        sourceTargetTable.getTableHeader().setReorderingAllowed(false);

        ListSelectionListener selectSegmentHandler = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                selectedSegment();
            }
        };
        tableSelectionModel = sourceTargetTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(selectSegmentHandler);

        sourceTargetTable.setDefaultRenderer(Integer.class, new IntegerRenderer());
        sourceTargetTable.setDefaultRenderer(ITSMetadata.class,
                new ITSMetadataRenderer());
        sourceTargetTable.setDefaultRenderer(String.class,
                new SegmentTextRenderer());

        tableColumnModel = sourceTargetTable.getColumnModel();
        tableColumnModel.getSelectionModel().addListSelectionListener(
                selectSegmentHandler);
        tableColumnModel.getColumn(0).setMinWidth(20);
        tableColumnModel.getColumn(0).setPreferredWidth(25);
        tableColumnModel.getColumn(0).setMaxWidth(50);

        tableColumnModel.getColumn(segmentController.getSegmentTargetColumnIndex())
                .setCellEditor(new SegmentEditor());
        int flagMinWidth = 15, flagPrefWidth = 15, flagMaxWidth = 20;
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
        segmentController.fireTableDataChanged();
        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segmentController.getSegmentNumColumnIndex())
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + segmentController.getNumSegments()));
        updateRowHeights();
    }

    public void requestFocusTable() {
        sourceTargetTable.requestFocus();
    }

    public void addFilters() {
        sort = new TableRowSorter(segmentController.getSegmentTableModel());
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
                    String text = segmentController.getSegment(modelRow).getSource().getCodedText();
                    segmentCell.setText(text);
                } else if (col == 2) {
                    String text = segmentController.getSegment(modelRow).getTarget().getCodedText();
                    segmentCell.setText(text);
                } else if (col == 3) {
                    String text;
                    if (segmentController.enabledTargetDiff()) {
                        ArrayList<String> textDiff = segmentController.getSegment(modelRow).getTargetDiff();
                        StringBuilder displayText = new StringBuilder();
                        for (int i = 0; i < textDiff.size(); i += 2) {
                            displayText.append(textDiff.get(i));
                        }
                        text = displayText.toString();
                    } else {
                        text = segmentController.getSegment(modelRow).getOriginalTarget().getCodedText();
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
            selectedSeg = segmentController.getSegment(sort.convertRowIndexToModel(
                sourceTargetTable.getSelectedRow()));
        }
        return selectedSeg;
    }

    public void selectedSegment() {
        Segment seg = getSelectedSegment();
        if (seg != null) {
            attrView.setSelectedSegment(seg);
            int colIndex = sourceTargetTable.getSelectedColumn();
            if (colIndex >= SegmentTableModel.NONFLAGCOLS) {
                int adjustedFlagIndex = colIndex - SegmentTableModel.NONFLAGCOLS;
                ITSMetadata its = ruleConfig.getTopDataCategory(seg, adjustedFlagIndex);
                if (its != null) {
                    attrView.setSelectedMetadata(its);
                }
            }
        }
    }

    public void notifyAddedLQI(LanguageQualityIssue lqi, Segment seg) {
        attrView.addLQIMetadata(lqi);
    }

    public void notifyModifiedLQI(LanguageQualityIssue lqi, Segment seg) {
        attrView.setSelectedMetadata(lqi);
        attrView.setSelectedSegment(seg);
        segmentController.updateSegment(seg);
        int selectedRow = sourceTargetTable.getSelectedRow();
        reloadTable();
        sourceTargetTable.setRowSelectionInterval(selectedRow, selectedRow);
        requestFocusTable();
    }

    public void notifyAddedProv(Provenance prov) {
        attrView.addProvMetadata(prov);
    }

    public void notifyDeletedSegments() {
        attrView.deletedSegments();
    }

    /**
     * Rule configuration methods.
     */
    public RuleConfiguration getRuleConfig() {
        return this.ruleConfig;
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

    /**
     * TableCellRenderer for source/target text in the SegmentTableView.
     */
    public class SegmentTextRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
            boolean isSelected, boolean hasFocus, int row, int col) {
            SegmentTextCell renderTextPane = new SegmentTextCell();
            if (segmentController.getNumSegments() > row) {
                Segment seg = segmentController.getSegment(sort.convertRowIndexToModel(row));
                TextContainer tc = null;
                if (segmentController.getSegmentSourceColumnIndex() == col) {
                    tc = seg.getSource();
                } else if (segmentController.getSegmentTargetColumnIndex() == col) {
                    tc = seg.getTarget();
                } else if (segmentController.getSegmentTargetOriginalColumnIndex() == col) {
                    if (!segmentController.enabledTargetDiff()) {
                        tc = seg.getOriginalTarget();
                    }
                }
                if (tc != null) {
                    renderTextPane.setTextContainer(tc, false);
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
            Segment seg = segmentController.getSegment(sort.convertRowIndexToModel(row));

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
            Segment seg = segmentController.getSegment(sort.convertRowIndexToModel(row));
            editListener.setBeginEdit(seg, seg.getTarget().getCodedText());
            editorComponent = new SegmentTextCell(seg.getTarget(), false);
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
            return editorComponent.getTextContainer();
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
        private String codedText;
        private TextContainer targetClone;

        public void setBeginEdit(Segment seg, String codedText) {
            this.seg = seg;
            this.codedText = codedText;
            if (!this.seg.hasOriginalTarget()) {
                this.targetClone = seg.getTarget().clone();
            }
            pluginManager.notifySegmentTargetEnter(seg);
        }

        @Override
        public void editingStopped(ChangeEvent ce) {
            pluginManager.notifySegmentTargetExit(seg);
            if (!this.seg.getTarget().getCodedText().equals(codedText)) {
                if (!this.seg.hasOriginalTarget()) {
                    this.seg.setOriginalTarget(this.targetClone);
                }
                this.seg.setTargetDiff(EditDistance.styleTextDifferences(this.seg.getTarget(),
                        this.seg.getOriginalTarget()));
                segmentController.updateSegment(seg);
            }
            attrView.setSelectedSegment(seg);
            reloadTable();
        }

        @Override
        public void editingCanceled(ChangeEvent ce) {
            // TODO: Cancel not supported.
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
                seg = segmentController.getSegment(r);
            }

            if (seg != null) {
                ContextMenu menu = new ContextMenu(seg);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
