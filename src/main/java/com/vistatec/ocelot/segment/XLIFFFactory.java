package com.vistatec.ocelot.segment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.config.ProvenanceConfig;

public interface XLIFFFactory {

    public XLIFFParser newXLIFFParser(File detectVersion) throws FileNotFoundException, IOException, XMLStreamException;

    public XLIFFWriter newXLIFFWriter(XLIFFParser parser, ProvenanceConfig config);
}
