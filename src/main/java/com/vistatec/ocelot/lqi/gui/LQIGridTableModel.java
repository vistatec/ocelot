package com.vistatec.ocelot.lqi.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory.LQIShortCut;
import com.vistatec.ocelot.lqi.model.LQIGrid;

/**
 * Table model for the LQI grid.
 */
public class LQIGridTableModel extends AbstractTableModel {

	/** Serial version UID. */
	private static final long serialVersionUID = -1813995821444213075L;

//	/** Error category column index. */
//	private static final int ERROR_CAT_COLUMN = 0;
//
//	/** The error category weight column index. */
//	private static final int ERROR_CAT_WEIGHT_COLUMN = 1;
//
//	/** Minor severity column index. */
//	private static final int MINOR_COLUMN = 2;
//
//	/** Serious severity column index. */
//	private static final int SERIOUS_COLUMN = 3;
//
//	/** Critical severity column index. */
//	private static final int CRITICAL_COLUMN = 4;
//
//	/** Comment column index. */
//	private static final int COMMENT_COLUMN = 5;

	private enum Column {
		ERROR_CAT("Error Category"), ERROR_WEIGHT("Weight"), MINOR_SCORE(
		        "Minor ($SCORE$)"), SERIOUS_SCORE("Serious ($SCORE$)"), CRITICAL_SCORE(
		        "Critical ($SCORE$)"), COMMENT("Comment");

		private String name;

		private Column(String name) {
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}

	/** String to be replaced with the actual score value in the column names. */
	private static final String ERROR_SCORE_REPLACE_STRING = "$SCORE$";

//	/** Column names. */
//	private final String[] columnNames = { "Error Category", "Weight",
//	        "Minor ($SCORE$)", "Serious ($SCORE$)", "Critical ($SCORE$)",
//	        "Comment" };

//	private Map<Integer, Boolean> enabledColumns;
	private EnumMap<Column, Boolean> enabledColumns;

	/** The LQI grid object. */
	private LQIGrid lqiGridObj;

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
	public LQIGridTableModel(final LQIGrid lqiGridObj, final int mode) {
		this.lqiGridObj = lqiGridObj;
		this.mode = mode;
		initColumns();
	}

	private void changedMode() {
		enabledColumns.put(Column.ERROR_WEIGHT,
		        mode == LQIGridDialog.CONFIG_MODE);
		fireTableStructureChanged();
	}

	private void initColumns() {

		enabledColumns = new EnumMap<Column, Boolean>(Column.class);
		for (Column c: Column.values()) {
			enabledColumns.put(c, true);
		}
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
		for (Column c: Column.values()) {
			if (enabledColumns.get(c)) {
				count++;
			}
		}
		return count;
	}

	private Column getColumn(int colIndex){
		
		Column column = null;
		int count = 0;
		for(Column c: Column.values()){
			if(enabledColumns.get(c)){
				if(count == colIndex){
					column = c;
					break;
				}
				count++;
			}
		}
		return column;
	}
	
