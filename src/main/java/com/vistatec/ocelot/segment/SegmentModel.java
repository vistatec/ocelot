package com.vistatec.ocelot.segment;

/**
 * A set of segments.
 */
public interface SegmentModel {
    public OcelotSegment getSegment(int row);

    public int getNumSegments();
}
