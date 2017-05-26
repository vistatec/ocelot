package com.vistatec.ocelot.services;

import com.vistatec.ocelot.xliff.XLIFFDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Service for performing XLIFF related operations.
 */
public interface XliffService extends OcelotEventQueueListener {

    public XLIFFDocument parse(File xliffFile) throws IOException, XMLStreamException;

    public void save(XLIFFDocument xliffFile, File dest) throws FileNotFoundException, IOException;

    public void saveTime(Double time);

    public void saveLqiConfiguration(String lqiConfigurationName);
}
