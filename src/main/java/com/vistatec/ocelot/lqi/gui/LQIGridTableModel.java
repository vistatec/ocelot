package com.vistatec.ocelot.lqi.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

/**
 * Table model for the LQI grid.
 */
public class LQIGridTableModel extends AbstractTableModel {

	/** Serial version UID. */
	private static final long serialVersionUID = -1813995821444213075L;

	/** Issues annotations mode constant. */
	public static final int ISSUES_ANNOTS_MODE = 0;

	/** Configuration mode constant. */
	public static final int CONFIG_MODE = 1;
	
	/** Error category column index. */
	private static final int ERROR_CAT_COLUMN = 0;

	/** The error category weight column index. */
	private static final int ERROR_CAT_WEIGHT_COLUMN = 1;

	/** Comment column index. */
	private static final int COMMENT_COLUMN = 2;

	/** List if severity columns. */
	private List<LQIGridColumn> severityColumns;

	/** List of fixed columns. */
	private List<LQIGridColumn> fixedColumns = new ArrayList<LQIGridColumn>(
	        Arrays.asList(new LQIGridColumn[] {
	                new LQIGridColumn("Error Category"),
	                new LQIGridColumn("Weight"), new LQIGridColumn("Comment") }));

	/** The list of table columns. */
	private List<LQIGridColumn> tableColumns;

	/** A map stating which columns are enabled. */
	private Map<LQIGridColumn, Boolean> enabledColumns;

	/** The LQI grid object. */
	private LQIGridConfiguration lqiGridObj;

	/** The current mode (Issue creation/configuration) */
	private int mode;

	/**
	 * A boolean stating if the model has been changed when in configuration
	 * mode.
	 */
	private boolean changed;

	/** List of cell listeners. */
	private List<TableCellListener> cellListeners;

	/**
	 * Constructor.
	 * 
	 * @param lqiGridObj
	 *            the lQI grid object.
	 * @param mode
	 *            the current LQI grid mode.
	 */
	public LQIGridTableModel(final LQIGridConfiguration lqiGridObj,
	        final int mode) {
		this.lqiGridObj = lqiGridObj;
		this.mode = mode;
		initColumns();
	}

	/**
	 * Changes the mode.
	 */
	private void changedMode() {
		enabledColumns.put(fixedColumns.get(ERROR_CAT_WEIGHT_COLUMN),
		        mode == CONFIG_MODE);
		fireTableStructureChanged();
	}

