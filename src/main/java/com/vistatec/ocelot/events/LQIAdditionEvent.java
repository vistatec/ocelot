package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIAdditionEvent extends LQIEvent {
    private final OcelotSegment segment;

    public LQIAdditionEvent(LanguageQualityIssue lqi, OcelotSegment segment) {
        super(lqi);
        this.segment = segment;
    }

    public OcelotSegment getSegment() {
        return this.segment;
    }
}
