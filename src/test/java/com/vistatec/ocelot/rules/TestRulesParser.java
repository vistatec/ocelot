/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.*;

import static org.junit.Assert.*;

public class TestRulesParser {
    @Test
    public void testIgnoreRulesWithNoMatchers() throws Exception {
        RuleConfiguration config = getConfig("/no-matchers.properties");
        assertEquals(0, config.getRules().size());
        assertNull(config.getRule("myrule"));
    }
    
    @Test
    public void testProvToolMatcher() throws Exception {
        RuleConfiguration config = getConfig("/tool.properties");
        assertEquals(1, config.getRules().size());
        Rule r = config.getRules().get(0);
        assertEquals(1, r.matchers.size());
        assertEquals(new RuleMatcher(DataCategoryField.PROV_TOOL, Matchers.regex("Tool")), r.matchers.get(0));
    }

    private RuleConfiguration getConfig(String resource) throws IOException, IllegalAccessException, InstantiationException {
        RulesParser parser = new RulesParser();
        return parser.parse(new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(resource), "UTF-8")));
    }
}
