package com.vistatec.ocelot.events;

import com.vistatec.ocelot.its.Provenance;

public class ProvenanceAddedEvent {
    private Provenance prov;

    public ProvenanceAddedEvent(Provenance prov) {
        this.prov = prov;
    }

    public Provenance getProvenance() {
        return prov;
    }
}
