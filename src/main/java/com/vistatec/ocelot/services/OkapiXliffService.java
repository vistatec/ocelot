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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.okapi.common.LocaleId;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentNoteEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;
import com.vistatec.ocelot.xliff.XLIFFFactory;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.XLIFFVersion;
import com.vistatec.ocelot.xliff.XLIFFWriter;
import com.vistatec.ocelot.xliff.okapi.OkapiXLIFFFactory;

/**
 * Service for performing Okapi XLIFF operations.
 */
public class OkapiXliffService implements XliffService {
    private XLIFFFactory xliffFactory = new OkapiXLIFFFactory();

    private final JsonConfigService cfgService;
    private final OcelotEventQueue eventQueue;
    private Double time;
    private String lqiConfiguration;

    public OkapiXliffService(JsonConfigService cfgService, OcelotEventQueue eventQueue) {
        this.cfgService = cfgService;
        this.eventQueue = eventQueue;
    }

    @Subscribe
    public void updateSegment(SegmentEditEvent e) {
        getDoc(e.getDocument()).getWriter().updateSegment(e.getSegment());
    }
    

    @Subscribe
    public void updateNotes(SegmentNoteEditEvent e ) {
        getDoc(e.getDocument()).getWriter().updateNotes(e.getSegment());
    }
    
    @Override
    public void saveTime(Double time){
    	this.time = time;
    }

    @Override
    public void saveLqiConfiguration(String lqiConfigurationName){
    	this.lqiConfiguration = lqiConfigurationName;
    }

    private OkapiXLIFFDocument getDoc(XLIFFDocument xliff) {
        if (!(xliff instanceof OkapiXLIFFDocument)) {
            throw new IllegalArgumentException("Unknown XLIFF file object");
        }
        return (OkapiXLIFFDocument)xliff;
    }

    @Override
    public XLIFFDocument parse(File xliffFile) throws IOException, XMLStreamException {
        XLIFFVersion version = xliffFactory.detectXLIFFVersion(xliffFile);
        XLIFFParser newParser = xliffFactory.newXLIFFParser(version);
        List<OcelotSegment> xliffSegments = newParser.parse(xliffFile);

        XLIFFParser xliffParser = newParser;
        XLIFFWriter segmentWriter = xliffFactory.newXLIFFWriter(xliffParser,
                cfgService.getUserProvenance(), eventQueue);
        return new OkapiXLIFFDocument(xliffFile, version, LocaleId.fromString(xliffParser.getSourceLang()),
                                  LocaleId.fromString(xliffParser.getTargetLang()), xliffParser.getOriginalFileName(), xliffSegments,
                                  xliffParser, segmentWriter);
    }

    @Override
    public void save(XLIFFDocument xliffFile, File dest) throws FileNotFoundException, IOException {
    	
        OkapiXLIFFDocument okapiFile = getDoc(xliffFile);
        okapiFile.getWriter().updateTiming(time);
        okapiFile.getWriter().updateLqiConfiguration(lqiConfiguration);
        okapiFile.getWriter().save(dest);
        time = null;
    }

}
