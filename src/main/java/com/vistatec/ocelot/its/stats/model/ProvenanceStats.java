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
package com.vistatec.ocelot.its.stats.model;

import java.util.Objects;

/**
 * Aggregate data representation for Provenance displayed in SegmentAttributeTableView.
 */
public class ProvenanceStats implements ITSStats {
    private Type displayType;
    private String value;
    private Integer count = 1;

    public enum Type {
        person,
        org,
        tool,
        revPerson,
        revOrg,
        revTool;
    }

    public ProvenanceStats() { }

    public ProvenanceStats(Type type, String value) {
        this.displayType = type;
        this.value = value;
    }

    /**
     * Combination of "displayType:value" used to identify unique ProvStatistics.
     * Each ITSProvenance generates multiple ProvStatistics records for each
     * data attribute=value key-value pair.
     */
    @Override
    public String getKey() {
        return getClass().getName() + ":" + displayType + ":" + value;
    }

    @Override
    public void combine(ITSStats stats) {
        count++;
    }

    @Override
    public String getDataCategory() {
        return "Provenance";
    }

    @Override
    public String getType() {
        return displayType.toString();
    }

    public void setType(Type type) {
        this.displayType = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    public void setValue(String val) {
        this.value = val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof ProvenanceStats)) return false;
        ProvenanceStats prov = (ProvenanceStats)o;
        return Objects.equals(displayType, prov.displayType) &&
               Objects.equals(value, prov.value) &&
               Objects.equals(count, prov.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayType, value, count);
    }
}
