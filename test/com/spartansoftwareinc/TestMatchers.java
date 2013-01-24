package com.spartansoftwareinc;

import org.junit.*;
import static org.junit.Assert.*;

import static com.spartansoftwareinc.DataCategoryField.Matcher;

public class TestMatchers {
	
	@Test
	public void testRegexMatcher() {
		Matcher m = new Matchers.RegexMatcher();
		assertTrue(m.validatePattern("a|b|c"));
		assertFalse(m.validatePattern("\\"));
		m.setPattern("a|b|c");
		assertTrue(m.matches("a"));
		assertTrue(m.matches("b"));
		assertTrue(m.matches("c"));
		assertFalse(m.matches("d"));
		assertFalse(m.matches("ab"));
	}
	
	@Test
	public void testRationalMatcher() {
		Matcher m = new Matchers.NumericMatcher();
		assertTrue(m.validatePattern("0-100"));
		assertTrue(m.validatePattern(" 50   - 75 "));
		assertFalse(m.validatePattern("abc"));
		m.setPattern("60-79");
		for (int i = 60; i < 80; i++) {
			assertTrue("Failed to match: " + i, m.matches(i));
		}
		assertFalse(m.matches(59));
		assertFalse(m.matches(80));
		assertFalse(m.matches(100));
		assertFalse(m.matches(Integer.MAX_VALUE));
	}
}
