package com.spartansoftwareinc;

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

        setViewportView(lqiTable);
    }

    public void selectedLQI() {
        int rowIndex = lqiTable.getSelectedRow();
        if (rowIndex >= 0) {
            segAttrView.setSelectedMetadata(lqiTableModel.rows.get(rowIndex));
        }
    }

    public class LQITableModel extends AbstractTableModel {

        public static final int NUMCOLS = 4;
        public String[] colNames = {"#", "Type", "Severity", "Comment"};
        private List<LanguageQualityIssue> rows;

        public void setRows(List<LanguageQualityIssue> attrs) {
            rows = attrs;
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
                    tableCell = row;
                    break;

                case 1:
                    tableCell = rows.get(row).getType();
                    break;

                case 2:
                    tableCell = rows.get(row).getSeverity();
                    break;
                case 3:
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
}