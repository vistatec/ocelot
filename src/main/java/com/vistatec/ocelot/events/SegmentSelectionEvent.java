package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.OcelotSegment;

/**
 * Signals that a segment has been selected in the UI.
 */
public class SegmentSelectionEvent extends SegmentEvent {
    public SegmentSelectionEvent(OcelotSegment segment) {
        super(segment);
    }
}
