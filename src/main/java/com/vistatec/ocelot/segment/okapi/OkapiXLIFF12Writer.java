/*
 * Copyright (C) 2013, 2014, VistaTEC or third-party contributors as indicated
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

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentVariant;
import com.vistatec.ocelot.segment.XLIFFWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Namespaces;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.skeleton.GenericSkeleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.events.api.OcelotEventQueue;

/**
 * Write out XLIFF files using Okapi's XLIFFSkeletonWriter.
 * Handles synchronization between workbench Segments and the Okapi Event list
 * retrieved from the XLIFFParser.
 */
public class OkapiXLIFF12Writer extends OkapiSegmentWriter implements XLIFFWriter {
    private Logger LOG = LoggerFactory.getLogger(OkapiXLIFF12Writer.class);
    private OkapiXLIFF12Parser parser;

    public OkapiXLIFF12Writer(OkapiXLIFF12Parser xliffParser,
            ProvenanceConfig provConfig, OcelotEventQueue eventQueue) {
        super(provConfig, eventQueue);
        this.parser = xliffParser;
    }

    public OkapiXLIFF12Parser getParser() {
        return this.parser;
    }

    @Override
    public void updateSegment(Segment seg) {
        Event event = getParser().getSegmentEvent(seg.getSourceEventNumber());
        if (event == null) {
            LOG.error("Failed to find Okapi Event associated with segment #"+seg.getSegmentNumber());

        } else if (event.isTextUnit()) {
            ITextUnit textUnit = event.getTextUnit();
            String rwRef = "RW" + seg.getSegmentNumber();

            updateITSLQIAnnotations(textUnit, seg, rwRef);

            ITSProvenanceAnnotations provAnns = addRWProvenance(seg);
            textUnit.setProperty(new Property(Property.ITS_PROV, " its:provenanceRecordsRef=\"#" + rwRef + "\""));
            provAnns.setData(rwRef);
            textUnit.setAnnotation(provAnns);

            if (seg.hasOriginalTarget()) {
                // Make sure the Okapi Event is aware that the target has changed.
                textUnit.setTarget(LocaleId.fromString(parser.getTargetLang()), unwrap(seg.getTarget()));
                updateOriginalTarget(seg);
            }
        } else {
            LOG.error("Event associated with Segment was not an Okapi TextUnit!");
            LOG.error("Failed to update event for segment #"+seg.getSegmentNumber());
        }
    }

