package com.vistatec.ocelot.lqi.constants;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory.LQIShortCut;
import com.vistatec.ocelot.lqi.model.LQIGrid;

/**
 * LQI Constants class.
 */
public abstract class LQIConstants {

	/** Default minor score constant. */
	public final static int DEFAULT_MINOR_SCORE = 10;

	/** Default serious score constant. */
	public final static int DEFAULT_SERIOUS_SCORE = 50;

	/** Default critical score constant. */
	public final static int DEFAULT_CRITICAL_SCORE = 100;

	/** Minor severity name constant. */
	public final static String MINOR_SEVERITY_NAME = "Minor";

	/** Serious severity name constant. */
	public final static String SERIOUS_SEVERITY_NAME = "Serious";

	/** Critical severity name constant. */
	public final static String CRITICAL_SEVERITY_NAME = "Critical";

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
		lqiGrid.setCriticalScore(DEFAULT_CRITICAL_SCORE);
		lqiGrid.setMinorScore(DEFAULT_MINOR_SCORE);
		lqiGrid.setSeriousScore(DEFAULT_SERIOUS_SCORE);
		List<LQIErrorCategory> errorCategories = new ArrayList<LQIErrorCategory>();
		for (String catName : DEFAULT_CATEGORIES_NAMES) {
			errorCategories.add(new LQIErrorCategory(catName));
		}
		lqiGrid.setErrorCategories(errorCategories);
//		// TODO DELETE, JUST FOR TEST PURPOSE
//		lqiGrid.getErrorCategories()
//		        .get(0)
//		        .setMinorShortcut(
//		                new LQIShortCut(KeyEvent.VK_G,
//		                        new int[] { KeyEvent.CTRL_DOWN_MASK }));
//		//
		return lqiGrid;
	}
}
