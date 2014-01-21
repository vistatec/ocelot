/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.log4j.Logger;

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
	public static class NumericMatcher implements DataCategoryField.Matcher {
                private static Logger LOG = Logger.getLogger(NumericMatcher.class);
		private double lowerBound = -1, upperBound = -1;

		@Override
		public boolean validatePattern(String pattern) {
                        Values numValues = null;
                        try {
                            numValues = getValues(pattern);
                        } catch (NumberFormatException e) {
                            LOG.error("Unaccepted Numeric Matcher Syntax: "+pattern, e);
                        }
			return (numValues != null);
		}

		@Override
		public void setPattern(String pattern) {
			Values v = getValues(pattern);
                        if (v == null) {
                            LOG.error(new IllegalArgumentException(
                                    "Unaccepted Numeric Matcher Syntax: "+pattern));
                        }
			lowerBound = v.min;
			upperBound = v.max;
		}

		private static final Pattern VALUE_PATTERN = 
				Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)\\s*-\\s*([0-9]+(?:\\.[0-9]+)?)$");
		private Values getValues(String pattern) throws NumberFormatException {
			pattern = pattern.trim();
			Matcher m = VALUE_PATTERN.matcher(pattern);
			if (!m.find()) {
				return null;
			}
			Values v = new Values();
                        v.min = Double.valueOf(m.group(1));
                        v.max = Double.valueOf(m.group(2));
			return v;
		}
		
		class Values {
			double min;
			double max;
		}
		
		@Override
		public boolean matches(Object value) {
			if (lowerBound == -1 || upperBound == -1) {
				throw new IllegalStateException("setPattern() was not called");
			}
                        if (value instanceof Integer) {
                            Integer v = (Integer) value;
                            return lowerBound == upperBound ? v == lowerBound :
                                    (v >= lowerBound && v < upperBound);
                        }
                        if (value instanceof Double) {
                            Double v = (Double) value;
                            return lowerBound == upperBound ? v == lowerBound :
                                    (v >= lowerBound && v < upperBound);
                        }
                        return false;
		}
		
		@Override
		public String toString() {
			return "NumericMatcher(" + lowerBound + ", " + upperBound + ")";
		}

	}
}
