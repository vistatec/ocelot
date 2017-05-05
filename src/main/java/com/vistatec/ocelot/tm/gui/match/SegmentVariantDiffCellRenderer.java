package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;

import com.vistatec.ocelot.segment.view.SegmentTextCell;

/**
 * Table cell renderer for segment diffs. It renders the table with alternate rows color.
 */
public class SegmentVariantDiffCellRenderer extends AlternateRowsColorRenderer {
    private final SegmentTextCell renderTextPane = SegmentTextCell.createCell();

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.tm.gui.match.AlternateRowsColorRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o,
        boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(jtable, o, isSelected, hasFocus, row, col);
        List<String> segVariant = (List<String>) o;
        if(o != null){
            renderTextPane.setTargetDiff(segVariant);
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
