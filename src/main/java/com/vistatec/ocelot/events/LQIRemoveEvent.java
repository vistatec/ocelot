package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.OcelotSegment;

public class LQIRemoveEvent extends LQIEvent {
    private final OcelotSegment segment;

    public LQIRemoveEvent(LanguageQualityIssue lqi, OcelotSegment segment) {
        super(lqi);
        this.segment = segment;
    }

    public OcelotSegment getSegment() {
        return this.segment;
    }
}
