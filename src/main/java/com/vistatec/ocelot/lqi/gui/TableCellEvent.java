package com.vistatec.ocelot.lqi.gui;

/**
 * This class is an event listened by the {@link TableCellListener}.
 */
public class TableCellEvent {
	
	/** cell old value. */
	private Object oldValue;
	 
	/** The cell new value. */
	private Object newValue;
	
	/** The cell row index. */
	private int rowIndex;
	
	/** The cell column index. */
	private int colIndex;
	

	/**
	 * Constructor.
	 * @param oldValue the old value 
	 * @param newValue the new value
	 * @param rowIndex the row index
	 * @param colIndex the column index
	 */
	public TableCellEvent(Object oldValue, Object newValue, int rowIndex,
            int colIndex) {
	    this.oldValue = oldValue;
	    this.newValue = newValue;
	    this.rowIndex = rowIndex;
	    this.colIndex = colIndex;
    }

	/**
	 * Gets the old value
	 * @return the old value
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Gets the new value
	 * @return the new value
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Gets the row index.
	 * @return the row index.
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * Gets the column index.
	 * @return the column index.
	 */
	public int getColIndex() {
		return colIndex;
	}
	
}
