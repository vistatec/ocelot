package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

public class LQIEditEvent extends LQIEvent {
    private final Segment segment;
    private final LanguageQualityIssue segmentLQI;

    public LQIEditEvent(LanguageQualityIssue editedLQI, Segment segment,
            LanguageQualityIssue segmentLQI) {
        super(editedLQI);
        this.segment = segment;
        this.segmentLQI = segmentLQI;
    }

    public Segment getSegment() {
        return this.segment;
    }

    public LanguageQualityIssue getSegmentLQI() {
        return segmentLQI;
    }
}
