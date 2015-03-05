package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.OcelotSegment;

public class SegmentTargetResetEvent extends SegmentEvent {
    public SegmentTargetResetEvent(OcelotSegment segment) {
        super(segment);
    }
}
