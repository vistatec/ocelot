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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

public class TestOkapiXliff20Parser {

    @Test
    public void testParser() throws URISyntaxException, IOException {
        File testFile = new File(TestOkapiXliff20Parser.class.getResource(
                "XLIFF2.0_example.xlf").toURI());
        OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
        List<OcelotSegment> testSegments = parser.parse(testFile);
        List<OcelotSegment> goalSegments = getGoalSegments();
        assertTrue(testSegments.size() > 0);
        compareSegmentsIgnoringWhitespace(testSegments, goalSegments);
    }

    @Test
    public void testTagParser() throws URISyntaxException, IOException {
        File testFile = new File(TestOkapiXliff20Parser.class.getResource(
                "LQE_xliff_2.0.xlf").toURI());
        OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
        List<OcelotSegment> testSegments = parser.parse(testFile);
        List<OcelotSegment> goalSegments = getTagGoalSegments();
        compareSegmentsIgnoringWhitespace(testSegments, goalSegments);
    }

    @Test
    public void testMissingTarget() throws Exception {
        File testFile = new File(getClass().getResource("/xliff20/noTargets.xlf").toURI());
        OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
        List<OcelotSegment> testSegments = parser.parse(testFile);
        assertEquals(6, testSegments.size());
    }
    
    @Test
	public void testEnrichedFile() throws Exception {

		File testFile = new File(getClass().getResource(
		        "/xliff20/xliff2.0.enriched.xlf").toURI());
		OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
		List<OcelotSegment> testSegments = parser.parse(testFile);
		Map<String, List<Enrichment>> expectedEnrichMap = EnrichmentBuilder
		        .getExpectedEnrichmentsXliff20();
		for (OcelotSegment segment : testSegments) {
			EnrichmentAssertor.assertEnrichments(
			        expectedEnrichMap.get(segment.getSegmentId()),
			        new ArrayList<Enrichment>(((BaseSegmentVariant) segment
			                .getSource()).getEnirchments()));
		}
	}

    public void compareSegmentsIgnoringWhitespace(List<OcelotSegment> testSegs, List<OcelotSegment> goalSegs) {
        Iterator<OcelotSegment> testIter = testSegs.iterator();
        Iterator<OcelotSegment> goalIter = goalSegs.iterator();
        while (testIter.hasNext()) {
            OcelotSegment testSeg = testIter.next();
            OcelotSegment goalSeg = goalIter.next();
            assertEquals(goalSeg.getSource().getDisplayText().replaceAll("\\s", ""),
                    testSeg.getSource().getDisplayText().replaceAll("\\s", ""));
            assertEquals(goalSeg.getTarget().getDisplayText().replaceAll("\\s", ""),
                    testSeg.getTarget().getDisplayText().replaceAll("\\s", ""));
        }
        assertFalse(goalIter.hasNext());
    }

    public List<OcelotSegment> getGoalSegments() {
        List<OcelotSegment> segs = new ArrayList<>();

        SimpleSegment.Builder seg1 = new SimpleSegment.Builder()
                .segmentNumber(1);
        seg1.source().text("Sentence 1. Sentence 2.");
        seg1.target().text("");
        segs.add(seg1.build());

        SimpleSegment.Builder seg2 = new SimpleSegment.Builder()
                .segmentNumber(2);
        seg2.source().text("Sentence 3 (no-trans). Sentence 4 (no-trans).");
        seg2.target().text("");
        segs.add(seg2.build());

        SimpleSegment.Builder seg3 = new SimpleSegment.Builder()
                .segmentNumber(3);
        seg3.source().text("Sentence 5.");
        seg3.target().text("");
        segs.add(seg3.build());

        SimpleSegment.Builder seg4 = new SimpleSegment.Builder()
                .segmentNumber(4);
        seg4.source().text("Sentence 6 (no-trans).");
        seg4.target().text("");
        segs.add(seg4.build());

        SimpleSegment.Builder seg5 = new SimpleSegment.Builder()
                .segmentNumber(5);
        seg5.source().text("Sentence 7. Sentence 8. ");
        seg5.target().text("");
        segs.add(seg5.build());

        SimpleSegment.Builder seg6 = new SimpleSegment.Builder()
                .segmentNumber(6);
        seg6.source().text("Sentence with A. Sentence with <cp hex=\"0001\"/>. ");
        seg6.target().text("Sentence with A. Sentence with <cp hex=\"0001\"/>. ");
        segs.add(seg6.build());

        return segs;
    }