	/**
	 * Initializes the columns.
	 */
	private void initColumns() {

		enabledColumns = new HashMap<LQIGridColumn, Boolean>();
		for (LQIGridColumn col : fixedColumns) {
			enabledColumns.put(col, true);
		}
		if (lqiGridObj != null && lqiGridObj.getSeverities() != null) {
			severityColumns = new ArrayList<LQIGridColumn>();
			LQIGridColumn sevColumn = null;
			for (LQISeverity sev : lqiGridObj.getSeverities()) {
				sevColumn = new LQIGridColumn(sev.getName());
				severityColumns.add(sevColumn);
				enabledColumns.put(sevColumn, true);

			}
		}
		tableColumns = new ArrayList<LQIGridColumn>();
		tableColumns.add(fixedColumns.get(ERROR_CAT_COLUMN));
		tableColumns.add(fixedColumns.get(ERROR_CAT_WEIGHT_COLUMN));
		if (severityColumns != null) {
			tableColumns.addAll(severityColumns);
		}
		tableColumns.add(fixedColumns.get(COMMENT_COLUMN));
		changedMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int count = 0;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null) {
			count = lqiGridObj.getErrorCategories().size();
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		int count = 0;
		for (LQIGridColumn col : fixedColumns) {
			if (enabledColumns.get(col)) {
				count++;
			}
		}
		if (severityColumns != null) {
			for (LQIGridColumn col : severityColumns) {
				if (enabledColumns.get(col)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the severity columns count.
	 * 
	 * @return the severity columns count.
	 */
	public int getSeverityColumnsCount() {

		int count = 0;
		if (severityColumns != null) {
			count = severityColumns.size();
		}
		return count;
	}

	/**
	 * Gets the severity columns start index.
	 * 
	 * @return the severity columns start index.
	 */
	public int getSeverityColsStartIndex() {

		int index = -1;
		if (severityColumns != null && !severityColumns.isEmpty()) {
			index = getColumnIndex(severityColumns.get(0));
		}
		return index;
	}

	/**
	 * Gets the column corresponding to the given index.
	 * 
	 * @param colIndex
	 *            the column index.
	 * @return the column corresponding to the given index.
	 */
	private LQIGridColumn getColumn(int colIndex) {

		LQIGridColumn column = null;
		int count = 0;
		for (LQIGridColumn col : tableColumns) {
			if (enabledColumns.get(col)) {
				if (count == colIndex) {
					column = col;
					break;
				}
				count++;
			}
		}
		return column;
	}

	/**
	 * Gets the column index for a specific column.
	 * 
	 * @param column
	 *            the column.
	 * @return the column index.
	 */
	private int getColumnIndex(LQIGridColumn column) {

		int colIndex = -1;
		if (enabledColumns.get(column)) {
			for (LQIGridColumn col : tableColumns) {
				if (enabledColumns.get(col)) {
					colIndex++;
				}
				if (col.equals(column)) {
					break;
				}
			}
		}
		return colIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int colIndex) {
		String colName = null;
		if (colIndex < tableColumns.size()) {
			LQIGridColumn column = getColumn(colIndex);
			colName = column.getName();
			if (severityColumns != null && severityColumns.contains(column)) {
				colName += " (" + lqiGridObj.getSeverityScore(colName) + ")";
			}
		}
		return colName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object retValue = null;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && rowIndex < lqiGridObj.getErrorCategories().size()) {

			LQIErrorCategory errorCat = lqiGridObj.getErrorCategories().get(
			        rowIndex);
			LQIGridColumn col = getColumn(columnIndex);
			if (fixedColumns.contains(col)) {
				if (col.equals(fixedColumns.get(ERROR_CAT_COLUMN))) {
					retValue = errorCat.getName();
				} else if (col
				        .equals(fixedColumns.get(ERROR_CAT_WEIGHT_COLUMN))
				        && errorCat.getWeight() > 0) {
					retValue = errorCat.getWeight();
				} else if (col.equals(fixedColumns.get(COMMENT_COLUMN))) {
					retValue = errorCat.getComment();
				}
			} else if (severityColumns.contains(col)) {
				retValue = errorCat.getShortcut(col.getName());
			}
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && !lqiGridObj.getErrorCategories().isEmpty()) {

			LQIErrorCategory errorCat = lqiGridObj.getErrorCategories().get(
			        rowIndex);
			LQIGridColumn col = getColumn(columnIndex);
			if (col != null) {
				if (col.equals(fixedColumns.get(ERROR_CAT_COLUMN))) {
					if (aValue != null && !((String) aValue).isEmpty()
					        && (errorCat == null || !((String) aValue).equals(errorCat.getName()))) {
						TableCellEvent event = new TableCellEvent(
						        errorCat.getName(), aValue, rowIndex,
						        columnIndex);
						errorCat.setName((String) aValue);
						if (cellListeners != null) {
							for (TableCellListener listener : cellListeners) {
								listener.cellValueChanged(event);
							}
						}
						changed = true;
					}
				} else if (col.equals(fixedColumns.get(COMMENT_COLUMN))) {
					errorCat.setComment((String) aValue);
				} else if (col
				        .equals(fixedColumns.get(ERROR_CAT_WEIGHT_COLUMN))) {
					String weigthString = (String) aValue;
					errorCat.setWeight(Float.parseFloat(weigthString));
					changed = true;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		LQIGridColumn col = getColumn(columnIndex);
		return col != null
		        && (mode == ISSUES_ANNOTS_MODE && !col.equals(fixedColumns
		                .get(ERROR_CAT_COLUMN)))
		        || (mode == CONFIG_MODE && !col.equals(fixedColumns
		                .get(COMMENT_COLUMN)));
	}

	/**
	 * Gets the error category object at a specific row.
	 * 
	 * @param row
	 *            the row
	 * @return the error category at that row.
	 */
	public LQIErrorCategory getErrorCategoryAtRow(int row) {

		LQIErrorCategory errorCat = null;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && row < lqiGridObj.getErrorCategories().size()) {

			errorCat = lqiGridObj.getErrorCategories().get(row);
		}
		return errorCat;
	}

	/**
	 * Gets the error category column index.
	 * 
	 * @return the error category column index.
	 */
	public int getErrorCategoryColumn() {
		return getColumnIndex(fixedColumns.get(ERROR_CAT_COLUMN));
	}

	/**
	 * Gets the comment column index.
	 * 
	 * @return the comment column index.
	 */
	public int getCommentColumn() {
		return getColumnIndex(fixedColumns.get(COMMENT_COLUMN));
	}

	/**
	 * Gets the error category weight column index.
	 * 
	 * @return the error category weight column index.
	 */
	public int getErrorCatWeightColumn() {
		return getColumnIndex(fixedColumns.get(ERROR_CAT_WEIGHT_COLUMN));
	}

	/**
	 * Gets the severity score for a specific severity column.
	 * 
	 * @param column
	 *            the column index
	 * @return the severity score
	 */
	public double getScoreForColumn(int column) {

		double score = 0;
		LQIGridColumn col = getColumn(column);
		if (col != null && severityColumns.contains(col)) {
			score = lqiGridObj.getSeverityScore(col.getName());
		}
		return score;
	}

	/**
	 * Sets the score value for a specific severity column.
	 * 
	 * @param score
	 *            the score value
	 * @param column
	 *            the severity column index.
	 */
	public void setScoreForColumn(double score, int column) {
		LQIGridColumn col = getColumn(column);
		if (col != null && severityColumns.contains(col)) {
			lqiGridObj.setSeverityScore(col.getName(), score);
			changed = true;
		}
	}

	/**
	 * Gets the severity name for a specific column.
	 * 
	 * @param severityColumn
	 *            the severity column index
	 * @return the severity name.
	 */
	public String getSeverityNameForColumn(int severityColumn) {

		String severityName = "";
		LQIGridColumn col = getColumn(severityColumn);
		if (severityColumns.contains(col)) {
			severityName = col.getName();
		}
		return severityName;
	}

	/**
	 * Gets the column index for a specific severity.
	 * 
	 * @param severityName
	 *            the severity name.
	 * @return the column index.
	 */
	public int getSeverityColIndex(String severityName) {

		int index = -1;
		LQIGridColumn sevColumn = null;
		if (severityColumns != null) {
			for (LQIGridColumn sevCol : severityColumns) {
				if (sevCol.getName().equals(severityName)) {
					sevColumn = sevCol;
					break;
				}
			}
		}
		if (sevColumn != null) {
			index = getColumnIndex(sevColumn);
		}
		return index;
	}

	/**
	 * Sets a new shortcut for a cell at specific row and column.
	 * 
	 * @param row
	 *            the row
	 * @param column
	 *            the column
	 * @param keyCode
	 *            the shortcut key code
	 * @param modifiers
	 *            the shortcut modifiers
	 */
	public void setShortCut(int row, int column, int keyCode, int[] modifiers) {

		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && row < lqiGridObj.getErrorCategories().size()) {

			LQIErrorCategory errorCat = lqiGridObj.getErrorCategories()
			        .get(row);

			LQIGridColumn col = getColumn(column);
			if (col != null && severityColumns.contains(col)) {
				LQIShortCut newShortCut = new LQIShortCut(
				        lqiGridObj.getSeverity(col.getName()), keyCode,
				        modifiers);
				errorCat.setShortcut(newShortCut);
				changed = true;
			}
			fireTableCellUpdated(row, column);
		}
	}

	/**
	 * Sets the mode.
	 * 
	 * @param mode
	 *            the mode.
	 */
	public void setMode(int mode) {
		this.mode = mode;
		changedMode();
	}

	/**
	 * Gets the mode.
	 * 
	 * @return the mode.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Set the changed value.
	 * 
	 * @param changed
	 *            a boolean value for the changed field.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * Checks if the model has changed.
	 * 
	 * @return <code>true</code> if the model has changed; <code>false</code>
	 *         otherwise
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Gets the LQI grid object.
	 * 
	 * @return the LQI grid object.
	 */
	public LQIGridConfiguration getLQIGrid() {
		return lqiGridObj;
	}

	/**
	 * Sets the LQI grid.
	 * 
	 * @param lqiGrid
	 *            the LQI grid.
	 */
	public void setLQIGrid(LQIGridConfiguration lqiGrid) {
		this.lqiGridObj = lqiGrid;
		initColumns();
		fireTableDataChanged();
	}

	/**
	 * Adds a new row to the model.
	 * 
	 * @return the error category related to the added row.
	 */
	public LQIErrorCategory addRow() {
		LQIErrorCategory newErrorCat = null;
		if (lqiGridObj != null) {
			newErrorCat = new LQIErrorCategory("");
			if (lqiGridObj.getErrorCategories() == null) {
				lqiGridObj
				        .setErrorCategories(new ArrayList<LQIErrorCategory>());
			}
			lqiGridObj.getErrorCategories().add(newErrorCat);
			changed = true;
			fireTableRowsInserted(lqiGridObj.getErrorCategories().size() - 1,
			        lqiGridObj.getErrorCategories().size() - 1);
		}
		return newErrorCat;

	}

	/**
	 * Deletes a specific row.
	 * 
	 * @param rowIndex
	 *            the row index.
	 * @return the deleted error category object.
	 */
	public LQIErrorCategory deleteRow(int rowIndex) {

		LQIErrorCategory deletedErrorCat = null;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && rowIndex < lqiGridObj.getErrorCategories().size()) {
			deletedErrorCat = lqiGridObj.getErrorCategories().remove(rowIndex);
			changed = true;
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
		return deletedErrorCat;
	}

	/**
	 * Adds a table cell listener.
	 * 
	 * @param listener
	 *            the table cell listener.
	 */
	public void addTableCellListener(TableCellListener listener) {
		if (cellListeners == null) {
			cellListeners = new ArrayList<TableCellListener>();
		}
		cellListeners.add(listener);
	}

	/**
	 * Removes a specific table cell listener.
	 * 
	 * @param listener
	 *            table cell listener to remove.
	 */
	public void removeTableCellListener(TableCellListener listener) {
		if (cellListeners != null) {
			cellListeners.remove(listener);
		}
	}

	/**
	 * Gets all LQI categories currently in use in the LQI grid.
	 * 
	 * @return
	 */
	public List<String> getUsedCategoryName() {
		List<String> usedNames = new ArrayList<String>();
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null) {
			for (LQIErrorCategory errCat : lqiGridObj.getErrorCategories()) {
				usedNames.add(errCat.getName());
			}
		}
		return usedNames;
	}

	/**
	 * Moves a row up.
	 * 
	 * @param row
	 *            the row index.
	 * @return <code>true</code> if the row has been moved; <code>false</code>
	 *         otherwise.
	 */
	public boolean moveRowUp(int row) {

		boolean moved = false;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && lqiGridObj.getErrorCategories().size() > 1 && row > 0) {

			LQIErrorCategory catToMove = lqiGridObj.getErrorCategories().get(
			        row);
			lqiGridObj.getErrorCategories().set(row,
			        lqiGridObj.getErrorCategories().get(row - 1));
			lqiGridObj.getErrorCategories().set(row - 1, catToMove);
			moved = true;
			changed = true;
			fireTableRowsUpdated(row - 1, row);
		}
		return moved;
	}

	/**
	 * Moves a row down.
	 * 
	 * @param row
	 *            the row index.
	 * @return <code>true</code> if the row has been moved.
	 */
	public boolean moveRowDown(int row) {
		boolean moved = false;
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null
		        && lqiGridObj.getErrorCategories().size() > 1
		        && row < lqiGridObj.getErrorCategories().size() - 1) {
			LQIErrorCategory catToMove = lqiGridObj.getErrorCategories().get(
			        row);
			lqiGridObj.getErrorCategories().set(row,
			        lqiGridObj.getErrorCategories().get(row + 1));
			lqiGridObj.getErrorCategories().set(row + 1, catToMove);
			moved = true;
			changed = true;
			fireTableRowsUpdated(row, row + 1);
		}
		return moved;
	}

	/**
	 * Gets the comment for a specific category.
	 * 
	 * @param categoryName
	 *            the category name.
	 * @return the comment.
	 */
	public String getCommentByCategory(String categoryName) {

		String comment = null;

		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null) {
			for (int i = 0; i < lqiGridObj.getErrorCategories().size(); i++) {
				if (lqiGridObj.getErrorCategories().get(i).getName()
				        .equals(categoryName)) {
					comment = lqiGridObj.getErrorCategories().get(i)
					        .getComment();
					break;
				}
			}
		}
		return comment;
	}

