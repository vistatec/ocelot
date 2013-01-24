package com.spartansoftwareinc;

public enum DataCategoryField {
	LQI_TYPE("locQualityIssueType", Matchers.RegexMatcher.class),
	LQI_COMMENT("locQualityIssueComment", Matchers.RegexMatcher.class),
	LQI_SEVERITY("locQualityIssueType", Matchers.NumericMatcher.class);
	
	private String name;
	private Class<? extends Matcher> clazz;
	
	DataCategoryField(String name, Class<? extends Matcher> clazz) {
		this.name = name;
		this.clazz = clazz;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<? extends Matcher> getMatcherClass() {
		return clazz;
	}
	
	public static DataCategoryField byName(String s) {
		for (DataCategoryField f : values()) {
			if (f.name.equals(s)) {
				return f;
			}
		}
		return null;
	}
	
	public interface Matcher {
		boolean validatePattern(String pattern);
		void setPattern(String pattern);
		boolean matches(Object value);
	}
}
