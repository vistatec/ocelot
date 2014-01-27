package com.vistatec.ocelot.rules;

import java.util.HashMap;

import javax.swing.RowFilter;

import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifier;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentTableModel;

/**
 * RowFilter that uses a {@link RuleConfiguration} to filter out entries
 * from the segment table in {@link SegmentView}.
 */
public class RuleBasedRowFilter extends RowFilter<SegmentTableModel, Integer> {

    private RuleConfiguration ruleConfig;
    
    public RuleBasedRowFilter(RuleConfiguration ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
    
    @Override
    public boolean include(Entry<? extends SegmentTableModel, ? extends Integer> entry) {
        if (ruleConfig.getAllSegments()) { 
            return true; 
        }

        SegmentTableModel model = entry.getModel();
        Segment s = model.getSegment(entry.getIdentifier());
        if (ruleConfig.getAllMetadataSegments()) {
            return s.getAllITSMetadata().size() > 0;
        } else {
            HashMap<StateQualifier, Boolean> stateQualifierRules = 
                    ruleConfig.getStateQualifierRules();
            for (StateQualifier sq : stateQualifierRules.keySet()) {
                if (stateQualifierRules.get(sq) && sq.getName().equals(
                    s.getStateQualifier())) {
                    return true;
                }
            }
            for (RuleFilter r : ruleConfig.getRules().values()) {
                if (r.getEnabled() && r.matches(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
