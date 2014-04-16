package com.vistatec.ocelot.rules;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;

import static org.junit.Assert.*;
import static com.vistatec.ocelot.rules.StateQualifier.*;

public class TestRuleConfiguration {

    @Test
    public void testRuleEnablingAndRuleListener() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        config.addRuleConstaint("rule1", 
                new RuleMatcher(DataCategoryField.LQI_COMMENT, new NullMatcher()));
        config.addRuleConstaint("rule2", 
                new RuleMatcher(DataCategoryField.LQI_SEVERITY, new NullMatcher()));
        assertFalse(config.getRule("rule1").getEnabled());
        assertFalse(config.getRule("rule2").getEnabled());

        Rule rule1 = config.getRule("rule1");
        Rule rule2 = config.getRule("rule2");
        config.enableRule(rule1, true);
        assertTrue(rule1.getEnabled());
        assertFalse(rule2.getEnabled());
        assertTrue(listener.isEnabled("rule1"));
        assertFalse(listener.isEnabled("rule2"));

        config.enableRule(rule2, true);
        assertTrue(rule1.getEnabled());
        assertTrue(rule2.getEnabled());
        assertTrue(listener.isEnabled("rule1"));
        assertTrue(listener.isEnabled("rule2"));

        config.enableRule(rule1, false);
        config.enableRule(rule2, false);
        assertFalse(rule1.getEnabled());
        assertFalse(rule2.getEnabled());
        assertFalse(listener.isEnabled("rule1"));
        assertFalse(listener.isEnabled("rule2"));
    }
    
    @Test
    public void testStateQualifierRuleListener() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        
        // Verify initial state
        assertFalse(config.getStateQualifierEnabled(EXACT));
        assertFalse(config.getStateQualifierEnabled(FUZZY));
        assertFalse(config.getStateQualifierEnabled(ID));
        assertFalse(config.getStateQualifierEnabled(MT));

        // Verify that setting them generates the appropriate rule 
        // listener events
        config.setStateQualifierEnabled(EXACT, true);
        config.setStateQualifierEnabled(FUZZY, true);
        config.setStateQualifierEnabled(ID, true);
        config.setStateQualifierEnabled(MT, true);
        assertTrue(listener.enabledRules.get(EXACT.getName()));
        assertTrue(listener.enabledRules.get(FUZZY.getName()));
        assertTrue(listener.enabledRules.get(ID.getName()));
        assertTrue(listener.enabledRules.get(MT.getName()));

        // Verify that we don't notify the listener if the
        // state doesn't change
        listener.enabledRules.clear();
        config.setStateQualifierEnabled(EXACT, true);
        config.setStateQualifierEnabled(FUZZY, true);
        config.setStateQualifierEnabled(ID, true);
        config.setStateQualifierEnabled(MT, true);
        assertEquals(0, listener.enabledRules.size());
    }
    
    @Test
    public void testFilterModeListener() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        
        config.setFilterMode(FilterMode.ALL);
        assertEquals(FilterMode.ALL, listener.mode);
        config.setFilterMode(FilterMode.ALL_WITH_METADATA);
        assertEquals(FilterMode.ALL_WITH_METADATA, listener.mode);
        config.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        assertEquals(FilterMode.SELECTED_SEGMENTS, listener.mode);
    }
    
    class TestRuleListener implements RuleListener {
        Map<String, Boolean> enabledRules = new HashMap<String, Boolean>();
        RuleConfiguration.FilterMode mode;
        
        boolean isEnabled(String ruleLabel) {
            return enabledRules.containsKey(ruleLabel) && enabledRules.get(ruleLabel);
        }
        
        @Override
        public void enabledRule(String ruleLabel, boolean enabled) {
            enabledRules.put(ruleLabel, enabled);            
        }

        @Override
        public void setFilterMode(RuleConfiguration.FilterMode mode) {
            this.mode = mode;
        }
    }
    
    class NullMatcher implements DataCategoryField.Matcher {
        @Override
        public boolean validatePattern(String pattern) {
            return false;
        }
        @Override
        public void setPattern(String pattern) {
        }
        @Override
        public boolean matches(Object value) {
            return false;
        }
    }
}
