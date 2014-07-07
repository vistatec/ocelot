package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

public class LQIModificationEvent {
    private LanguageQualityIssue lqi;
    private Segment segment;

    public LQIModificationEvent(LanguageQualityIssue lqi, Segment segment) {
        this.lqi = lqi;
        this.segment = segment;
    }

    public Segment getSegment() {
        return segment;
    }

    public LanguageQualityIssue getLQI() {
        return lqi;
    }
}
