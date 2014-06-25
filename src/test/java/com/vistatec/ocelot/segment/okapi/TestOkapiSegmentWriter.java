package com.vistatec.ocelot.segment.okapi;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;

import org.junit.*;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentController;

import static org.junit.Assert.*;

public class TestOkapiSegmentWriter {

    @Test
    public void testMissingProvenance() throws Exception {
        Segment seg = new Segment();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        // pass empty provenance properties
        TestSegmentWriter segmentWriter = new TestSegmentWriter(new UserProvenance(null, null, null));
        // OC-16: make sure this doesn't crash
        ITSProvenanceAnnotations provAnns = segmentWriter.addRWProvenance(seg);
        // XXX It's not clear to me that this is the correct behavior, but it's the
        // current behavior.  A bogus second its-prov annotation with 
        // null/empty values is added.
        assertEquals(2, provAnns.getAnnotations("its-prov").size());
    }

    @Test
    public void testDontAddRedundantProvenance() throws Exception {
        Segment seg = new Segment();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        TestSegmentWriter segmentWriter = new TestSegmentWriter(new UserProvenance("T", "S", "X"));
        ITSProvenanceAnnotations provAnns = segmentWriter.addRWProvenance(seg);
        assertEquals(1, provAnns.getAnnotations("its-prov").size());
    }

    class TestSegmentWriter extends OkapiSegmentWriter {
        TestSegmentWriter(UserProvenance prov) {
            super(prov);
        }
        @Override
        public void updateEvent(Segment seg, SegmentController segController) {
        }
    }
}
