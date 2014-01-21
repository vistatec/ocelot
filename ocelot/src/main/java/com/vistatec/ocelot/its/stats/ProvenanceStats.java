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
package com.vistatec.ocelot.its.stats;

/**
 * Aggregate data representation for Provenance displayed in SegmentAttributeTableView.
 */
public class ProvenanceStats implements ITSStats {
    private String displayType, provType, value;
    private Double minRange, maxRange;
    private Integer count = 0;

    @Override
    public String getDataCategory() {
        return "Provenance";
    }

    @Override
    public String getType() {
        return displayType;
    }

    public void setType(String type) {
        this.displayType = type;
    }

    /**
     * Combination of "displayType:value" used to identify unique ProvStatistics.
     * Each ITSProvenance generates multiple ProvStatistics records for each
     * data attribute=value key-value pair.
     */
    public String getProvType() {
        return provType;
    }

    public void setProvType(String provType) {
        this.provType = provType;
    }

    @Override
    public String getValue() {
        if (minRange != null && maxRange != null) {
            return minRange + "-" + maxRange;
        } else if (value != null) {
            return value;
        }
        return null;
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
        count++;
    }
}
