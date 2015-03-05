package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIModificationEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;
    private final OcelotSegment segment;

    public LQIModificationEvent(LanguageQualityIssue lqi, OcelotSegment segment) {
        this.lqi = lqi;
        this.segment = segment;
    }

    public OcelotSegment getSegment() {
        return segment;
    }

    public LanguageQualityIssue getLQI() {
        return lqi;
    }
}
