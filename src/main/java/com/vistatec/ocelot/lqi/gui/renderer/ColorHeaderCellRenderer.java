package com.vistatec.ocelot.lqi.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * Renderer for LQI grid header. It displays header cells colored with a specific color. 
 */
public class ColorHeaderCellRenderer extends ColorCellRenderer {

	/** Serial version UID. */
	private static final long serialVersionUID = -7754518222953942417L;
    
	/** The cells border color. */
    private Color borderColor;

    
    /**
     * Constructor.
     * @param color the background color.
     * @param borderColor the border color.
     */
    public ColorHeaderCellRenderer(Color color, Color borderColor) {
    	super(color);
    	this.borderColor = borderColor;
    }
    
    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.lqi.gui.ColorCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label  = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        label.setBorder(new LineBorder(borderColor));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

}
