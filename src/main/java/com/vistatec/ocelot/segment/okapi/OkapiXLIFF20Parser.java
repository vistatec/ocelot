/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.okapi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.XLIFFParser;

import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.StartXliffData;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.reader.Event;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;

/**
 * Parse XLIFF 2.0 file for use in the workbench.
 */
public class OkapiXLIFF20Parser implements XLIFFParser {
    private List<Event> events;
    private Map<Integer, Integer> segmentEventMapping;
    private int documentSegmentNum;
    private String sourceLang, targetLang;

    public List<Event> getEvents() {
        return this.events;
    }

    public Event getSegmentEvent(int segEventNumber) {
        return this.events.get(segmentEventMapping.get(segEventNumber));
    }

    @Override
    public List<Segment> parse(File xliffFile) throws IOException {
        List<Segment> segments = new LinkedList<Segment>();
        segmentEventMapping = new HashMap<Integer, Integer>();
        events = new LinkedList<Event>();
        this.documentSegmentNum = 1;
        int fileEventNum = 0;

        XLIFFReader reader = new XLIFFReader();
        reader.open(xliffFile);
        while (reader.hasNext()) {
            Event event = reader.next();
            this.events.add(event);

            if (event.isStartXliff()) {
                StartXliffData xliffElement = event.getStartXliffData();
                this.sourceLang = xliffElement.getSourceLanguage();
                // optional unless document contains target elements underneath <segment> or <ignorable>
                if (xliffElement.getTargetLanguage() != null) {
                    this.targetLang = xliffElement.getTargetLanguage();
                }

            } else if (event.isUnit()) {
                Unit unit = event.getUnit();
                for (Part unitPart : unit) {
                    if (unitPart.isSegment()) {
                        segments.add(convertPartToSegment(unit, unitPart, fileEventNum));
                    }
                }
            }

            fileEventNum++;
        }
        return segments;
    }

    public Segment convertPartToSegment(Unit parentUnit, Part unitPart, int fileEventNum) {
        segmentEventMapping.put(this.documentSegmentNum, this.events.size()-1);
        return new Segment(this.documentSegmentNum++, fileEventNum, fileEventNum,
                new FragmentVariant(unitPart.getSource()),
                new FragmentVariant(unitPart.getTarget(Part.GetTarget.CREATE_EMPTY)),
                null); //TODO: load original target from file
    }

    @Override
    public String getSourceLang() {
        return this.sourceLang;
    }

    @Override
    public String getTargetLang() {
        return this.targetLang;
    }
}
