package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;

public class ItsDocStatsUpdateLqiEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;

    public ItsDocStatsUpdateLqiEvent(LanguageQualityIssue lqi) {
        this.lqi = lqi;
    }

    public LanguageQualityIssue getLqi() {
        return this.lqi;
    }
}
