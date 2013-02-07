package com.spartansoftwareinc;

import java.util.LinkedList;
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
public class SegmentAttributeTableView extends JScrollPane {
    private SegmentAttributeView segAttrView;
    protected JTable segAttrTable;
    protected JTable docStatsTable;
    protected SegmentAttributeTableModel segAttrModel;
    protected DocumentStatsTableModel docStatsModel;
    private ListSelectionModel tableSelectionModel;
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

    public void setSegment(Segment seg) {
        setViewportView(null);
        segAttrModel = new SegmentAttributeTableModel();
        segAttrTable = new JTable(segAttrModel);

        tableSelectionModel = segAttrTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new SegmentAttributeSelectionHandler());
        List<ITSMetadata> itsData = seg.getAllITSMetadata();
        segAttrModel.setRows(itsData);
        sort = new TableRowSorter(segAttrModel);
        segAttrTable.setRowSorter(sort);
        setViewportView(segAttrTable);
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

    public class SegmentAttributeTableModel extends AbstractTableModel {
        public static final int NUMCOLS = 4;
        public String[] colNames = {"Metadata ID", "Data Category",
                                    "Type", "Value"};

        private List<ITSMetadata> rows;
        
        public void setRows(List<ITSMetadata> attrs) {
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
                    tableCell = rows.get(row).getDataCategory();
                    break;
            
                case 2:
                    tableCell = rows.get(row).getType();
                    break;
                case 3:
                    tableCell = rows.get(row).getValue();
                    break;
                    
                default:
                    throw new IllegalArgumentException("Incorrect number of columns: "+col);
            }
            return tableCell;
        }
    }
    
    public class SegmentAttributeSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex() &&
                lsm.getMinSelectionIndex() >= 0) {
                segAttrView.setSelectedMetadata(segAttrModel.rows.get(lsm.getMinSelectionIndex()));
            } else {
                // TODO: Log non-single selection error
            }
        }
    }
}
