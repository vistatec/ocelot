package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;

public class LQISelectionEvent extends ItsSelectionEvent implements OcelotEvent {
    public LQISelectionEvent(LanguageQualityIssue lqi) {
        super(lqi);
    }

    public LanguageQualityIssue getLQI() {
        return (LanguageQualityIssue)super.getITSMetadata();
    }
}
