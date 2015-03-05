package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIEditEvent extends LQIEvent {
    private final OcelotSegment segment;
    private final LanguageQualityIssue segmentLQI;

    public LQIEditEvent(LanguageQualityIssue editedLQI, OcelotSegment segment,
            LanguageQualityIssue segmentLQI) {
        super(editedLQI);
        this.segment = segment;
        this.segmentLQI = segmentLQI;
    }

    public OcelotSegment getSegment() {
        return this.segment;
    }

    public LanguageQualityIssue getSegmentLQI() {
        return segmentLQI;
    }
}
