package com.vistatec.ocelot.tm;

import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Match result format from TM.
 */
public interface TmMatch {
    public String getTmOrigin();
    public float getMatchScore();

    public SegmentVariant getSource();
    public SegmentVariant getTarget();
}
