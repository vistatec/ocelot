package com.vistatec.ocelot.config.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root element for the LQI Grid configuration file.
 */
@XmlRootElement(name = "LQIGrid")
public class LQIGridConfig implements RootConfig {

	/** List of error categories. */
	private List<LQICategory> lqiCategories;

	/** List of severities. */
	private List<LQIConfigSeverity> lqiSeverities;

	/**
	 * Gets the list of categories.
	 * 
	 * @return the list of categories.
	 */
	@XmlElement
	public List<LQICategory> getLqiCategories() {
		return lqiCategories;
	}

	/**
	 * Sets the list of categories
	 * 
	 * @param lqiCategories
	 *            the list of categories.
	 */
	public void setLqiCategories(List<LQICategory> lqiCategories) {
		this.lqiCategories = lqiCategories;
	}

	/**
	 * Gets the list of severities.
	 * 
	 * @return the list of severities.
	 */
	@XmlElement
	public List<LQIConfigSeverity> getLqiSeverities() {
		return lqiSeverities;
	}

	/**
	 * Sets the list of severities.
	 * 
	 * @param lqiSeverities
	 *            the list of severities.
	 */
	public void setLqiSeverities(List<LQIConfigSeverity> lqiSeverities) {
		this.lqiSeverities = lqiSeverities;
	}

	/**
	 * Clears all data.
	 */
	public void clear() {
		lqiCategories = null;
		lqiSeverities = null;
	}

	/**
	 * The LQI error category node.
	 */
	public static class LQICategory {

		/** The category name. */
		private String name;

		/** The position. */
		private int position;

		/** The weight. */
		private float weight;

		/** The list of shortcuts. */
		@XmlElement(nillable = true)
		private List<Shortcut> shortcuts;

		/**
		 * Gets the category name.
		 * 
		 * @return the category name.
		 */
		@XmlElement
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
		 * Gets the position.
		 * 
		 * @return the position.
		 */
		@XmlElement
		public int getPosition() {
			return position;
		}

		/**
		 * Sets the position.
		 * 
		 * @param position
		 *            the position.
		 */
		public void setPosition(int position) {
			this.position = position;
		}

		/**
		 * Gets the weight.
		 * 
		 * @return the weight.
		 */
		@XmlElement
		public float getWeight() {
			return weight;
		}

		/**
		 * Sets the weight.
		 * 
		 * @param weight
		 *            the weight.
		 */
		public void setWeight(float weight) {
			this.weight = weight;
		}

		/**
		 * Gets the list of shortcuts.
		 * 
		 * @return the list of shortcuts.
		 */
		public List<Shortcut> getShortcuts() {
			return shortcuts;
		}

		/**
		 * Sets the list of shortcuts.
		 * 
		 * @param shortcuts
		 *            the list of shortcuts.
		 */
		public void setShortCuts(List<Shortcut> shortcuts) {
			this.shortcuts = shortcuts;
		}
	}

	/**
	 * The LQI severity node.
	 */
	public static class LQIConfigSeverity {

		/** The severity name. */
		private String name;

		/** The severity score. */
		private double score;

		/**
		 * Sets the severity name.
		 * 
		 * @param name
		 *            the severity name.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the severity name.
		 * 
		 * @return the severity name.
		 */
		@XmlElement
		public String getName() {
			return name;
		}

		/**
		 * Sets the severity score.
		 * 
		 * @param score
		 *            the severity score.
		 */
		public void setScore(double score) {
			this.score = score;
		}

		/**
		 * Gets the severity score.
		 * 
		 * @return the severity score.
		 */
		@XmlElement
		public double getScore() {
			return score;
		}
	}

	/**
	 * The shortcut node.
	 */
	public static class Shortcut {

		/** The key code. */
		private int keyCode;

		/** The modifiers. */
		private String modifiers;

		/** The related severity name. */
		private String severityName;

		/**
		 * Gets the key code.
		 * @return the key code.
		 */
		@XmlElement
		public int getKeyCode() {
			return keyCode;
		}

		/**
		 * Sets the key code.
		 * @param keyCode the key code.
		 */
		public void setKeyCode(int keyCode) {
			this.keyCode = keyCode;
		}

		/**
		 * Gets the modifiers.
		 * @return the modifiers.
		 */
		@XmlElement
		public String getModifiers() {
			return modifiers;
		}

		/**
		 * Sets the modifiers.
		 * @param modifiers the modifiers.
		 */
		public void setModifiers(String modifiers) {
			this.modifiers = modifiers;
		}

		/**
		 * Gets the severity name.
		 * @return the severity name.
		 */
		@XmlElement(name = "severity")
		public String getSeverityName() {
			return severityName;
		}

		/**
		 * Sets the severity name.
		 * @param severityName the severity name.
		 */
		public void setSeverityName(String severityName) {
			this.severityName = severityName;
		}

	}

}
