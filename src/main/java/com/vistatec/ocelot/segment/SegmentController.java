package com.vistatec.ocelot.segment;

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
import java.util.List;

/**
 * Class for handling events related to segments, such as parsing/updating the
 * data, writing out the segments, refreshing their table view, etc.
 */
public class SegmentController {
    private SegmentTableModel segmentModel;
    private SegmentView segmentView;
    private OkapiSegmentWriter segmentWriter;
    private XLIFFParser xliffParser;
    private HTML5Parser html5Parser;
    private boolean openFile = false, isHTML, targetDiff = true;

    public SegmentController() {
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
        segmentWriter = new XLIFFWriter(xliffParser);
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
        segmentWriter = new HTML5Writer(html5Parser);
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
