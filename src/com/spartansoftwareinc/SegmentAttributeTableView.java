package com.spartansoftwareinc;

import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * Table View for displaying segment ITS metadata.
 */
public class SegmentAttributeTableView extends JScrollPane {
    private SegmentAttributeView segAttrView;
    protected JTable segAttrTable;
    protected SegmentAttributeTableModel segAttrModel;
    private ListSelectionModel tableSelectionModel;
    
    public SegmentAttributeTableView(SegmentAttributeView sav) {
        segAttrView = sav;
        segAttrModel = new SegmentAttributeTableModel();
        segAttrTable = new JTable(segAttrModel);

        tableSelectionModel = segAttrTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new SegmentAttributeSelectionHandler());
    }
    
    public void setSegment(Segment seg) {
        List<ITSMetadata> itsData = seg.getAllITSMetadata();
        segAttrModel.setRows(itsData);
        setViewportView(segAttrTable);
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
