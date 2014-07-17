/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.ClearAllSegmentsEvent;
import com.vistatec.ocelot.events.ITSDocStatsChangedEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.ProvenanceAddedEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.segment.okapi.OkapiSegmentWriter;
import com.vistatec.ocelot.segment.okapi.XLIFFParser;
import com.vistatec.ocelot.segment.okapi.XLIFFWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling events related to segments, such as parsing/updating the
 * data, writing out the segments, refreshing their table view, etc.
 */
public class SegmentController {
    private Logger LOG = LoggerFactory.getLogger(SegmentController.class);

    private ArrayList<Segment> segments = new ArrayList<Segment>(100);
    private OkapiSegmentWriter segmentWriter;
    private XLIFFParser xliffParser;
    private boolean openFile = false, targetDiff = true;
    private ProvenanceConfig provConfig;
    private EventBus eventBus;
    private boolean dirty = false;
    private ITSDocStats docStats = new ITSDocStats();

    public SegmentController(EventBus eventBus, RuleConfiguration ruleConfig,
                             ProvenanceConfig provConfig) {
        this.eventBus = eventBus;
        this.provConfig = provConfig;
        eventBus.register(this);
    }

    public boolean enabledTargetDiff() {
        return this.targetDiff;
    }

    /**
     * Check if a file has been opened by the workbench.
     */
    public boolean openFile() {
        return this.openFile;
    }

    public void setOpenFile(boolean openFile) {
        this.openFile = openFile;
    }

    public Segment getSegment(int row) {
        return segments.get(row);
    }

    public int getNumSegments() {
        return segments.size();
    }

    /**
     * Return the current summary statistics for this document.
     * This view is *LIVE* and will always reflect the current
     * data for this controller.  {@link ITSDocStatsChangedEvent} will
     * be raised when these values change.
     *
     * @return current document statistics
     */
    public ITSDocStats getStats() {
        return docStats;
    }
    
    protected void notifyResetTarget(Segment seg) {
        eventBus.post(new SegmentTargetResetEvent(seg));
    }

    /**
     * Returns whether there are unsaved changes in the segment data.
     * This includes segment edits and changes to LQI and Provenance data.
     * @return true if there are unsaved changes
     */
    public boolean isDirty() {
        return dirty;
    }

    public void notifyAddedLQI(LanguageQualityIssue lqi, Segment seg) {
        // TODO - rename this
        docStats.addLQIStats(lqi);
    }

    public void notifyModifiedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        eventBus.post(new LQIModificationEvent(lqi, seg));
    }

    @Subscribe
    public void clearAllSegments(ClearAllSegmentsEvent e) {
        segments.clear();
        docStats.clear();
        eventBus.post(new ITSDocStatsChangedEvent());
    }

    @Subscribe
    public void segmentEdited(SegmentEditEvent e) {
        updateSegment(e.getSegment());
    }

    // XXX This is overloaded -- both used during xliff parse/load,
    // but aslo when we add provenance during editing
    public void notifyAddedProv(Provenance prov) {
        dirty = true;
        eventBus.post(new ProvenanceAddedEvent(prov));
        docStats.addProvenanceStats(prov);
    }

    public void parseXLIFFFile(File xliffFile) throws IOException {
        XLIFFParser newParser = new XLIFFParser();
        List<Segment> segments = newParser.parseXLIFFFile(xliffFile);

        eventBus.post(new ClearAllSegmentsEvent());
        xliffParser = newParser;
        for (Segment seg : segments) {
            seg.setSegmentListener(this);
            addSegment(seg);
        }

        setOpenFile(true);
        segmentWriter = new XLIFFWriter(xliffParser, provConfig);
        dirty = false;
    }

    public void addSegment(Segment seg) {
        segments.add(seg);
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            notifyAddedLQI(lqi, seg);
        }
        for (Provenance prov : seg.getProv()) {
            notifyAddedProv(prov);
        }
    }

    public void updateSegment(Segment seg) {
        segmentWriter.updateEvent(seg, this);
        dirty = true;
    }

    public String getFileSourceLang() {
        return xliffParser.getSourceLang();
    }

    public String getFileTargetLang() {
        return xliffParser.getTargetLang();
    }

    /**
     * Save the XLIFF file to the file system.
     * @param file
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void save(File file) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        XLIFFWriter okapiXLIFFWriter = (XLIFFWriter) segmentWriter;
        okapiXLIFFWriter.save(file);
        this.dirty = false;
    }
}
