package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public abstract class SegmentEvent implements OcelotEvent {
    private OcelotSegment segment;
    private XLIFFDocument xliff;

    protected SegmentEvent(XLIFFDocument xliff, OcelotSegment segment) {
        this.xliff = xliff;
        this.segment = segment;
    }

    public XLIFFDocument getDocument() {
        return xliff;
    }

    public OcelotSegment getSegment() {
        return segment;
    }
}
