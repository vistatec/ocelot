package com.vistatec.ocelot.lqi.gui.editor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Cell editor displaying a text area.
 */
public class TextAreaTableCellEditor extends DefaultCellEditor {

	/** The serial version UID. */
	private static final long serialVersionUID = 7243499768414128027L;

	/** The text area. */
	private JTextArea txtArea;
	
	/**
	 * Constructor.
	 */
	public TextAreaTableCellEditor() {
		super(new JTextField());
		setClickCountToStart(2);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(final JTable table, Object value,
	        boolean isSelected, int row, int column) {

		txtArea = new JTextArea();
		if (value != null) {
			txtArea.setText((String) value);
		}
		txtArea.setWrapStyleWord(true);
		txtArea.setLineWrap(true);
		
		txtArea.setBackground(table.getBackground());
		txtArea.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				//do nothing
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				//do nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					stopCellEditing();
					table.getSelectionModel().clearSelection();
					
				}
			}
		});
		JScrollPane scrollpane = new JScrollPane(txtArea);
		scrollpane.setSize(table.getColumnModel().getColumn(column).getWidth(),
		        table.getRowHeight());
		
		return scrollpane;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
	    return txtArea.getText();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable(java.util.EventObject anEvent) {
	
		return anEvent instanceof MouseEvent &&  ((MouseEvent) anEvent).getButton() == MouseEvent.BUTTON1 && ((MouseEvent) anEvent).getClickCount() == 2; 
		
	}
}
