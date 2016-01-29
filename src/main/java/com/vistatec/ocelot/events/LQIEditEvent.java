package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIEditEvent extends LQIEvent {
    private final OcelotSegment segment;
    private final LanguageQualityIssue segmentLQI;
    private final LanguageQualityIssue oldLQI;

    public LQIEditEvent(LanguageQualityIssue editedLQI, LanguageQualityIssue oldLQI, OcelotSegment segment,
            LanguageQualityIssue segmentLQI) {
        super(editedLQI);
        this.segment = segment;
        this.segmentLQI = segmentLQI;
        this.oldLQI = oldLQI;
    }

    public OcelotSegment getSegment() {
        return this.segment;
    }

    public LanguageQualityIssue getSegmentLQI() {
        return segmentLQI;
    }
    
    public LanguageQualityIssue getOldLQI(){
    	return oldLQI;
    }
}
