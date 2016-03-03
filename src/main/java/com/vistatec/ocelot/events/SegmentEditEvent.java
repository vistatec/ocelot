package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

/**
 * Signals that a segment has been edited by the user.
 */
public class SegmentEditEvent extends SegmentEvent {
    public SegmentEditEvent(XLIFFDocument xliff, OcelotSegment segment) {
        super(xliff, segment);
    }
}
