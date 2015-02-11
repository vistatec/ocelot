package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.Segment;

import java.util.List;

import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Service for performing segment related operations.
 */
public interface SegmentService extends OcelotEventQueueListener {
    // TODO: Remove requirement for segment model to be table based
    public Segment getSegment(int row);

    public int getNumSegments();

    public void setSegments(List<Segment> segments);

    public void updateSegmentTarget(SegmentTargetUpdateEvent e);

    public void addLQI(LQIAdditionEvent e);
    public void editLQI(LQIEditEvent e);

    public void clearAllSegments();
}
