package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.ITSMetadata;

public class ItsSelectionEvent implements OcelotEvent {
    private final ITSMetadata its;
    public ItsSelectionEvent(ITSMetadata its) {
        this.its = its;
    }

    public ITSMetadata getITSMetadata() {
        return its;
    }
}
