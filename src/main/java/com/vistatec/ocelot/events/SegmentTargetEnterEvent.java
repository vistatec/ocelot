package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;

public class SegmentTargetEnterEvent extends SegmentEvent {
    public SegmentTargetEnterEvent(OcelotSegment segment) {
        super(segment);
    }
}
