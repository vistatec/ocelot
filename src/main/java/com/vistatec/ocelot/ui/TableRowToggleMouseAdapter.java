package com.vistatec.ocelot.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Toggle the (assumed to be boolean) value of a row whenever any column
 * in that row is clicked.
 */
public abstract class TableRowToggleMouseAdapter extends MouseAdapter {

    /**
     * Implement this method to indicate whether or not a click event to
     * the specified row and column should toggle the row value. 
     * @param row
     * @param column
     * @return
     */
    protected abstract boolean acceptEvent(int row, int column);

    /**
     * Specifies the column in the row that contains the value to be
     * toggled.  The default implementation returns 0.
     * @return
     */
    protected int getValueColumn() {
        return 0;
    }
    
    public void mouseClicked(MouseEvent e) {
        JTable target = (JTable)e.getSource();
        int row = target.getSelectedRow();
        int column = target.getSelectedColumn();
        TableModel model = target.getModel();
        if (acceptEvent(row, column)) {
            boolean current = (Boolean)model.getValueAt(row, getValueColumn());
            model.setValueAt(!current, row, getValueColumn());
        }
    }
}
