package com.vistatec.ocelot.segment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.*;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ConfigsForProvTesting;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.ITSDocStatsChangedEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.ProvenanceAddedEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.its.stats.ITSStats;
import com.vistatec.ocelot.its.stats.LanguageQualityIssueStats;
import com.vistatec.ocelot.its.stats.ProvenanceStats;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.okapi.OkapiXLIFFFactory;

import static org.junit.Assert.*;

/**
 * Tests for segment model updates and event propagation.
 */
public class TestSegmentController {

    private EventBus eventBus;

    @Before
    public void before() {
        eventBus = new EventBus();
    }

    private SegmentController emptyController() throws Exception {
        SegmentController controller = new SegmentController(new SimpleXLIFFFactory("en", "fr", new ArrayList<Segment>()),
                eventBus, new RuleConfiguration(),
                new ProvenanceConfig(new ConfigsForProvTesting("", null)));
        return controller;
    }
    private SegmentController loadResourceAsController(String resource) throws Exception {
        SegmentController controller = new SegmentController(new OkapiXLIFFFactory(),
                eventBus, new RuleConfiguration(),
                new ProvenanceConfig(new ConfigsForProvTesting("", null)));
        controller.parseXLIFFFile(new File(getClass().getResource(resource).toURI()),
                new File(getClass().getResource(resource).toURI()));
        return controller;
    }

    @Test
    public void testDefaultFlags() throws Exception {
        SegmentController controller = emptyController();
        assertFalse(controller.isDirty());
        assertFalse(controller.openFile());
    }
 
    @Test
    public void testSimpleXLIFF() throws Exception {
        SegmentController controller = loadResourceAsController("/test.xlf");
        assertEquals(2, controller.getNumSegments());
        assertEquals("en", controller.getFileSourceLang());
        assertEquals("fr", controller.getFileTargetLang());
        assertTrue(controller.openFile());
        assertFalse(controller.isDirty());
    }

    @Test
    public void testLQIDocStatsDuringSegmentLoad() throws Exception {
        SegmentController controller = emptyController();
        ITSDocStats stats = controller.getStats();
        assertEquals(0, stats.getStats().size());
        Segment seg = TestSegment.newSegment();
        LanguageQualityIssue lqi = RulesTestHelpers.lqi("omission", 85);
        seg.addLQI(lqi);
        controller.setSegments(Collections.singletonList(seg));
        assertEquals(Collections.singletonList(new LanguageQualityIssueStats(lqi)), stats.getStats());
    }
    
    @Test
    public void testProvDocStatsDuringSegmentLoad() throws Exception {
        SegmentController controller = emptyController();
        ITSDocStats stats = controller.getStats();
        List<ITSStats> expectedStats = Lists.newArrayList();
        Segment seg = new Segment();
        seg.addProvenance(new UserProvenance("a", "b", "c"));
        controller.setSegments(Collections.singletonList(seg));
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revPerson, "a"));
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revOrg, "b"));
        assertEquals(expectedStats, stats.getStats());
    }

    @Test
    public void testLQIDocStatsWhenModifyingLQIOnLiveSegment() throws Exception {
        SegmentController controller = emptyController();
        // XXX HACK - I need to "parse" a file to create the internal 
        // SegmentWriter and avoid a crash.  This is bad.
        controller.parseXLIFFFile(null, null);
        ITSDocStats stats = controller.getStats();
        ModifyLQIListener lqiListener = new ModifyLQIListener();
        DocStatsUpdateListener statsListener = new DocStatsUpdateListener();
        eventBus.register(lqiListener);
        eventBus.register(statsListener);
        Segment seg = TestSegment.newSegment();
        controller.setSegments(Collections.singletonList(seg));
        // Stats changed when we set segments
        assertEquals(1, statsListener.callbackCount);
        assertEquals(1, controller.getNumSegments());
        assertEquals(Collections.emptyList(), stats.getStats());
        // Adding an LQI to an existing segment should also trigger a
        // stats update
        LanguageQualityIssue lqi = RulesTestHelpers.lqi("omission", 85);
        seg.addLQI(lqi);
        assertEquals(Collections.singletonList(lqi), seg.getLQI());
        assertEquals(Collections.singletonList(new LanguageQualityIssueStats(lqi)), stats.getStats());
        assertEquals(1, lqiListener.callbackCount);
        assertEquals(2, statsListener.callbackCount);
        // Now test removal
        seg.removeLQI(lqi);
        assertEquals(Collections.emptyList(), stats.getStats());
        assertEquals(Collections.emptyList(), seg.getLQI());
        assertEquals(2, lqiListener.callbackCount);
        assertEquals(3, statsListener.callbackCount);
    }

    @Test
    public void testDocStatsWhenAddingProvOnLiveSegment() throws Exception {
        SegmentController controller = emptyController();
        controller.parseXLIFFFile(null, null); // XXX see above
        ITSDocStats stats = controller.getStats();
        ProvenanceAddedListener listener = new ProvenanceAddedListener();
        eventBus.register(listener);
        Segment seg = TestSegment.newSegment();
        controller.setSegments(Collections.singletonList(seg));
        assertEquals(Collections.emptyList(), stats.getStats());
        Provenance prov = new UserProvenance("a", "b", "c");
        seg.addProvenance(prov);
        assertEquals(Collections.singletonList(prov), seg.getProv());
        List<ITSStats> expectedStats = Lists.newArrayList();
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revPerson, "a"));
        expectedStats.add(new ProvenanceStats(ProvenanceStats.Type.revOrg, "b"));
        assertEquals(expectedStats, stats.getStats());
        assertTrue(listener.called);
    }
    
    @Test
    public void testResetTarget() throws Exception {
        SegmentController controller = emptyController();
        controller.parseXLIFFFile(null, null); // XXX see above
        controller.setSegments(Collections.singletonList(TestSegment.newSegment()));
        Segment seg = controller.getSegment(0);
        ResetTargetListener resetListener = new ResetTargetListener();
        SegmentUpdateListener updateListener = new SegmentUpdateListener();
        eventBus.register(resetListener);
        eventBus.register(updateListener);
        seg.updateTarget(new SimpleSegmentVariant("newtarget"));
        assertEquals(1, updateListener.callbackCount);
        seg.resetTarget();
        assertTrue(resetListener.called);
    }

    class ProvenanceAddedListener {
        boolean called = false;
        @Subscribe
        public void callback(ProvenanceAddedEvent e) {
            called = true;
        }
    }
    class ResetTargetListener {
        boolean called = false;
        @Subscribe
        public void callback(SegmentTargetResetEvent e) {
            called = true;
        }
    }
    class ModifyLQIListener {
        int callbackCount = 0;
        @Subscribe
        public void callback(LQIModificationEvent e) {
            callbackCount++;
        }
    }
    class SegmentUpdateListener {
        int callbackCount = 0;
        @Subscribe
        public void callback(SegmentEditEvent e) {
            callbackCount++;
        }
    }
    class DocStatsUpdateListener {
        int callbackCount = 0;
        @Subscribe
        public void callback(ITSDocStatsChangedEvent e) {
            callbackCount++;
        }
    }
}
