package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;

public class SegmentTargetExitEvent extends SegmentEvent {
    public SegmentTargetExitEvent(OcelotSegment segment) {
        super(segment);
    }
}
