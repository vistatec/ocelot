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

import com.vistatec.ocelot.rules.Matchers;
import com.vistatec.ocelot.rules.RuleMatcher;
import com.vistatec.ocelot.rules.RuleFilter;
import com.vistatec.ocelot.rules.DataCategoryField;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.OtherITSMetadata;
import com.vistatec.ocelot.rules.DataCategoryField.Matcher;
import com.vistatec.ocelot.segment.Segment;

import static org.junit.Assert.*;

public class TestRules {

    @Test
    public void testMtConfidence() throws Exception {
        List<RuleMatcher> ruleMatchers = new ArrayList<RuleMatcher>();
        // Look for MT confidence of 75 and below
        ruleMatchers.add(new RuleMatcher(DataCategoryField.MT_CONFIDENCE, numericMatcher(0, 75)));
        RuleFilter filter = new RuleFilter(ruleMatchers);
        
        Segment segment = new Segment(1, 1, 1, null, null, null);
        segment.addOtherITSMetadata(new OtherITSMetadata(DataCategoryField.MT_CONFIDENCE, new Double(50)));
        assertTrue(filter.matches(segment));
        
        segment = new Segment(1, 1, 1, null, null, null);
        segment.addOtherITSMetadata(new OtherITSMetadata(DataCategoryField.MT_CONFIDENCE, new Double(80)));
        assertFalse(filter.matches(segment));
    }
    
	@Test
	public void testLQIMatching() throws Exception {
		List<RuleMatcher> ruleMatchers = new ArrayList<RuleMatcher>();
		// look for omissions with severity 85 and up
		ruleMatchers.add(new RuleMatcher(DataCategoryField.LQI_TYPE, regexMatcher("omission")));
		ruleMatchers.add(new RuleMatcher(DataCategoryField.LQI_SEVERITY, numericMatcher(85, 100)));
		
		RuleFilter filter = new RuleFilter(ruleMatchers);
		
		// This one should match
		LanguageQualityIssue lqi1 = new LanguageQualityIssue();
		lqi1.setSeverity(85);
		lqi1.setType("omission");

		// This one should not match - incorrect type
		LanguageQualityIssue lqi2 = new LanguageQualityIssue();
		lqi2.setSeverity(85);
		lqi2.setType("terminology");
		
		// This one should not match - incorrect severity
		LanguageQualityIssue lqi3 = new LanguageQualityIssue();
		lqi3.setSeverity(60);
		lqi3.setType("omission");
		
		Segment segment = new Segment(1, 1, 1, null, null, null);
		segment.addLQI(lqi1);
		segment.addLQI(lqi2);
		segment.addLQI(lqi3);
		assertTrue(filter.matches(segment));
		
		segment = new Segment(2, 2, 2, null, null, null);
		segment.addLQI(lqi1);
		assertTrue(filter.matches(segment));
		
		segment = new Segment(3, 3, 3, null, null, null);
		segment.addLQI(lqi2);
		assertFalse(filter.matches(segment));

		segment = new Segment(4, 4, 4, null, null, null);
		segment.addLQI(lqi3);
		assertFalse(filter.matches(segment));
		
		segment = new Segment(5, 5, 5, null, null, null);
		segment.addLQI(lqi1);
		segment.addLQI(lqi2);
		assertTrue(filter.matches(segment));

		// Tricky!  Make sure we don't get a false positive
		// because we have an omission AND a valid severity!
		// (We do have each, but not on the same issue.)
		segment = new Segment(6, 6, 6, null, null, null);
		segment.addLQI(lqi2);
		segment.addLQI(lqi3);
		assertFalse(filter.matches(segment));
	}
	
	private Matcher regexMatcher(String regex) {
		Matcher m = new Matchers.RegexMatcher();
		assertTrue(m.validatePattern(regex));
		m.setPattern(regex);
		return m;
	}
	
	private Matcher numericMatcher(int min, int max) {
		Matcher m = new Matchers.NumericMatcher();
		String s = "" + min + "-" + max; // Hacky.....
		assertTrue(m.validatePattern(s));
		m.setPattern(s);
		return m;
	}
}
