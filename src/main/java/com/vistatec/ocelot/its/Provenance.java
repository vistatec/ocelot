/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
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
public class Provenance extends ITSMetadata {
    private String person, org, tool, revPerson, revOrg, revTool, provRef, recsRef;

    public Provenance(GenericAnnotation ga) {
        super();
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
