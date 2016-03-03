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
    PROV_PROVREF("provRef", Matchers.RegexMatcher.class),
    MT_CONFIDENCE("mtConfidence", Matchers.NumericMatcher.class),
    TA_IDENT_REF("taIdentRef", Matchers.RegexMatcher.class),
    TA_CLASS_REF("taClassRef", Matchers.RegexMatcher.class),
    TA_CONFIDENCE("taConfidence", Matchers.RegexMatcher.class),
    TA_ENTITY("taEntity", Matchers.RegexMatcher.class),
    TERM("term", Matchers.RegexMatcher.class),
    TERM_SOURCE("termSource", Matchers.RegexMatcher.class),
    TERM_TARGET("termTarget", Matchers.RegexMatcher.class),
    TERM_SENSE("termSense", Matchers.RegexMatcher.class),
    TERM_CONFIDENCE("termConfidence", Matchers.RegexMatcher.class),
    ANNOTATORS_REF("annotatorsRef", Matchers.RegexMatcher.class);
	
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
