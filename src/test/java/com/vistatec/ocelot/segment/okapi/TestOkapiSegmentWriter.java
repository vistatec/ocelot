package com.vistatec.ocelot.segment.okapi;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;

import org.junit.*;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentController;

import static org.junit.Assert.*;

public class TestOkapiSegmentWriter {

    @Test
    public void testMissingProvenance() {
        Segment seg = new Segment();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        // pass empty provenance properties
        TestSegmentWriter segmentWriter = new TestSegmentWriter(new TestProvenanceConfig(null, null, null));
        // OC-16: make sure this doesn't crash
        ITSProvenanceAnnotations provAnns = segmentWriter.addRWProvenance(seg);
        // We shouldn't add a second annotation record for our empty user provenance
        assertEquals(1, provAnns.getAnnotations("its-prov").size());

        // Do it again, make sure it doesn't crash
        ITSProvenanceAnnotations provAnns2 = segmentWriter.addRWProvenance(seg);
        assertEquals(1, provAnns2.getAnnotations("its-prov").size());
    }

    @Test
    public void testDontAddRedundantProvenance() throws Exception {
        Segment seg = new Segment();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        TestSegmentWriter segmentWriter = new TestSegmentWriter(new TestProvenanceConfig("T", "S", "X"));
        ITSProvenanceAnnotations provAnns = segmentWriter.addRWProvenance(seg);
        assertEquals(1, provAnns.getAnnotations("its-prov").size());
    }

    @Test
    public void testAddUserProvenance() throws Exception {
        Segment seg = new Segment();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        TestSegmentWriter segmentWriter = new TestSegmentWriter(new TestProvenanceConfig("A", "B", "C"));
        ITSProvenanceAnnotations provAnns = segmentWriter.addRWProvenance(seg);
        assertEquals(2, provAnns.getAnnotations("its-prov").size());
        GenericAnnotation origAnno = provAnns.getAnnotations("its-prov").get(0);
        assertEquals("T", origAnno.getString(GenericAnnotationType.PROV_REVPERSON));
        assertEquals("S", origAnno.getString(GenericAnnotationType.PROV_REVORG));
        assertEquals("X", origAnno.getString(GenericAnnotationType.PROV_PROVREF));
        GenericAnnotation userAnno = provAnns.getAnnotations("its-prov").get(1);
        assertEquals("A", userAnno.getString(GenericAnnotationType.PROV_REVPERSON));
        assertEquals("B", userAnno.getString(GenericAnnotationType.PROV_REVORG));
        assertEquals("C", userAnno.getString(GenericAnnotationType.PROV_PROVREF));
    }

    class TestProvenanceConfig extends ProvenanceConfig {
        private String revPerson, revOrg, extRef;
        public TestProvenanceConfig(String revPerson, String revOrg, String extRef) {
            super();
            this.revPerson = revPerson;
            this.revOrg = revOrg;
            this.extRef = extRef;
        }
        @Override
        public UserProvenance getUserProvenance() {
            return new UserProvenance(revPerson, revOrg, extRef);
        }
    }
    
    class TestSegmentWriter extends OkapiSegmentWriter {
        TestSegmentWriter(ProvenanceConfig config) {
            super(config);
        }
        @Override
        public void updateEvent(Segment seg, SegmentController segController) {
        }
    }
}
