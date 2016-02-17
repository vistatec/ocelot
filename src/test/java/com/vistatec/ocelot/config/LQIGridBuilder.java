package com.vistatec.ocelot.config;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

public class LQIGridBuilder {

	public static LQIGrid buildLqiGrid(){
		
		LQIGrid grid = new LQIGrid();
		
		//severities
		List<LQISeverity> severities = new ArrayList<LQISeverity>();
		severities.add(new LQISeverity("Minor", 1.0));
		severities.add(new LQISeverity("Major", 2.0));
		severities.add(new LQISeverity("Critical", 4.0));
		grid.setSeverities(severities);
		
		List<LQIErrorCategory> categories = new ArrayList<LQIErrorCategory>();
		LQIErrorCategory errCat = new LQIErrorCategory("terminology");
		errCat.setWeight(20.0f);
		List<LQIShortCut> shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(0), 127, ""));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);
		
		errCat = new LQIErrorCategory("duplication");
		errCat.setWeight(25.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(1), 54, "Ctrl+Alt"));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);
		
		errCat = new LQIErrorCategory("mistranslation");
		errCat.setWeight(30.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(2), 68, "Ctrl+Shift"));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);
		
		errCat = new LQIErrorCategory("duplication");
		errCat.setWeight(30.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(0), 66, "Ctrl+Alt+Shift"));
		shortcuts.add(new LQIShortCut(severities.get(1), 54, "Alt+Shift"));
		shortcuts.add(new LQIShortCut(severities.get(2), 68, ""));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);
		
		errCat = new LQIErrorCategory("omission");
		errCat.setWeight(15.0f);
		categories.add(errCat);
		
		errCat = new LQIErrorCategory("mistranslation");
		errCat.setWeight(35.0f);
		categories.add(errCat);
		
		grid.setErrorCategories(categories);
		
		return grid;
	}
}
