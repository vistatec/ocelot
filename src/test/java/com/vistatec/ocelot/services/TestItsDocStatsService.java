package com.vistatec.ocelot.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.ItsDocStatsAddedProvEvent;
import com.vistatec.ocelot.events.ItsDocStatsChangedEvent;
import com.vistatec.ocelot.events.ItsDocStatsUpdateLqiEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.stats.model.ITSDocStats;
import com.vistatec.ocelot.its.stats.model.ITSStats;
import com.vistatec.ocelot.its.stats.model.LanguageQualityIssueStats;
import com.vistatec.ocelot.its.stats.model.ProvenanceStats;
import com.vistatec.ocelot.rules.RulesTestHelpers;

public class TestItsDocStatsService {
    private final Mockery mockery = new Mockery();

    private ITSDocStatsService docStatsService;
    private ITSDocStats docStats;
    private final OcelotEventQueue mockEventQueue = mockery.mock(OcelotEventQueue.class);

    @Before
    public void before() {
        this.docStats = new ITSDocStats();
        this.docStatsService = new ITSDocStatsService(docStats, mockEventQueue);
    }

    @Test
    public void testAddLQI() {
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ItsDocStatsChangedEvent.class)));
        }});
        LanguageQualityIssue lqi = RulesTestHelpers.lqi("omission", 85);
        docStatsService.updateLQIStats(new ItsDocStatsUpdateLqiEvent(lqi));
        assertEquals(Collections.singletonList(new LanguageQualityIssueStats(lqi)),
                docStats.getStats());
    }

    @Test
    public void testAddProvenance() {
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ItsDocStatsChangedEvent.class)));
        }});

        Provenance prov = new UserProvenance("a", "b", "c");
        docStatsService.addProvenanceStats(new ItsDocStatsAddedProvEvent(prov));

        List<ITSStats> expectedStats = new ArrayList<>();
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revPerson, "a"));
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revOrg, "b"));
        assertEquals(expectedStats, docStats.getStats());
    }
}
