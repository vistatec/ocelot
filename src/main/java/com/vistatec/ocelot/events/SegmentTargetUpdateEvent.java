package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentTargetUpdateEvent extends SegmentEvent {
    
	private final SegmentVariant updatedTarget;

    
    public SegmentTargetUpdateEvent(XLIFFDocument xliff, OcelotSegment segment, SegmentVariant updatedTarget) {
        super(xliff, segment);
        this.updatedTarget = updatedTarget;
    }

    public SegmentVariant getUpdatedTarget() {
        return this.updatedTarget;
    }
}
