package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table Cell Renderer class, displaying table with alternate rows color.
 */
public class AlternateRowsColorRenderer extends DefaultTableCellRenderer {

	/** The serial version UID. */
	private static final long serialVersionUID = -6117683543689824822L;

	/** Even rows color. */
	private final Color evenRowColor = new Color(255, 255, 255);

	/** Odd row color. */
	private final Color oddRowColor = new Color(240, 240, 240);

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object o,
			boolean isSelected, boolean hasFocus, int row, int col) {

		Component comp = super.getTableCellRendererComponent(table, o,
				isSelected, hasFocus, row, col);
		if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
			comp.setForeground(table.getSelectionForeground());
		} else {
			if (row % 2 == 0) {
				//if even row, assign even color background
				comp.setBackground(evenRowColor);
			} else {
				//if odd row, assign odd color background
				comp.setBackground(oddRowColor);
			}
		}
		return comp;
	}
}
