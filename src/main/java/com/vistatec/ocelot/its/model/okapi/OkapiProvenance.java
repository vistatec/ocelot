package com.vistatec.ocelot.its.model.okapi;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;

import com.vistatec.ocelot.its.model.Provenance;

public class OkapiProvenance extends Provenance {
    public OkapiProvenance(GenericAnnotation ga) {
        super();
        if (ga.getString(GenericAnnotationType.PROV_RECSREF) != null) {
            setRecsRef(ga.getString(GenericAnnotationType.PROV_RECSREF));
        }
        if (ga.getString(GenericAnnotationType.PROV_PERSON) != null) {
            setPerson(ga.getString(GenericAnnotationType.PROV_PERSON));
        }
        if (ga.getString(GenericAnnotationType.PROV_ORG) != null) {
            setOrg(ga.getString(GenericAnnotationType.PROV_ORG));
        }
        if (ga.getString(GenericAnnotationType.PROV_TOOL) != null) {
            setTool(ga.getString(GenericAnnotationType.PROV_TOOL));
        }
        if (ga.getString(GenericAnnotationType.PROV_REVPERSON) != null) {
            setRevPerson(ga.getString(GenericAnnotationType.PROV_REVPERSON));
        }
        if (ga.getString(GenericAnnotationType.PROV_REVORG) != null) {
            setRevOrg(ga.getString(GenericAnnotationType.PROV_REVORG));
        }
        if (ga.getString(GenericAnnotationType.PROV_REVTOOL) != null) {
            setRevTool(ga.getString(GenericAnnotationType.PROV_REVTOOL));
        }
        if (ga.getString(GenericAnnotationType.PROV_PROVREF) != null) {
            setProvRef(ga.getString(GenericAnnotationType.PROV_PROVREF));
        }
    }

    public OkapiProvenance(net.sf.okapi.lib.xliff2.its.Provenance p) {
        super();
        if (p.getPerson() != null) {
            setPerson(p.getPerson());
        }
        if (p.getOrg() != null) {
            setOrg(p.getOrg());
        }
        if (p.getTool() != null) {
            setTool(p.getTool());
        }
        if (p.getRevPerson() != null) {
            setRevPerson(p.getRevPerson());
        }
        if (p.getRevOrg() != null) {
            setRevOrg(p.getRevOrg());
        }
        if (p.getRevTool() != null) {
            setRevTool(p.getRevTool());
        }
        if (p.getProvRef() != null) {
            setProvRef(p.getProvRef());
        }
    }
}
