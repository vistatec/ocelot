package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIModificationEvent implements OcelotEvent {
    private final LanguageQualityIssue lqi;
    private final OcelotSegment segment;
    private final boolean quiet;

    public LQIModificationEvent(LanguageQualityIssue lqi, OcelotSegment segment) {
        this(lqi, segment, false);
    }

    public LQIModificationEvent(LanguageQualityIssue lqi, OcelotSegment segment, boolean quiet) {
        this.lqi = lqi;
        this.segment = segment;
        this.quiet = quiet;
    }

    public OcelotSegment getSegment() {
        return segment;
    }

    public LanguageQualityIssue getLQI() {
        return lqi;
    }

    public boolean isQuiet() {
        return quiet;
    }
}
