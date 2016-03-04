package com.vistatec.ocelot.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;

import static com.vistatec.ocelot.rules.RulesTestHelpers.lqi;
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
        assertEquals(FilterMode.ALL, listener.filterMode);
        config.setFilterMode(FilterMode.ALL_WITH_METADATA);
        assertEquals(FilterMode.ALL_WITH_METADATA, listener.filterMode);
        config.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        assertEquals(FilterMode.SELECTED_SEGMENTS, listener.filterMode);
    }

    @Test
    public void testStateQualifierListener() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        
        config.setStateQualifierMode(StateQualifierMode.ALL);
        assertEquals(StateQualifierMode.ALL, listener.stateQualifierMode);
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        assertEquals(StateQualifierMode.SELECTED_STATES, listener.stateQualifierMode);
    }

    @Test
    public void testGetFlagForMetadata() {
        RuleConfiguration config = new RuleConfiguration(new TestRuleListener());
        DataCategoryFlag flag1 = new DataCategoryFlag();
        DataCategoryFlag flag2 = new DataCategoryFlag();
        config.addRule(createRule(new RuleMatcher(DataCategoryField.LQI_SEVERITY, Matchers.numeric(80, 100)), flag1));
        config.addRule(createRule(new RuleMatcher(DataCategoryField.LQI_SEVERITY, Matchers.numeric(90, 100)), flag2));
        assertEquals(flag1, config.getFlagForMetadata(lqi("omission", 85)));
        assertEquals(flag2, config.getFlagForMetadata(lqi("omission", 95)));
        assertEquals(null, config.getFlagForMetadata(lqi("omission", 50)));
    }

    private Rule createRule(RuleMatcher matcher, DataCategoryFlag flag) {
        Rule r = new Rule();
        r.addRuleMatcher(matcher);
        r.setFlag(flag);
        return r;
    }

    class TestRuleListener implements RuleListener {
        Map<String, Boolean> enabledRules = new HashMap<String, Boolean>();
        RuleConfiguration.FilterMode filterMode;
        RuleConfiguration.StateQualifierMode stateQualifierMode;
        
        
        boolean isEnabled(String ruleLabel) {
            return enabledRules.containsKey(ruleLabel) && enabledRules.get(ruleLabel);
        }
        
        @Override
        public void enabledRule(String ruleLabel, boolean enabled) {
            enabledRules.put(ruleLabel, enabled);            
        }

        @Override
        public void setFilterMode(RuleConfiguration.FilterMode mode) {
            this.filterMode = mode;
        }

        @Override
        public void setStateQualifierMode(StateQualifierMode mode) {
            this.stateQualifierMode = mode;
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
