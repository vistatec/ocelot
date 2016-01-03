package com.vistatec.ocelot.tm.gui.match;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vistatec.ocelot.tm.TmMatch;

/**
 * Table data model for TM match tables. The extending models should provide
 * following columns: source, target, match score and TM name.
 */
public abstract class TmMatchTableModel extends DefaultTableModel {

	/** serial version UID. */
	private static final long serialVersionUID = -6256335853992768787L;

	/** the list of TM being the actual model. */
	protected List<TmMatch> model;

	/** the model column names. */
	private String[] columns;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the list of TMs.
	 * @param columns
	 *            the array of column names.
	 */
	public TmMatchTableModel(final List<TmMatch> model, final String[] columns) {
		this.model = model;
		this.columns = columns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int count = 0;
		if (model != null) {
			count = model.size();
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		int count = 0;
		if (columns != null) {
			count = columns.length;
		}
		return count;
	}

	/**
	 * Sets the TM list.
	 * 
	 * @param model
	 *            the TM list.
	 */
	public void setModel(List<TmMatch> model) {
		this.model = model;
		fireTableDataChanged();
	}

	/**
	 * Gets the element lying at the queried row.
	 * 
	 * @param row
	 *            the row
	 * @return the element lying at the queried row.
	 */
	public TmMatch getElementAtRow(final int row) {

		TmMatch element = null;
		if (model != null && row < model.size()) {
			element = model.get(row);
		}
		return element;

	}

	/**
	 * Gets the Source column index.
	 * 
	 * @return the Source column index.
	 */
	public abstract int getSourceColumnIdx();

	/**
	 * Gets the Target column index.
	 * 
	 * @return the Target column index.
	 */
	public abstract int getTargetColumnIdx();

	/**
	 * Gets the Match Score column index.
	 * 
	 * @return the Match Score column index.
	 */
	public abstract int getMatchScoreColumnIdx();

	/**
	 * Gets the TM name column index.
	 * 
	 * @return the TM name column index.
	 */
	public abstract int getTmColumnIdx();

	@Override
	public void setValueAt(Object aValue, int row, int column) {
	    //does nothing
	}
}
