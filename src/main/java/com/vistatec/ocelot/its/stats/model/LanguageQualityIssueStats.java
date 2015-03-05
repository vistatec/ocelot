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

import com.vistatec.ocelot.its.model.LanguageQualityIssue;

/**
 * Aggregate data representation for LQI displayed in SegmentAttributeTableView.
 */
public class LanguageQualityIssueStats implements ITSStats {
    private String type;
    private double minRange = Double.POSITIVE_INFINITY,
                   maxRange = Double.NEGATIVE_INFINITY;
    private Integer count = 1;

    public LanguageQualityIssueStats() { }

    public LanguageQualityIssueStats(LanguageQualityIssue lqi) {
        this.type = lqi.getType();
        setRange(lqi.getSeverity());
    }

    @Override
    public String getDataCategory() {
        return "LQI";
    }

    @Override
    public String getKey() {
        return getClass().getName() + ":" + type;
    }

    @Override
    public void combine(ITSStats stats) {
        setRange(((LanguageQualityIssueStats)stats).minRange);
        setRange(((LanguageQualityIssueStats)stats).maxRange);
        count += stats.getCount();
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return minRange + "-" + maxRange;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    public void setRange(double range) {
        if (minRange > range) {
            minRange = range;
        }
        if (maxRange < range) {
            maxRange = range;
        }
    }

    /**
     * Equality only takes type and range into account, not count.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof LanguageQualityIssueStats)) return false;
        LanguageQualityIssueStats lqi = (LanguageQualityIssueStats)o;
        return type.equals(lqi.type) &&
               minRange == lqi.minRange &&
               maxRange == lqi.maxRange &&
               Objects.equals(count, lqi.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, minRange, maxRange, count);
    }
}
