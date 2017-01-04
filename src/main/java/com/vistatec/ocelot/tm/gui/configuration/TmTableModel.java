package com.vistatec.ocelot.tm.gui.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.config.json.TmManagement.TmConfig;


/**
 * Table model for TM elements. It is the data model associated to the table
 * displayed in the TM configuration dialog.
 */
public class TmTableModel extends AbstractTableModel {

	/** serial version UID. */
	private static final long serialVersionUID = -361910238366547586L;

	/** The TM Name column index. */
	public static final int TM_NAME_COL = 0;

	/** The TM directory column index. */
	public static final int TM_ROOT_DIR_PATH_COL = 1;

	/** The penalty column index. */
	public static final int TM_PENALTY_COL = 2;

	/** The Enabled column index. */
	public static final int TM_ENABLED_COL = 3;

	/** Column names array. */
	private final String[] columnNames = { "Name", "Path", "Penalty", "Enabled" };

	/** The list of TM objects being the actual data model. */
	private List<TmConfig> model;

	/** States if data in the table has been edited. */
	private boolean edited;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the list of TMs.
	 */
	public TmTableModel(final List<TmConfig> model) {

		if (model != null) {
			this.model = new ArrayList<TmConfig>();
			this.model.addAll(model);
		}

	}

	/**
	 * Gets the number of rows in the model.
	 * 
	 * @return the number of rows in the model.
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {

		int count = 0;
		if (model != null) {
			count = model.size();
		}
		return count;
	}

	/**
	 * Gets the number of columns in the model.
	 * 
	 * @return the number of columns in the model.
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return columnNames.length;
	}

	/**
	 * Gets the name of the column at the given index.
	 */
	@Override
	public String getColumnName(int column) {

		String colName = "";
		if (column < columnNames.length) {
			colName = columnNames[column];
		}
		return colName;
	}

	/**
	 * Gets the class of elements displayed in the column at
	 * <code>columnIndex</code>.
	 * 
	 * @return the class of elements displayed in the queried column.
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {

		Class<?> colClass = null;
		switch (columnIndex) {
		case TM_ENABLED_COL:
			colClass = Boolean.class;
			break;
		case TM_NAME_COL:
			colClass = String.class;
			break;
		case TM_PENALTY_COL:
			colClass = Float.class;
			break;
		case TM_ROOT_DIR_PATH_COL:
			colClass = String.class;
			break;
		default:
			break;
		}
		return colClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object retValue = null;
		if (model != null && rowIndex < model.size()) {
			TmConfig currTm = model.get(rowIndex);
			if (currTm != null) {
				switch (columnIndex) {
				case TM_ENABLED_COL:
					retValue = currTm.isEnabled();
					break;
				case TM_NAME_COL:
					retValue = currTm.getTmName();
					break;
				case TM_PENALTY_COL:
					retValue = currTm.getPenalty();
					break;
				case TM_ROOT_DIR_PATH_COL:
					retValue = currTm.getTmDataDir();
					break;
				default:
					break;
				}
			}
		}
		return retValue;
	}

	/**
	 * Assigns a value to the cell identified by row and column indexes passed
	 * as parameter. This method assigns a value only to cells being in the
	 * editable columns: enabled and penalty columns.
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 *      int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (model != null && rowIndex < model.size()) {
			TmConfig currTm = model.get(rowIndex);
			switch (columnIndex) {
			case TM_ENABLED_COL:
				currTm.setEnabled((boolean) aValue);
				edited = true;
				break;
			case TM_PENALTY_COL:
				currTm.setPenalty((float) aValue);
				edited = true;
				break;

			default:
				break;
			}
		}

	}

	/**
	 * Checks if a cell is editable. The only editable cells in this model lie
	 * in Penalty and Enabled columns.
	 * 
	 * @return <code>true</code> if the column being queried is either Enabled
	 *         or Penalty; <code>false</code> otherwise.
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == TM_ENABLED_COL || columnIndex == TM_PENALTY_COL;
	}

	/**
	 * Moves down a specific row.
	 * 
	 * @param rowIdx
	 *            the row to be moved.
	 * @return <code>true</code> if the row has been moved; <code>false</code>
	 *         otherwise.
	 */
	public boolean moveDownRow(int rowIdx) {
		boolean moved = false;
		if (model != null && rowIdx < model.size() - 1) {
			TmConfig tmToMove = model.get(rowIdx);
			model.set(rowIdx, model.get(rowIdx + 1));
			model.set(rowIdx + 1, tmToMove);
			fireTableRowsUpdated(rowIdx, rowIdx + 1);
			moved = true;
		}
		return moved;
	}

	/**
	 * Moves up a specific row.
	 * 
	 * @param rowIdx
	 *            the row to be moved.
	 * @return <code>true</code> if the row has been moved; <code>false</code>
	 *         otherwise.
	 */
	public boolean moveUpRow(int rowIdx) {
		boolean moved = false;
		if (model != null && rowIdx > 0 && rowIdx < model.size()) {
			TmConfig tmToMove = model.get(rowIdx);
			model.set(rowIdx, model.get(rowIdx - 1));
			model.set(rowIdx - 1, tmToMove);
			fireTableRowsUpdated(rowIdx - 1, rowIdx);
			moved = true;
		}
		return moved;
	}

	/**
	 * Deletes a row in the model.
	 * 
	 * @param row
	 *            the row to be deleted
	 * @return the deleted object if the row has been successfully deleted;
	 *         <code>null</code> otherwise.
	 */
	public TmConfig deleteRow(final int row) {

		TmConfig deletedTm = null;
		if (model != null && row < model.size()) {
			deletedTm = model.remove(row);
			fireTableRowsDeleted(row, row);
		}
		return deletedTm;
	}

	/**
	 * Gets the TM object at a specific row.
	 * 
	 * @param row
	 *            the row index
	 * @return the TM at the queried row.
	 */
	public TmConfig getTmAtRow(final int row) {

		TmConfig tm = null;
		if (model != null && row < model.size()) {
			tm = model.get(row);
		}
		return tm;
	}

	/**
	 * Gets the TM list.
	 * 
	 * @return the TM list.
	 */
	public List<TmConfig> getTmList() {

		return model;
	}

	/**
	 * Adds a row to the model.
	 * 
	 * @param newTm
	 *            the TM to be added.
	 */
	public void addRow(final TmConfig newTm) {

		if (model != null) {
			model.add(newTm);
			fireTableDataChanged();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.AbstractTableModel#fireTableChanged(javax.swing.event
	 * .TableModelEvent)
	 */
	@Override
	public void fireTableChanged(TableModelEvent e) {
		super.fireTableChanged(e);
		edited = true;
	}

	/**
	 * Checks if the model has been edited.
	 * 
	 * @return <code>true</code> if the model has been edited;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEdited() {
		return edited;
	}

	/**
	 * Sets the <code>edited</code> field value.
	 * 
	 * @param edited
	 *            a boolean value.
	 */
	public void setEdited(final boolean edited) {
		this.edited = edited;
	}
}