	/**
	 * Clears the comment cell for a specific category.
	 * 
	 * @param category
	 *            the category name.
	 */
	public void clearCommentForCategory(String category) {

		for (int i = 0; i < lqiGridObj.getErrorCategories().size(); i++) {
			if (lqiGridObj.getErrorCategories().get(i).getName()
			        .equals(category)) {
				lqiGridObj.getErrorCategories().get(i).setComment("");
				fireTableCellUpdated(i,
				        getColumnIndex(fixedColumns.get(COMMENT_COLUMN)));
				break;
			}
		}

	}

	/**
	 * Removes the column related to a specific severity.
	 * 
	 * @param severity
	 *            the severity.
	 */
	public void removeSeverityColumn(LQISeverity severity) {

		if (lqiGridObj.getSeverities() != null) {
			lqiGridObj.getSeverities().remove(severity);
		}
		for (LQIErrorCategory cat : lqiGridObj.getErrorCategories()) {
			cat.removeShortcut(severity.getName());
		}
		initColumns();
		changed = true;
		fireTableStructureChanged();

	}

	/**
	 * Adds a column for a specific severity.
	 * 
	 * @param newSeverity
	 *            the severity.
	 */
	public void addSeverityColumn(LQISeverity newSeverity) {

		if (lqiGridObj.getSeverities() == null) {
			lqiGridObj.setSeverities(new ArrayList<LQISeverity>());
		}
		lqiGridObj.getSeverities().add(newSeverity);
		Collections.sort(lqiGridObj.getSeverities(),
		        new LQISeverityComparator());
		initColumns();
		changed = true;
		fireTableStructureChanged();
	}

