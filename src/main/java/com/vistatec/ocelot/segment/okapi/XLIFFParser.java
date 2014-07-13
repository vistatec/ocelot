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
import com.vistatec.ocelot.its.OtherITSMetadata;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.rules.DataCategoryField;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.Segment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFPhase;
import net.sf.okapi.common.annotation.XLIFFPhaseAnnotation;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.annotation.XLIFFToolAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.xliff.Parameters;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse XLIFF file for use in the workbench.
 * The Event list is used when writing out files through Okapi; updates to
 * the workbench segments must then be reflected(synchronized) in the proper Event.
 */
public class XLIFFParser {
    private static Logger LOG = LoggerFactory.getLogger(XLIFFParser.class);
    private LinkedList<Event> events;
    private XLIFFFilter filter;
    private int documentSegmentNum;
    private String sourceLang, targetLang, fileOriginal;

    public XLIFFParser() {}

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

    public Event getSegmentEvent(int segEventNumber) {
        return this.events.get(segEventNumber);
    }

    public List<Event> getSegmentEvents() {
        return this.events;
    }

    public List<Segment> parseXLIFFFile(InputStream file) {
        events = new LinkedList<Event>();
        List<Segment> segments = new LinkedList<Segment>();
        documentSegmentNum = 1;

        RawDocument fileDoc = new RawDocument(file, "UTF-8", LocaleId.EMPTY, LocaleId.EMPTY);
        this.filter = new XLIFFFilter();
        Parameters filterParams = new Parameters();
        filterParams.setAddAltTrans(true);
        this.filter.setParameters(filterParams);
        this.filter.open(fileDoc);
        int fileEventNum = 0;

        fileOriginal = "";
        while(this.filter.hasNext()) {
            Event event = this.filter.next();
            events.add(event);

            if (event.isStartSubDocument()) {
                StartSubDocument fileElement = (StartSubDocument)event.getResource();
                XLIFFToolAnnotation toolAnn = fileElement.getAnnotation(XLIFFToolAnnotation.class);
                if (toolAnn == null) {
                    toolAnn = new XLIFFToolAnnotation();
                    fileElement.setAnnotation(toolAnn);
                }
                if (toolAnn.get("Ocelot") == null) {
                    toolAnn.add(new XLIFFTool("Ocelot", "Ocelot"), fileElement);
                }
                if (fileElement.getProperty("sourceLanguage") != null) {
                    String fileSourceLang = fileElement.getProperty("sourceLanguage").getValue();
                    if (getSourceLang() != null && !getSourceLang().equals(fileSourceLang)) {
                        LOG.warn("Mismatch between source languages in file elements");
                    }
                    setSourceLang(fileSourceLang);
                    fileDoc.setSourceLocale(LocaleId.fromString(fileSourceLang));
                }
                if (fileElement.getProperty("targetLanguage") != null) {
                    String fileTargetLang = fileElement.getProperty("targetLanguage").getValue();
                    if (getTargetLang() != null && !getTargetLang().equals(fileTargetLang)) {
                        LOG.warn("Mismatch between target languages in file elements");
                    }
                    setTargetLang(fileTargetLang);
                    fileDoc.setTargetLocale(LocaleId.fromString(fileTargetLang));
                }
                fileOriginal = fileElement.getName();

            } else if (event.isTextUnit()) {
                ITextUnit tu = (ITextUnit) event.getResource();
                segments.add(convertTextUnitToSegment(tu, fileEventNum));
            }
            fileEventNum++;
        }
        return segments;
    }

    public Segment convertTextUnitToSegment(ITextUnit tu, int fileEventNum) {
        TextContainer srcTu = tu.getSource();
        TextContainer tgtTu = new TextContainer();

        Set<LocaleId> targetLocales = tu.getTargetLocales();
        if (targetLocales.size() > 1) {
            LocaleId chosenTargetLocale = targetLocales.iterator().next();
            LOG.warn("More than 1 target locale: " + targetLocales);
            LOG.warn("Using target locale '"+chosenTargetLocale+"'");
            tgtTu = tu.getTarget(chosenTargetLocale);
        } else if (targetLocales.size() == 1) {
            for (LocaleId tgt : targetLocales) {
                tgtTu = tu.getTarget(tgt);
            }
        } else {
            tu.setTarget(LocaleId.fromString(getTargetLang()), tgtTu);
        }

        TextContainer oriTgtTu = retrieveOriginalTarget(tgtTu);

        Segment seg = new Segment(documentSegmentNum++, fileEventNum, fileEventNum,
                new TextContainerVariant(srcTu), new TextContainerVariant(tgtTu),
                oriTgtTu != null ? new TextContainerVariant(oriTgtTu) : null);
        seg.setFileOriginal(fileOriginal);
        seg.setTransUnitId(tu.getId());
        Property stateQualifier = tgtTu.getProperty("state-qualifier");
        if (stateQualifier != null) {
            StateQualifier sq = StateQualifier.get(stateQualifier.getValue());
            if (sq != null) {
                seg.setStateQualifier(sq);
            }
            else {
                LOG.info("Ignoring state-qualifier value '" + 
                         stateQualifier.getValue() + "'");
            }
        }
        XLIFFPhaseAnnotation phaseAnn = tu.getAnnotation(XLIFFPhaseAnnotation.class);
        if (phaseAnn != null) {
            XLIFFPhase refPhase = phaseAnn.getReferencedPhase();
            seg.setPhaseName(refPhase.getPhaseName());
        }
        attachITSDataToSegment(seg, tu, srcTu, tgtTu);
        return seg;
    }
    
