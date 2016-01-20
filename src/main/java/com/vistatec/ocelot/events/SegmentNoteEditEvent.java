package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentNoteEditEvent extends SegmentEvent {
    private XLIFFDocument xliff;
	public SegmentNoteEditEvent(XLIFFDocument xliff, OcelotSegment segment) {
	    super(segment);
    }

    public XLIFFDocument getDocument() {
        return xliff;
    }
}
