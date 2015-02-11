package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;

public class ItsDocStatsAddedLqiEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;

    public ItsDocStatsAddedLqiEvent(LanguageQualityIssue lqi) {
        this.lqi = lqi;
    }

    public LanguageQualityIssue getLqi() {
        return this.lqi;
    }
}
