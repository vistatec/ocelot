package com.vistatec.ocelot.its.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorCategoryAndSeverityMapper {

	public static final int ITS_MODE = 0;

	public static final int DQF_MODE = 1;

	private static ErrorCategoryAndSeverityMapper instance;

	private final Map<String, String> its_cat_2_dqf_cat;
	private final Map<String, String> dqf_cat_2_its_cat;

	private final Map<String, String> its_sev_2_dqf_sev;
	private final Map<String, String> dqf_sev_2_its_sev;

	private int activeMode;

	private ErrorCategoryAndSeverityMapper() {
		
		activeMode = ITS_MODE;
		
		its_cat_2_dqf_cat = new HashMap<>();
		its_cat_2_dqf_cat.put("addition", "Addition");
		its_cat_2_dqf_cat.put("omission", "Omission");
		its_cat_2_dqf_cat.put("mistranslation", "Mistranslation");
		its_cat_2_dqf_cat.put("untranslated", "Untranslated");
		its_cat_2_dqf_cat.put("typographical", "Punctuation");
		its_cat_2_dqf_cat.put("misspelling", "Spelling");
		its_cat_2_dqf_cat.put("grammar", "Grammar");
		its_cat_2_dqf_cat.put("register", "Grammatical register");
		its_cat_2_dqf_cat.put("inconsistency", "Inconsistency");
		its_cat_2_dqf_cat.put("characters", "Character encoding");
		its_cat_2_dqf_cat.put("terminology", "Terminology");
		its_cat_2_dqf_cat.put("style", "Style");
		its_cat_2_dqf_cat.put("length", "Length");
		its_cat_2_dqf_cat.put("formatting", "Local formatting");
		its_cat_2_dqf_cat.put("markup", "Markup");
		its_cat_2_dqf_cat.put("internationalization", "Locale convention");
		its_cat_2_dqf_cat.put("locale-specific-content", "Culture-specific reference");
		its_cat_2_dqf_cat.put("other", "Other");

		dqf_cat_2_its_cat = new HashMap<>();
		dqf_cat_2_its_cat.put("Addition", "addition");
		dqf_cat_2_its_cat.put("Omission", "omission");
		dqf_cat_2_its_cat.put("Mistranslation", "mistranslation");
		dqf_cat_2_its_cat.put("Untranslated", "untranslated");
		dqf_cat_2_its_cat.put("Punctuation", "typographical");
		dqf_cat_2_its_cat.put("Spelling", "misspelling");
		dqf_cat_2_its_cat.put("Grammar", "grammar");
		dqf_cat_2_its_cat.put("Grammatical register", "register");
		dqf_cat_2_its_cat.put("Inconsistency", "inconsistency");
		dqf_cat_2_its_cat.put("Character encoding", "characters");
		dqf_cat_2_its_cat.put("Terminology", "terminology");
		dqf_cat_2_its_cat.put("Style", "style");
		dqf_cat_2_its_cat.put("length", "Length");
		dqf_cat_2_its_cat.put("Local formatting", "formatting");
		dqf_cat_2_its_cat.put("markup", "Markup");
		dqf_cat_2_its_cat.put("Locale convention", "internationalization");
		dqf_cat_2_its_cat.put("Culture-specific reference", "locale-specific-content");
		dqf_cat_2_its_cat.put("Other", "other");

		its_sev_2_dqf_sev = new HashMap<>();
		its_sev_2_dqf_sev.put("Minor", "minor");
		its_sev_2_dqf_sev.put("Major", "major");
		its_sev_2_dqf_sev.put("Critical", "critical");

		dqf_sev_2_its_sev = new HashMap<>();
		dqf_sev_2_its_sev.put("minor", "Minor");
		dqf_sev_2_its_sev.put("major", "Major");
		dqf_sev_2_its_sev.put("critical", "Critical");
	}

	public static ErrorCategoryAndSeverityMapper getInstance() {
		if (instance == null) {
			instance = new ErrorCategoryAndSeverityMapper();
		}
		return instance;
	}

	public static void destroy() {
		instance = null;
	}

	public String getDQFCategoryName(String itsErrorCategoryName) {

		return its_cat_2_dqf_cat.get(itsErrorCategoryName);
	}

	public String getITSCategoryName(String categoryName) {
		if(activeMode == ITS_MODE){
			return categoryName;
		} else {
			return dqf_cat_2_its_cat.get(categoryName);
		}
	}

	public String getDQFSeverityName(String lqiSeverityName) {

		return its_sev_2_dqf_sev.get(lqiSeverityName);
	}

	public String getITSSeverityName(String dqfSeverityName) {
		return dqf_sev_2_its_sev.get(dqfSeverityName);
	}
	
	public void setMode(int mode){
		this.activeMode = mode;
	}
	
	public int getMode(){
		return activeMode;
	}
	
	public String getErrorCategory(String itsErrorCategory){
		
		String retCat = itsErrorCategory;
		if (activeMode == DQF_MODE){
			
			retCat = its_cat_2_dqf_cat.get(itsErrorCategory);
		}
		return retCat != null ? retCat : "";
	}
	
	public List<String> getErrorCategoryList(List<String> itsCategories){
		
		List<String> categories = new ArrayList<>();
		if(activeMode == ITS_MODE){
			categories.addAll(itsCategories);
		} else {
			String mappedCat = null;
			for(String itsCat: itsCategories){
				mappedCat = its_cat_2_dqf_cat.get(itsCat);
				categories.add(mappedCat != null ? mappedCat : "");
			}
		}
		return categories;
	}
	
	public List<String> getMappedDQFCategories(){
		return new ArrayList<>(dqf_cat_2_its_cat.keySet());
	}
	
}
