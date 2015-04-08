package com.vistatec.ocelot.tm;

import java.util.List;

/**
 * Applies penalties to returned TM match scores to indicate a loss of
 * reliability in the translation match.
 */
public interface TmPenalizer {
    List<TmMatch> applyPenalties(List<TmMatch> matches);
}
