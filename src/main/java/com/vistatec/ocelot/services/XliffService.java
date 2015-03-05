package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Service for performing XLIFF related operations.
 */
public interface XliffService extends OcelotEventQueueListener {

    public List<OcelotSegment> parse(File xliffFile, File detectVersion) throws FileNotFoundException, IOException, XMLStreamException;

    public void save(File file) throws FileNotFoundException, IOException;

    public String getSourceLang();

    public String getTargetLang();
}
