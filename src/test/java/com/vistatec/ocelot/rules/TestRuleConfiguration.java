package com.vistatec.ocelot.rules;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;

public class TestRuleConfiguration {

    @Test
    public void testRuleEnablingAndRuleListener() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        config.addRuleConstaint("rule1", 
                new RuleMatcher(DataCategoryField.LQI_COMMENT, new NullMatcher()));
        config.addRuleConstaint("rule2", 
                new RuleMatcher(DataCategoryField.LQI_SEVERITY, new NullMatcher()));
        assertFalse(config.getRuleEnabled("rule1"));
        assertFalse(config.getRuleEnabled("rule2"));

        config.enableRule("rule1", true);
        assertTrue(config.getRuleEnabled("rule1"));
        assertFalse(config.getRuleEnabled("rule2"));
        assertTrue(listener.isEnabled("rule1"));
        assertFalse(listener.isEnabled("rule2"));

        config.enableRule("rule2", true);
        assertTrue(config.getRuleEnabled("rule1"));
        assertTrue(config.getRuleEnabled("rule2"));
        assertTrue(listener.isEnabled("rule1"));
        assertTrue(listener.isEnabled("rule2"));

        config.enableRule("rule1", false);
        config.enableRule("rule2", false);
        assertFalse(config.getRuleEnabled("rule1"));
        assertFalse(config.getRuleEnabled("rule2"));
        assertFalse(listener.isEnabled("rule1"));
        assertFalse(listener.isEnabled("rule2"));
    }
    
    // XXX Default behavior of RuleConfiguration is that
    // allSegments is true and allMetadataSegments is false
    @Test
    public void testSegmentToggles() {
        TestRuleListener listener = new TestRuleListener();
        RuleConfiguration config = new RuleConfiguration(listener);
        
        config.setAllSegments(false);
        assertFalse(listener.allSegmentsIsSet);
        config.setAllSegments(true);
        assertTrue(listener.allSegmentsIsSet);
        
        config.setMetadataSegments(true);
        assertTrue(listener.allMetadataSegmentsIsSet);
        config.setMetadataSegments(false);
        assertFalse(listener.allMetadataSegmentsIsSet);
    }
    
    class TestRuleListener implements RuleListener {
        Map<String, Boolean> enabledRules = new HashMap<String, Boolean>();
        boolean allSegmentsIsSet = false;
        boolean allMetadataSegmentsIsSet = false;
        
        boolean isEnabled(String ruleLabel) {
            return enabledRules.containsKey(ruleLabel) && enabledRules.get(ruleLabel);
        }
        
        @Override
        public void enabledRule(String ruleLabel, boolean enabled) {
            enabledRules.put(ruleLabel, enabled);            
        }

        @Override
        public void allSegments(boolean enabled) {
            allSegmentsIsSet = enabled;
        }

        @Override
        public void allMetadataSegments(boolean enabled) {
            allMetadataSegmentsIsSet = enabled;
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
