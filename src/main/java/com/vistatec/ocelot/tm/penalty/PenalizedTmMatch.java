package com.vistatec.ocelot.tm.penalty;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

/**
 * Wrapper around a {@link TmMatch} that both applies and keeps track of the penalty applied.
 */
public class PenalizedTmMatch implements TmMatch {
    private final TmMatch match;
    private final float penalty;

    public PenalizedTmMatch(TmMatch match, float penalty) {
        this.match = match;
        this.penalty = penalty;
    }

    @Override
    public String getTmOrigin() {
        return this.match.getTmOrigin();
    }

    @Override
    public float getMatchScore() {
        return this.match.getMatchScore() - penalty;
    }

    @Override
    public SegmentVariant getSource() {
        return this.match.getSource();
    }

    @Override
    public SegmentVariant getTarget() {
        return this.match.getTarget();
    }

}
