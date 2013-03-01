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
            data.add(segModel.lqiStats.get(type));
        }
        docStatsModel.addRows(data);

        sort = new TableRowSorter(docStatsModel);
        docStatsTable.setRowSorter(sort);

        setViewportView(docStatsTable);
    }
    
    public void addITSMetadata(ITSMetadata its) {
        docStatsModel.updateITSStats(its);
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

        protected void updateITSStats(ITSMetadata its) {
            boolean foundExistingStat = false;
            for (ITSStats stat : rows) {
                if (stat.getDataCategory().equals(its.getDataCategory()) &&
                    stat.getType().equals(its.getType())) {
                    stat.setCount(stat.getCount()+1);
                    foundExistingStat = true;
                }
            }
            if (!foundExistingStat) {
                if (its instanceof LanguageQualityIssue) {
                    LQIStatistics lqi = new LQIStatistics();
                    lqi.setType(its.getType());
                    lqi.setRange(Double.parseDouble(its.getValue()));
                    rows.add(lqi);
                }
            }
            fireTableDataChanged();
        }
    }
}
