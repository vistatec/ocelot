package com.vistatec.ocelot.tm.okapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;

import com.google.inject.Inject;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmService;

/**
 * Use Okapi Pensieve to search the Lucene index.
 */
public class OkapiTmService implements TmService {
    private final OkapiTmManager manager;
    private final TmPenalizer penalizer;
    private final JsonConfigService cfgService;

    @Inject
    public OkapiTmService(OkapiTmManager manager, TmPenalizer penalizer, JsonConfigService cfgService) {
        this.manager = manager;
        this.penalizer = penalizer;
        this.cfgService = cfgService;
    }

    @Override
    public List<TmMatch> getFuzzyTermMatches(List<SegmentAtom> segment) throws IOException {
        Iterator<OkapiTmManager.TmPair> tmPairs = manager.getSeekers();
        int pensieveThreshold = new Double(cfgService.getFuzzyThreshold()).intValue();

        List<TmMatch> matches = new ArrayList<>();
        while (tmPairs.hasNext()) {
            OkapiTmManager.TmPair tmPair = tmPairs.next();
            if (checkTmEnabled(tmPair)) {
                List<TmHit> results = tmPair.getSeeker().searchFuzzy(
                        new TextFragment(getSearchText(segment)),
                        pensieveThreshold, cfgService.getMaxResults(), null);

                matches.addAll(convertOkapiTmHit(tmPair.getTmOrigin(), results));
            }
            tmPair.getSeeker().close();
        }
        return penalizer.applyPenalties(matches);
    }

    @Override
    public List<TmMatch> getConcordanceMatches(List<SegmentAtom> segment) throws IOException {
        Iterator<OkapiTmManager.TmPair> tmPairs = manager.getSeekers();
        int pensieveThreshold = new Double(cfgService.getFuzzyThreshold()).intValue();

        List<TmMatch> matches = new ArrayList<>();
        while(tmPairs.hasNext()) {
            OkapiTmManager.TmPair tmPair = tmPairs.next();
            if (checkTmEnabled(tmPair)) {
                List<TmHit> results = tmPair.getSeeker().searchSimpleConcordance(
                        getSearchText(segment), pensieveThreshold,
                        cfgService.getMaxResults(), null);

                matches.addAll(convertOkapiTmHit(tmPair.getTmOrigin(), results));
            }
            tmPair.getSeeker().close();
        }
        return penalizer.applyPenalties(matches);
    }

    public List<TmMatch> convertOkapiTmHit(String tmOrigin, List<TmHit> leverageResults) {
        List<TmMatch> matches = new ArrayList<>();
        for (TmHit hit : leverageResults) {
            matches.add(new PensieveTmMatch(tmOrigin, hit));
        }
        return matches;
    }

    private String getSearchText(List<SegmentAtom> segment) {
        StringBuilder searchText = new StringBuilder();
        for (SegmentAtom atom : segment) {
            searchText.append(atom.getData());
        }
        return searchText.toString();
    }

    private boolean checkTmEnabled(OkapiTmManager.TmPair tmPair) {
        TmConfig config = this.manager.fetchTm(tmPair.getTmOrigin());
        return config != null && config.isEnabled();
    }
}
