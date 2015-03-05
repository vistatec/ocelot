package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;

public abstract class LQIEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;

    protected LQIEvent(LanguageQualityIssue lqi) {
        this.lqi = lqi;
    }

    public LanguageQualityIssue getLQI() {
        return this.lqi;
    }
}
