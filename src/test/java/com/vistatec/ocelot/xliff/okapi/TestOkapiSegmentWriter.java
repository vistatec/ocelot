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
package com.vistatec.ocelot.xliff.okapi;

import com.vistatec.ocelot.its.model.okapi.OkapiProvenance;
import com.vistatec.ocelot.xliff.okapi.OkapiXLIFF12Writer;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;

import org.junit.*;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.segment.model.SimpleSegment;

public class TestOkapiSegmentWriter {
    private final Mockery mockery = new Mockery();

    private final OcelotEventQueue mockEventQueue = mockery.mock(OcelotEventQueue.class);

    @Test
    public void testMissingProvenance() {
        OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("")
                .target("")
                .build();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        // pass empty provenance properties
        OkapiXLIFF12Writer segmentWriter = new OkapiXLIFF12Writer(null,
                new UserProvenance(null, null, null), mockEventQueue);
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
                .source("")
                .target("")
                .build();
        seg.addProvenance(new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X")));
        OkapiXLIFF12Writer segmentWriter = new OkapiXLIFF12Writer(null,
                new UserProvenance("T", "S", "X"), mockEventQueue);
        ITSProvenanceAnnotations provAnns = segmentWriter.addOcelotProvenance(seg);
        assertEquals(1, provAnns.getAnnotations("its-prov").size());
    }

    @Test
    public void testAddUserProvenance() throws Exception {
        final OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("")
                .target("")
                .build();
        Provenance prov = new OkapiProvenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_REVORG, "S",
                GenericAnnotationType.PROV_REVPERSON, "T",
                GenericAnnotationType.PROV_PROVREF, "X"));
        seg.addProvenance(prov);
        mockery.checking(new Expectations() {{
            oneOf(mockEventQueue).post(with(any(ProvenanceAddEvent.class)));
        }});

        OkapiXLIFF12Writer segmentWriter = new OkapiXLIFF12Writer(null,
                new UserProvenance("A", "B", "C"), mockEventQueue);
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
}
