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
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.ITSDocStatsChangedEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.ProvenanceAddedEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.rules.RuleConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data model for a document.  This handles most manipulations of the 
 * segment model and generates most segment-related events.
 */
public class SegmentController implements SegmentModel {
    private ArrayList<Segment> segments = new ArrayList<Segment>(100);
    private XLIFFFactory xliffFactory;
    private XLIFFWriter segmentWriter;
    private XLIFFParser xliffParser;
    private boolean openFile = false;
    private ProvenanceConfig provConfig;
    private EventBus eventBus;
    private boolean dirty = false;
    private ITSDocStats docStats = new ITSDocStats();

    public SegmentController(XLIFFFactory xliffFactory, EventBus eventBus, 
                             RuleConfiguration ruleConfig,
                             ProvenanceConfig provConfig) {
        this.xliffFactory = xliffFactory;
        this.eventBus = eventBus;
        this.provConfig = provConfig;
        eventBus.register(this);
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

    @Override
    public Segment getSegment(int row) {
        return segments.get(row);
    }

    @Override
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

    private void recalculateDocStats() {
        docStats.clear();
        for (Segment seg : segments) {
            for (LanguageQualityIssue lqi : seg.getLQI()) {
                docStats.addLQIStats(lqi);
            }
            for (Provenance prov : seg.getProv()) {
                docStats.addProvenanceStats(prov);
            }
        }
        eventBus.post(new ITSDocStatsChangedEvent());
    }

    void notifyModifiedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        docStats.addLQIStats(lqi);
        eventBus.post(new ITSDocStatsChangedEvent());
        eventBus.post(new LQIModificationEvent(lqi, seg));
    }

    void notifyRemovedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        recalculateDocStats();
        eventBus.post(new LQIModificationEvent(lqi, seg));
    }

    public void clearAllSegments() {
        segments.clear();
        docStats.clear();
        eventBus.post(new ITSDocStatsChangedEvent());
    }

    public void notifyUpdateSegment(Segment seg) {
        updateSegment(seg);
        eventBus.post(new SegmentEditEvent(seg));
    }

    // XXX Inconsistent naming - this is used when provenance is added
    // at runtime (for LQI, this is called notifyModifiedProv)
    void notifyAddedProv(Provenance prov) {
        dirty = true;
        eventBus.post(new ProvenanceAddedEvent(prov));
        docStats.addProvenanceStats(prov);
    }

    public void parseXLIFFFile(File xliffFile, File detectVersion) throws IOException, FileNotFoundException, XMLStreamException {
        XLIFFParser newParser = xliffFactory.newXLIFFParser(detectVersion);
        List<Segment> xliffSegments = newParser.parse(xliffFile);

        clearAllSegments();
        xliffParser = newParser;
        setSegments(xliffSegments);

        setOpenFile(true);
        segmentWriter = xliffFactory.newXLIFFWriter(xliffParser, provConfig);
        dirty = false;
    }

    void setSegments(List<Segment> segments) {
        for (Segment seg : segments) {
            addSegment(seg);
        }
        recalculateDocStats();
    }

    private void addSegment(Segment seg) {
        seg.setSegmentListener(this);
        segments.add(seg);
    }

    public void updateSegment(Segment seg) {
        segmentWriter.updateSegment(seg, this);
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
        segmentWriter.save(file);
        this.dirty = false;
    }
}
