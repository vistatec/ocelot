package com.vistatec.ocelot.segment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.config.ProvenanceConfig;

/**
 * Dummy XLIFF factory for testing.  Parser returns canned data;
 * writer does nothing.
 */
public class SimpleXLIFFFactory implements XLIFFFactory {
    private String sourceLang, targetLang;
    private List<Segment> segs;

    public SimpleXLIFFFactory(String sourceLang, String targetLang, List<Segment> segs) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.segs = segs;
    }

    @Override
    public XLIFFParser newXLIFFParser() {

        return new XLIFFParser() {
            @Override
            public List<Segment> parse(File xliffFile) throws IOException {
                return new ArrayList<Segment>(segs);
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
            ProvenanceConfig config) {
        return new XLIFFWriter() {
            @Override
            public void updateSegment(Segment seg, SegmentController controller) {
            }

            @Override
            public void save(File file) throws IOException,
                    UnsupportedEncodingException {
            }
        };
    }
}
