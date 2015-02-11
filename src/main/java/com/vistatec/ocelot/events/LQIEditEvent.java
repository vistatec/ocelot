package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

public class LQIEditEvent extends LQIEvent {
    private final Segment segment;

    public LQIEditEvent(LanguageQualityIssue lqi, Segment segment) {
        super(lqi);
        this.segment = segment;
    }

    public Segment getSegment() {
        return this.segment;
    }
}
