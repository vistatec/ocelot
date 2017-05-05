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

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detail view showing the raw source and target segment text.
 */
public class SegmentDetailView extends JScrollPane {
    private static final long serialVersionUID = 1L;

    private Logger LOG = LoggerFactory.getLogger(SegmentDetailView.class);
    private JTable table;
    private DetailTableModel tableModel;

    public SegmentDetailView() {
        tableModel = new DetailTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new TextRenderer());
        TableColumnModel tableColumnModel = table.getColumnModel();
        tableColumnModel.getColumn(0).setMinWidth(15);
        tableColumnModel.getColumn(0).setPreferredWidth(60);
        tableColumnModel.getColumn(0).setMaxWidth(100);
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
        setViewportView(table);
    }

    public void setSegment(OcelotSegment selectedSegment) {
        tableModel.setSegment(selectedSegment);
        updateRowHeights();
    }

    public void clearDisplay() {
        setViewportView(null);
    }

    private final SegmentTextCell segmentCell = SegmentTextCell.createCell();

    protected void updateRowHeights() {
        setViewportView(null);

        FontMetrics font = table.getFontMetrics(table.getFont());
        int rowHeight = font.getHeight();
        for (int row = 0; row < DetailTableModel.SEGMENTROWS; row++) {
            segmentCell.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (tableModel.hasSegment()) {
                rowHeight = getLabelRowHeight(tableModel.getValueAt(row, 0).toString(),
                        table.getColumnModel().getColumn(0).getWidth(), row);
                for (int col = 1; col < 2; col++) {
                    int width = table.getColumnModel().getColumn(col).getWidth();
                    SegmentVariant sv = (SegmentVariant) tableModel.getValueAt(row, col);
                    segmentCell.setVariant(row, sv, true);
                    // Need to set width to force text area to calculate a pref height
                    segmentCell.setSize(new Dimension(width, table.getRowHeight(row)));
                    rowHeight = Math.max(rowHeight, segmentCell.getPreferredSize().height);
                }
                table.setRowHeight(row, rowHeight);
            }
        }
        for (int row = DetailTableModel.SEGMENTROWS; row < table.getRowCount(); row++) {
            String detailLabel = tableModel.getValueAt(row, 0).toString();
            int width = table.getColumnModel().getColumn(0).getWidth();
            table.setRowHeight(row, getLabelRowHeight(detailLabel, width, row));
        }
        setViewportView(table);
    }

    public int getLabelRowHeight(String label, int colWidth, int row) {
        JTextPane jtp = new JTextPane();
        jtp.setText(label);
        // Need to set width to force text area to calculate a pref height
        jtp.setSize(new Dimension(colWidth, table.getRowHeight(row)));
        return jtp.getPreferredSize().height;
    }

    public class DetailTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        public static final int SEGMENTROWS = 3;
        private String[] column = {"Label", "Segments"};
        private String[] rowName = {"Source:", "Target:", "Original Target:", "Edit Distance: "};
        private OcelotSegment segment;

        public void setSegment(OcelotSegment seg) {
            segment = seg;
        }

        public boolean hasSegment() {
            return segment != null;
        }

        @Override
        public int getRowCount() {
            return segment != null ? 4 : 0;
        }

        @Override
        public String getColumnName(int col) {
            return column[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return (col == 0) ? String.class : SegmentVariant.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (segment != null) {
                switch(row) {
                    case 0:
                        return col == 0 ? rowName[row] : segment.getSource();

                    case 1:
                        return col == 0 ? rowName[row] : segment.getTarget();

                    case 2:
                        return col == 0 ? rowName[row] : segment.getOriginalTarget();

                    case 3:
                        if (col == 0) {
                            return rowName[row];
                        }
                        return segment.getEditDistance();
                    default:
                        LOG.warn("Invalid row for SegmentDetailView '"+row+"'");
                        break;
                }
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    public class TextRenderer implements TableCellRenderer {
        private final SegmentTextCell renderTextPane = SegmentTextCell.createCell();

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
            boolean isSelected, boolean hasFocus, int row, int col) {
            if (tableModel.getRowCount() > row) {
                if (col > 0) {
                    if (row > 2) {
                        Integer editDistance = (Integer) tableModel.getValueAt(row, col);
                        renderTextPane.setText(editDistance.toString());
                    } else {
                        renderTextPane.setVariant(row, (SegmentVariant) o, true);
                    }
                } else {
                    String s = (String)tableModel.getValueAt(row, col);
                    renderTextPane.setText(s);
                }
                renderTextPane.setBackground(isSelected ? jtable.getSelectionBackground() : jtable.getBackground());
                renderTextPane.setForeground(isSelected ? jtable.getSelectionForeground() : jtable.getForeground());
                renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            }

            return renderTextPane;
        }
    }
}
