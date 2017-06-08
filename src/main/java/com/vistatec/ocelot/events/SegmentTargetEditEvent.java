package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentTargetEditEvent extends SegmentTargetUpdateEvent {

    public SegmentTargetEditEvent(XLIFFDocument xliff, OcelotSegment segment, SegmentVariant updatedTarget) {
        super(xliff, segment, updatedTarget);
    }
}
