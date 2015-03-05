package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public abstract class SegmentEvent implements OcelotEvent {
    private OcelotSegment segment;

    protected SegmentEvent(OcelotSegment segment) {
        this.segment = segment;
    }

    public OcelotSegment getSegment() {
        return segment;
    }
}
