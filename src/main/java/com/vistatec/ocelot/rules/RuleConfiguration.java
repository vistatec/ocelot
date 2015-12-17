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

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Collection of RuleFilters used to determine whether to filter out a segment
 * from the SegmentView.
 */
public class RuleConfiguration {
    private HashMap<String,Rule> rules = new HashMap<String, Rule>();
    private List<Rule> ruleOrdering = new ArrayList<Rule>();
    private ArrayList<RuleListener> ruleListeners = new ArrayList<RuleListener>();
    private EnumMap<StateQualifier, StateQualifierRule> stateQualifierRules =
            new EnumMap<StateQualifier, StateQualifierRule>(StateQualifier.class);
    protected FilterMode filterMode = FilterMode.ALL;
    protected StateQualifierMode stateQualifierMode = StateQualifierMode.ALL;

    public enum FilterMode {
        ALL,
        ALL_WITH_METADATA,
        SELECTED_SEGMENTS;
    }

    public enum StateQualifierMode {
        ALL,
        SELECTED_STATES;
    }
    
    public RuleConfiguration(RuleListener listener) {
        this();
        this.ruleListeners.add(listener);
    }

    public RuleConfiguration() {
        initStateQualifierRules();
    }

    void initStateQualifierRules() {
        for (StateQualifier sq : StateQualifier.values()) {
            StateQualifierRule rule = new StateQualifierRule(sq);
            stateQualifierRules.put(sq, rule);
        }
    }
    
    public void addRuleListener(RuleListener listener) {
        this.ruleListeners.add(listener);
    }

    public void enableRule(Rule rule, boolean enabled) {
        rule.setEnabled(enabled);
        for (RuleListener listener : ruleListeners) {
            listener.enabledRule(rule.getLabel(), enabled);
        }
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode mode) {
        this.filterMode = mode;
        for (RuleListener listener : ruleListeners) {
            listener.setFilterMode(mode);
        }
    }

    /**
     * Return a list of all rules in the order they're defined.
     * @return list of Rule objects
     */
    public List<Rule> getRules() {
        return ruleOrdering;
    }

    public void addRule(Rule rule) {
        rules.put(rule.getLabel(), rule);
        ruleOrdering.add(rule);
    }

    public Rule removeRule(Rule rule) {
        Rule r = rules.remove(rule.getLabel());
        ruleOrdering.remove(rule);
        return r;
    }

    public Rule getRule(String label) {
        return rules.get(label);
    }

    private Rule getOrCreateRule(String label) {
        Rule r = getRule(label);
        if (r == null) {
            r = new Rule();
            r.setLabel(label);
            addRule(r);
        }
        return r;
    }

    void addRuleConstaint(String ruleLabel, RuleMatcher ruleMatcher) {
        getOrCreateRule(ruleLabel).addRuleMatcher(ruleMatcher);
    }

    private DataCategoryFlag getDataCategoryFlag(String ruleLabel) {
        return getOrCreateRule(ruleLabel).getFlag();
    }

    void addFill(String ruleLabel, Color fill) {
        getDataCategoryFlag(ruleLabel).setFill(fill);
    }

    void addBorder(String ruleLabel, Color border) {
        getDataCategoryFlag(ruleLabel).setBorderColor(border);
    }

    void addText(String ruleLabel, String text) {
        getDataCategoryFlag(ruleLabel).setText(text);
    }

    /**
     * Find the first flag for the specified metadata flag.
     * @param its
     * @return
     */
    public DataCategoryFlag getFlagForMetadata(ITSMetadata its) {
        for (int pos = ruleOrdering.size() - 1; pos >= 0; pos--) {
            Rule r = ruleOrdering.get(pos);
            if (r.matches(its)) {
                return r.getFlag();
            }
        }
        return null;
    }

    /**
     * Note: this examines rules in reverse-order in which they are added
     * to the configuration (ie, last rule in rules.properties) is checked
     * first.
     */
    public ITSMetadata getTopDataCategory(OcelotSegment seg, int flagCol) {
        LinkedList<ITSMetadata> displayFlags = new LinkedList<ITSMetadata>();
        for (int pos = ruleOrdering.size()-1; pos >= 0; pos--) {
            Rule r = ruleOrdering.get(pos);
            List<ITSMetadata> itsMatches = r.displayMatches(seg);
            for (ITSMetadata its : itsMatches) {
                its.setFlag(r.getFlag());
                if (!displayFlags.contains(its)) {
                    displayFlags.add(its);
                }
            }

            if (displayFlags.size() > flagCol) {
                return displayFlags.get(flagCol);
            }
        }

        for (ITSMetadata its : seg.getITSMetadata()) {
            if (!displayFlags.contains(its)) {
                displayFlags.add(its);
                if (displayFlags.size() > flagCol) {
                    return displayFlags.get(flagCol);
                }
            }
        }

        return null;
    }

    public StateQualifierMode getStateQualifierMode() {
        return stateQualifierMode;
    }

    public void setStateQualifierMode(StateQualifierMode mode) {
        this.stateQualifierMode = mode;
        for (RuleListener listener : ruleListeners) {
            listener.setStateQualifierMode(mode);
        }
    }

    public void setStateQualifierColor(StateQualifier stateQualifier, Color color) {
        stateQualifierRules.get(stateQualifier).setColor(color);
    }

    public Color getStateQualifierColor(StateQualifier stateQualifier) {
        return stateQualifierRules.get(stateQualifier).getColor();
    }

    public boolean getStateQualifierEnabled(StateQualifier stateQualifier) {
        return stateQualifierRules.get(stateQualifier).getEnabled();
    }

    public void setStateQualifierEnabled(StateQualifier stateQualifier, boolean flag) {
        StateQualifierRule rule = stateQualifierRules.get(stateQualifier);
        // Only notify listeners on change
        if (flag != rule.getEnabled()) {
            rule.setEnabled(flag);
            for (RuleListener listener : ruleListeners) {
                listener.enabledRule(stateQualifier.getName(), flag);
            }
        }
    }

}
