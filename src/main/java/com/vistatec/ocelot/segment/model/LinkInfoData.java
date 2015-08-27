package com.vistatec.ocelot.segment.model;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkInfoData {

	private static final String INPUT_DATE_PATTERN = "yyyy-MM-dd";

	private static final String OUTPUT_DATE_PATTERN = "dd-MM-yyyy";

	private String propName;

	private String label;

	private String value;

	private String unitOfMeasure;

	private Class<?> type;

	public LinkInfoData() {
	}

	public LinkInfoData(String propName, String label, Class<?> dataType,
	        String unitOfMeasure) {

		this.propName = propName;
		this.label = label;
		this.type = dataType;
		this.unitOfMeasure = unitOfMeasure;
	}
	
	public LinkInfoData(String propName, String label, Class<?> dataType) {
		
		this(propName, label, dataType, null);
	
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

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
			} else if(type.equals(Float.class)){
				NumberFormat numberFormatter = NumberFormat.getInstance();
				numberFormatter.setMaximumFractionDigits(3);
				this.value = numberFormatter.format(Float.valueOf(value));
			}
		}
		this.value = value;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
	
	public String getDisplayString(){
		
		return /*"<html> <b>" +*/ label + ": " + value + (unitOfMeasure != null ? (" " + unitOfMeasure) : "")/* + "</html>"*/; 
	}

}
