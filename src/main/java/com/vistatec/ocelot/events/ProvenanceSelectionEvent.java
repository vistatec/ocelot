package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.Provenance;

public class ProvenanceSelectionEvent extends ITSSelectionEvent {
    public ProvenanceSelectionEvent(Provenance prov) {
        super(prov);
    }

    public Provenance getProvenance() {
        return (Provenance)super.getITSMetadata();
    }
}
