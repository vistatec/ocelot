package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.view.SegmentTextCell;

/**
 * Table cell renderer for segment elements. It renders the table with alternate rows color.
 */
public class SegmentVariantCellRenderer extends AlternateRowsColorRenderer {
    private final SegmentTextCell renderTextPane = SegmentTextCell.createCell();

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.tm.gui.match.AlternateRowsColorRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
	@Override
    public Component getTableCellRendererComponent(JTable jtable, Object o,
        boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(jtable, o, isSelected, hasFocus, row, col);
        SegmentVariant segVariant = (SegmentVariant)o;
        if(o != null){
            renderTextPane.setVariant(row, segVariant, false);
        	renderTextPane.setBackground(comp.getBackground());
            if (isSelected) {
                renderTextPane.setForeground(comp.getForeground());
            }
            else {
                renderTextPane.setForeground(jtable.getForeground());
            }
        	renderTextPane.setSelectionColor(Color.blue);
        	renderTextPane.setSelectedTextColor(Color.white);
        	renderTextPane.setEditable(false);
        }

        renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());

        return renderTextPane;
    }
}
