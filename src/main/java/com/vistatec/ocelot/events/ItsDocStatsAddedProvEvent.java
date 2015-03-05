package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.Provenance;

public class ItsDocStatsAddedProvEvent implements OcelotEvent {
    private final Provenance prov;

    public ItsDocStatsAddedProvEvent(Provenance prov) {
        this.prov = prov;
    }

    public Provenance getProv() {
        return prov;
    }
}
