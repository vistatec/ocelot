package com.vistatec.ocelot.lqi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LQI error category object,.
 */
public class LQIErrorCategory {

	/** The category name. */
	private String name;

	/** The list of shortcuts. */
	private List<LQIShortCut> shortcuts;

	/** The comment. */
	private String comment;

	/** The weight. */
	private double weight;
	

	/**
	 * Default constructor.
	 */
	public LQIErrorCategory() {
		
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the category name.
	 */
	public LQIErrorCategory( final String name) {

		this(name, 0, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the category name.
	 * @param weight
	 *            the weight.
	 * @param shortcuts
	 *            the list of shortcuts.
	 */
	public LQIErrorCategory(final String name, final float weight,
	        final List<LQIShortCut> shortcuts) {
		
		this.name = name;
		this.weight = weight;
		this.shortcuts = shortcuts;
	}

	
	
	/**
	 * Gets the category name.
	 * 
	 * @return the category name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the category name.
	 * 
	 * @param name
	 *            the category name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param comment
	 *            the comment.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Gets the weight.
	 * 
	 * @return the weight.
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 * 
	 * @param weight
	 *            the weight.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Sets the shortcuts list.
	 * 
	 * @param shortcuts
	 *            the shortcuts list.
	 */
	public void setShortcuts(List<LQIShortCut> shortcuts) {
		this.shortcuts = shortcuts;
	}

	/**
	 * Gets the shortcuts list.
	 * 
	 * @return the shortcuts list.
	 */
	public List<LQIShortCut> getShortcuts() {
		return shortcuts;
	}

	/**
	 * Clones this category.
	 * 
	 * @param severities
	 *            the list of severities.
	 * @return a category being a clone of this one.
	 */
	public LQIErrorCategory clone(List<LQISeverity> severities) {

		LQIErrorCategory clonedErrCat = new LQIErrorCategory( name);
		clonedErrCat.setWeight(weight);
		if (shortcuts != null) {
			List<LQIShortCut> clonedShortcuts = new ArrayList<LQIShortCut>();
			for (LQIShortCut shortcut : shortcuts) {
				clonedShortcuts.add(shortcut.clone(getSeverityByName(shortcut
				        .getSeverity().getName(), severities)));
			}
			clonedErrCat.setShortcuts(clonedShortcuts);
		}
		return clonedErrCat;

	}

	/**
	 * Gets the severity by name.
	 * 
	 * @param severityName
	 *            the severity name.
	 * @param severities
	 *            the list of severities
	 * @return the severity haviing the desired name.
	 */
	private LQISeverity getSeverityByName(String severityName,
	        List<LQISeverity> severities) {

		LQISeverity severity = null;
		if (severities != null) {
			for (LQISeverity currSev : severities) {
				if (currSev.getName().equals(severityName)) {
					severity = currSev;
					break;
				}
			}
		}
		return severity;
	}

	/**
	 * Gets the shortcut for a specific severity.
	 * 
	 * @param severityName
	 *            the severity name
	 * @return the shortcut if it exists; <code>null</code> otherwise
	 */
	public LQIShortCut getShortcut(String severityName) {

		LQIShortCut shortcut = null;
		if (shortcuts != null) {
			for (LQIShortCut sc : shortcuts) {
				if (sc.getSeverity().getName().equals(severityName)) {
					shortcut = sc;
					break;
				}
			}
		}
		return shortcut;
	}

	/**
	 * Adds a shortcuts or replaces the existing one if it already exists.
	 * 
	 * @param shortcut
	 *            the shortcut.
	 */
	public void setShortcut(LQIShortCut shortcut) {

		if (shortcuts == null) {
			shortcuts = new ArrayList<LQIShortCut>();
		}
		LQIShortCut existingShortcut = getShortcut(shortcut.getSeverity()
		        .getName());
		if (existingShortcut != null) {
			shortcuts.remove(existingShortcut);
		}
		shortcuts.add(shortcut);
	}

	/**
	 * Removes a shortcut for a specific severity.
	 * 
	 * @param severityName
	 *            the severity name.
	 */
	public void removeShortcut(String severityName) {

		LQIShortCut shortcut = getShortcut(severityName);
		if (shortcut != null) {
			shortcuts.remove(shortcut);
		}
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == this) return true;
	    if (obj == null || !(obj instanceof LQIErrorCategory)) return false;
	    LQIErrorCategory c = (LQIErrorCategory)obj;
	    return Objects.equals(name, c.name) &&
	           weight == c.weight &&
	           Objects.equals(comment, c.comment) &&
	           Objects.equals(shortcuts, c.shortcuts);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(name, weight, comment, shortcuts);
	}
}
