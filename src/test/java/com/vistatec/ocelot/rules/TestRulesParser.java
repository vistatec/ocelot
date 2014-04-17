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
