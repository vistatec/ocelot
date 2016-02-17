package com.vistatec.ocelot.lqi.gui;

/**
 * Column used in the LQI grid.
 */
public class LQIGridColumn {

	/** The column name. */
	private String name;


	/**
	 * Constructor.
	 * @param name the column name.
	 */
	public LQIGridColumn(String name) {
		this.name = name;
	}

	/**
	 * Gets the column name.
	 * @return the column name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the column name.
	 * @param name the column name.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