    void updateITSLQIAnnotations(ITextUnit tu, Segment seg, String rwRef) {
        ITSLQIAnnotations lqiAnns = new ITSLQIAnnotations();
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.LQI,
                    GenericAnnotationType.LQI_TYPE, lqi.getType(),
                    GenericAnnotationType.LQI_COMMENT, lqi.getComment(),
                    GenericAnnotationType.LQI_SEVERITY, lqi.getSeverity(),
                    GenericAnnotationType.LQI_ENABLED, lqi.isEnabled());
            lqiAnns.add(ga);
        }

        if (lqiAnns.size() > 0) {
            tu.setProperty(new Property(Property.ITS_LQI, " its:locQualityIssuesRef=\"#"+rwRef+"\""));
            tu.setAnnotation(lqiAnns);
        } else {
            tu.setProperty(new Property(Property.ITS_LQI, ""));
            tu.setAnnotation(null);
        }
        lqiAnns.setData(rwRef);

        removeITSLQITextUnitSourceAnnotations(tu, seg);
        removeITSLQITextUnitTargetAnnotations(tu, seg);
    }

    private TextContainer unwrap(SegmentVariant v) {
        return ((TextContainerVariant)v).getTextContainer();
    }

    void removeITSLQITextUnitSourceAnnotations(ITextUnit tu, Segment seg) {
        TextContainer tc = unwrap(seg.getSource());
        tc.setProperty(new Property(Property.ITS_LQI, ""));
        tc.setAnnotation(null);
        tu.setSource(tc);
    }

    void removeITSLQITextUnitTargetAnnotations(ITextUnit tu, Segment seg) {
        Set<LocaleId> targetLocales = tu.getTargetLocales();
        if (targetLocales.size() == 1) {
            for (LocaleId tgt : targetLocales) {
                TextContainer tgtTC = tu.getTarget(tgt);
                tgtTC.setProperty(new Property(Property.ITS_LQI, ""));
                tgtTC.setAnnotation(null);
                tu.setTarget(tgt, tgtTC);
            }
        } else if (targetLocales.isEmpty()) {
            tu.setTarget(LocaleId.fromString(parser.getTargetLang()), 
                         unwrap(seg.getTarget()));

        } else {
            LOG.warn("Only 1 target locale in text-unit is currently supported");
        }
    }

    /**
     * Add an alt-trans containing the original target if one from this tool
     * doesn't exist already.
     * @param seg - Segment edited
     */
    public void updateOriginalTarget(Segment seg) {
        TextContainer segTarget = unwrap(seg.getTarget());
        TextContainer segSource = unwrap(seg.getSource());
        TextContainer segOriTarget = unwrap(seg.getOriginalTarget());
        TextContainer oriTarget = getParser().retrieveOriginalTarget(segTarget);
        if (oriTarget == null) {
            AltTranslation rwbAltTrans = new AltTranslation(LocaleId.fromString(parser.getSourceLang()),
                    LocaleId.fromString(parser.getTargetLang()), null,
                    segSource.getUnSegmentedContentCopy(), segOriTarget.getUnSegmentedContentCopy(),
                    MatchType.EXACT, 100, "Ocelot");
            XLIFFTool rwbAltTool = new XLIFFTool("Ocelot", "Ocelot");
            rwbAltTrans.setTool(rwbAltTool);
            AltTranslationsAnnotation altTrans = segTarget.getAnnotation(AltTranslationsAnnotation.class);
            altTrans = altTrans == null ? new AltTranslationsAnnotation() : altTrans;
            altTrans.add(rwbAltTrans);
            segTarget.setAnnotation(altTrans);
        }
    }

    @Override
    public void save(File source) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        saveEvents(parser.getFilter(), parser.getSegmentEvents(),
                source.getAbsolutePath(), LocaleId.fromString(parser.getTargetLang()));
    }

    // HACK fix for OC-21.  As of M23, the XLIFF Filter doesn't properly manage
    // ITS namespace insertion for all cases, so we insert it into the <xliff> element
    // if one isn't already present.
    private boolean foundXliffElement = false;
    private static final Pattern XLIFF_ELEMENT_PATTERN = Pattern.compile("(.*<xliff)([^>]*)(>.*)");
    private static final Pattern ITS_NAMESPACE_PATTERN = Pattern.compile("xmlns(:[^=]+)?=\"" + Namespaces.ITS_NS_URI + "\"");

    @Override
    protected DocumentPart preprocessDocumentPart(DocumentPart dp) {
        if (foundXliffElement) return dp;

        String origSkel = dp.getSkeleton().toString();
        Matcher m = XLIFF_ELEMENT_PATTERN.matcher(origSkel);
        if (m.find()) {
            foundXliffElement = true;
            String xliffAttributes = m.group(2);
            Matcher attrM = ITS_NAMESPACE_PATTERN.matcher(xliffAttributes);
            // If we found the namespace, we don't need to change anything
            if (attrM.find()) {
                return dp;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(m.group(1));
            sb.append(m.group(2));
            sb.append(" xmlns:")
              .append(Namespaces.ITS_NS_PREFIX)
              .append("=\"")
              .append(Namespaces.ITS_NS_URI)
              .append("\" ");
            sb.append(m.group(3));
            GenericSkeleton newSkel = new GenericSkeleton(sb.toString());
            dp.setSkeleton(newSkel);
        }
        return dp;
    }
}
