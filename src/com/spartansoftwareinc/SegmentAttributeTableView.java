package com.spartansoftwareinc;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS metadata.
 */
public class SegmentAttributeTableView extends JScrollPane {
    private SegmentAttributeView segAttrView;
    protected JTable docStatsTable;
    protected DocumentStatsTableModel docStatsModel;
    private TableRowSorter sort;
    
    public SegmentAttributeTableView(SegmentAttributeView sav) {
        segAttrView = sav;
    }

    public void setDocument() {
        setViewportView(null);
        docStatsModel = new DocumentStatsTableModel();
        docStatsTable = new JTable(docStatsModel);

        LinkedList<ITSStats> data = new LinkedList<ITSStats>();
        SegmentTableModel segModel = segAttrView.segmentView.getSegments();
        for (String type : segModel.lqiStats.keySet()) {
            segModel.lqiStats.get(type).setCategory("LQI");
            segModel.lqiStats.get(type).setType(type);
            data.add(segModel.lqiStats.get(type));
        }
        docStatsModel.addRows(data);

        sort = new TableRowSorter(docStatsModel);
        docStatsTable.setRowSorter(sort);

        setViewportView(docStatsTable);
    }

    public class DocumentStatsTableModel extends AbstractTableModel {
        public static final int NUMCOLS = 4;
        public String[] colNames = {"Data Category", "Type", "Value", "Count"};
        private List<ITSStats> rows;

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
        public Class getColumnClass(int col) {
            if (col == 3) {
                return Integer.class;
            } else {
                return String.class;
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object tableCell;
            switch (col) {
                case 0:
                    tableCell = rows.get(row).getDataCategory();
                    break;

                case 1:
                    tableCell = rows.get(row).getType();
                    break;

                case 2:
                    tableCell = rows.get(row).getValue();
                    break;

                case 3:
                    tableCell = rows.get(row).getCount();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: "+col);
            }
            return tableCell;
        }

        public void addRows(List<ITSStats> stats) {
            rows = stats;
        }
    }
}
