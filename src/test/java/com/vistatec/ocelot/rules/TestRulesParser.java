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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static com.vistatec.ocelot.rules.RulesTestHelpers.lqi;

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

    @Test
    public void testLQIRules() throws Exception {
        RuleConfiguration config = getConfig("/lqi.properties");
        assertEquals(2, config.getRules().size());
        List<Rule> rules = new ArrayList<Rule>();
        Rule r = new Rule();
        r.setLabel("any_50_100");
        r.setFlag(getDataCategoryFlag("*", "#00ff00", "#0000ff"));
        r.addRuleMatcher(new RuleMatcher(DataCategoryField.LQI_SEVERITY, Matchers.numeric(50, 100)));
        rules.add(r);
        r = new Rule();
        r.setLabel("noncon_90_100");
        r.setFlag(getDataCategoryFlag("*", "#ff0000", "#00ff00"));
        r.addRuleMatcher(new RuleMatcher(DataCategoryField.LQI_TYPE, Matchers.regex("non-conformance")));
        r.addRuleMatcher(new RuleMatcher(DataCategoryField.LQI_SEVERITY, Matchers.numeric(90, 100)));
        rules.add(r);
        assertEquals(rules, config.getRules());
    }

    @Test
    public void testStateQualifierRules() throws Exception {
        RuleConfiguration config = getConfig("/statequals.properties");
        assertEquals(0, config.getRules().size());
        assertEquals(new Color(Integer.decode("#f0f0f0")), config.getStateQualifierColor(StateQualifier.MT));
        assertEquals(new Color(Integer.decode("#ff0000")), config.getStateQualifierColor(StateQualifier.EXACT));
        assertEquals(new Color(Integer.decode("#00ff00")), config.getStateQualifierColor(StateQualifier.ID));
        assertEquals(new Color(Integer.decode("#0000ff")), config.getStateQualifierColor(StateQualifier.FUZZY));
    }

    private DataCategoryFlag getDataCategoryFlag(String text, String fill, String border) {
        DataCategoryFlag flag = new DataCategoryFlag();
        flag.setText(text);
        flag.setFill(new Color(Integer.decode(fill)));
        flag.setBorderColor(new Color(Integer.decode(border)));
        return flag;
    }

    private RuleConfiguration getConfig(String resource) throws IOException, IllegalAccessException, InstantiationException {
        RulesParser parser = new RulesParser();
        return parser.parse(new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(resource), "UTF-8")));
    }
}
