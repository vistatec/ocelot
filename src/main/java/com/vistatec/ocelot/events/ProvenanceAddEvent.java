package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;

public class ProvenanceAddEvent implements OcelotEvent {
    private final Provenance prov;
    private final Segment seg;
    public final boolean isOcelotProv;

    public ProvenanceAddEvent(Provenance prov, Segment seg, boolean isOcelotProv) {
        this.prov = prov;
        this.seg = seg;
        this.isOcelotProv = isOcelotProv;
    }

    public Provenance getProvenance() {
        return prov;
    }

    public Segment getSegment() {
        return seg;
    }

}
