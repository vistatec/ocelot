/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
import com.vistatec.ocelot.segment.Segment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.its.html5.HTML5Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse HTML5 files for use in the workbench.
 * Requires that the two HTML5 files are aligned, in that each event generated
 * by Okapi when parsing the two files in parallel correspond to each other.
 * The Event list is used when writing out files through Okapi; updates to
 * the workbench segments must then be reflected(sync'd) in the proper Event.
 * @deprecated
 */
public class HTML5Parser {
    private Logger LOG = LoggerFactory.getLogger(HTML5Parser.class);
    private LinkedList<Event> srcEvents, tgtEvents;
    private int documentSegmentNum;
    private String sourceLang, targetLang;

    public HTML5Parser() {}

    public String getSourceLang() {
        return this.sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return this.targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public Event getSegmentSourceEvent(int segSourceEventNumber) {
        return this.srcEvents.get(segSourceEventNumber);
    }

    public List<Event> getSegmentSourceEvents() {
        return this.srcEvents;
    }

    public Event getSegmentTargetEvent(int segTargetEventNumber) {
        return this.tgtEvents.get(segTargetEventNumber);
    }

    public List<Event> getSegmentTargetEvents() {
        return this.tgtEvents;
    }

    public List<Segment> parseHTML5Files(InputStream src, InputStream tgt) {
        srcEvents = new LinkedList<Event>();
        tgtEvents = new LinkedList<Event>();
        List<Segment> segments = new LinkedList<Segment>();
        documentSegmentNum = 1;

        // TODO: Actually get the locale
        setSourceLang("en");
        setTargetLang("de");
        RawDocument srcDoc = new RawDocument(src, "UTF-8", LocaleId.fromString("en"));
        RawDocument tgtDoc = new RawDocument(tgt, "UTF-8", LocaleId.fromString("de"));
        IFilter srcFilter = new HTML5Filter();
        IFilter tgtFilter = new HTML5Filter();
        srcFilter.open(srcDoc);
        tgtFilter.open(tgtDoc);
        int srcEventNum = 0, tgtEventNum = 0;

        while(srcFilter.hasNext() && tgtFilter.hasNext()) {
            Event srcEvent = srcFilter.next();
            Event tgtEvent = tgtFilter.next();

            ITextUnit srcTu, tgtTu;
            if (srcEvent.isTextUnit() && tgtEvent.isTextUnit()) {
                srcTu = (ITextUnit) srcEvent.getResource();
                tgtTu = (ITextUnit) tgtEvent.getResource();

                segments.add(convertTextUnitToSegment(srcTu, tgtTu, srcEventNum, tgtEventNum));
            }
            srcEvents.add(srcEvent);
            tgtEvents.add(tgtEvent);
            srcEventNum++;
            tgtEventNum++;
        }
        if (srcFilter.hasNext() || tgtFilter.hasNext()) {
            LOG.error("Documents not aligned?");
            while (srcFilter.hasNext()) {
                srcEvents.add(srcFilter.next());
                srcEventNum++;
            }
            while (tgtFilter.hasNext()) {
                tgtEvents.add(tgtFilter.next());
                tgtEventNum++;
            }
        }
        return segments;
    }

    public Segment convertTextUnitToSegment(ITextUnit srcTu, ITextUnit tgtTu, int srcEventNum, int tgtEventNum) {
        TextContainer srcTc = srcTu.getSource();
        TextContainer tgtTc = tgtTu.getSource();

        GenericAnnotations srcITSTags = srcTc.getAnnotation(GenericAnnotations.class);
        GenericAnnotations tgtITSTags = tgtTc.getAnnotation(GenericAnnotations.class);
        List<GenericAnnotation> anns = new LinkedList<GenericAnnotation>();
        if (srcITSTags != null) {
            anns.addAll(srcITSTags.getAnnotations(GenericAnnotationType.LQI));
            anns.addAll(srcITSTags.getAnnotations(GenericAnnotationType.PROV));
        }
        if (tgtITSTags != null) {
            anns.addAll(tgtITSTags.getAnnotations(GenericAnnotationType.LQI));
            anns.addAll(tgtITSTags.getAnnotations(GenericAnnotationType.PROV));
        }

        return addSegment(srcTc, tgtTc, anns, srcEventNum, tgtEventNum, "N/A", "N/A");
    }

    public Segment addSegment(TextContainer sourceText, TextContainer targetText,
            List<GenericAnnotation> annotations, int srcEventNum, int tgtEventNum,
            String fileOri, String transUnitId) {
        Segment seg = new Segment(documentSegmentNum++, srcEventNum, tgtEventNum,
                new TextContainerVariant(sourceText), new TextContainerVariant(targetText), null);
        seg.setFileOriginal(fileOri);
        seg.setTransUnitId(transUnitId);
        attachITSDataToSegment(seg, annotations);
        return seg;
    }

    public void attachITSDataToSegment(Segment seg, List<GenericAnnotation> annotations) {
        List<LanguageQualityIssue> lqis = new ArrayList<LanguageQualityIssue>();
        List<Provenance> provs = new ArrayList<Provenance>();
        for (GenericAnnotation annotation : annotations) {
            if (annotation.getType().equals(GenericAnnotationType.LQI)) {
                lqis.add(new LanguageQualityIssue(annotation));

            } else if (annotation.getType().equals(GenericAnnotationType.PROV)) {
                provs.add(new OkapiProvenance(annotation));

            }
        }
        seg.setLQI(lqis);
        seg.setProv(provs);
    }
}
