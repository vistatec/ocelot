package com.vistatec.ocelot.xliff;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

/**
 * Dummy XLIFF factory for testing.  Parser returns canned data;
 * writer does nothing.
 */
public class SimpleXLIFFFactory implements XLIFFFactory {
    private String sourceLang, targetLang;
    private List<OcelotSegment> segs;

    public SimpleXLIFFFactory(String sourceLang, String targetLang, List<OcelotSegment> segs) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.segs = segs;
    }

    @Override
    public XLIFFParser newXLIFFParser(File detectVersion) {

        return new XLIFFParser() {
            @Override
            public List<OcelotSegment> parse(File xliffFile) throws IOException {
                return new ArrayList<OcelotSegment>(segs);
            }

            @Override
            public String getTargetLang() {
                return targetLang;
            }

            @Override
            public String getSourceLang() {
                return sourceLang;
            }
        };
    }

    @Override
    public XLIFFWriter newXLIFFWriter(XLIFFParser parser,
            UserProvenance userProvenance, OcelotEventQueue eventQueue) {
        return new XLIFFWriter() {
            @Override
            public void updateSegment(OcelotSegment seg) {
            }

            @Override
            public void updateNotes(OcelotSegment seg) {
            }

            @Override
            public void save(File file) throws IOException,
                    UnsupportedEncodingException {
            }
        };
    }
}
