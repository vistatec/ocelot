package com.vistatec.ocelot.xliff;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public interface XLIFFFactory {

    public XLIFFVersion detectXLIFFVersion(File detectVersion) throws IOException, XMLStreamException;

    public XLIFFParser newXLIFFParser(XLIFFVersion xliffVersion) throws IOException, XMLStreamException;

    public XLIFFWriter newXLIFFWriter(XLIFFParser parser,
            UserProvenance userProvenance, OcelotEventQueue eventQueue);
}
