package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;

public class SegmentTargetUpdateEvent extends SegmentEvent {
    private final SegmentVariant updatedTarget;

    public SegmentTargetUpdateEvent(OcelotSegment segment, SegmentVariant updatedTarget) {
        super(segment);
        this.updatedTarget = updatedTarget;
    }

    public SegmentVariant getUpdatedTarget() {
        return this.updatedTarget;
    }
}
