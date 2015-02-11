package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.Segment;
import java.util.List;

/**
 * Service for performing segment related operations.
 */
public interface SegmentService {
    // TODO: Remove requirement for segment model to be table based
    public Segment getSegment(int row);

    public int getNumSegments();

    public void setSegments(List<Segment> segments);

    public void clearAllSegments();
}
