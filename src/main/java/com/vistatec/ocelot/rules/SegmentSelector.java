package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;
import com.vistatec.ocelot.segment.Segment;

/**
 * Class that uses a {@link RuleConfiguration} to decide whether
 * segment entries should be displayed or not.
 */
public class SegmentSelector {
    private RuleConfiguration ruleConfig;
    
    public SegmentSelector(RuleConfiguration ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
    
    public boolean matches(Segment s) {
        if (ruleConfig.getFilterMode() == FilterMode.ALL &&
            ruleConfig.getStateQualifierMode() == StateQualifierMode.ALL) { 
            return true; 
        }

        if (ruleConfig.getStateQualifierMode() == StateQualifierMode.SELECTED_STATES &&
            (s.getStateQualifier() == null || 
             !ruleConfig.getStateQualifierEnabled(s.getStateQualifier()))) {
            return false;
        }
        switch (ruleConfig.getFilterMode()) {
        case ALL:
            return true;
        case ALL_WITH_METADATA:
            return s.getAllITSMetadata().size() > 0;
        case SELECTED_SEGMENTS:
            for (Rule r : ruleConfig.getRules()) {
                if (r.getEnabled() && r.matches(s)) {
                    return true;
                }
            }
            break;
        }
        return false;
    }
}
