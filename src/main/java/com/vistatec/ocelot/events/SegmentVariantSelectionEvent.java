package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.SegmentVariant;

public class SegmentVariantSelectionEvent implements OcelotEvent {

    private SegmentVariant segmentVariant;
    
    public SegmentVariantSelectionEvent(SegmentVariant segmentVariant) {
        
        this.segmentVariant = segmentVariant;
    }

    public SegmentVariant getSegmentVariant() {
        return segmentVariant;
    }

}
