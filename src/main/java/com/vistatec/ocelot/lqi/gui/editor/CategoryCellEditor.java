package com.vistatec.ocelot.lqi.gui.editor;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;

/**
 * Editor for LQI grid category cells. It displays a combo box listing all
 * available LQI categories. 
 */
public class CategoryCellEditor extends DefaultCellEditor {

	/** Serial version UID. */
	private static final long serialVersionUID = -4255258806051066858L;

	/**
	 * Constructor.
	 */
	public CategoryCellEditor() {
		super(new JComboBox<String>());
		setClickCountToStart(2);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {

		JComboBox<String> comboBox = (JComboBox<String>) super
		        .getTableCellEditorComponent(table, value, isSelected, row,
		                column);
		Vector<String> comboModel = new Vector<String>(
		        Arrays.asList(LQIConstants.LQI_CATEGORIES_LIST));
		String currCat = "";
		if (value != null) {
			currCat = (String) value;
		}
		List<String> existingCat = ((LQIGridTableModel) table.getModel())
		        .getUsedCategoryName();
		existingCat.remove(currCat);
		comboModel.removeAll(existingCat);
		comboBox.setModel(new DefaultComboBoxModel<>(comboModel));
		comboBox.setBackground(table
		        .getCellRenderer(row, column)
		        .getTableCellRendererComponent(table, value, isSelected, true,
		                row, column).getBackground());
		comboBox.setSelectedItem(currCat);
		return comboBox;
	}

}
