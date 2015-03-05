/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.its.model;

import com.vistatec.ocelot.rules.DataCategoryField;

import java.util.EnumMap;
import java.util.Map;
/**
 * Represents Provenance Data Category in the ITS 2.0 spec.
 */
public abstract class Provenance extends ITSMetadata {
    private String person, org, tool, revPerson, revOrg, revTool, provRef, recsRef;

    public Provenance() { }

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

    public void setPerson(String person) {
        this.person = person;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public void setRevPerson(String revPerson) {
        this.revPerson = revPerson;
    }

    public void setRevOrg(String revOrg) {
        this.revOrg = revOrg;
    }

    public void setRevTool(String revTool) {
        this.revTool = revTool;
    }

    public void setProvRef(String provRef) {
        this.provRef = provRef;
    }

    public void setRecsRef(String recsRef) {
        this.recsRef = recsRef;
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
