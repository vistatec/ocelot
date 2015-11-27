package com.vistatec.ocelot.lqi.gui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextAreaTableCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7243499768414128027L;

	private JTextArea txtArea;
	
	public TextAreaTableCellEditor() {
		super(new JTextField());
		setClickCountToStart(2);
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, Object value,
	        boolean isSelected, int row, int column) {
		// super.getTableCellEditorComponent(table, value, isSelected, row,
		// column);

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
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
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
	
	@Override
	public Object getCellEditorValue() {
	    return txtArea.getText();
	}
	
	public boolean isCellEditable(java.util.EventObject anEvent) {
	
		return anEvent instanceof MouseEvent &&  ((MouseEvent) anEvent).getButton() == MouseEvent.BUTTON1 && ((MouseEvent) anEvent).getClickCount() == 2; 
		
	}
}
