package com.spartansoftwareinc.vistatec.rwb.segment;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Detail view showing the raw source and target segment text.
 */
public class SegmentDetailView extends JScrollPane {
    private JTable table;
    private DetailTableModel tableModel;

    public SegmentDetailView() {
        tableModel = new DetailTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(String.class, new TextRenderer());
        TableColumnModel tableColumnModel = table.getColumnModel();
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

    public void setSegment(Segment selectedSegment) {
        tableModel.setSegment(selectedSegment);
        updateRowHeights();
    }

    public void clearDisplay() {
        setViewportView(null);
    }

    protected void updateRowHeights() {
        setViewportView(null);

        SegmentTextCell segmentCell = new SegmentTextCell();
        segmentCell.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        FontMetrics font = table.getFontMetrics(table.getFont());
        int rowHeight = font.getHeight();
        for (int col = 0; col < 2; col++) {
            int width = table.getColumnModel().getColumn(col).getWidth();
            segmentCell.setTextContainer((TextContainer)tableModel.getValueAt(0, col), true);
            // Need to set width to force text area to calculate a pref height
            segmentCell.setSize(new Dimension(width, table.getRowHeight(0)));
            rowHeight = Math.max(rowHeight, segmentCell.getPreferredSize().height);
        }
        table.setRowHeight(0, rowHeight);
        setViewportView(table);
    }

    public class DetailTableModel extends AbstractTableModel {
        private String[] column = {"Source Raw", "Target Raw"};
        private Segment segment;

        public void setSegment(Segment seg) {
            segment = seg;
        }
        
        @Override
        public int getRowCount() {
            return segment != null ? 1 : 0;
        }

        @Override
        public String getColumnName(int col) {
            return column[col];
        }

        @Override
        public Class getColumnClass(int col) {
            return String.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (segment != null) {
                return col == 0 ? segment.getSource() : segment.getTarget();
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    public class TextRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
            boolean isSelected, boolean hasFocus, int row, int col) {
            SegmentTextCell renderTextPane = new SegmentTextCell();
            if (tableModel.getRowCount() > row) {
                renderTextPane.setTextContainer((TextContainer)o, true);
                renderTextPane.setBackground(isSelected ? jtable.getSelectionBackground() : jtable.getBackground());
                renderTextPane.setForeground(isSelected ? jtable.getSelectionForeground() : jtable.getForeground());
                renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            }

            return renderTextPane;
        }
    }
}
