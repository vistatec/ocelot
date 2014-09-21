package com.vistatec.ocelot.segment;

/**
 * A set of segments.
 */
public interface SegmentModel {
    public Segment getSegment(int row);

    public int getNumSegments();
}