    public void attachITSDataToSegment(Segment seg, ITextUnit tu, TextContainer srcTu, TextContainer tgtTu) {
        ITSLQIAnnotations lqiAnns = retrieveITSLQIAnnotations(tu, srcTu, tgtTu);
        List<LanguageQualityIssue> lqiList = new ArrayList<LanguageQualityIssue>();
        for (GenericAnnotation ga : lqiAnns.getAnnotations(GenericAnnotationType.LQI)) {
            lqiList.add(new LanguageQualityIssue(ga));
            seg.setLQIID(lqiAnns.getData());
        }
        seg.setLQI(lqiList);

        ITSProvenanceAnnotations provAnns = retrieveITSProvAnnotations(tu, srcTu, tgtTu);
        List<GenericAnnotation> provAnnList = provAnns.getAnnotations(GenericAnnotationType.PROV);
        if (provAnnList != null) {
            List<Provenance> provList = new ArrayList<Provenance>();
            for (GenericAnnotation ga : provAnnList) {
                provList.add(new OkapiProvenance(ga));
                seg.setProvID(provAnns.getData());
            }
            seg.setProv(provList);
        }

        if (tgtTu != null) {
            List<OtherITSMetadata> otherList = new ArrayList<OtherITSMetadata>();
            for (GenericAnnotation mtAnn : retrieveITSMTConfidenceAnnotations(tgtTu)) {
                otherList.add(new OtherITSMetadata(DataCategoryField.MT_CONFIDENCE,
                        mtAnn.getDouble(GenericAnnotationType.MTCONFIDENCE_VALUE)));
            }
            seg.setOtherITSMetadata(otherList);
        }
    }

    public ITSLQIAnnotations retrieveITSLQIAnnotations(ITextUnit tu, TextContainer srcTu, TextContainer tgtTu) {
        ITSLQIAnnotations lqiAnns = tu.getAnnotation(ITSLQIAnnotations.class);
        lqiAnns = lqiAnns == null ? new ITSLQIAnnotations() : lqiAnns;

        ITSLQIAnnotations srcLQIAnns = srcTu.getAnnotation(ITSLQIAnnotations.class);
        if (srcLQIAnns != null) {
            lqiAnns.addAll(srcLQIAnns);
        }
        if (tgtTu != null) {
            ITSLQIAnnotations tgtLQIAnns = tgtTu.getAnnotation(ITSLQIAnnotations.class);
            if (tgtLQIAnns != null) {
                lqiAnns.addAll(tgtLQIAnns);
            }
        }
        return lqiAnns;
    }

    public ITSProvenanceAnnotations retrieveITSProvAnnotations(ITextUnit tu, TextContainer srcTu, TextContainer tgtTu) {
        ITSProvenanceAnnotations provAnns = tu.getAnnotation(ITSProvenanceAnnotations.class);
        provAnns = provAnns == null ? new ITSProvenanceAnnotations() : provAnns;

        ITSProvenanceAnnotations srcProvAnns = srcTu.getAnnotation(ITSProvenanceAnnotations.class);
        if (srcProvAnns != null) {
            provAnns.addAll(srcProvAnns);
        }

        if (tgtTu != null) {
            ITSProvenanceAnnotations tgtProvAnns = tgtTu.getAnnotation(ITSProvenanceAnnotations.class);
            if (tgtProvAnns != null) {
                provAnns.addAll(tgtProvAnns);
            }
        }
        return provAnns;
    }

    public List<GenericAnnotation> retrieveITSMTConfidenceAnnotations(TextContainer tgtTu) {
        GenericAnnotations tgtAnns = tgtTu.getAnnotation(GenericAnnotations.class);
        List<GenericAnnotation> mtAnns = new LinkedList<GenericAnnotation>();
        if (tgtAnns != null) {
            mtAnns = tgtAnns.getAnnotations(GenericAnnotationType.MTCONFIDENCE);
        }
        return mtAnns;
    }

    public TextContainer retrieveOriginalTarget(TextContainer target) {
        AltTranslationsAnnotation altTrans = target.getAnnotation(AltTranslationsAnnotation.class);
        if (altTrans != null) {
            Iterator<AltTranslation> iterAltTrans = altTrans.iterator();
            while (iterAltTrans.hasNext()) {
                AltTranslation altTran = iterAltTrans.next();
                // Check if alt-trans is Ocelot generated.
                XLIFFTool altTool = altTran.getTool();
                if (altTool != null && altTool.getName().equals("Ocelot")) {
                    return altTran.getTarget();
                }
            }
        }
        return null;
    }

    protected XLIFFFilter getFilter() {
        return this.filter;
    }
}
