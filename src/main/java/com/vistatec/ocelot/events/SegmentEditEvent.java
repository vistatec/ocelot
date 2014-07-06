package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

/**
 * Signals that a segment has been edited by the user.
 */
public class SegmentEditEvent extends SegmentEvent {
    public SegmentEditEvent(Segment segment) {
        super(segment);
    }
}
