package com.spartansoftwareinc.vistatec.rwb.segment.okapi;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentTableModel;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFPhase;
import net.sf.okapi.common.annotation.XLIFFPhaseAnnotation;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.annotation.XLIFFToolAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
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
    private SegmentController segmentController;

    public XLIFFParser(SegmentController segController) {
        this.segmentController = segController;
    }

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

    public void parseXLIFFFile(FileInputStream file, SegmentTableModel segmentModel) {
        events = new LinkedList<Event>();
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
                convertTextUnitToSegment(tu, fileEventNum);
            }
            fileEventNum++;
        }
    }

    public void convertTextUnitToSegment(ITextUnit tu, int fileEventNum) {
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

        Segment seg = new Segment(documentSegmentNum++, fileEventNum, fileEventNum,
                srcTu, tgtTu, segmentController);
        seg.setFileOriginal(fileOriginal);
        seg.setTransUnitId(tu.getId());
        XLIFFPhaseAnnotation phaseAnn = tu.getAnnotation(XLIFFPhaseAnnotation.class);
        if (phaseAnn != null) {
            XLIFFPhase refPhase = phaseAnn.getReferencedPhase();
            seg.setPhaseName(refPhase.getPhaseName());
        }
        attachITSDataToSegment(seg, tu, srcTu, tgtTu);
        segmentController.addSegment(seg);
    }
    
    public void attachITSDataToSegment(Segment seg, ITextUnit tu, TextContainer srcTu, TextContainer tgtTu) {
        ITSLQIAnnotations lqiAnns = retrieveITSLQIAnnotations(tu, srcTu, tgtTu);
        List<GenericAnnotation> lqiList = lqiAnns.getAnnotations(GenericAnnotationType.LQI);
        for (GenericAnnotation ga : lqiList) {
            seg.addLQI(new LanguageQualityIssue(ga));
            seg.setLQIID(lqiAnns.getData());
        }

        ITSProvenanceAnnotations provAnns = retrieveITSProvAnnotations(tu, srcTu, tgtTu);
        List<GenericAnnotation> provList = provAnns.getAnnotations(GenericAnnotationType.PROV);
        if (provList != null) {
            for (GenericAnnotation ga : provList) {
                seg.addProvenance(new Provenance(ga));
                seg.setProvID(provAnns.getData());
            }
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

    protected XLIFFFilter getFilter() {
        return this.filter;
    }
}
