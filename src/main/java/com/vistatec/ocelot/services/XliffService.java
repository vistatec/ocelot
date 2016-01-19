package com.vistatec.ocelot.services;

import com.vistatec.ocelot.xliff.XLIFFFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Service for performing XLIFF related operations.
 */
public interface XliffService extends OcelotEventQueueListener {

    public XLIFFFile parse(File xliffFile) throws IOException, XMLStreamException;

    public void save(XLIFFFile xliffFile, File dest) throws FileNotFoundException, IOException;

}
