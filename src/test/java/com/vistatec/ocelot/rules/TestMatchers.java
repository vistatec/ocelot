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

import org.junit.*;

import com.vistatec.ocelot.rules.DataCategoryField.Matcher;

import static org.junit.Assert.*;

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
		for (int i = 60; i < 79; i++) {
			assertTrue("Failed to match: " + i, m.matches(i));
		}
		assertFalse(m.matches(59));
		assertFalse(m.matches(80));
		assertFalse(m.matches(100));
		assertFalse(m.matches(Integer.MAX_VALUE));
	}

	@Test
	public void testRegexMatcherEquality() {
	    Matcher m1 = Matchers.regex("test"),
	            m2 = Matchers.regex("test");
	    assertEquals(m1, m2);
	}

	@Test
	public void testNumericMatcher() {
	    Matcher m1 = Matchers.numeric(50, 60),
                m2 = Matchers.numeric(50, 60);
        assertEquals(m1, m2);
	}
}
