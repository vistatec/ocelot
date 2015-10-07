package com.vistatec.ocelot.segment.model.enrichment;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represent the data displayed in the Info page of the link
 * enrichments frame.
 */
public class LinkInfoData {

	/** The input date pattern. */
	private static final String INPUT_DATE_PATTERN = "yyyy-MM-dd";

	/** The output date pattern. */
	private static final String OUTPUT_DATE_PATTERN = "dd-MM-yyyy";

	/** The property name. */
	private String propName;

	/** The label to be used in frame. */
	private String label;

	/** The property value. */
	private String value;

	/** The unit of measure, if defined. */
	private String unitOfMeasure;

	/** The type of the value. */
	private Class<?> type;

	/**
	 * Default constructor.
	 */
	public LinkInfoData() {
	}

	/**
	 * Constructor.
	 * 
	 * @param propName
	 *            the property name.
	 * @param label
	 *            the label
	 * @param dataType
	 *            the data type.
	 * @param unitOfMeasure
	 *            the unit of measure.
	 */
	public LinkInfoData(String propName, String label, Class<?> dataType,
	        String unitOfMeasure) {

		this.propName = propName;
		this.label = label;
		this.type = dataType;
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * Constructor.
	 * 
	 * @param propName
	 *            the property name
	 * @param label
	 *            the label
	 * @param dataType
	 *            the data type
	 */
	public LinkInfoData(String propName, String label, Class<?> dataType) {

		this(propName, label, dataType, null);

	}

	/**
	 * Gets the label.
	 * 
	 * @return the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value.
	 */
	public void setValue(String value) {
		if (value != null) {
			if (type.equals(Date.class)) {
				try {
					SimpleDateFormat dateFormatter = new SimpleDateFormat(
					        INPUT_DATE_PATTERN);
					Date date = dateFormatter.parse(value);
					dateFormatter = new SimpleDateFormat(OUTPUT_DATE_PATTERN);
					this.value = dateFormatter.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (type.equals(Float.class)) {
				NumberFormat numberFormatter = NumberFormat.getInstance();
				numberFormatter.setMaximumFractionDigits(3);
				this.value = numberFormatter.format(Float.valueOf(value));
			}
		}
		this.value = value;
	}

	/**
	 * Gets the unit of measure.
	 * 
	 * @return the unit of measure if defined; <code>null</code> otherwise.
	 */
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	/**
	 * Sets the unit of measure.
	 * 
	 * @param unitOfMeasure
	 *            the unit of measure.
	 */
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * Gets the property name.
	 * 
	 * @return the property name.
	 */
	public String getPropName() {
		return propName;
	}

	/**
	 * Sets the property name.
	 * 
	 * @param propName
	 *            the property name.
	 */
	public void setPropName(String propName) {
		this.propName = propName;
	}

	/**
	 * Gets the string to be displayed in the panel.
	 * 
	 * @return the string to be displayed in the panel.
	 */
	public String getDisplayString() {

		return label + ": " + value
		        + (unitOfMeasure != null ? (" " + unitOfMeasure) : "");
	}

}
