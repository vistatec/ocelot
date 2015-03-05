package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.Provenance;

public class ProvenanceSelectionEvent extends ItsSelectionEvent implements OcelotEvent {
    public ProvenanceSelectionEvent(Provenance prov) {
        super(prov);
    }

    public Provenance getProvenance() {
        return (Provenance)super.getITSMetadata();
    }
}
