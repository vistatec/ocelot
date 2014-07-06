package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.LanguageQualityIssue;

public class LQISelectionEvent {
    private LanguageQualityIssue lqi;

    public LQISelectionEvent(LanguageQualityIssue lqi) {
        this.lqi = lqi;
    }

    public LanguageQualityIssue getLQI() {
        return lqi;
    }

}
