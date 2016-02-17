package com.vistatec.ocelot.lqi.gui;

/**
 * Listener to table cell changes.
 */
public interface TableCellListener {

	/**
	 * Handles the event a table cell has changed.
	 * 
	 * @param e
	 *            the table cell event.
	 */
	public void cellValueChanged(TableCellEvent e);

}
