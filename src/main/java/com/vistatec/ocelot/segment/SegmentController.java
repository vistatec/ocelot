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
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.segment.okapi.OkapiSegmentWriter;
import com.vistatec.ocelot.segment.okapi.XLIFFParser;
import com.vistatec.ocelot.segment.okapi.XLIFFWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling events related to segments, such as parsing/updating the
 * data, writing out the segments, refreshing their table view, etc.
 */
public class SegmentController {
    private Logger LOG = LoggerFactory.getLogger(SegmentController.class);

    private SegmentTableModel segmentModel;
    private SegmentView segmentView;
    private OkapiSegmentWriter segmentWriter;
    private XLIFFParser xliffParser;
    private boolean openFile = false, targetDiff = true;
    private ProvenanceConfig provConfig;
    private EventBus eventBus;
    private boolean dirty = false;

    public SegmentController(EventBus eventBus, RuleConfiguration ruleConfig,
                             ProvenanceConfig provConfig) {
        this.eventBus = eventBus;
        this.provConfig = provConfig;
        this.segmentModel = new SegmentTableModel(eventBus, ruleConfig);
        eventBus.register(this);
    }

    public void setSegmentView(SegmentView segView) {
        this.segmentView = segView;
    }

    public boolean enabledTargetDiff() {
        return this.targetDiff;
    }

    public void setEnabledTargetDiff(boolean enableTargetDiff) {
        this.targetDiff = enableTargetDiff;
        this.segmentView.reloadTable();
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

    /**
     * For setting the JTable and TableRowSorter models. Also used in PluginManagerView (TODO: Change).
     */
    public SegmentTableModel getSegmentTableModel() {
        return this.segmentModel;
    }

    protected Segment getSegment(int row) {
        return getSegmentTableModel().getSegment(row);
    }

    protected int getNumSegments() {
        return getSegmentTableModel().getRowCount();
    }

    protected int getSegmentNumColumnIndex() {
        return getSegmentTableModel().getColumnIndex(SegmentTableModel.COLSEGNUM);
    }

    protected int getSegmentSourceColumnIndex() {
        return getSegmentTableModel().getColumnIndex(SegmentTableModel.COLSEGSRC);
    }

    protected int getSegmentTargetColumnIndex() {
        return getSegmentTableModel().getColumnIndex(SegmentTableModel.COLSEGTGT);
    }

    protected int getSegmentTargetOriginalColumnIndex() {
        return getSegmentTableModel().getColumnIndex(SegmentTableModel.COLSEGTGTORI);
    }

    protected void fireTableDataChanged() {
        getSegmentTableModel().fireTableDataChanged();
        segmentView.updateRowHeights();
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
        // TODO sort out event stuff here and convert to an 
        // event handler
        segmentView.notifyAddedLQI(lqi, seg);
    }

    public void notifyModifiedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        eventBus.post(new LQIModificationEvent(lqi, seg));
        this.dirty = true;
        // TODO: convert this to an event handler
        segmentView.notifyModifiedLQI(lqi, seg);
    }

    @Subscribe
    public void segmentEdited(SegmentEditEvent e) {
        this.dirty = true;
    }

    public void notifyAddedProv(Provenance prov) {
        dirty = true;
        // TODO: convert to event
        segmentView.notifyAddedProv(prov);
    }

    public void parseXLIFFFile(File xliffFile) throws FileNotFoundException {
        XLIFFParser newParser = new XLIFFParser();
        List<Segment> segments = newParser.parseXLIFFFile(new FileInputStream(xliffFile));

        segmentView.clearTable(); // XXX make this an event listener
        eventBus.post(new ClearAllSegmentsEvent());
        xliffParser = newParser;
        for (Segment seg : segments) {
            seg.setSegmentListener(this);
            addSegment(seg);
        }

        setOpenFile(true);
        segmentWriter = new XLIFFWriter(xliffParser, provConfig);
        segmentView.reloadTable();
        dirty = false;
    }

    public void addSegment(Segment seg) {
        getSegmentTableModel().addSegment(seg);
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            notifyAddedLQI(lqi, seg);
        }
        for (Provenance prov : seg.getProv()) {
            notifyAddedProv(prov);
        }
    }

    public void updateSegment(Segment seg) {
        segmentWriter.updateEvent(seg, this);
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
