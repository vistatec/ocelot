package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TooltipCellRenderer extends DefaultTableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7079489996462488540L;

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
