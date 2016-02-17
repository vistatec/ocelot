package com.vistatec.ocelot.lqi.gui.editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import com.vistatec.ocelot.lqi.gui.LQIGridButton;
import com.vistatec.ocelot.lqi.gui.renderer.LQIGridButtonRenderer;

/**
 * Editor for button cells in the LQI grid.
 */
public class LQIGridButtonEditor extends DefaultCellEditor {

	/** Serial version UID. */
	private static final long serialVersionUID = -7750602522468180706L;

	/** The LQI grid button. */
	private LQIGridButton gridButton;

	/**
	 * Constructor.
	 * 
	 * @param annotAction
	 *            the action for the LQI grid button.
	 */
	public LQIGridButtonEditor(final Action annotAction) {
		super(new JTextField());
		gridButton = new LQIGridButton(annotAction);
		gridButton.setForeground(Color.gray);
		setClickCountToStart(1);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {

		String shortCut = null;
		if (value != null) {
			shortCut = value.toString();
		}
		gridButton.setText(shortCut);
		TableCellRenderer renderer = table.getCellRenderer(row, column);
		if (renderer instanceof LQIGridButtonRenderer) {
			gridButton.setBackground(((LQIGridButtonRenderer) renderer)
			        .getBackground());
		}

		gridButton.setCategoryRow(row);
		gridButton.setSeverityColumn(column);
		gridButton.setBorder(new LineBorder(table.getGridColor()));
		return gridButton;
	}

}
