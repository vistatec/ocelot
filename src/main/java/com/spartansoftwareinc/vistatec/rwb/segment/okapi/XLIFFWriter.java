package com.spartansoftwareinc.vistatec.rwb.segment.okapi;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
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
    public void updateEvent(Segment seg) {
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

    public void save(File source) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        saveEvents(parser.getFilter(), parser.getSegmentEvents(),
                source.getAbsolutePath(), LocaleId.fromString(parser.getTargetLang()));
    }
}
