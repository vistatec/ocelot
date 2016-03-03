package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.events.ItsDocStatsAddedProvEvent;
import com.vistatec.ocelot.events.ItsDocStatsUpdateLqiEvent;
import com.vistatec.ocelot.events.ItsDocStatsChangedEvent;
import com.vistatec.ocelot.events.ItsDocStatsClearEvent;
import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.ItsDocStatsRemovedLqiEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.its.stats.model.ITSDocStats;
import com.vistatec.ocelot.its.stats.model.ITSStats;
import com.vistatec.ocelot.its.stats.model.LanguageQualityIssueStats;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class ITSDocStatsService implements OcelotEventQueueListener {
    private final ITSDocStats itsDocStats;
    private final OcelotEventQueue eventQueue;

    @Inject
    public ITSDocStatsService(ITSDocStats itsDocStats, OcelotEventQueue eventQueue) {
        this.itsDocStats = itsDocStats;
        this.eventQueue = eventQueue;
    }

    public int getNumStats() {
        return this.itsDocStats.getStats().size();
    }

    public ITSStats getItsStatistic(int row) {
        return this.itsDocStats.getStats().get(row);
    }

    @Subscribe
    public void updateLQIStats(ItsDocStatsUpdateLqiEvent e) {
        LanguageQualityIssue lqi = e.getLqi();
        itsDocStats.updateStats(new LanguageQualityIssueStats(lqi));
        eventQueue.post(new ItsDocStatsChangedEvent());
    }

    @Subscribe
    public void removeLQIStats(ItsDocStatsRemovedLqiEvent e) {
        eventQueue.post(new ItsDocStatsRecalculateEvent(e.getSegments()));
    }

    @Subscribe
    public void recalculateStats(ItsDocStatsRecalculateEvent e) {
        itsDocStats.clear();
        for (OcelotSegment seg : e.getSegments()) {
            for (LanguageQualityIssue lqi : seg.getLQI()) {
                itsDocStats.updateStats(new LanguageQualityIssueStats(lqi));
            }
            for (Provenance prov : seg.getProvenance()) {
                itsDocStats.addProvenanceStats(prov);
            }
            for(TextAnalysisMetaData ta: seg.getTextAnalysis()){
            	itsDocStats.addTextAnalysisStats(ta);
            }
            for(TerminologyMetaData term: seg.getTerms()){
            	itsDocStats.addTerminologyStats(term);
            }
        }
        eventQueue.post(new ItsDocStatsChangedEvent());
    }
    

 
    @Subscribe
    public void addProvenanceStats(ItsDocStatsAddedProvEvent e) {
        Provenance prov = e.getProv();
        itsDocStats.addProvenanceStats(prov);
        eventQueue.post(new ItsDocStatsChangedEvent());
    }

    @Subscribe
    public void clear(ItsDocStatsClearEvent e) {
        itsDocStats.clear();
        eventQueue.post(new ItsDocStatsChangedEvent());
    }
}
