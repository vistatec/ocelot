package com.vistatec.ocelot.lqi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The LQI grid object.
 */
public class LQIGrid {

	/** List of severities. */
	private List<LQISeverity> severities;

	/** List of error categories. */
	private List<LQIErrorCategory> errorCategories;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		LQIGrid clonedGrid = new LQIGrid();
		if (severities != null) {
			List<LQISeverity> clonedSeverities = new ArrayList<LQISeverity>();
			for (LQISeverity severity : severities) {
				clonedSeverities.add((LQISeverity) severity.clone());
			}
			clonedGrid.setSeverities(clonedSeverities);
		}
		if (errorCategories != null) {
			List<LQIErrorCategory> clonedCategories = new ArrayList<LQIErrorCategory>();
			for (LQIErrorCategory currCat : errorCategories) {
				clonedCategories.add(currCat.clone(severities));
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
	 * @param severityName the severity name.
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
	 * @param severityName the severity name.
	 * @param score the severity score.
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
	 * @param severityName the severity name. 
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
}
