package com.spartansoftwareinc;

public enum DataCategoryField {
	LQI_TYPE("locQualityIssueType", Matchers.RegexMatcher.class),
	LQI_COMMENT("locQualityIssueComment", Matchers.RegexMatcher.class),
	LQI_SEVERITY("locQualityIssueSeverity", Matchers.NumericMatcher.class),
        PROV_ORG("org", Matchers.RegexMatcher.class),
        PROV_PERSON("person", Matchers.RegexMatcher.class),
        PROV_TOOL("tool", Matchers.RegexMatcher.class),
        PROV_REVORG("revOrg", Matchers.RegexMatcher.class),
        PROV_REVPERSON("revPerson", Matchers.RegexMatcher.class),
        PROV_REVTOOL("revTool", Matchers.RegexMatcher.class),
        PROV_PROVREF("provRef", Matchers.RegexMatcher.class);
	
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
