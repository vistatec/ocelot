package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

public class SegmentTargetExitEvent extends SegmentEvent {
    public SegmentTargetExitEvent(Segment segment) {
        super(segment);
    }
}
