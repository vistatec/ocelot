package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ItsDocStatsAddedProvEvent;
import com.vistatec.ocelot.events.ItsDocStatsUpdateLqiEvent;
import com.vistatec.ocelot.events.ItsDocStatsChangedEvent;
import com.vistatec.ocelot.events.ItsDocStatsClearEvent;
import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.ItsDocStatsRemovedLqiEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.its.stats.ITSStats;
import com.vistatec.ocelot.its.stats.LanguageQualityIssueStats;
import com.vistatec.ocelot.segment.Segment;

public class ITSDocStatsService implements OcelotEventQueueListener {
    private final ITSDocStats itsDocStats;
    private final OcelotEventQueue eventQueue;

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
        for (Segment seg : e.getSegments()) {
            for (LanguageQualityIssue lqi : seg.getLQI()) {
                itsDocStats.updateStats(new LanguageQualityIssueStats(lqi));
            }
            for (Provenance prov : seg.getProv()) {
                itsDocStats.addProvenanceStats(prov);
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