package com.vistatec.ocelot.ui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A table that doesn't allow cell selection and displays no cell 
 * highlighting border.
 */
public class OTable extends JTable {
    private static final long serialVersionUID = 1L;

    public OTable(TableModel model) {
        super(model);

        setShowHorizontalLines(false);
        setShowVerticalLines(true);
        setCellSelectionEnabled(false);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (c instanceof JComponent) {
            JComponent jc = (JComponent)c;
            // This screws up checkbox rendering (at least on Mac), unless the
            // default table renderer is also set to horizontal alignment 
            jc.setBorder(null);
        }
        return c;
    }

}