    public List<OcelotSegment> getTagGoalSegments() {
        List<OcelotSegment> segs = new ArrayList<>();

        SimpleSegment.Builder seg1 = new SimpleSegment.Builder();
        seg1.source().text("Sentence 1.").code("1", "<mrk1>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\">")
                .text("LQI").code("1", "</mrk1>", "</mrk>").text(" Sentence 2.");
        seg1.target().text("Sentence 1.").code("1", "<mrk2>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\">")
                .text("Prov").code("1", "</mrk2>", "</mrk>").text(" Sentence 2.");
        segs.add(seg1.build());

        SimpleSegment.Builder seg2 = new SimpleSegment.Builder();
        seg2.source().text("Sentence with A. Sentence with <cp hex=\"0001\"/>. ");
        seg2.target().text("Sentence with A. Sentence with <cp hex=\"0001\"/>. ");
        segs.add(seg2.build());

        SimpleSegment.Builder seg3 = new SimpleSegment.Builder();
        seg3.source().text("Ph element ").code("1", "<phph1/>", "<ph id=\"ph1\"/>").text(" #1.");
        seg3.target().text("Ph element ").code("1", "<phph1/>", "<ph id=\"ph1\"/>").text(" #1.");
        segs.add(seg3.build());

        SimpleSegment.Builder seg4 = new SimpleSegment.Builder();
        seg4.source().text("Pc element ").code("1", "<pcpc1>", "<pc id=\"pc1\">").text("Important")
                .code("1", "</pcpc1>", "</pc>").text(" #1.");
        seg4.target().text("Pc element ").code("1", "<pcpc1>", "<pc id=\"pc1\">")
                .text("Important").code("1", "</pcpc1>", "</pc>").text(" #1.");
        segs.add(seg4.build());

        SimpleSegment.Builder seg5 = new SimpleSegment.Builder();
        seg5.source().text("Text in ").code("1", "<scsc1/>", "<sc id=\"sc1\"")
                .text("bold ").code("1", "<pcsc2>", "<sc id=\"sc2\"/>")
                .text("and").code("1", "<ecsc1/>", "<ec startRef=\"sc1\"/>")
                .text(" italics").code("1", "</pcsc2>", "<ec startRef=\"sc2\"/>")
                .text(".");
        seg5.target().text("Text in ").code("1", "<scsc1/>", "<sc id=\"sc1\"/>")
                .text("bold ").code("1", "<pcsc2>", "<sc id=\"sc2\"/>")
                .text("and").code("1", "<ecsc1/>", "<ec startRef=\"sc1\"/>")
                .text(" italics").code("1", "</pcsc2>", "<ec startRef=\"sc2\"/>")
                .text(".");
        segs.add(seg5.build());

        SimpleSegment.Builder seg6 = new SimpleSegment.Builder();
        seg6.source().text("Mrk element ").code("1", "<mrkmrk1>", "<mrk id=\"mrk1\" translate=\"yes\">")
                .text("Important").code("1", "</mrkmrk1>", "</mrk>").text(" #1.");
        seg6.target().text("Mrk element ").code("1", "<mrkmrk1>", "<mrk id=\"mrk1\" translate=\"yes\">")
                .text("Important").code("1", "</mrkmrk1>", "</mrk>").text(" #1.");
        segs.add(seg6.build());

        SimpleSegment.Builder seg7 = new SimpleSegment.Builder();
        seg7.source().text("Sm split element ").code("1", "<smsm1/>", "<sm id=\"sm1\" translate=\"no\"/>")
                .text(" #1.");
        seg7.target().text("Sm split element ").code("1", "<smsm1/>", "<sm id=\"sm1\" translate=\"no\"/>")
                .text(" #1.");
        segs.add(seg7.build());

        SimpleSegment.Builder seg8 = new SimpleSegment.Builder();
        seg8.source().text("Em split element ").code("1", "<emsm1/>", "<em startRef=\"sm1\"/>")
                .text(" #1.");
        seg8.target().text("Em split element ").code("1", "<emsm1/>", "<em startRef=\"sm1\"/>")
                .text(" #1.");
        segs.add(seg8.build());
        return segs;
    }
    
    @Test
    public void testReadNotesMultipleSegmentsPerUnit() throws Exception{
    	
    	File testFile = new File(TestOkapiXliff20Parser.class.getResource(
                "file_with_notes_multiple_segm_2.0.xlf").toURI());
		OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
		List<OcelotSegment> testSegments = parser.parse(testFile);
		for(OcelotSegment segment: testSegments){
			if(segment.getSegmentId().equals("s1")){
				assertEquals(1, segment.getNotes().size());
				assertEquals("This is a note for the segment s1", segment.getNotes().getOcelotNote().getContent());
			} else if(segment.getSegmentId().equals("s3")){
				assertEquals(1, segment.getNotes().size());
				assertEquals("This is a note for the segment s3", segment.getNotes().getOcelotNote().getContent());
			} else {
				assertEquals(0, segment.getNotes().size());
			}
		}
    }
    
    @Test
    public void testReadNotesOneSegmentPerUnit() throws Exception {
    	
    	File testFile = new File(TestOkapiXliff20Parser.class.getResource(
                "file_with_notes_single_segm_2.0.xlf").toURI());
		OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
		List<OcelotSegment> testSegments = parser.parse(testFile);
		for(OcelotSegment segment: testSegments){
			if(segment.getSegmentId().equals("s1")){
				assertEquals(1, segment.getNotes().size());
				assertEquals("Note for Segment s1", segment.getNotes().getOcelotNote().getContent());
			} else if(segment.getSegmentId().equals("s6")){
				assertEquals(1, segment.getNotes().size());
				assertEquals("Note for Segment s6", segment.getNotes().getOcelotNote().getContent());
			} else {
				assertEquals(0, segment.getNotes().size());
			}
		}
    }
    
}
