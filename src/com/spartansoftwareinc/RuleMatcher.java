package com.spartansoftwareinc;

/**
 * A rule element consisting of a field and an instance of a configured matcher 
 * appropriate for that field.
 */
public class RuleMatcher {

	private DataCategoryField field;
	private DataCategoryField.Matcher matcher;
	
	public RuleMatcher(DataCategoryField field, DataCategoryField.Matcher matcher) {
		this.field = field;
		this.matcher = matcher;
	}
		
	// Returns the field name that this matches against
	public DataCategoryField getField() {
		return field;
	}
	
	public boolean matches(Object o) {
		return matcher.matches(o);
	}

	@Override
	public String toString() {
		return field.toString() + "=>" + matcher;
	}
}
