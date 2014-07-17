package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

public class SegmentTargetResetEvent extends SegmentEvent {
    public SegmentTargetResetEvent(Segment segment) {
        super(segment);
    }
}
