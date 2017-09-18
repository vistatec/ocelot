package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIAdditionEvent extends LQIEvent {
    private final OcelotSegment segment;
    private final boolean quiet;

    public LQIAdditionEvent(LanguageQualityIssue lqi, OcelotSegment segment) {
        this(lqi, segment, false);
    }

    public LQIAdditionEvent(LanguageQualityIssue lqi, OcelotSegment segment, boolean quiet) {
        super(lqi);
        this.segment = segment;
        this.quiet = quiet;
    }

    public OcelotSegment getSegment() {
        return this.segment;
    }

    public boolean isQuiet() {
        return this.quiet;
    }
}
