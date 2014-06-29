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

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.segment.okapi.HTML5Parser;
import com.vistatec.ocelot.segment.okapi.HTML5Writer;
import com.vistatec.ocelot.segment.okapi.OkapiSegmentWriter;
import com.vistatec.ocelot.segment.okapi.XLIFFParser;
import com.vistatec.ocelot.segment.okapi.XLIFFWriter;

import java.io.File;
import java.io.FileInputStream;
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

    private SegmentTableModel segmentModel;
    private SegmentView segmentView;
    private OkapiSegmentWriter segmentWriter;
    private XLIFFParser xliffParser;
    private HTML5Parser html5Parser;
    private boolean openFile = false, isHTML, targetDiff = true;
    private ProvenanceConfig provConfig;
    private List<SegmentSelectionListener> segmentSelectionListeners =
            new ArrayList<SegmentSelectionListener>();

    public SegmentController(ProvenanceConfig provConfig) {
        this.provConfig = provConfig;
        this.segmentModel = new SegmentTableModel(this);
    }

    public void setSegmentView(SegmentView segView) {
        this.segmentView = segView;
    }

    public boolean isHTML() {
        return isHTML;
    }

    public void setHTML(boolean isHTML) {
        this.isHTML = isHTML;
    }

    public boolean enabledTargetDiff() {
        return this.targetDiff;
    }

    public void addSegmentSelectionListener(SegmentSelectionListener listener) {
        this.segmentSelectionListeners.add(listener);
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

    /**
     * Signal that a segment has been selected.  Notify all listeners.
     * @param segment
     */
    public void selectSegment(Segment segment) {
        for (SegmentSelectionListener l : segmentSelectionListeners) {
            l.segmentSelected(segment);
        }
    }
    
    protected void fireTableDataChanged() {
        getSegmentTableModel().fireTableDataChanged();
        segmentView.updateRowHeights();
    }

    public void notifyAddedLQI(LanguageQualityIssue lqi, Segment seg) {
        segmentView.notifyAddedLQI(lqi, seg);
    }

    public void notifyModifiedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        segmentView.notifyModifiedLQI(lqi, seg);
    }

    public void notifyAddedProv(Provenance prov) {
        segmentView.notifyAddedProv(prov);
    }

    public void notifyDeletedSegments() {
        segmentView.notifyDeletedSegments();
    }

    public RuleConfiguration getRuleConfig() {
        return segmentView.ruleConfig;
    }

    public void parseXLIFFFile(File xliffFile) throws FileNotFoundException {
        XLIFFParser newParser = new XLIFFParser();
        List<Segment> segments = newParser.parseXLIFFFile(new FileInputStream(xliffFile));

        segmentView.clearTable();
        getSegmentTableModel().deleteSegments();
        xliffParser = newParser;
        for (Segment seg : segments) {
            seg.setSegmentListener(this);
            addSegment(seg);
        }

        setOpenFile(true);
        setHTML(false);
        segmentWriter = new XLIFFWriter(xliffParser, provConfig.getUserProvenance());
        segmentView.reloadTable();
    }

    public void parseHTML5Files(File srcHTMLFile, File tgtHTMLFile) throws FileNotFoundException {
        HTML5Parser newParser = new HTML5Parser();
        List<Segment> segments = newParser.parseHTML5Files(new FileInputStream(srcHTMLFile),
                new FileInputStream(tgtHTMLFile));

        segmentView.clearTable();
        getSegmentTableModel().deleteSegments();
        html5Parser = newParser;
        for (Segment seg : segments) {
            seg.setSegmentListener(this);
            addSegment(seg);
        }

        setOpenFile(true);
        setHTML(true);
        segmentWriter = new HTML5Writer(html5Parser, provConfig.getUserProvenance());
        segmentView.reloadTable();
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
        return isHTML() ? html5Parser.getSourceLang() : xliffParser.getSourceLang();
    }

    public String getFileTargetLang() {
        return isHTML() ? html5Parser.getTargetLang() : xliffParser.getTargetLang();
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
    }

    /**
     * Save the aligned HTML5 source and target files to the file system.
     * @param source
     * @param target
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void save(File source, File target) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        HTML5Writer okapiHTML5Writer = (HTML5Writer) segmentWriter;
        okapiHTML5Writer.save(source, target);
    }
}
