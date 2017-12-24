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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.okapi.OkapiProvenance;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.okapi.Note;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.xliff.XLIFFWriter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Namespaces;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFNote;
import net.sf.okapi.common.annotation.XLIFFNoteAnnotation;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Write out XLIFF files using Okapi's XLIFFSkeletonWriter.
 * Handles synchronization between workbench Segments and the Okapi Event list
 * retrieved from the XLIFFParser.
 */
public class OkapiXLIFF12Writer implements XLIFFWriter {
    private Logger LOG = LoggerFactory.getLogger(OkapiXLIFF12Writer.class);
    private OkapiXLIFF12Parser parser;
    private final UserProvenance userProvenance;
    private final OcelotEventQueue eventQueue;
    private Double time;
    private String lqiConfiguration;
    private Xliff12HeaderWriter headerWriter;

    public OkapiXLIFF12Writer(OkapiXLIFF12Parser xliffParser,
            UserProvenance userProvenance, OcelotEventQueue eventQueue) {
        this.parser = xliffParser;
        this.userProvenance = userProvenance;
        this.eventQueue = eventQueue;
        headerWriter = new Xliff12HeaderWriter();
    }

    public OkapiXLIFF12Parser getParser() {
        return this.parser;
    }

    @Override
    public void updateSegment(OcelotSegment seg) {
        OkapiSegment okapiSeg = (OkapiSegment) seg;
        Event event = getParser().getSegmentEvent(okapiSeg.eventNum);
        if (event == null) {
            LOG.error("Failed to find Okapi Event associated with segment #"+okapiSeg.getSegmentNumber());

        } else if (event.isTextUnit()) {
            ITextUnit textUnit = event.getTextUnit();
            String rwRef = "RW" + okapiSeg.getSegmentNumber();

            updateITSLQIAnnotations(textUnit, okapiSeg, rwRef);

            ITSProvenanceAnnotations provAnns = addOcelotProvenance(okapiSeg);
            if (provAnns.getAllAnnotations().size() > 0) {
                textUnit.setProperty(new Property(Property.ITS_PROV, " its:provenanceRecordsRef=\"#" + rwRef + "\""));
                provAnns.setData(rwRef);
                textUnit.setAnnotation(provAnns);
            }

            if (okapiSeg.hasOriginalTarget()) {
                // Make sure the Okapi Event is aware that the target has changed.
                textUnit.setTarget(LocaleId.fromString(parser.getTargetLang()), unwrap(okapiSeg.getTarget()));
                updateOriginalTarget(okapiSeg);
            }
        } else {
            LOG.error("Event associated with Segment was not an Okapi TextUnit!");
            LOG.error("Failed to update event for segment #"+okapiSeg.getSegmentNumber());
        }
    }

    ITSProvenanceAnnotations addOcelotProvenance(OcelotSegment seg) {
        ITSProvenanceAnnotations provAnns = new ITSProvenanceAnnotations();
        for (Provenance prov : seg.getProvenance()) {
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

            // Check for existing Ocelot annotation.
            if (Objects.equals(prov.getRevPerson(), userProvenance.getRevPerson()) &&
                Objects.equals(prov.getRevOrg(), userProvenance.getRevOrg()) &&
                Objects.equals(prov.getProvRef(), userProvenance.getProvRef())) {
                seg.setOcelotProvenance(true);
            }
        }

        if (!seg.hasOcelotProvenance() && !userProvenance.isEmpty()) {
            GenericAnnotation provGA = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_REVPERSON, userProvenance.getRevPerson(),
                    GenericAnnotationType.PROV_REVORG, userProvenance.getRevOrg(),
                    GenericAnnotationType.PROV_PROVREF, userProvenance.getProvRef());
            provAnns.add(provGA);
            Provenance ocelotProv = new OkapiProvenance(provGA);
            eventQueue.post(new ProvenanceAddEvent(ocelotProv, seg, true));
        }

