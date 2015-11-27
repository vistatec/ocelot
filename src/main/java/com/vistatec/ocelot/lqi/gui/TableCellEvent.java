package com.vistatec.ocelot.lqi.gui;

public class TableCellEvent {
	
	private Object oldValue;
	
	private Object newValue;
	
	private int rowIndex;
	
	private int colIndex;
	

	public TableCellEvent(Object oldValue, Object newValue, int rowIndex,
            int colIndex) {
	    this.oldValue = oldValue;
	    this.newValue = newValue;
	    this.rowIndex = rowIndex;
	    this.colIndex = colIndex;
    }

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}
	
	
}
