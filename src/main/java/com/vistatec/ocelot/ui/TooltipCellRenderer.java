package com.vistatec.ocelot.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This table cell renderer displays assigns a tooltip text to the renderer component.
 * The tooltip textt is the text assigned to the component itself.
 */
public class TooltipCellRenderer extends DefaultTableCellRenderer{

	/** The serial version UID. */
	private static final long serialVersionUID = -7079489996462488540L;

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		if(value != null){
			label.setToolTipText(value.toString());
		}
		return label;
	}

	
}
