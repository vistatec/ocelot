package com.vistatec.ocelot.freme.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

/**
 * Table model for tables displaying enrichmens details.
 */
public class EnrichmentTableModel extends DefaultTableModel {

	/** The serial version UID. */
	private static final long serialVersionUID = -3616098048861377131L;

	/** The description column index. */
	public static final int DESCRIPTION_COL = 1;

	/** The button column index. */
	public static final int BUTTON_COL = 0;

	/** The list of enrichments being the model. */
	private List<Enrichment> model;

	/** States if the description column is editable. */
	private boolean descrColEditable;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the list of enrichments
	 * @param descrColEditable
	 *            a boolean stating if the description column is editable.
	 */
	public EnrichmentTableModel(List<Enrichment> model, boolean descrColEditable) {

		this.model = model;
		this.descrColEditable = descrColEditable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {

		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int count = 0;
		if (model != null) {
			count = model.size();
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {

		Class clazz = null;
		switch (columnIndex) {
		case DESCRIPTION_COL:
			clazz = String.class;
			break;
		case BUTTON_COL:
			clazz = Boolean.class;
			break;
		default:
			break;
		}
		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {

		Object retValue = null;
		if (model != null) {
			if (row < model.size()) {
				Enrichment enrich = model.get(row);
				switch (column) {
				case DESCRIPTION_COL:
					retValue = enrich.toString();
					break;
				case BUTTON_COL:
					retValue = !enrich.isDisabled();
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
	 * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int row, int column) {

		if (model != null && row < model.size()) {
			Enrichment enrich = model.get(row);
			switch (column) {
			case BUTTON_COL:
				enrich.setDisabled(!(Boolean) aValue);
				break;

			default:
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == BUTTON_COL
				|| (descrColEditable && column == DESCRIPTION_COL);
	}

	/**
	 * Adds an enrichment to the model. This is equivalent to adding a row to
	 * the table.
	 * 
	 * @param enrichment
	 *            the enrichment
	 */
	public void addEnrichment(final Enrichment enrichment) {
		if (enrichment != null) {
			if (model == null) {
				model = new ArrayList<Enrichment>();
			}
			model.add(enrichment);
		}
	}

	/**
	 * Gets the enrichment lying at a specific row.
	 * 
	 * @param row
	 *            the row.
	 * @return the enrichment.
	 */
	public Enrichment getEnrichmentAtRow(int row) {

		Enrichment enrich = null;
		if (model != null && row < model.size()) {
			enrich = model.get(row);
		}
		return enrich;
	}

}
