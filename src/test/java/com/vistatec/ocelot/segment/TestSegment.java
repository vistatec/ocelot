package com.vistatec.ocelot.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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


    private static int nextSegmentId = 1;
    public static Segment newSegment() {
        int id = nextSegmentId++;
        return new Segment(id, id, id, new SimpleSegmentVariant("source"),
                new SimpleSegmentVariant("target"), null);
    }
}
