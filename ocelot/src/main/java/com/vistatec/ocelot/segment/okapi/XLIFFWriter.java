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
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write out XLIFF files using Okapi's XLIFFSkeletonWriter.
 * Handles synchronization between workbench Segments and the Okapi Event list
 * retrieved from the XLIFFParser.
 */
public class XLIFFWriter extends OkapiSegmentWriter {
    private Logger LOG = LoggerFactory.getLogger(XLIFFWriter.class);
    private XLIFFParser parser;

    public XLIFFWriter(XLIFFParser xliffParser) {
        this.parser = xliffParser;
    }

    public XLIFFParser getParser() {
        return this.parser;
    }

    @Override
    public void updateEvent(Segment seg, SegmentController segController) {
        Event event = getParser().getSegmentEvent(seg.getSourceEventNumber());
        if (event == null) {
            LOG.error("Failed to find Okapi Event associated with segment #"+seg.getSegmentNumber());

        } else if (event.isTextUnit()) {
            ITextUnit textUnit = event.getTextUnit();
            String rwRef = "RW" + seg.getSegmentNumber();

            updateITSLQIAnnotations(textUnit, seg, rwRef);

            ITSProvenanceAnnotations provAnns = addRWProvenance(seg);
            textUnit.setProperty(new Property(Property.ITS_PROV, " xmlns:its=\"http://www.w3.org/2005/11/its\" its:provenanceRecordsRef=\"#" + rwRef + "\""));
            provAnns.setData(rwRef);
            textUnit.setAnnotation(provAnns);

            if (seg.hasOriginalTarget()) {
                updateOriginalTarget(seg, segController);
            }
        } else {
            LOG.error("Event associated with Segment was not an Okapi TextUnit!");
            LOG.error("Failed to update event for segment #"+seg.getSegmentNumber());
        }
    }

    public void updateITSLQIAnnotations(ITextUnit tu, Segment seg, String rwRef) {
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

    public void removeITSLQITextUnitSourceAnnotations(ITextUnit tu, Segment seg) {
        seg.getSource().setProperty(new Property(Property.ITS_LQI, ""));
        seg.getSource().setAnnotation(null);
        tu.setSource(seg.getSource());
    }

    public void removeITSLQITextUnitTargetAnnotations(ITextUnit tu, Segment seg) {
        Set<LocaleId> targetLocales = tu.getTargetLocales();
        if (targetLocales.size() == 1) {
            for (LocaleId tgt : targetLocales) {
                TextContainer tgtTC = tu.getTarget(tgt);
                tgtTC.setProperty(new Property(Property.ITS_LQI, ""));
                tgtTC.setAnnotation(null);
                tu.setTarget(tgt, tgtTC);
            }
        } else if (targetLocales.isEmpty()) {
            tu.setTarget(LocaleId.fromString(parser.getTargetLang()), seg.getTarget());

        } else {
            LOG.warn("Only 1 target locale in text-unit is currently supported");
        }
    }

    /**
     * Add an alt-trans containing the original target if one from this tool
     * doesn't exist already.
     * @param seg - Segment edited
     * @param segController
     */
    public void updateOriginalTarget(Segment seg, SegmentController segController) {
        TextContainer oriTarget = getParser().retrieveOriginalTarget(seg.getTarget());
        if (oriTarget == null) {
            AltTranslation rwbAltTrans = new AltTranslation(LocaleId.fromString(segController.getFileSourceLang()),
                    LocaleId.fromString(segController.getFileTargetLang()), null,
                    seg.getSource().getUnSegmentedContentCopy(), seg.getOriginalTarget().getUnSegmentedContentCopy(),
                    MatchType.EXACT, 100, "Ocelot");
            XLIFFTool rwbAltTool = new XLIFFTool("Ocelot", "Ocelot");
            rwbAltTrans.setTool(rwbAltTool);
            AltTranslationsAnnotation altTrans = seg.getTarget().getAnnotation(AltTranslationsAnnotation.class);
            altTrans = altTrans == null ? new AltTranslationsAnnotation() : altTrans;
            altTrans.add(rwbAltTrans);
            seg.getTarget().setAnnotation(altTrans);
        }
    }

    public void save(File source) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        saveEvents(parser.getFilter(), parser.getSegmentEvents(),
                source.getAbsolutePath(), LocaleId.fromString(parser.getTargetLang()));
    }
}
