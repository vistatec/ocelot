package com.vistatec.ocelot.its;

import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentAttributeView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS metadata.
 */
public class LanguageQualityIssueTableView extends JScrollPane {
    private SegmentAttributeView segAttrView;
    protected JTable lqiTable;
    protected LQITableModel lqiTableModel;
    private ListSelectionModel tableSelectionModel;
    private TableRowSorter sort;
    
    public LanguageQualityIssueTableView(SegmentAttributeView sav) {
        segAttrView = sav;
        addMouseListener(new LQIPopupMenuListener());
    }

    public void setSegment(Segment seg) {
        setViewportView(null);
        lqiTableModel = new LQITableModel();
        lqiTable = new JTable(lqiTableModel);

        tableSelectionModel = lqiTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new LQISelectionHandler());

        List<LanguageQualityIssue> lqiData = seg.getLQI();
        lqiTableModel.setRows(lqiData);

        sort = new TableRowSorter(lqiTableModel);
        lqiTable.setRowSorter(sort);
        lqiTable.addMouseListener(new LQIPopupMenuListener());

        setViewportView(lqiTable);
    }

    public void clearSegment() {
        if (lqiTable != null) {
            lqiTable.clearSelection();
            lqiTableModel.deleteRows();
            lqiTable.setRowSorter(null);
        }
        setViewportView(null);
    }

    public void selectedLQI() {
        int rowIndex = lqiTable.getSelectedRow();
        if (rowIndex >= 0) {
            segAttrView.setSelectedMetadata(lqiTableModel.getRow(rowIndex));
        }
    }

    public void deselectLQI() {
        if (lqiTable != null) {
            lqiTable.clearSelection();
        }
    }

    public class LQITableModel extends AbstractTableModel {

        public static final int NUMCOLS = 3;
        public String[] colNames = {"Type", "Severity", "Comment"};
        private List<LanguageQualityIssue> rows;

        public LanguageQualityIssue getRow(int row) {
            return rows.get(row);
        }

        public void setRows(List<LanguageQualityIssue> attrs) {
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
            Object tableCell;
            switch (col) {
                case 0:
                    tableCell = rows.get(row).getType();
                    break;

                case 1:
                    tableCell = rows.get(row).getSeverity();
                    break;
                case 2:
                    tableCell = rows.get(row).getComment();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: " + col);
            }
            return tableCell;
        }
    }

    public class LQISelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedLQI();
        }
    }

    public class LQIPopupMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LanguageQualityIssue selectedLQI = null;
            if (lqiTable != null) {
                int r = lqiTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < lqiTable.getRowCount()) {
                    lqiTable.setRowSelectionInterval(r, r);
                    selectedLQI = lqiTableModel.getRow(r);
                }
            }
            if (e.isPopupTrigger() && segAttrView.getSelectedSegment() != null) {
                ContextMenu menu = selectedLQI == null ?
                        new ContextMenu(segAttrView.getSelectedSegment()) :
                        new ContextMenu(segAttrView.getSelectedSegment(), selectedLQI);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}