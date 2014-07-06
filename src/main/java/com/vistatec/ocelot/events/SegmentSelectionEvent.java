package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

public class SegmentSelectionEvent {
    private Segment segment;

    public SegmentSelectionEvent(Segment segment) {
        this.segment = segment;
    }

    public Segment getSegment() {
        return segment;
    }
}
