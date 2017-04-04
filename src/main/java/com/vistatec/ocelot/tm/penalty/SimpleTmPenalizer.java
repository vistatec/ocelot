package com.vistatec.ocelot.tm.penalty;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmPenalizer;

/**
 * Applies the penalty specified in the {@link TmManagement.TmConfig} for each
 * match based on their TM origin if applicable.
 */
public class SimpleTmPenalizer implements TmPenalizer {
    private final TmManager tmManager;

    @Inject
    public SimpleTmPenalizer(TmManager tmManager) {
        this.tmManager = tmManager;
    }

    @Override
    public List<TmMatch> applyPenalties(List<TmMatch> matches) {
        List<TmMatch> penalizedMatches = new ArrayList<>();
        for (TmMatch match : matches) {
            TmConfig config = tmManager.fetchTm(match.getTmOrigin());
            penalizedMatches.add(new PenalizedTmMatch(match,
                    config == null ? 0 : config.getPenalty()));
        }
        return penalizedMatches;
    }

}