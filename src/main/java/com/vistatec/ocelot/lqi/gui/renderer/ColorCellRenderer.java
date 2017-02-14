package com.vistatec.ocelot.lqi.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;

/**
 * Renderer for LQI grid cells. It displays cells colored with a specific color.
 * When the cell is selected and the configuration mode is on a slightly darker
 * color is displayed.
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

	/** Serial version UID. */
	private static final long serialVersionUID = -2993462149707170305L;

	/** The background color. */
	private Color color;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            the color.
	 */
	public ColorCellRenderer(final Color color) {

		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
	 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {

		Component comp = super.getTableCellRendererComponent(table, value,
		        isSelected, hasFocus, row, column);

		if (isSelected && isConfigurationModeOn(table)) {
			Color darkerColor = new Color((int) (color.getRed() * 0.95),
			        (int) (color.getGreen() * 0.95),
			        (int) (color.getBlue() * 0.95));
			comp.setBackground(darkerColor);
		} else {
			comp.setBackground(color);
		}
		return comp;
	}

	/**
	 * Gets the background color.
	 * 
	 * @return the background color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Checks if the configuration mode is on.
	 * 
	 * @param table
	 *            the LQI grid table.
	 * @return <code>true</code> if the configuration mode is on;
	 *         <code>false</code> otherwise.
	 */
	private boolean isConfigurationModeOn(JTable table) {

		return table.getModel() instanceof LQIGridTableModel
		        && ((LQIGridTableModel) table.getModel()).getMode() == LQIGridTableModel.CONFIG_MODE;
	}

}
