package com.vistatec.ocelot.tm.gui.match;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

public class ConcordanceMatchTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2115553091508295586L;

	public static final int SOURCE_COL = 0;

	public static final int MATCH_SCORE_COL = 1;

	public static final int TARGET_COL = 2;

	public static final int TM_NAME_COL = 3;

	private List<TmMatch> model;

	private String[] columns = { "", "", "", "" };

	public ConcordanceMatchTableModel(final List<TmMatch> model) {

		this.model = model;
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
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {

		String colName = "";
		if (column < columns.length) {
			colName = columns[column];
		}
		return colName;
	}

	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {

		Class<?> clazz = Object.class;
		switch (columnIndex) {
		case MATCH_SCORE_COL:
		case TM_NAME_COL:
			clazz = String.class;
			break;
		case SOURCE_COL:
		case TARGET_COL:
			clazz = SegmentVariant.class;
		default:
			break;
		}
		return clazz;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Object getValueAt(int row, int column) {

		Object retValue = null;
		if (model != null && row < model.size()) {
			TmMatch currMatch = model.get(row);
			switch (column) {
			case SOURCE_COL:
				retValue = currMatch.getSource();
				break;
			case MATCH_SCORE_COL:
				
				retValue = String.valueOf(currMatch.getMatchScore() * 100) + "%";
				break;
			case TARGET_COL:
				retValue = currMatch.getTarget();
				break;
			case TM_NAME_COL:
				retValue = currMatch.getTmOrigin();
				break;
			default:
				break;
			}
		}
		return retValue;
	}
	
	public void setModel(List<TmMatch> model){
		this.model = model;
		fireTableDataChanged();
	}
	
	public TmMatch getElementAtRow(final int row){
		
		TmMatch element = null;
		if(model != null && row<model.size()){
			element = model.get(row);
		}
		return element;
				
	}

}
