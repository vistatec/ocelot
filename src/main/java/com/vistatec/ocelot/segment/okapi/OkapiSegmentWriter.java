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

import com.vistatec.ocelot.ObjectUtils;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentController;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for using Okapi's ISkeletonWriter interface to write out the
 * workbench segments as different file formats.
 */
public abstract class OkapiSegmentWriter {
    private Logger LOG = LoggerFactory.getLogger(OkapiSegmentWriter.class);

    public abstract void updateEvent(Segment seg, SegmentController segController);
    private UserProvenance userProvenance;

    public OkapiSegmentWriter(UserProvenance userProv) {
        this.userProvenance = userProv;
    }
    
    public ITSProvenanceAnnotations addRWProvenance(Segment seg) {
        ITSProvenanceAnnotations provAnns = new ITSProvenanceAnnotations();
        for (Provenance prov : seg.getProv()) {
            String revPerson = prov.getRevPerson();
            String revOrg = prov.getRevOrg();
            String provRef = prov.getProvRef();
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_PERSON, prov.getPerson(),
                    GenericAnnotationType.PROV_ORG, prov.getOrg(),
                    GenericAnnotationType.PROV_TOOL, prov.getTool(),
                    GenericAnnotationType.PROV_REVPERSON, revPerson,
                    GenericAnnotationType.PROV_REVORG, revOrg,
                    GenericAnnotationType.PROV_REVTOOL, prov.getRevTool(),
                    GenericAnnotationType.PROV_PROVREF, provRef);
            provAnns.add(ga);

            // Check for existing RW annotation.
            if (ObjectUtils.safeEquals(prov.getRevPerson(), userProvenance.getRevPerson()) &&
                ObjectUtils.safeEquals(prov.getRevOrg(), userProvenance.getRevOrg()) &&
                ObjectUtils.safeEquals(prov.getProvRef(), userProvenance.getProvRef())) {
                seg.setAddedRWProvenance(true);
            }
        }

        if (!seg.addedRWProvenance()) {
            GenericAnnotation provGA = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_REVPERSON, userProvenance.getRevPerson(),
                    GenericAnnotationType.PROV_REVORG, userProvenance.getRevOrg(),
                    GenericAnnotationType.PROV_PROVREF, userProvenance.getProvRef());
            provAnns.add(provGA);
            seg.addProvenance(new OkapiProvenance(provGA));
            seg.setAddedRWProvenance(true);
        }

        return provAnns;
    }

    public void saveEvents(IFilter filter, List<Event> events, String output, LocaleId locId) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuilder tmp = new StringBuilder();
        ISkeletonWriter skelWriter = filter.createSkeletonWriter();
        EncoderManager encoderManager = filter.getEncoderManager();
        for (Event event : events) {
            switch (event.getEventType()) {
                case START_DOCUMENT:
                    tmp.append(skelWriter.processStartDocument(locId, "UTF-8", null, encoderManager,
                            (StartDocument) event.getResource()));
                    break;
                case END_DOCUMENT:
                    tmp.append(skelWriter.processEndDocument((Ending) event.getResource()));
                    break;
                case START_SUBDOCUMENT:
                    tmp.append(skelWriter.processStartSubDocument((StartSubDocument) event
                            .getResource()));
                    break;
                case END_SUBDOCUMENT:
                    tmp.append(skelWriter.processEndSubDocument((Ending) event.getResource()));
                    break;
                case TEXT_UNIT:
                    ITextUnit tu = event.getTextUnit();
                    tmp.append(skelWriter.processTextUnit(tu));
                    break;
                case DOCUMENT_PART:
                    DocumentPart dp = (DocumentPart) event.getResource();
                    tmp.append(skelWriter.processDocumentPart(dp));
                    break;
                case START_GROUP:
                    StartGroup startGroup = (StartGroup) event.getResource();
                    tmp.append(skelWriter.processStartGroup(startGroup));
                    break;
                case END_GROUP:
                    tmp.append(skelWriter.processEndGroup((Ending) event.getResource()));
                    break;
                case START_SUBFILTER:
                    StartSubfilter startSubfilter = (StartSubfilter) event.getResource();
                    tmp.append(skelWriter.processStartSubfilter(startSubfilter));
                    break;
                case END_SUBFILTER:
                    tmp.append(skelWriter.processEndSubfilter((EndSubfilter) event.getResource()));
                    break;
                default:
                    break;
            }
        }
        skelWriter.close();
        Writer outputFile = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        outputFile.write(tmp.toString());
        outputFile.flush();
        outputFile.close();
    }
}
