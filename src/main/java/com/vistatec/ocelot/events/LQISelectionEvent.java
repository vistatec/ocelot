package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;

public class LQISelectionEvent extends ITSSelectionEvent {
    public LQISelectionEvent(LanguageQualityIssue lqi) {
        super(lqi);
    }

    public LanguageQualityIssue getLQI() {
        return (LanguageQualityIssue)super.getITSMetadata();
    }
}
