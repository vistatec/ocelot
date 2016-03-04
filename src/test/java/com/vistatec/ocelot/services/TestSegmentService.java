package com.vistatec.ocelot.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.events.ItsDocStatsUpdateLqiEvent;
import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class TestSegmentService {
    private final Mockery mockery = new Mockery();

    private SegmentService segmentService;
    private final OcelotEventQueue mockEventQueue = mockery.mock(OcelotEventQueue.class);

    @Before
    public void before() {
        this.segmentService = new SegmentServiceImpl(mockEventQueue);
    }

    @Test
    public void testResetSegmentTarget() {
        final OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("source")
                .target("target")
                .originalTarget("original_target")
                .build();

        final XLIFFDocument xliff = mockery.mock(XLIFFDocument.class);
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(SegmentEditEvent.class)));
            allowing(xliff).getSegments();
                will(returnValue(Collections.singletonList(seg)));
        }});

        segmentService.resetSegmentTarget(new SegmentTargetResetEvent(xliff, seg));
        assertTrue(seg.getTarget().getDisplayText().equals(
                seg.getOriginalTarget().getDisplayText()));
    }

    @Test
    public void testItsDocStatsLoadSegments() {
        final List<OcelotSegment> segments = new ArrayList<>();
        segments.add(new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("source")
                .target("target")
                .originalTarget("original_target")
                .build());

        final XLIFFDocument xliff = mockery.mock(XLIFFDocument.class);
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ItsDocStatsRecalculateEvent.class)));
            allowing(xliff).getSegments();
                will(returnValue(segments));
        }});

        assertEquals(0, segmentService.getNumSegments());
        segmentService.setSegments(xliff);
        assertEquals(1, segmentService.getNumSegments());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testEditLQI() {
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ItsDocStatsUpdateLqiEvent.class)));
            oneOf(mockEventQueue).post(with(any(SegmentEditEvent.class)));
            oneOf(mockEventQueue).post(with(any(LQIModificationEvent.class)));
        }});

        LanguageQualityIssue lqi = RulesTestHelpers.lqi("omission", 85);
        OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("source")
                .target("target")
                .originalTarget("original_target")
                .build();
        seg.addLQI(lqi);

        LanguageQualityIssue modifiedLqi = RulesTestHelpers.lqi("grammar", 75);
        assertTrue(!seg.getLQI().isEmpty());
        assertTrue(seg.getLQI().get(0).getType().equals("omission"));
        assertTrue(seg.getLQI().get(0).getSeverity() == 85);

        segmentService.editLQI(new LQIEditEvent(modifiedLqi, lqi, seg, lqi));

        assertTrue(!seg.getLQI().isEmpty());
        assertTrue(seg.getLQI().get(0).getType().equals("grammar"));
        assertTrue(seg.getLQI().get(0).getSeverity() == 75);
    }
}
