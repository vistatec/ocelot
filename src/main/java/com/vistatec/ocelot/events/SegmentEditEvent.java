package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.OcelotSegment;

/**
 * Signals that a segment has been edited by the user.
 */
public class SegmentEditEvent extends SegmentEvent {
    public SegmentEditEvent(OcelotSegment segment) {
        super(segment);
    }
}
