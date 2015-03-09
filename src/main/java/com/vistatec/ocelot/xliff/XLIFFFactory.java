package com.vistatec.ocelot.xliff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public interface XLIFFFactory {

    public XLIFFParser newXLIFFParser(File detectVersion) throws FileNotFoundException, IOException, XMLStreamException;

    public XLIFFWriter newXLIFFWriter(XLIFFParser parser,
            UserProvenance userProvenance, OcelotEventQueue eventQueue);
}
