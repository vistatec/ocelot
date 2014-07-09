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

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.its.html5.HTML5Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write out HTML5 files using Okapi's HTML5SkeletonWriter.
 * @deprecated This class has not been kept up to date with the workbench.
 */
public class HTML5Writer extends OkapiSegmentWriter {
    private Logger LOG = LoggerFactory.getLogger(HTML5Writer.class);
    private HTML5Parser parser;

    public HTML5Writer(HTML5Parser parser, ProvenanceConfig config) {
        super(config);
        this.parser = parser;
    }

    @Override
    public void updateEvent(Segment seg, SegmentController segController) {
        Event srcEvent = parser.getSegmentSourceEvent(seg.getSourceEventNumber());
        Event tgtEvent = parser.getSegmentTargetEvent(seg.getTargetEventNumber());
        if (srcEvent == null) {
            LOG.error("Failed to find Okapi source HTML file Event associated with segment #"+seg.getSegmentNumber());
            return;
        } else if (tgtEvent == null) {
            LOG.error("Failed to find Okapi target HTML file Event associated with segment #"+seg.getSegmentNumber());
            return;
        }

        // TODO: Update to work with the new ITS annotations in Okapi.
        ITextUnit srcTu = srcEvent.getTextUnit();
        TextFragment srcTf = srcTu.createTarget(LocaleId.fromString(parser.getSourceLang()), true, IResource.COPY_ALL).getFirstContent();
        ITextUnit tgtTu = tgtEvent.getTextUnit();
        TextFragment tgtTf = tgtTu.createTarget(LocaleId.fromString(parser.getTargetLang()), true, IResource.COPY_ALL).getFirstContent();

        GenericAnnotations lqiAnns = new GenericAnnotations();
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.LQI,
                    GenericAnnotationType.LQI_TYPE, lqi.getType(),
                    GenericAnnotationType.LQI_COMMENT, lqi.getComment(),
                    GenericAnnotationType.LQI_SEVERITY, lqi.getSeverity(),
                    GenericAnnotationType.LQI_ENABLED, lqi.isEnabled());
            lqiAnns.add(ga);
            lqiAnns.setData(lqi.getIssuesRef());
        }

        ITSProvenanceAnnotations provAnns = addRWProvenance(seg);
        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, provAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, provAnns);
    }
    
    public void save(File source, File target) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        saveEvents(new HTML5Filter(), parser.getSegmentSourceEvents(),
                source.getAbsolutePath(), LocaleId.fromString(parser.getSourceLang()));
        saveEvents(new HTML5Filter(), parser.getSegmentTargetEvents(),
                target.getAbsolutePath(), LocaleId.fromString(parser.getTargetLang()));
    }
}
