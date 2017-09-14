/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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

import static com.vistatec.ocelot.rules.StateQualifier.EXACT;
import static com.vistatec.ocelot.rules.StateQualifier.FUZZY;
import static com.vistatec.ocelot.rules.StateQualifier.ID;
import static com.vistatec.ocelot.rules.StateQualifier.MT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.OtherITSMetadata;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Test Okapi XLIFF parser conversion to Ocelot Segments.
 */
public class TestXLIFFParser {

    @Test
    public void testTargetLocales() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();

        LocaleId frFr = LocaleId.fromBCP47("fr-FR");
        for (OcelotSegment seg : parser.parse(new File(getClass().getResource("xliff_test.xlf").toURI()))) {
            assertTrue(seg instanceof OkapiSegment);
            OkapiSegment okapiSeg = (OkapiSegment) seg;
            Event e = parser.getSegmentEvent(okapiSeg.eventNum);
            ITextUnit tu = e.getTextUnit();
            TextContainer tc = ((TextContainerVariant)seg.getTarget()).getTextContainer();
            assertEquals(tu.getTarget(frFr), tc);
        }
    }

    @Test
    public void testXLIFFToSegment() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<OcelotSegment> segments = parser.parse(new File(getClass().getResource("xliff_test.xlf").toURI()));

        testReadProvenance(segments.get(0));
        testReadMultipleProv(segments.get(1));
        testReadLQI(segments.get(2));
        testReadMultipleLQI(segments.get(3));
        testReadExistingAltTrans(segments.get(4));
        testIgnoreUnrelatedAltTrans(segments.get(5));
        testReadCorrectAltTrans(segments.get(6));
        testReadReviewPhaseName(segments.get(7));
        testReadRebuttalPhaseName(segments.get(8));
        testReadFinalReviewPhaseName(segments.get(9));
        testReadTranslatorApprovalPhaseName(segments.get(10));
        testReadUnhandledPhaseName(segments.get(11));
        testReadMissingPhaseRef(segments.get(12));
        testReadMTConfidence(segments.get(13));
    }
    
    @Test
    public void testEnrichedFile() throws Exception {
    	
		OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
		List<OcelotSegment> segments = parser.parse(new File(getClass()
		        .getResource("xliff1.2.enriched.xlf").toURI()));
		Map<Integer, List<Enrichment>> expectedEnrichMap = EnrichmentBuilder
		        .getExpectedEnrichmentsXliff12();
		for (OcelotSegment segment : segments) {
			EnrichmentAssertor.assertEnrichments(
			        expectedEnrichMap.get(segment.getSegmentNumber()),
			        new ArrayList<Enrichment>(((BaseSegmentVariant) segment
			                .getSource()).getEnirchments()));
		}
        
    }

    public void testReadProvenance(OcelotSegment seg) {
        List<Provenance> provRecords = seg.getProvenance();
        assertEquals("Discrepancy in provenance records", 1, provRecords.size());
        Provenance prov = provRecords.get(0);
        assertEquals("Provenance person is incorrect", "translator-1", prov.getPerson());
        assertEquals("Provenance revPerson is incorrect", "reviewer-1", prov.getRevPerson());
        assertEquals("Provenance org is incorrect", "VistaTEC", prov.getOrg());
        assertEquals("Provenance revOrg is incorrect", "VistaTEC", prov.getRevOrg());
        assertEquals("Provenance tool is incorrect", "Ocelot", prov.getTool());
        assertEquals("Provenance revTool is incorrect", "Ocelot", prov.getRevTool());
    }

    public void testReadMultipleProv(OcelotSegment seg) {
        List<Provenance> provRecords = seg.getProvenance();
        assertEquals("Discrepancy in provenance records", 2, provRecords.size());
        for (Provenance prov : provRecords) {
            if (prov.getPerson() != null) {
                assertEquals("Provenance person is incorect", "translator-2", prov.getPerson());
                assertEquals("Provenance org is incorect", "VistaTEC", prov.getOrg());
                assertEquals("Provenance tool is incorect", "Ocelot", prov.getTool());
            } else {
                assertEquals("Provenance revPerson is incorect", "reviewer-2", prov.getRevPerson());
                assertEquals("Provenance revOrg is incorect", "VistaTEC", prov.getRevOrg());
                assertEquals("Provenance revTool is incorect", "Ocelot", prov.getRevTool());
            }
        }
    }

    public void testReadLQI(OcelotSegment seg) {
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 1, lqiRecords.size());
        LanguageQualityIssue lqi = lqiRecords.get(0);
        assertTrue("LQI severity is incorrect", 70 == lqi.getSeverity());
        assertEquals("LQI type is incorrect", "mistranslation", lqi.getType());
        assertEquals("LQI comment is incorrect", "comment1", lqi.getComment());
    }

    public void testReadMultipleLQI(OcelotSegment seg) {
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 2, lqiRecords.size());
        for (LanguageQualityIssue lqi : lqiRecords) {
            if (lqi.getSeverity() == 70) {
                assertEquals("LQI type is incorrect", "mistranslation", lqi.getType());
            } else {
                assertEquals("LQI type is incorrect", "untranslated", lqi.getType());
            }
            assertEquals("LQI comment is incorrect", "comment2", lqi.getComment());
        }
    }

    public void testReadSourceTargetLQI(OcelotSegment seg) {
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 1, lqiRecords.size());
    }

    public void testReadExistingAltTrans(OcelotSegment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target is incorrect", "Original example target 5", originalTarget.getDisplayText());
    }

    public void testIgnoreUnrelatedAltTrans(OcelotSegment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target", "", originalTarget.getDisplayText());
    }

    public void testReadCorrectAltTrans(OcelotSegment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target is incorrect", "Original example target 7", originalTarget.getDisplayText());
    }

    public void testReadReviewPhaseName(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertEquals("Phase name is incorrect", "review", okapiSeg.phaseName);
        assertTrue(seg.isEditable());
    }

    public void testReadRebuttalPhaseName(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertEquals("Phase name is incorrect", "rebuttal", okapiSeg.phaseName);
        assertFalse(seg.isEditable());
    }

    public void testReadFinalReviewPhaseName(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertEquals("Phase name is incorrect", "final review", okapiSeg.phaseName);
        assertTrue(seg.isEditable());
    }

    public void testReadTranslatorApprovalPhaseName(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertEquals("Phase name is incorrect", "translator approval", okapiSeg.phaseName);
        assertFalse(seg.isEditable());
    }

    public void testReadUnhandledPhaseName(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertEquals("Phase name is incorrect", "unknown", okapiSeg.phaseName);
        assertTrue(seg.isEditable());
    }

    public void testReadMissingPhaseRef(OcelotSegment seg) {
        assertTrue(seg instanceof OkapiSegment);
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        assertNull(okapiSeg.phaseName);
        assertTrue(seg.isEditable());
    }

    public void testReadMTConfidence(OcelotSegment seg) {
        List<OtherITSMetadata> otherITSMetadata = seg.getOtherITSMetadata();
        assertNotNull(otherITSMetadata);
        assertEquals("Discrepancy in the number of MTConfidence annotations", 1, otherITSMetadata.size());
        Double mtConfidence = (Double)otherITSMetadata.get(0).getValue();
        assertEquals(0.85, mtConfidence, 0.01);
    }

    @Test
    public void testStateQualifiers() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<OcelotSegment> segments = parser.parse(
                new File(getClass().getResource("state_qualifiers.xlf").toURI()));
        assertEquals(ID, segments.get(0).getStateQualifier());
        assertEquals(EXACT, segments.get(1).getStateQualifier());
        assertEquals(MT, segments.get(2).getStateQualifier());
        assertEquals(FUZZY, segments.get(3).getStateQualifier());
        assertEquals(null, segments.get(4).getStateQualifier());
        assertEquals(null, segments.get(5).getStateQualifier());
    }

    @Test
    public void testEmptyAltTransTarget() throws Exception {
        // OC-26. Workaround for an issue in the Okapi XLIFF reader
        // (Okapi Issue 412).  If the alt-trans contains an empty
        // target, don't crash.
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<OcelotSegment> segments = parser.parse(new File(getClass().getResource("/oc26.xlf").toURI()));
        assertEquals(1, segments.size());
    }
    
    @Test
    public void testReadNotes() throws Exception {
    	
    	OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
    	File testFile = new File(getClass()
		        .getResource("test_file_with_note_xliff1.2.xlf").toURI());
    	List<OcelotSegment> segments = parser.parse(testFile);
    	OcelotSegment firstSegment = null;
    	for(OcelotSegment segment: segments){
    		if(segment.getSegmentNumber() == 1){
    			firstSegment = segment;
    			break;
    		}
    	}
    	
    	assertNotNull(firstSegment.getNotes());
    	assertEquals(firstSegment.getNotes().size(), 1);
    	assertEquals(firstSegment.getNotes().get(0).getContent(), "Note 1");
    }
}
