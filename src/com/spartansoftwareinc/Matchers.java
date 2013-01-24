package com.spartansoftwareinc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Matchers {

	public static class RegexMatcher implements DataCategoryField.Matcher {
		private Pattern pattern;
		
		@Override
		public boolean validatePattern(String s) {
			return (getPattern(s) != null);
		}
		
		@Override
		public void setPattern(String s) {
			pattern = getPattern(s);
		}

		private Pattern getPattern(String s) {
			try {
				return Pattern.compile(s);
			}
			catch (PatternSyntaxException e) {
				return null;
			}
		}
		
		@Override
		public boolean matches(Object value) {
			if (pattern == null) {
				throw new IllegalStateException("setPattern() was not called");
			}
			if (!(value instanceof String)) return false;
			String s = (String)value;
			return pattern.matcher(s).matches();
		}
		
		@Override
		public String toString() {
			return "RegexMatcher(" + pattern + ")";
		}
	}
	
	// Matches bounded ranges within 0-100
	// Handles syntax like:
	// 		[min]-[max]
	// Bounds are inclusive.
	// For now, only handles integer values.  This is a bug - it needs
	// to handle decimals
	public static class NumericMatcher implements DataCategoryField.Matcher {
		private int lowerBound = -1, upperBound = -1;

		@Override
		public boolean validatePattern(String pattern) {
			return (getValues(pattern) != null);
		}

		@Override
		public void setPattern(String pattern) {
			Values v = getValues(pattern);
			lowerBound = v.min;
			upperBound = v.max;
		}

		private static final Pattern VALUE_PATTERN = 
				Pattern.compile("^(\\d+)\\s*-\\s*(\\d+)$");
		private Values getValues(String pattern) {
			pattern = pattern.trim();
			Matcher m = VALUE_PATTERN.matcher(pattern);
			if (!m.find()) {
				return null;
			}
			Values v = new Values();
			try {
				v.min = Integer.valueOf(m.group(1));
				v.max = Integer.valueOf(m.group(2));
			}
			catch (NumberFormatException e) {
				return null;
			}
			return v;
		}
		
		class Values {
			int min;
			int max;
		}
		
		@Override
		public boolean matches(Object value) {
			if (lowerBound == -1 || upperBound == -1) {
				throw new IllegalStateException("setPattern() was not called");
			}
			if (!(value instanceof Integer)) return false;
			Integer v = (Integer)value;
			return (v >= lowerBound && v <= upperBound);
		}
		
		@Override
		public String toString() {
			return "NumericMatcher(" + lowerBound + ", " + upperBound + ")";
		}

	}
}
