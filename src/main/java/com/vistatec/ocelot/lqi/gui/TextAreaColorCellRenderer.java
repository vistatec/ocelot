package com.vistatec.ocelot.lqi.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

public class TextAreaColorCellRenderer extends ColorCellRenderer {

	public TextAreaColorCellRenderer(Color color) {
		super(color);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8367564808375095337L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {

		Component comp = super.getTableCellRendererComponent(table, value,
		        isSelected, hasFocus, row, column);
		JTextArea txtArea = new JTextArea();
		if (value != null) {
			txtArea.setText((String) value);
			txtArea.setToolTipText((String) value);
		}
		txtArea.setSize(table.getColumnModel().getColumn(column).getWidth(),
		        table.getRowHeight());
		txtArea.setBackground(comp.getBackground());
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		return txtArea;
	}
}
