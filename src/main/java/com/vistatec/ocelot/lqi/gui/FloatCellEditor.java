package com.vistatec.ocelot.lqi.gui;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class FloatCellEditor extends DefaultCellEditor  {

	private static final long serialVersionUID = -7583459009569524787L;

	private FloatDocument document;
	
	private JTextField editorComp;
	
	
	public FloatCellEditor() {
		super(new JTextField());
		document = new FloatDocument();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {
	    // TODO Auto-generated method stub
	    editorComp = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
	    editorComp.setDocument(document);
	    editorComp.addKeyListener(new KeyAdapter() {
	    	
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    		if(e.getKeyCode() == KeyEvent.VK_ENTER){
	    			stopCellEditing();
	    		}
	    	}
		});
	    return editorComp;
	}
	
	@Override
	public Object getCellEditorValue() {
	    return editorComp.getText();
	}
}
