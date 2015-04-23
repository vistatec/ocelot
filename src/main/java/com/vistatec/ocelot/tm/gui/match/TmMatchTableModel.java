package com.vistatec.ocelot.tm.gui.match;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vistatec.ocelot.tm.TmMatch;

public abstract class TmMatchTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6256335853992768787L;

	protected List<TmMatch> model;
	
	private String[] columns;
	
	public TmMatchTableModel(final List<TmMatch> model, final String[] columns) {
		this.model = model;
		this.columns = columns;
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
		int count = 0;
		if(columns != null ){
			count = columns.length;
		}
		return count;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
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
	
	public abstract int getSourceColumnIdx();
	
	public abstract int getTargetColumnIdx();
	
	public abstract int getMatchScoreColumnIdx();
	
	public abstract int getTmColumnIdx();
	
}
