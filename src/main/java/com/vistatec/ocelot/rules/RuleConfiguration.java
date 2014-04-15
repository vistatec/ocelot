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

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

/**
 * Collection of RuleFilters used to determine whether to filter out a segment
 * from the SegmentView.
 */
public class RuleConfiguration {
    private static Logger LOG = Logger.getLogger(RuleConfiguration.class);

    private HashMap<String,Rule> rules = new HashMap<String, Rule>();
    private List<Rule> ruleOrdering = new ArrayList<Rule>();
    private ArrayList<RuleListener> ruleListeners = new ArrayList<RuleListener>();
    private HashMap<String, LanguageQualityIssue> quickAdd;
    private HashMap<Integer, String> quickAddHotkeys;
    private EnumMap<StateQualifier, StateQualifierRule> stateQualifierRules =
            new EnumMap<StateQualifier, StateQualifierRule>(StateQualifier.class);
    protected boolean all = true, allWithMetadata;

    public RuleConfiguration(RuleListener listener) {
        this();
        this.ruleListeners.add(listener);
    }

    public RuleConfiguration() {
        quickAdd = new HashMap<String, LanguageQualityIssue>();
        quickAddHotkeys = new HashMap<Integer, String>();
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

    public void enableRule(String ruleLabel, boolean enabled) {
        rules.get(ruleLabel).setEnabled(enabled);
        for (RuleListener listener : ruleListeners) {
            listener.enabledRule(ruleLabel, enabled);
        }
    }

    public boolean getAllSegments() {
        return all;
    }

    public void setAllSegments(boolean enabled) {
        if (this.all != enabled) {
            this.all = enabled;
            for (RuleListener listener : ruleListeners) {
                listener.allSegments(enabled);
            }
        }
    }

    public boolean getAllMetadataSegments() {
        return allWithMetadata;
    }

    public void setMetadataSegments(boolean enabled) {
        if (this.allWithMetadata != enabled) {
            this.allWithMetadata = enabled;
            for (RuleListener listener : ruleListeners) {
                listener.allMetadataSegments(enabled);
            }
        }
    }

    void addQuickAddHotkey(Integer hotkey, String ruleLabel) {
        quickAddHotkeys.put(hotkey, ruleLabel);
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
        getDataCategoryFlag(ruleLabel).setBorder(BorderFactory.createLineBorder(border));
    }

    void addText(String ruleLabel, String text) {
        getDataCategoryFlag(ruleLabel).setText(text);
    }

    public LanguageQualityIssue getQuickAddLQI(int hotkey) {
        return quickAdd.get(quickAddHotkeys.get(hotkey));
    }

    public LanguageQualityIssue getQuickAddLQI(String ruleLabel) {
        LanguageQualityIssue lqi = quickAdd.get(ruleLabel);
        if (lqi == null) {
            lqi = new LanguageQualityIssue();
        }
        return lqi;
    }

    public void setLQIType(String ruleLabel, String type) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setType(type);
        quickAdd.put(ruleLabel, lqi);
    }

    public void setLQISeverity(String ruleLabel, double severity) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setSeverity(severity);
        quickAdd.put(ruleLabel, lqi);
    }

    public void setLQIComment(String ruleLabel, String comment) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setComment(comment);
        quickAdd.put(ruleLabel, lqi);
    }

    /**
     * Note: this examines rules in reverse-order in which they are added
     * to the configuration (ie, last rule in rules.properties) is checked
     * first.
     */
    public ITSMetadata getTopDataCategory(Segment seg, int flagCol) {
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

        for (ITSMetadata its : seg.getAllITSMetadata()) {
            if (!displayFlags.contains(its)) {
                displayFlags.add(its);
                if (displayFlags.size() > flagCol) {
                    return displayFlags.get(flagCol);
                }
            }
        }

        return null;
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
