package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.ITSMetadata;

public class ITSSelectionEvent {
    private ITSMetadata its;
    public ITSSelectionEvent(ITSMetadata its) {
        this.its = its;
    }

    public ITSMetadata getITSMetadata() {
        return its;
    }
}
