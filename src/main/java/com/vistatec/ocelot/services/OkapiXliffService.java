/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
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
import com.vistatec.ocelot.xliff.okapi.OkapiXLIFFFactory;

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
