package com.spartansoftwareinc;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static com.spartansoftwareinc.DataCategoryField.*;

public class TestRules {

	@Test
	public void testLQIMatching() throws Exception {
		List<RuleMatcher> ruleMatchers = new ArrayList<RuleMatcher>();
		// look for omissions with severity 85 and up
		ruleMatchers.add(new RuleMatcher(LQI_TYPE, regexMatcher("omission")));
		ruleMatchers.add(new RuleMatcher(LQI_SEVERITY, numericMatcher(85, 100)));
		
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
		
		Segment segment = new Segment(1, "source", "target");
		segment.addLQI(lqi1);
		segment.addLQI(lqi2);
		segment.addLQI(lqi3);
		assertTrue(filter.matches(segment));
		
		segment = new Segment(2, "source", "target");
		segment.addLQI(lqi1);
		assertTrue(filter.matches(segment));
		
		segment = new Segment(3, "source", "target");
		segment.addLQI(lqi2);
		assertFalse(filter.matches(segment));

		segment = new Segment(4, "source", "target");
		segment.addLQI(lqi3);
		assertFalse(filter.matches(segment));
		
		segment = new Segment(6, "source", "target");
		segment.addLQI(lqi1);
		segment.addLQI(lqi2);
		assertTrue(filter.matches(segment));

		// Tricky!  Make sure we don't get a false positive
		// because we have an omission AND a valid severity!
		// (We do have each, but not on the same issue.)
		segment = new Segment(7, "source", "target");
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
