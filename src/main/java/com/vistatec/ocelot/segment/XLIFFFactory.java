package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.config.ProvenanceConfig;

public interface XLIFFFactory {

    public XLIFFParser newXLIFFParser();

    public XLIFFWriter newXLIFFWriter(XLIFFParser parser, ProvenanceConfig config);
}
