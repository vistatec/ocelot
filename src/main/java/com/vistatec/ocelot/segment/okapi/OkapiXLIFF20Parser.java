/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.okapi;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.XLIFFParser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import net.sf.okapi.lib.xliff2.core.MTag;

import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.StartXliffData;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.TagType;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.its.LocQualityIssue;
import net.sf.okapi.lib.xliff2.its.LocQualityIssues;
import net.sf.okapi.lib.xliff2.its.Provenances;
import net.sf.okapi.lib.xliff2.reader.Event;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;

/**
 * Parse XLIFF 2.0 file for use in the workbench.
 */
public class OkapiXLIFF20Parser implements XLIFFParser {
    private List<Event> events;
    private List<Part> segmentUnitParts;
    private Map<Integer, Integer> segmentEventMapping;
    private int documentSegmentNum;
    private String sourceLang, targetLang;

    public List<Event> getEvents() {
        return this.events;
    }

    public Event getSegmentEvent(int segEventNumber) {
        return this.events.get(segmentEventMapping.get(segEventNumber));
    }

    public Part getSegmentUnitPart(int segmentUnitPartIndex) {
        return this.segmentUnitParts.get(segmentUnitPartIndex);
    }

    @Override
    public List<Segment> parse(File xliffFile) throws IOException {
        List<Segment> segments = new LinkedList<Segment>();
        segmentEventMapping = new HashMap<Integer, Integer>();
        events = new LinkedList<Event>();
        segmentUnitParts = new LinkedList<Part>();
        this.documentSegmentNum = 1;
        int segmentUnitPartIndex = 0;

        XLIFFReader reader = new XLIFFReader();
        reader.open(xliffFile);
        while (reader.hasNext()) {
            Event event = reader.next();
            this.events.add(event);

            if (event.isStartXliff()) {
                StartXliffData xliffElement = event.getStartXliffData();
                this.sourceLang = xliffElement.getSourceLanguage();
                // optional unless document contains target elements underneath <segment> or <ignorable>
                if (xliffElement.getTargetLanguage() != null) {
                    this.targetLang = xliffElement.getTargetLanguage();
                }

            } else if (event.isUnit()) {
                Unit unit = event.getUnit();
                for (Part unitPart : unit) {
                    if (unitPart.isSegment()) {
                        segments.add(convertPartToSegment(unitPart, segmentUnitPartIndex++));
                        this.segmentUnitParts.add(unitPart);
                    }
                }
            }

        }
        return segments;
    }

    /**
     * Converts Okapi XLIFF 2.0 Unit Parts to the Ocelot Segment format.
     * @param unitPart &lt;segment> or &lt;ignorable> element. See {@link Part} for more details.
     * @param segmentUnitPartIndex - Index of the associated original Okapi XLIFF 2.0 Event from which the Segment was derived.
     * @return Segment - Ocelot Segment
     * @throws MalformedURLException
     */
    private Segment convertPartToSegment(Part unitPart, int segmentUnitPartIndex) throws MalformedURLException {
        segmentEventMapping.put(this.documentSegmentNum, this.events.size()-1);
        Segment seg = new Segment(this.documentSegmentNum++, segmentUnitPartIndex, segmentUnitPartIndex,
                new FragmentVariant(unitPart.getSource()),
                new FragmentVariant(unitPart.getTarget(Part.GetTarget.CREATE_EMPTY)),
                null); //TODO: load original target from file
        seg.setLQI(parseLqiData(unitPart));
        seg.setProv(parseProvData(unitPart));
        return seg;
    }

    private List<LanguageQualityIssue> parseLqiData(Part unitPart) throws MalformedURLException {
        List<LanguageQualityIssue> ocelotLqiList = new ArrayList<LanguageQualityIssue>();

        List<Tag> sourceTags = unitPart.getSource().getOwnTags();
        ocelotLqiList.addAll(convertOkapiToOcelotLqiData(sourceTags));

        if (unitPart.getTarget() != null) {
            List<Tag> targetTags = unitPart.getTarget().getOwnTags();
            ocelotLqiList.addAll(convertOkapiToOcelotLqiData(targetTags));
        }

        return ocelotLqiList;
    }

