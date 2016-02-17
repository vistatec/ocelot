package com.vistatec.ocelot.lqi.gui.editor;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.vistatec.ocelot.lqi.gui.FloatDocument;

/**
 * Cell editor letting users only insert float values. 
 */
public class FloatCellEditor extends DefaultCellEditor  {

	/** The serial version UID. */
	private static final long serialVersionUID = -7583459009569524787L;

	/** The float document. */
	private FloatDocument document;
	
	/** The text field being the editor component. */
	private JTextField editorComp;
	
	
	/**
	 * Constructor.
	 */
	public FloatCellEditor() {
		super(new JTextField());
		document = new FloatDocument();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {
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
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
	    return editorComp.getText();
	}
}
