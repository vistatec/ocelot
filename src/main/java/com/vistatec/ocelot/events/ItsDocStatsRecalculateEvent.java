package com.vistatec.ocelot.events;

import java.util.List;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class ItsDocStatsRecalculateEvent implements OcelotEvent {
    private final List<OcelotSegment> segments;

    public ItsDocStatsRecalculateEvent(List<OcelotSegment> segments) {
        this.segments = segments;
    }

    public List<OcelotSegment> getSegments() {
        return segments;
    }
}