        return provAnns;
    }

    void updateITSLQIAnnotations(ITextUnit tu, OcelotSegment seg, String rwRef) {
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

    void removeITSLQITextUnitSourceAnnotations(ITextUnit tu, OcelotSegment seg) {
        TextContainer tc = unwrap(seg.getSource());
        tc.setProperty(new Property(Property.ITS_LQI, ""));
        tc.setAnnotation(null);
        tu.setSource(tc);
    }

    void removeITSLQITextUnitTargetAnnotations(ITextUnit tu, OcelotSegment seg) {
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
    public void updateOriginalTarget(OcelotSegment seg) {
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
    public void save(File source) throws IOException {
        saveEvents(parser.getFilter(), parser.getSegmentEvents(),
                source.getAbsolutePath(), LocaleId.fromString(parser.getTargetLang()));
        resetSavedData();
    }
    
    private void resetSavedData(){
    	time = null;
    }

    // HACK fix for OC-21.  As of M23, the XLIFF Filter doesn't properly manage
    // ITS namespace insertion for all cases, so we insert it into the <xliff> element
    // if one isn't already present.
    private boolean foundXliffElement = false;
    private static final Pattern XLIFF_ELEMENT_PATTERN = Pattern.compile("(.*<xliff)([^>]*)(>.*)");
    private static final Pattern ITS_NAMESPACE_PATTERN = Pattern.compile("xmlns(:[^=]+)?=\"" + Namespaces.ITS_NS_URI + "\"");

	@Override
    public void updateNotes(OcelotSegment seg) {
	    // TODO: refactor some of this code with updateSegment
	    OkapiSegment okapiSeg = (OkapiSegment) seg;
        Event event = getParser().getSegmentEvent(okapiSeg.eventNum);
        if (event == null) {
            LOG.error("Failed to find Okapi Event associated with segment #"+okapiSeg.getSegmentNumber());

        } else if (event.isTextUnit()) {
            ITextUnit textUnit = event.getTextUnit();

            Note ocelotNote = seg.getNotes() != null ? seg.getNotes().getOcelotNote() : null;
            XLIFFNoteAnnotation noteAnnotation = textUnit.getAnnotation(XLIFFNoteAnnotation.class);
            noteAnnotation = noteAnnotation != null ? noteAnnotation : new XLIFFNoteAnnotation();
            XLIFFNote ocelotOkapiNote = parser.readOcelotNote(noteAnnotation);	
            if(ocelotNote != null ){
            	// CASE 1 - new note created for this segment
            	if(ocelotOkapiNote == null){
            		ocelotOkapiNote = new XLIFFNote(ocelotNote.getContent());
            		ocelotOkapiNote.setFrom(Note.OCELOT_FROM_PROPERTY);
            		noteAnnotation.add(ocelotOkapiNote);
            		textUnit.setAnnotation(noteAnnotation);
            		LOG.debug("Created note for " + seg.getTuId() + ": '" + ocelotNote.getContent() + "'");
            	// CASE 2 - note updated for this segment	
            	} else {
            		ocelotOkapiNote.setNoteText(ocelotNote.getContent());
            		LOG.debug("Updated note for " + seg.getTuId() + " to '" + ocelotNote.getContent() + "'");
            	}
            //CASE 3 - note has been deleted
            } else if (ocelotOkapiNote != null){
            	ocelotOkapiNote.setNoteText(null);
            	LOG.debug("Deleted note for " + seg.getTuId());
            }
        } else {
            LOG.error("Event associated with Segment was not an Okapi TextUnit!");
            LOG.error("Failed to update event for segment #"+okapiSeg.getSegmentNumber());
        }
    }
	

    private void saveEvents(IFilter filter, List<Event> events, String output, LocaleId locId) throws IOException {
        StringBuilder tmp = new StringBuilder();
        ISkeletonWriter skelWriter = filter.createSkeletonWriter();
        EncoderManager encoderManager = filter.getEncoderManager();
        for (Event event : events) {
            switch (event.getEventType()) {
                case START_DOCUMENT:
                    tmp.append(skelWriter.processStartDocument(locId, "UTF-8", null, encoderManager,
                                    event.getStartDocument()));
                    break;
                case END_DOCUMENT:
                    tmp.append(skelWriter.processEndDocument(event.getEnding()));
                    break;
                case START_SUBDOCUMENT:
                	headerWriter.writeHeader(event.getStartSubDocument(), time, userProvenance, lqiConfiguration);
                    tmp.append(skelWriter.processStartSubDocument(event.getStartSubDocument()));
                    break;
                case END_SUBDOCUMENT:
                    tmp.append(skelWriter.processEndSubDocument(event.getEnding()));
                    break;
                case TEXT_UNIT:
                    tmp.append(skelWriter.processTextUnit(event.getTextUnit()));
                    break;
                case DOCUMENT_PART:
                    tmp.append(skelWriter.processDocumentPart(
                            preprocessDocumentPart(event.getDocumentPart())));
                    break;
                case START_GROUP:
                    tmp.append(skelWriter.processStartGroup(event.getStartGroup()));
                    break;
                case END_GROUP:
                    tmp.append(skelWriter.processEndGroup(event.getEnding()));
                    break;
                case START_SUBFILTER:
                    tmp.append(skelWriter.processStartSubfilter(event.getStartSubfilter()));
                    break;
                case END_SUBFILTER:
                    tmp.append(skelWriter.processEndSubfilter(event.getEndSubfilter()));
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
    

	private DocumentPart preprocessDocumentPart(DocumentPart dp) {
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

	@Override
	public void updateTiming(Double time) {
		this.time = time;
	}

	@Override
	public void updateLqiConfiguration(String lqiConfName) {
		this.lqiConfiguration = lqiConfName;
	}
}