    private List<LanguageQualityIssue> convertOkapiToOcelotLqiData(List<Tag> okapiXliff2Tags) throws MalformedURLException {
        List<LanguageQualityIssue> ocelotLqiList = new ArrayList<LanguageQualityIssue>();

        for (Tag tag : okapiXliff2Tags) {
            // ITS XLIFF 2.0 LQI Mapping must be done using the <mrk> element
            if (tag.isMarker()) {
                MTag mtag = (MTag) tag;
                // Same Tag object is generated twice for paired elements; only take the opening LQI
                if (mtag.hasITSItem() && (mtag.getTagType() == TagType.OPENING
                        || mtag.getTagType() == TagType.STANDALONE)) {
                    LocQualityIssues lqiIssues = (LocQualityIssues) mtag.getITSItems().get(LocQualityIssue.class);
                    if (lqiIssues != null) {
                        for (LocQualityIssue lqiIssue : lqiIssues.getList()) {
                            ocelotLqiList.add(convertOkapiToOcelotLqi(lqiIssue));
                        }
                    }
                }
            }
        }

        return ocelotLqiList;
    }

    /**
     * Convert from Okapi parsed version of an LQI
     * @param lqi - Okapi representation of an ITS Language Quality Issue
     * @return - Ocelot representation of an ITS Language Quality Issue
     * @throws MalformedURLException
     */
    private LanguageQualityIssue convertOkapiToOcelotLqi(LocQualityIssue lqi) throws MalformedURLException {
        LanguageQualityIssue ocelotLQI = new LanguageQualityIssue();
        ocelotLQI.setType(lqi.getType());
        ocelotLQI.setComment(lqi.getComment());
        ocelotLQI.setSeverity(lqi.getSeverity() != null ? lqi.getSeverity() : 0);

        URL profileRef = lqi.getProfileRef() != null ? new URL(lqi.getProfileRef()) : null;
        ocelotLQI.setProfileReference(profileRef);

        ocelotLQI.setEnabled(lqi.isEnabled());
        return ocelotLQI;
    }

    private List<Provenance> parseProvData(Part unitPart) {
        List<Provenance> ocelotProvList = new ArrayList<Provenance>();

        List<Tag> sourceTags = unitPart.getSource().getOwnTags();
        List<Tag> targetTags = unitPart.getTarget().getOwnTags();
        ocelotProvList.addAll(convertOkapiToOcelotProvData(sourceTags));
        ocelotProvList.addAll(convertOkapiToOcelotProvData(targetTags));

        return ocelotProvList;
    }

    private List<Provenance> convertOkapiToOcelotProvData(List<Tag> okapiXliff2Tags) {
        List<Provenance> ocelotProvList = new ArrayList<Provenance>();

        for (Tag tag : okapiXliff2Tags) {
            // ITS XLIFF 2.0 Provenance Mapping must be done using the <mrk> element
            if (tag.isMarker()) {
                MTag mtag = (MTag) tag;
                if (mtag.hasITSItem() && (mtag.getTagType() == TagType.OPENING
                        || mtag.getTagType() == TagType.STANDALONE)) {
                    Provenances provMetadata = (Provenances) mtag.getITSItems()
                            .get(net.sf.okapi.lib.xliff2.its.Provenance.class);
                    if (provMetadata != null) {
                        for (net.sf.okapi.lib.xliff2.its.Provenance p : provMetadata.getList()) {
                            ocelotProvList.add(new OkapiProvenance(p));
                        }
                    }
                }
            }
        }

        return ocelotProvList;
    }

    @Override
    public String getSourceLang() {
        return this.sourceLang;
    }

    @Override
    public String getTargetLang() {
        return this.targetLang;
    }
}
