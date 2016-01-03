package com.vistatec.ocelot.lqi.gui;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Button used in the LQI grid.
 */
public class LQIGridButton extends JButton {

	/** Serial version UID. */
	private static final long serialVersionUID = -1048939778142475738L;

	/** The category row for this button. */
	private int categoryRow;

	/** The severity column for this button. */
	private int severityColumn;

	/**
	 * Constructor.
	 * 
	 * @param annotAction
	 *            the action for this button.
	 */
	public LQIGridButton(final Action annotAction) {

		addActionListener(annotAction);
	}

	/**
	 * Gets the category row.
	 * 
	 * @return the category row.
	 */
	public int getCategoryRow() {
		return categoryRow;
	}

	/**
	 * Sets the category row.
	 * 
	 * @param categoryRow
	 *            the category row.
	 */
	public void setCategoryRow(int categoryRow) {
		this.categoryRow = categoryRow;
	}

	/**
	 * Gets the severity column.
	 * 
	 * @return the severity column.
	 */
	public int getSeverityColumn() {
		return severityColumn;
	}

	/**
	 * Sets the severity column.
	 * 
	 * @param severityColumn
	 *            the severity column.
	 */
	public void setSeverityColumn(int severityColumn) {
		this.severityColumn = severityColumn;
	}
}
