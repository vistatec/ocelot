package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.Segment;

public abstract class SegmentEvent implements OcelotEvent {
    private Segment segment;

    protected SegmentEvent(Segment segment) {
        this.segment = segment;
    }

    public Segment getSegment() {
        return segment;
    }
}
