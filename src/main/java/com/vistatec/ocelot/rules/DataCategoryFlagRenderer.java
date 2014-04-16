package com.vistatec.ocelot.rules;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * Render {@link DataCategoryFlag} data (fill, border, glyph) in a table cell.
 */
public class DataCategoryFlagRenderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    public DataCategoryFlagRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
        DataCategoryFlag flag = (DataCategoryFlag)obj;
        if (flag != null) {
            setBackground(flag.getFill());
            setBorder(hasFocus ?
                    UIManager.getBorder("Table.focusCellHighlightBorder") :
                        flag.getBorder());
            setText(flag.getText());
        }
        setHorizontalAlignment(CENTER);
        return this;
    }
}