	private int getColumnIndex(Column column){
		
		int colIndex = -1;
		if(enabledColumns.get(column)){
			for(Column col: Column.values()){
				if(enabledColumns.get(col)){
					colIndex++;
				}
				if(col.equals(column)){
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
		Column col = getColumn(colIndex);
		if(col != null){
			switch (col) {
			case MINOR_SCORE:
				colName = col.getName().replace(
				        ERROR_SCORE_REPLACE_STRING,
				        String.valueOf(lqiGridObj.getMinorScore()));
				break;
			case CRITICAL_SCORE:
				colName = col.getName().replace(
				        ERROR_SCORE_REPLACE_STRING,
				        String.valueOf(lqiGridObj.getCriticalScore()));
				break;
			case SERIOUS_SCORE:
				colName = col.getName().replace(
				        ERROR_SCORE_REPLACE_STRING,
				        String.valueOf(lqiGridObj.getSeriousScore()));
				break;
			default:
				colName = col.getName();
				break;
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
			Column col = getColumn(columnIndex);
			if (col != null) {
				switch (col) {
				case CRITICAL_SCORE:
					retValue = errorCat.getCriticalShortcut();
					break;
				case ERROR_CAT:
					retValue = errorCat.getName();
					break;
				case MINOR_SCORE:
					retValue = errorCat.getMinorShortcut();
					break;
				case SERIOUS_SCORE:
					retValue = errorCat.getSeriousShortcut();
					break;
				case COMMENT:
					retValue = errorCat.getComment();
					break;
				case ERROR_WEIGHT:
					if (errorCat.getWeight() > 0) {
						retValue = errorCat.getWeight();
					}
					break;
				default:
					break;
				}
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
			Column col = getColumn(columnIndex);
			if (col != null) {
				if (col.equals(Column.ERROR_CAT)) {
					if (aValue != null && !((String) aValue).isEmpty()
					        && !errorCat.getName().equals((String) aValue)) {
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
				} else if (col.equals(Column.COMMENT)) {
					errorCat.setComment((String) aValue);
				} else if(col.equals(Column.ERROR_WEIGHT)) {
					String weigthString = (String)aValue;
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

		Column col = getColumn(columnIndex);
		return col != null && (mode == LQIGridDialog.ISSUES_ANNOTS_MODE && !col.equals(Column.ERROR_CAT))
		        || (mode == LQIGridDialog.CONFIG_MODE && !col.equals(Column.COMMENT));
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
	 * Gets the minor score column index.
	 * 
	 * @return the minor score column index.
	 */
	public int getMinorScoreColumn() {
		return getColumnIndex(Column.MINOR_SCORE);
	}

	/**
	 * Gets the serious score column index.
	 * 
	 * @return the serious score column index.
	 */
	public int getSeriousScoreColumn() {
		return getColumnIndex(Column.SERIOUS_SCORE);
	}

	/**
	 * Gets the critical score column index.
	 * 
	 * @return the critical score column index.
	 */
	public int getCriticalScoreColumn() {
		return getColumnIndex(Column.CRITICAL_SCORE);
	}

	/**
	 * Gets the error category column index.
	 * 
	 * @return the error category column index.
	 */
	public int getErrorCategoryColumn() {
		return getColumnIndex(Column.ERROR_CAT);
	}

	/**
	 * Gets the comment column index.
	 * 
	 * @return the comment column index.
	 */
	public int getCommentColumn() {
		return getColumnIndex(Column.COMMENT);
	}

	public int getErrorCatWeightColumn(){
		return getColumnIndex(Column.ERROR_WEIGHT);
	}
	
	/**
	 * Gets the severity score for a specific severity column.
	 * 
	 * @param column
	 *            the column index
	 * @return the severity score
	 */
	public int getScoreForColumn(int column) {

		int score = 0;
		Column col = getColumn(column);
		if (col != null) {
			switch (col) {
			case CRITICAL_SCORE:
				score = lqiGridObj.getCriticalScore();
				break;
			case MINOR_SCORE:
				score = lqiGridObj.getMinorScore();
				break;
			case SERIOUS_SCORE:
				score = lqiGridObj.getSeriousScore();
				break;
			default:
				break;
			}
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
	public void setScoreForColumn(int score, int column) {
		Column col = getColumn(column);
		if (col != null) {
			switch (col) {
			case CRITICAL_SCORE:
				if (score != lqiGridObj.getCriticalScore()) {
					lqiGridObj.setCriticalScore(score);
					changed = true;
				}
				break;
			case MINOR_SCORE:
				if (score != lqiGridObj.getMinorScore()) {
					lqiGridObj.setMinorScore(score);
					changed = true;
				}
				break;
			case SERIOUS_SCORE:
				if (score != lqiGridObj.getSeriousScore()) {
					lqiGridObj.setSeriousScore(score);
					changed = true;
				}
				break;
			default:
				break;
			}
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
		Column col = getColumn(severityColumn);
		if (col != null && !col.equals(Column.ERROR_CAT) ) {
			severityName = col.getName().replace("("
			        + ERROR_SCORE_REPLACE_STRING + ")", "");
		}
		return severityName;
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
			LQIShortCut newShortCut = new LQIShortCut(keyCode, modifiers);
			Column col = getColumn(column);
			if(col != null){
				switch (col) {
				case CRITICAL_SCORE:
					if (errorCat.getCriticalShortcut() == null
					        || !errorCat.getCriticalShortcut().equals(
					                newShortCut)) {
						errorCat.setCriticalShortcut(newShortCut);
						changed = true;
					}
					break;
				case MINOR_SCORE:
					if (errorCat.getMinorShortcut() == null
					        || !errorCat.getMinorShortcut().equals(newShortCut)) {
						errorCat.setMinorShortcut(newShortCut);
						changed = true;
					}
					break;
				case SERIOUS_SCORE:
					if (errorCat.getSeriousShortcut() == null
					        || !errorCat.getSeriousShortcut().equals(
					                newShortCut)) {
						errorCat.setSeriousShortcut(newShortCut);
						changed = true;
					}
					break;
				default:
					break;
				}
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
	public LQIGrid getLQIGrid() {
		return lqiGridObj;
	}

	public void setLQIGrid(LQIGrid lqiGrid) {
		this.lqiGridObj = lqiGrid;
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

	public void clearCommentForCategory(String category) {

		for (int i = 0; i < lqiGridObj.getErrorCategories().size(); i++) {
			if (lqiGridObj.getErrorCategories().get(i).getName()
			        .equals(category)) {
				lqiGridObj.getErrorCategories().get(i).setComment("");
				fireTableCellUpdated(i, getColumnIndex(Column.COMMENT));
				break;
			}
		}

	}
}
