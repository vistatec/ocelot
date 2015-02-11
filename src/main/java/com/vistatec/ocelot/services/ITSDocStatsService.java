package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ItsDocStatsChangedEvent;
import com.vistatec.ocelot.events.ItsDocStatsAddedLqiEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.its.stats.LanguageQualityIssueStats;

public class ITSDocStatsService implements OcelotEventQueueListener {
    private ITSDocStats itsDocStats;
    private final OcelotEventQueue eventQueue;

    public ITSDocStatsService(OcelotEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Subscribe
    public void addLQIStats(ItsDocStatsAddedLqiEvent e) {
        LanguageQualityIssue lqi = e.getLqi();
        itsDocStats.updateStats(new LanguageQualityIssueStats(lqi));
        eventQueue.post(new ItsDocStatsChangedEvent());
    }
}
