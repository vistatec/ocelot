package com.spartansoftwareinc.vistatec.rwb.segment.okapi;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
import java.io.InputStream;
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
 */
public class HTML5Parser {
    private Logger LOG = LoggerFactory.getLogger(HTML5Parser.class);
    private LinkedList<Event> srcEvents, tgtEvents;
    private int documentSegmentNum;
    private SegmentController segmentController;
    private String sourceLang, targetLang;

    public HTML5Parser(SegmentController segController) {
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

    public void parseHTML5Files(InputStream src, InputStream tgt) {
        srcEvents = new LinkedList<Event>();
        tgtEvents = new LinkedList<Event>();
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

                convertTextUnitToSegment(srcTu, tgtTu, srcEventNum, tgtEventNum);
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
    }

    public void convertTextUnitToSegment(ITextUnit srcTu, ITextUnit tgtTu, int srcEventNum, int tgtEventNum) {
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

        addSegment(srcTc, tgtTc, anns, srcEventNum, tgtEventNum, "N/A", "N/A");
    }

    public void addSegment(TextContainer sourceText, TextContainer targetText,
            List<GenericAnnotation> annotations, int srcEventNum, int tgtEventNum,
            String fileOri, String transUnitId) {
        Segment seg = new Segment(documentSegmentNum++, srcEventNum, tgtEventNum,
                sourceText, targetText, null, segmentController);
        seg.setFileOriginal(fileOri);
        seg.setTransUnitId(transUnitId);
        attachITSDataToSegment(seg, annotations);
        segmentController.addSegment(seg);
    }

    public void attachITSDataToSegment(Segment seg, List<GenericAnnotation> annotations) {
        for (GenericAnnotation annotation : annotations) {
            if (annotation.getType().equals(GenericAnnotationType.LQI)) {
                seg.addLQI(new LanguageQualityIssue(annotation));

            } else if (annotation.getType().equals(GenericAnnotationType.PROV)) {
                seg.addProvenance(new Provenance(annotation));

            }
        }
    }
}
