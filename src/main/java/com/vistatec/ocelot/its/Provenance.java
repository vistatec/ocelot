package com.vistatec.ocelot.its;

import com.vistatec.ocelot.rules.DataCategoryField;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import java.util.EnumMap;
import java.util.Map;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;

/**
 * Represents Provenance Data Category in the ITS 2.0 spec.
 */
public class Provenance extends DataCategoryFlag implements ITSMetadata {
    private String person, org, tool, revPerson, revOrg, revTool, provRef, recsRef;

    public Provenance(GenericAnnotation ga) {
        if (ga.getString(GenericAnnotationType.PROV_RECSREF) != null) {
            this.recsRef = ga.getString(GenericAnnotationType.PROV_RECSREF);
        }
        if (ga.getString(GenericAnnotationType.PROV_PERSON) != null) {
            this.person = ga.getString(GenericAnnotationType.PROV_PERSON);
        }
        if (ga.getString(GenericAnnotationType.PROV_ORG) != null) {
            this.org = ga.getString(GenericAnnotationType.PROV_ORG);
        }
        if (ga.getString(GenericAnnotationType.PROV_TOOL) != null) {
            this.tool = ga.getString(GenericAnnotationType.PROV_TOOL);
        }
        if (ga.getString(GenericAnnotationType.PROV_REVPERSON) != null) {
            this.revPerson = ga.getString(GenericAnnotationType.PROV_REVPERSON);
        }
        if (ga.getString(GenericAnnotationType.PROV_REVORG) != null) {
            this.revOrg = ga.getString(GenericAnnotationType.PROV_REVORG);
        }
        if (ga.getString(GenericAnnotationType.PROV_REVTOOL) != null) {
            this.revTool = ga.getString(GenericAnnotationType.PROV_REVTOOL);
        }
        if (ga.getString(GenericAnnotationType.PROV_PROVREF) != null) {
            this.provRef = ga.getString(GenericAnnotationType.PROV_PROVREF);
        }
    }
    
    public String getRecsRef() {
        return recsRef;
    }

    public String getPerson() {
        return person;
    }

    public String getOrg() {
        return org;
    }

    public String getTool() {
        return tool;
    }

    public String getRevPerson() {
        return revPerson;
    }

    public String getRevOrg() {
        return revOrg;
    }

    public String getRevTool() {
        return revTool;
    }

    public String getProvRef() {
        return provRef;
    }

    @Override
    public Map<DataCategoryField, Object> getFieldValues() {
        Map<DataCategoryField, Object> map =
                new EnumMap<DataCategoryField, Object>(DataCategoryField.class);
        map.put(DataCategoryField.PROV_ORG, org);
        map.put(DataCategoryField.PROV_PERSON, person);
        map.put(DataCategoryField.PROV_TOOL, tool);
        map.put(DataCategoryField.PROV_REVORG, revOrg);
        map.put(DataCategoryField.PROV_REVPERSON, revPerson);
        map.put(DataCategoryField.PROV_REVTOOL, revTool);
        map.put(DataCategoryField.PROV_PROVREF, provRef);
        return map;
    }
}
