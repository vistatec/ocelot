package com.vistatec.ocelot.lqi.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

/**
 * Renderer for button cells of the LQI grid.
 */
public class LQIGridButtonRenderer extends ColorCellRenderer {

	/** Serial version UID. */
    private static final long serialVersionUID = 1038383274543803328L;

    /**
     * Constructor.
     * @param color the background color.
     */
    public LQIGridButtonRenderer(final Color color ) {
    	super(color);
    }
    
    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.lqi.gui.ColorCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

    	Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
    	JButton button = new JButton();
    	if(value != null){
    		button.setText(value.toString());
    	}
    	button.setBackground(comp.getBackground());
    	button.setForeground(Color.gray);
    	button.setBorder(new LineBorder(table.getGridColor()));
    	return button;
    }
    

}