	/**
	 * Sets the name for a severity column.
	 * 
	 * @param name
	 *            the name
	 * @param columnIndex
	 *            the column index.
	 */
	public void setNameForSeverityColumn(String name, int columnIndex) {

		LQIGridColumn column = getColumn(columnIndex);
		if (lqiGridObj.getSeverities() != null) {
			for (LQISeverity sev : lqiGridObj.getSeverities()) {
				if (sev.getName().equals(column.getName())) {
					sev.setName(name);
					break;
				}
			}
		}
		if (severityColumns.contains(column)) {
			column.setName(name);
		}
		changed = true;

	}

	/**
	 * Gets the severity related to a column with a specific index.
	 * 
	 * @param colIndex
	 *            the column index
	 * @return the severity if it exists; <code>null</code> otherwise
	 */
	public LQISeverity getSeverityByColumn(int colIndex) {

		LQISeverity severity = null;
		LQIGridColumn column = getColumn(colIndex);
		if (severityColumns.contains(column)
		        && lqiGridObj.getSeverities() != null) {
			for (LQISeverity sev : lqiGridObj.getSeverities()) {
				if (sev.getName().equals(column.getName())) {
					severity = sev;
					break;
				}
			}
		}
		return severity;
	}

	/**
	 * Sorts severity columns by severity score.
	 */
	public void sortSeverityColumns() {

		Collections.sort(lqiGridObj.getSeverities(),
		        new LQISeverityComparator());
		initColumns();
		changed = true;
		fireTableStructureChanged();
	}

}

/**
 * Severity comparator.
 */
class LQISeverityComparator implements Comparator<LQISeverity> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(LQISeverity sev1, LQISeverity sev2) {

		return Double.compare(sev1.getScore(), sev2.getScore());
	}

}
