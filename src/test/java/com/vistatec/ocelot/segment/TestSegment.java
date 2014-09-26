package com.vistatec.ocelot.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Other tests in SegmentController are also relevant to Segment
 * functionality.
 */
public class TestSegment {

    @Test
    public void testMultipleSegmentUpdates() throws Exception {
        Segment seg = newSegment();
        SegmentVariant originalTarget = seg.getTarget();
        SegmentVariant newTarget1 = new SimpleSegmentVariant("update1");
        seg.updateTarget(newTarget1);
        assertEquals(newTarget1, seg.getTarget());
        assertEquals(originalTarget, seg.getOriginalTarget());
        SegmentVariant newTarget2 = new SimpleSegmentVariant("update2");
        seg.updateTarget(newTarget2);
        assertEquals(newTarget2, seg.getTarget());
        // Original target is still the -original- target.
        assertEquals(originalTarget, seg.getOriginalTarget());
    }

    @Test
    public void testResetTarget() {
        Segment seg = newSegment();
        seg.updateTarget(new SimpleSegmentVariant("update"));
        assertTrue(seg.hasOriginalTarget());
        assertEquals("update", seg.getTarget().getDisplayText());
        assertNotNull(seg.getOriginalTarget());
        assertEquals("target", seg.getOriginalTarget().getDisplayText());
        seg.resetTarget();
        assertEquals("target", seg.getTarget().getDisplayText());
        assertTrue(seg.hasOriginalTarget());
        assertNotNull(seg.getOriginalTarget());
        assertEquals("target", seg.getTarget().getDisplayText());
        // Make sure the target diff got reset. XXX This has an ugly
        // dependency on the diff language.
        assertEquals(Lists.newArrayList("target", "regular"), seg.getTargetDiff());
    }

    @Test
    public void testResetWithNoOriginalTarget() {
        Segment seg = newSegment();
        seg.resetTarget();
        assertEquals("target", seg.getTarget().getDisplayText());
    }

    @Test
    public void testTargetChangesAffectEditDistance() {
        Segment seg = newSegment();
        seg.updateTarget(new SimpleSegmentVariant("targetA"));
        assertEquals(1, seg.getEditDistance());
        seg.updateTarget(new SimpleSegmentVariant("targetAB"));
        assertEquals(2, seg.getEditDistance());
        seg.resetTarget();
        assertEquals(0, seg.getEditDistance());
    }

    private static int nextSegmentId = 1;
    public static Segment newSegment() {
        int id = nextSegmentId++;
        return new Segment(id, id, id, new SimpleSegmentVariant("source"),
                new SimpleSegmentVariant("target"), null);
    }
}
