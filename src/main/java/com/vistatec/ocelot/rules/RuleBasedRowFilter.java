package com.vistatec.ocelot.rules;

import javax.swing.RowFilter;

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
            StateQualifier sq = StateQualifier.get(s.getStateQualifier());
            if (ruleConfig.getStateQualifierEnabled(sq)) {
                return true;
            }
            for (Rule r : ruleConfig.getRules().values()) {
                if (r.getEnabled() && r.matches(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
