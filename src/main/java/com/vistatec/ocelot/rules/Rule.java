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
package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of all data associated with a single, user-defined rule.
 */
public class Rule {

    List<RuleMatcher> matchers = new ArrayList<RuleMatcher>();
    private boolean enabled = false;
    private String label;
    private DataCategoryFlag flag = new DataCategoryFlag();

    public Rule(List<RuleMatcher> matchers) {
        this.matchers = matchers;
    }

    public Rule() {
    }

    public void addRuleMatcher(RuleMatcher matcher) {
        this.matchers.add(matcher);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DataCategoryFlag getFlag() {
        return flag;
    }

    public void setFlag(DataCategoryFlag flag) {
        this.flag = flag;
    }

    public boolean getEnabled() {
        return enabled;
    }

    /**
     * Enabled state should be set through
     * {@link RuleConfiguration#enableRule}
     */
    void setEnabled(boolean flag) {
        enabled = flag;
    }

    public List<ITSMetadata> displayMatches(OcelotSegment segment) {
        List<ITSMetadata> itsMatches = new ArrayList<ITSMetadata>();
        for (ITSMetadata its : segment.getITSMetadata()) {
            if (matches(its)) {
                itsMatches.add(its);
            }
        }
        return itsMatches;
    }

    /**
     * The filter matches a segment if all of its rules match some piece of ITS
     * metadata in that segment.
     */
    public boolean matches(OcelotSegment segment) {
        // I need to check each piece of metadata.
        // - If all the rules match that piece, success!
        // - If not all the rules match that piece, continue.
        // If I run out of metadata without success, fail.
        for (ITSMetadata its : segment.getITSMetadata()) {
            if (matches(its)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The filter matches a piece of ITS metadata if all of its rules match that
     * piece of metadata.  Rules with no matchers match nothing.
     */
    boolean matches(ITSMetadata its) {
        if (matchers.isEmpty()) {
            return false;
        }
        for (RuleMatcher matcher : matchers) {
            if (!matches(matcher, its)) {
                return false;
            }
        }

        return true;
    }

    /**
     * A matcher matches a piece of ITS metadata if it matches any field in that
     * piece of metadata.
     */
    boolean matches(RuleMatcher matcher, ITSMetadata its) {
        Map<DataCategoryField, Object> values = its.getFieldValues();
        for (Map.Entry<DataCategoryField, Object> e : values.entrySet()) {
            if (matcher.getField().equals(e.getKey())) {
                if (matcher.matches(e.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return label;
    }

    // BUG: This is overly strict, because it compares matchers as a
    // list instead of a set.  Really this should be using set
    // semantics
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof Rule)) return false;
        Rule r = (Rule)o;
        return enabled == r.enabled &&
               label.equals(r.label) &&
               flag.equals(r.flag) &&
               matchers.equals(r.matchers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, label, flag, matchers);
    }
}
