package com.vistatec.ocelot.tm.gui.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;

public class TmTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -361910238366547586L;

	public static final int TM_NAME_COL = 0;

	public static final int TM_ROOT_DIR_PATH_COL = 1;

	public static final int TM_PENALTY_COL = 2;

	public static final int TM_ENABLED_COL = 3;
	
//	public static final int TM_DEL_COL = 5;
//	
//	public static final int TM_CHANGE_DIR_COL = 4;
	
	private final String[] columnNames = { "Name", "Path", "Penalty", "Enabled" };

	private List<TmConfig> model;
	
	private boolean edited;
	
	public TmTableModel(final List<TmConfig> model) {
		
		if(model != null){
			this.model = new ArrayList<TmConfig>();
			this.model.addAll(model);
		}
		
	}

	@Override
	public int getRowCount() {

		int count = 0;
		if (model != null) {
			count = model.size();
		}
		return count;
	}

	@Override
	public int getColumnCount() {

		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {

		String colName = "";
		if (column < columnNames.length) {
			colName = columnNames[column];
		}
		return colName;
	}

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
//		case TM_DEL_COL:
//			colClass = ImageIcon.class;
//			break;
//		case TM_CHANGE_DIR_COL:
//			colClass = ImageIcon.class;
//			break;	
		default:
			break;
		}
		return colClass;
	}

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
//				case TM_DEL_COL:
//					retValue = deleteIcon;
//					break;
//				case TM_CHANGE_DIR_COL:
//					retValue = changeDirIcon;
//					break;
				default:
					break;
				}
			}
		}
		return retValue;
	}
	

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if(model != null && rowIndex < model.size()){
			TmConfig currTm = model.get(rowIndex);
			switch (columnIndex) {
			case TM_ENABLED_COL:
				currTm.setEnabled((boolean)aValue);
				edited = true;
				break;
			case TM_PENALTY_COL:
				currTm.setPenalty((float)aValue);
				edited = true;
				break;

			default:
				break;
			}
		}
		
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == TM_ENABLED_COL || columnIndex == TM_PENALTY_COL;
	}
	
	public boolean moveDownRow(int rowIdx){
		boolean moved = false;
		if(model != null && rowIdx < model.size() - 1){
			TmConfig tmToMove = model.get(rowIdx);
			model.set(rowIdx, model.get(rowIdx + 1));
			model.set(rowIdx + 1, tmToMove);
			fireTableRowsUpdated(rowIdx, rowIdx + 1);
			moved = true;
		}
		return moved;
	}
	
    public boolean moveUpRow(int rowIdx){
		boolean moved = false;
		if(model != null && rowIdx > 0 && rowIdx < model.size()){
			TmConfig tmToMove = model.get(rowIdx);
			model.set(rowIdx, model.get(rowIdx - 1));
			model.set(rowIdx - 1, tmToMove);
			fireTableRowsUpdated(rowIdx - 1, rowIdx);
			moved = true;
		}
		return moved;
	}

	public TmConfig deleteRow(int selRow) {
		
		TmConfig deletedTm = null;
		if(model != null && selRow < model.size()){
			deletedTm = model.remove(selRow);
//			fireTableDataChanged();
			fireTableRowsDeleted(selRow, selRow);
		}
		return deletedTm;
	}
	
	public TmConfig getTmAtRow(final int row){
		
		TmConfig tm = null;
		if(model != null && row < model.size()){
			tm = model.get(row);
		}
		return tm;
	}

	public List<TmConfig> getTmList() {
		
		return model;
	}

	public void addRow(TmConfig newTm) {

		if(model != null){
			model.add(newTm);
			fireTableDataChanged();
		}
		
	}
	
	
	@Override
	public void fireTableChanged(TableModelEvent e) {
		super.fireTableChanged(e);
		edited = true;
	}

	public boolean isEdited(){
		return edited;
	}
	
	public void setEdited(final boolean edited){
		this.edited = edited;
	}
}
