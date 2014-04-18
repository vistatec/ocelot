package com.vistatec.ocelot.rules;

import javax.swing.RowFilter;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;
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
        if (ruleConfig.getFilterMode() == FilterMode.ALL &&
            ruleConfig.getStateQualifierMode() == StateQualifierMode.ALL) { 
            return true; 
        }

        SegmentTableModel model = entry.getModel();
        Segment s = model.getSegment(entry.getIdentifier());
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
