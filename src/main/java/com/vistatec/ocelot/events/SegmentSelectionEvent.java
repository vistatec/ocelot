package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.Segment;

/**
 * Signals that a segment has been selected in the UI.
 */
public class SegmentSelectionEvent extends SegmentEvent {
    public SegmentSelectionEvent(Segment segment) {
        super(segment);
    }
}
