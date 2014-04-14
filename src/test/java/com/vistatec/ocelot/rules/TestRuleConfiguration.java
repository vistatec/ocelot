package com.vistatec.ocelot.rules;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

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

        config.enableRule("rule1", true);
        assertTrue(config.getRule("rule1").getEnabled());
        assertFalse(config.getRule("rule2").getEnabled());
        assertTrue(listener.isEnabled("rule1"));
        assertFalse(listener.isEnabled("rule2"));

        config.enableRule("rule2", true);
        assertTrue(config.getRule("rule1").getEnabled());
        assertTrue(config.getRule("rule2").getEnabled());
        assertTrue(listener.isEnabled("rule1"));
        assertTrue(listener.isEnabled("rule2"));

        config.enableRule("rule1", false);
        config.enableRule("rule2", false);
        assertFalse(config.getRule("rule1").getEnabled());
        assertFalse(config.getRule("rule2").getEnabled());
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
