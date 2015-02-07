package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

public class LQIModificationEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;
    private final Segment segment;

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
