package com.vistatec.ocelot.tm.gui.match;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.view.SegmentTextCell;

public class SegmentVariantCellRenderer extends AlternateRowsColorRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable jtable, Object o,
        boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(jtable, o, isSelected, hasFocus, row, col);
    	SegmentTextCell renderTextPane = new SegmentTextCell();
        SegmentVariant segVariant = (SegmentVariant)o;
        if(o != null){
        	renderTextPane.setVariant(segVariant, false);
        	renderTextPane.setBackground(comp.getBackground());
        	renderTextPane.setForeground(jtable.getForeground());
        }

            renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
        
            
        return renderTextPane;
    }
}
