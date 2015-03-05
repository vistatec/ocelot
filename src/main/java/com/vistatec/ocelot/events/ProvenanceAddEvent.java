package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class ProvenanceAddEvent implements OcelotEvent {
    private final Provenance prov;
    private final OcelotSegment seg;
    public final boolean isOcelotProv;

    public ProvenanceAddEvent(Provenance prov, OcelotSegment seg, boolean isOcelotProv) {
        this.prov = prov;
        this.seg = seg;
        this.isOcelotProv = isOcelotProv;
    }

    public Provenance getProvenance() {
        return prov;
    }

    public OcelotSegment getSegment() {
        return seg;
    }

}
