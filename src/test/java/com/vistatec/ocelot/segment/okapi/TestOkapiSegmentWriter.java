package com.vistatec.ocelot.segment.okapi;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;

import org.junit.*;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.segment.OcelotSegment;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.SimpleSegment;
import com.vistatec.ocelot.segment.SimpleSegmentVariant;

public class TestOkapiSegmentWriter {
    private final Mockery mockery = new Mockery();

    private final OcelotEventQueue mockEventQueue = mockery.mock(OcelotEventQueue.class);

    @Test
    public void testMissingProvenance() {
        OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant(""))
                .target(new SimpleSegmentVariant(""))
                .build();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        // pass empty provenance properties
        TestSegmentWriter segmentWriter = new TestSegmentWriter(
                new TestProvenanceConfig(null, null, null), mockEventQueue);
        // OC-16: make sure this doesn't crash
        ITSProvenanceAnnotations provAnns = segmentWriter.addOcelotProvenance(seg);
        // We shouldn't add a second annotation record for our empty user provenance
        assertEquals(1, provAnns.getAnnotations("its-prov").size());

        // Do it again, make sure it doesn't crash
        ITSProvenanceAnnotations provAnns2 = segmentWriter.addOcelotProvenance(seg);
        assertEquals(1, provAnns2.getAnnotations("its-prov").size());
    }

    @Test
    public void testDontAddRedundantProvenance() throws Exception {
        OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant(""))
                .target(new SimpleSegmentVariant(""))
                .build();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        TestSegmentWriter segmentWriter = new TestSegmentWriter(
                new TestProvenanceConfig("T", "S", "X"), mockEventQueue);
        ITSProvenanceAnnotations provAnns = segmentWriter.addOcelotProvenance(seg);
        assertEquals(1, provAnns.getAnnotations("its-prov").size());
    }

    @Test
    public void testAddUserProvenance() throws Exception {
        final OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant(""))
                .target(new SimpleSegmentVariant(""))
                .build();
        Provenance prov = new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X"));
        seg.addProvenance(prov);
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ProvenanceAddEvent.class)));
        }});

        TestSegmentWriter segmentWriter = new TestSegmentWriter(
                new TestProvenanceConfig("A", "B", "C"), mockEventQueue);
        ITSProvenanceAnnotations provAnns = segmentWriter.addOcelotProvenance(seg);
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
        TestSegmentWriter(ProvenanceConfig config, OcelotEventQueue eventQueue) {
            super(config, eventQueue);
        }
        @Override
        public void updateSegment(OcelotSegment seg) {
        }
    }
}
