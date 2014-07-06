package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

public class SegmentEditEvent {
    private Segment segment;

    public SegmentEditEvent(Segment segment) {
        this.segment = segment;
    }

    public Segment getSegment() {
        return segment;
    }
}
