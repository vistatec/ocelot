package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

/**
 * Signals that a segment has been selected in the UI.
 */
public class SegmentSelectionEvent extends SegmentEvent {
    public SegmentSelectionEvent(XLIFFDocument xliff, OcelotSegment segment) {
        super(xliff, segment);
    }
}
