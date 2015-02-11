package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

public class LQIAdditionEvent extends LQIEvent {
    private final Segment segment;

    public LQIAdditionEvent(LanguageQualityIssue lqi, Segment segment) {
        super(lqi);
        this.segment = segment;
    }

    public Segment getSegment() {
        return this.segment;
    }
}
