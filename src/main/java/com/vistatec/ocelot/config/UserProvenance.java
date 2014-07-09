package com.vistatec.ocelot.config;

import com.vistatec.ocelot.its.Provenance;

public class UserProvenance extends Provenance {
    public UserProvenance(String revPerson, String revOrg, String extRef) {
        setRevPerson(revPerson);
        setRevOrg(revOrg);
        setProvRef(extRef);
    }

    public boolean isEmpty() {
        return !(getRevPerson() != null ||
                 getRevOrg() != null||
                 getProvRef() != null);
    }
}
