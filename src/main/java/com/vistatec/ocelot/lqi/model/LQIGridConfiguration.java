package com.vistatec.ocelot.lqi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LQIGridConfiguration {

	private String name;

	private Double threshold;

	private String supplier;

	private boolean active;
	
	private List<LQIErrorCategory> errorCategories;

	private List<LQISeverity> severities;

	public LQIGridConfiguration() {
	 
		name = "";
    }
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplier() {
		return supplier;
	}

	/**
	 * Gets the list of error categories.
	 * 
	 * @return the list of error categories.
	 */
	public List<LQIErrorCategory> getErrorCategories() {
		return errorCategories;
	}

	/**
	 * Sets the list of error categories.
	 * 
	 * @param errorCategories
	 *            the list of error categories.
	 */
	public void setErrorCategories(List<LQIErrorCategory> errorCategories) {
		this.errorCategories = errorCategories;
	}

	/**
	 * Gets the list of severities.
	 * 
	 * @return the list of severities.
	 */
	public List<LQISeverity> getSeverities() {
		return severities;
	}

	/**
	 * Sets the list of severities.
	 * 
	 * @param severities
	 *            the list of severities.
	 */
	public void setSeverities(List<LQISeverity> severities) {
		this.severities = severities;
	}

	public void addSeverity(LQISeverity severity) {
		if (severities == null) {
			severities = new ArrayList<LQISeverity>();
		}
		severities.add(severity);
	}

	public void addErrorCategory(LQIErrorCategory errorCat, int position) {
		if (errorCategories == null) {
			errorCategories = new ArrayList<LQIErrorCategory>();
		}
		errorCategories.add(position, errorCat);
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		// LQIGrid clonedGrid = new LQIGrid();
		LQIGridConfiguration clonedGrid = new LQIGridConfiguration();
		clonedGrid.setName(name);
		clonedGrid.setSupplier(supplier);
		clonedGrid.setThreshold(threshold);
		clonedGrid.setActive(active);
		List<LQISeverity> clonedSeverities = null;
		if (severities != null) {
			clonedSeverities = new ArrayList<LQISeverity>();
			for (LQISeverity severity : severities) {
				clonedSeverities.add((LQISeverity) severity.clone());
			}
			clonedGrid.setSeverities(clonedSeverities);
		}
		if (errorCategories != null) {
			List<LQIErrorCategory> clonedCategories = new ArrayList<LQIErrorCategory>();
			for (LQIErrorCategory currCat : errorCategories) {
				clonedCategories.add(currCat.clone(clonedSeverities));
			}
			clonedGrid.setErrorCategories(clonedCategories);
		}
		return clonedGrid;
	}

	/**
	 * Checks if this LQI grid is empty.
	 * 
	 * @return <code>true</code> if it is empty; <code>false</code> otherwise
	 */
	public boolean isEmpty() {

		return (errorCategories == null || errorCategories.isEmpty())
		        && (severities == null || severities.isEmpty());
	}

	/**
	 * Gets the severity score for a specific severity.
	 * 
	 * @param severityName
	 *            the severity name.
	 * @return the severity score.
	 */
	public double getSeverityScore(String severityName) {

		double score = 0;
		if (severities != null) {
			for (LQISeverity sev : severities) {
				if (sev.getName().equals(severityName)) {
					score = sev.getScore();
					break;
				}
			}
		}
		return score;
	}

	/**
	 * Sets the severity score for a specific severity.
	 * 
	 * @param severityName
	 *            the severity name.
	 * @param score
	 *            the severity score.
	 */
	public void setSeverityScore(String severityName, double score) {

		if (severities != null) {
			for (LQISeverity sev : severities) {
				if (sev.getName().equals(severityName)) {
					sev.setScore(score);
					break;
				}
			}
		}
	}

	/**
	 * Gets the severity having a specific name.
	 * 
	 * @param severityName
	 *            the severity name.
	 * @return the severity.
	 */
	public LQISeverity getSeverity(String severityName) {

		LQISeverity severity = null;
		if (severities != null) {
			for (LQISeverity sev : severities) {
				if (sev.getName().equals(severityName)) {
					severity = sev;
					break;
				}
			}
		}
		return severity;
	}

	public void sortSeverities() {
		if (severities != null) {
			Collections.sort(severities, new SeverityComparator());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj != null && obj instanceof LQIGridConfiguration ){
			LQIGridConfiguration conf = (LQIGridConfiguration)obj;
			return name.equals(conf.getName());
		} else {
			return super.equals(obj);
		}
	}
	
	@Override
	public int hashCode() {
	    return name.hashCode();
	}
}

class SeverityComparator implements Comparator<LQISeverity> {

	@Override
	public int compare(LQISeverity o1, LQISeverity o2) {

		return Double.valueOf(o1.getScore()).compareTo(o2.getScore());
	}

}
