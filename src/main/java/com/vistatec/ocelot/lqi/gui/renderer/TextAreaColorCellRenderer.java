package com.vistatec.ocelot.lqi.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 * Table cell renderer displaying a text area.
 */
public class TextAreaColorCellRenderer extends ColorCellRenderer {

	/** The serial version UID. */
	private static final long serialVersionUID = 8367564808375095337L;
	
	/**
	 * Constructor.
	 * @param color the background color for this cell.
	 */
	public TextAreaColorCellRenderer(Color color) {
		super(color);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.lqi.gui.ColorCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
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
