package com.vistatec.ocelot.lqi.constants;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQISeverity;

/**
 * LQI Constants class.
 */
public abstract class LQIConstants {

	/** Default minor score constant. */
	public final static double DEFAULT_MINOR_SCORE = 1.0;

	/** Default serious score constant. */
	public final static double DEFAULT_MAJOR_SCORE = 2.0;

	/** Default critical score constant. */
	public final static double DEFAULT_CRITICAL_SCORE = 4.0;
	
	/** The default minor severity name. */
	public final static String DEFAULT_MINOR_SEV_NAME = "Minor";
	
	/** The default major severity name. */
	public final static String DEFAULT_MAJOR_SEV_NAME = "Major";
	
	/** The default critical severity name. */
	public final static String DEFAULT_CRITICAL_SEV_NAME = "Critical";

	/** Default category names. */
	public final static String[] DEFAULT_CATEGORIES_NAMES = { "terminology",
	        "style", "inconsistent-entities", "duplication",
	        "internationalization", "whitespace", "omission", "mistranslation" };

	/** Complete list of LQI categories. */
	public final static String[] LQI_CATEGORIES_LIST = { "terminology",
	        "mistranslation", "omission", "untranslated", "addition",
	        "duplication", "inconsistency", "grammar", "legal", "register",
	        "locale-specific-content", "locale-violation", "style",
	        "characters", "misspelling", "typographical", "formatting",
	        "inconsistent-entities", "numbers", "markup", "pattern-problem",
	        "whitespace", "internationalization", "length", "non-conformance",
	        "uncategorized", "other" };

	/**
	 * Gets the default LQI grid configuration.
	 * 
	 * @return the default LQI grid configuration.
	 */
	public static LQIGrid getDefaultLQIGrid() {

		LQIGrid lqiGrid = new LQIGrid();
		List<LQISeverity> severities = new ArrayList<LQISeverity>();
		severities.add(new LQISeverity(DEFAULT_MINOR_SEV_NAME, DEFAULT_MINOR_SCORE));
		severities.add(new LQISeverity(DEFAULT_MAJOR_SEV_NAME, DEFAULT_MAJOR_SCORE));
		severities.add(new LQISeverity(DEFAULT_CRITICAL_SEV_NAME, DEFAULT_CRITICAL_SCORE));
		lqiGrid.setSeverities(severities);
		List<LQIErrorCategory> errorCategories = new ArrayList<LQIErrorCategory>();
		for (String catName : DEFAULT_CATEGORIES_NAMES) {
			errorCategories.add(new LQIErrorCategory(catName));
		}
		lqiGrid.setErrorCategories(errorCategories);
		return lqiGrid;
	}
}
