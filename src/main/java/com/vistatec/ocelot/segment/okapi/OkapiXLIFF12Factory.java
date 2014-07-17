package com.vistatec.ocelot.segment.okapi;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.segment.XLIFFFactory;
import com.vistatec.ocelot.segment.XLIFFParser;
import com.vistatec.ocelot.segment.XLIFFWriter;

public class OkapiXLIFF12Factory implements XLIFFFactory {
    @Override
    public XLIFFParser newXLIFFParser() {
        return new OkapiXLIFF12Parser();
    }

    @Override
    public XLIFFWriter newXLIFFWriter(XLIFFParser parser,
            ProvenanceConfig config) {
        return new OkapiXLIFF12Writer((OkapiXLIFF12Parser)parser, config);
    }
}
