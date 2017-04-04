/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.*;

import com.vistatec.ocelot.rules.Matchers.NumericMatcher;
import com.vistatec.ocelot.rules.Matchers.RegexMatcher;
import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import static com.vistatec.ocelot.rules.RulesTestHelpers.lqi;

import static org.junit.Assert.*;

import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.segment.model.SimpleSegmentVariant;

public class TestSegmentSelector {

    static List<OcelotSegment> testSegments = new ArrayList<>();
    static OcelotSegment segPlain, segSQ, segA, segB, segSQ_A, segSQ_B;

    @BeforeClass
    public static void setup() {
        // I need:
        // - 1 seg with no sq, no filter
        segPlain = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("")
                .target("")
                .build();

        // - 1 seg with [some] sq
        segSQ = new SimpleSegment.Builder()
                .segmentNumber(2)
                .source("")
                .target("")
                .stateQualifier(StateQualifier.ID)
                .build();

        // - 1 seg with no sq, filter A
        segA = new SimpleSegment.Builder()
                .segmentNumber(3)
                .source("")
                .target("")
                .build();
        segA.addAllLQI(Collections.singletonList(lqi("omission", 85)));

        // - 1 seg with no sq, filter B
        segB = new SimpleSegment.Builder()
                .segmentNumber(4)
                .source("")
                .target("")
                .build();
        segB.addAllLQI(Collections.singletonList(lqi("terminology", 85)));

        // - 1 seg with [some] sq, filter A
        segSQ_A = new SimpleSegment.Builder()
                .segmentNumber(5)
                .source("")
                .target("")
                .stateQualifier(StateQualifier.ID)
                .build();
        segSQ_A.addAllLQI(Collections.singletonList(lqi("omission", 85)));

        // - 1 seg with [some] sq, filter B
        segSQ_B = new SimpleSegment.Builder()
                .segmentNumber(6)
                .source("")
                .target("")
                .stateQualifier(StateQualifier.ID)
                .build();
        segSQ_B.addAllLQI(Collections.singletonList(lqi("terminology", 85)));

        testSegments.add(segPlain);
        testSegments.add(segSQ);
        testSegments.add(segA);
        testSegments.add(segB);
        testSegments.add(segSQ_A);
        testSegments.add(segSQ_B);
    }

    // What combinations do I need to test?

    // - All filters, all states => all accepted
    @Test
    public void testSelectAllRules() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL);
        config.setStateQualifierMode(StateQualifierMode.ALL);
        assertEquals(testSegments, select(config, testSegments));
    }

    // - All filters, no states => none accepted
    @Test
    public void testSelectNoStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL);
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        assertEquals(Collections.emptyList(), select(config, testSegments));
    }

    // - All filters, some states => matching accepted
    @Test
    public void testSelectMatchingStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL);
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        config.setStateQualifierEnabled(StateQualifier.ID, true);
        assertEquals(new ArrayList<>(Arrays.asList(segSQ, segSQ_A, segSQ_B)),
                     select(config, testSegments));
    }

    // - All with metadata, all states => those with metadata accepted
    @Test
    public void testSelectAllWithMetadata() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL_WITH_METADATA);
        config.setStateQualifierMode(StateQualifierMode.ALL);
        assertEquals(new ArrayList<>(Arrays.asList(segA, segB, segSQ_A, segSQ_B)),
                     select(config, testSegments));
    }

    // - Specific rules, all states => those
    @Test
    public void testSelectFilterRules() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        config.setStateQualifierMode(StateQualifierMode.ALL);
        config.addRule(getOmissionRule());
        assertEquals(new ArrayList<>(Arrays.asList(segA, segSQ_A)),
                     select(config, testSegments));
    }

    // - All with metadata, no states => none
    @Test
    public void testSelectAllWithMetadataAndNoStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL_WITH_METADATA);
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        assertEquals(Collections.emptyList(), select(config, testSegments));
    }

    // - All with metadata, some states => some
    @Test
    public void testSelectAllWithMetadataAndStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.ALL_WITH_METADATA);
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        config.setStateQualifierEnabled(StateQualifier.ID, true);
        assertEquals(new ArrayList<>(Arrays.asList(segSQ_A, segSQ_B)), select(config, testSegments));
    }

    // - Specific rules, no states => none
    @Test
    public void testNoSelectedFiltersAndStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        config.addRule(getOmissionRule());
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        assertEquals(Collections.emptyList(), select(config, testSegments));
    }

    // - Specific rules, some states => those
    @Test
    public void testSelectedFiltersAndStateQualifiers() {
        RuleConfiguration config = new RuleConfiguration();
        config.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        config.addRule(getOmissionRule());
        config.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        config.setStateQualifierEnabled(StateQualifier.ID, true);
        assertEquals(new ArrayList<>(Arrays.asList(segSQ_A)), select(config, testSegments));
    }

    private Rule getOmissionRule() {
        Rule r = new Rule();
        NumericMatcher m = new Matchers.NumericMatcher();
        m.setLowerBound(50);
        m.setUpperBound(100);
        r.addRuleMatcher(new RuleMatcher(DataCategoryField.LQI_SEVERITY, m));
        RegexMatcher m2 = new Matchers.RegexMatcher();
        m2.setPattern("omission");
        r.addRuleMatcher(new RuleMatcher(DataCategoryField.LQI_TYPE, m2));
        r.setEnabled(true);
        return r;
    }

    List<OcelotSegment> select(RuleConfiguration config, List<OcelotSegment> candidates) {
        SegmentSelector selector = new SegmentSelector(config, true);
        List<OcelotSegment> selected = new ArrayList<>();
        for (OcelotSegment s : candidates) {
            if (selector.matches(s)) {
                selected.add(s);
            }
        }
        return selected;
    }
}
