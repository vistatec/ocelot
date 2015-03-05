package com.vistatec.ocelot.services;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFFactory;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.XLIFFWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.okapi.OkapiXLIFFFactory;

/**
 * Service for performing Okapi XLIFF operations.
 */
public class OkapiXliffService implements XliffService {

    private XLIFFFactory xliffFactory = new OkapiXLIFFFactory();
    private XLIFFParser xliffParser;
    private XLIFFWriter segmentWriter;

    private final ProvenanceConfig provConfig;
    private final OcelotEventQueue eventQueue;

    public OkapiXliffService(ProvenanceConfig provConfig, OcelotEventQueue eventQueue) {
        this.provConfig = provConfig;
        this.eventQueue = eventQueue;
    }

    @Subscribe
    public void updateSegment(SegmentEditEvent e) {
        segmentWriter.updateSegment(e.getSegment());
    }

    @Override
    public List<OcelotSegment> parse(File xliffFile, File detectVersion) throws FileNotFoundException, IOException, XMLStreamException {
        XLIFFParser newParser = xliffFactory.newXLIFFParser(detectVersion);
        List<OcelotSegment> xliffSegments = newParser.parse(xliffFile);

        xliffParser = newParser;
        segmentWriter = xliffFactory.newXLIFFWriter(xliffParser, provConfig, eventQueue);
        return xliffSegments;
    }

    @Override
    public void save(File file) throws FileNotFoundException, IOException {
        segmentWriter.save(file);
    }

    @Override
    public String getSourceLang() {
        return xliffParser.getSourceLang();
    }

    @Override
    public String getTargetLang() {
        return xliffParser.getTargetLang();
    }

}
