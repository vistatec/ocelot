package com.vistatec.ocelot.events;

import java.util.List;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.Segment;

public class ItsDocStatsRecalculateEvent implements OcelotEvent {
    private final List<Segment> segments;

    public ItsDocStatsRecalculateEvent(List<Segment> segments) {
        this.segments = segments;
    }

    public List<Segment> getSegments() {
        return segments;
    }
}
