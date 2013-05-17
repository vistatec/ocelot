package com.spartansoftwareinc.vistatec.rwb.segment;

import com.spartansoftwareinc.plugins.PluginManager;
import com.spartansoftwareinc.vistatec.rwb.its.ITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.rules.DataCategoryFlag;
import com.spartansoftwareinc.vistatec.rwb.rules.RuleConfiguration;
import com.spartansoftwareinc.vistatec.rwb.rules.RuleListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.its.html5.HTML5Filter;
import net.sf.okapi.filters.xliff.XLIFFFilter;
import org.apache.log4j.Logger;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener {
    private static Logger LOG = Logger.getLogger("com.spartansoftwareinc.SegmentView");
    protected JTable sourceTargetTable;
    private SegmentTableModel segments;
    private LinkedList<Event> srcEvents, tgtEvents;
    private ListSelectionModel tableSelectionModel;
    private SegmentAttributeView attrView;
    private TableColumnModel tableColumnModel;
    protected TableRowSorter sort;
    private boolean isHTML = false;
    private String srcSLang, srcTLang;
    protected RuleConfiguration ruleConfig;
    protected PluginManager pluginManager;
    private int documentSegmentNum;
    private IFilter filter;

    public SegmentView(SegmentAttributeView attr) throws IOException, InstantiationException, InstantiationException, IllegalAccessException {
        attrView = attr;
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
        ruleConfig = new RuleConfiguration(this);
        pluginManager = new PluginManager(segments);
        pluginManager.discover(pluginManager.getPluginDir());
    }

    public SegmentTableModel getSegments() {
        return segments;
    }

    public void initializeTable() {
        segments = new SegmentTableModel(this);
        sourceTargetTable = new JTable(segments);
        sourceTargetTable.getTableHeader().setReorderingAllowed(false);

        ListSelectionListener selectSegmentHandler = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                selectedSegment();
            }
        };
        tableSelectionModel = sourceTargetTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(selectSegmentHandler);

        DefaultTableCellRenderer segNumAlign = new DefaultTableCellRenderer();
        segNumAlign.setHorizontalAlignment(JLabel.LEFT);
        segNumAlign.setVerticalAlignment(JLabel.TOP);
        sourceTargetTable.setDefaultRenderer(Integer.class, segNumAlign);
        sourceTargetTable.setDefaultRenderer(DataCategoryFlag.class,
                new DataCategoryFlagRenderer());
        sourceTargetTable.setDefaultRenderer(String.class,
                new SegmentTextRenderer());

        tableColumnModel = sourceTargetTable.getColumnModel();
        tableColumnModel.getSelectionModel().addListSelectionListener(
                selectSegmentHandler);
        tableColumnModel.getColumn(0).setMinWidth(15);
        tableColumnModel.getColumn(0).setPreferredWidth(20);
        tableColumnModel.getColumn(0).setMaxWidth(50);

        tableColumnModel.getColumn(segments.getColumnIndex(
                SegmentTableModel.COLSEGTGT)).setCellEditor(new SegmentEditor());
        int flagMinWidth = 15, flagPrefWidth = 15, flagMaxWidth = 20;
        for (int i = SegmentTableModel.NONFLAGCOLS;
             i < SegmentTableModel.NONFLAGCOLS+SegmentTableModel.NUMFLAGS; i++) {
            tableColumnModel.getColumn(i).setMinWidth(flagMinWidth);
            tableColumnModel.getColumn(i).setPreferredWidth(flagPrefWidth);
            tableColumnModel.getColumn(i).setMaxWidth(flagMaxWidth);
        }

        tableColumnModel.addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent tcme) {}

            @Override
            public void columnRemoved(TableColumnModelEvent tcme) {}

            @Override
            public void columnMoved(TableColumnModelEvent tcme) {}

            @Override
            public void columnMarginChanged(ChangeEvent ce) {
                updateRowHeights();
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent lse) {}
        });
        setViewportView(sourceTargetTable);
    }

    public void reloadTable() {
        sourceTargetTable.clearSelection();
        sourceTargetTable.setRowSorter(null);
        attrView.treeView.clearTree();
        setViewportView(null);
        segments.fireTableDataChanged();
        addFilters();
        updateRowHeights();
        setViewportView(sourceTargetTable);
    }

    public void requestFocusTable() {
        sourceTargetTable.requestFocus();
    }

    public boolean isHTML() {
        return this.isHTML;
    }

    public void setHTML(boolean flag) {
        this.isHTML = flag;
    }

    public void parseSegmentsFromHTMLFile(File sourceFile, File targetFile) throws IOException {
        sourceTargetTable.clearSelection();
        segments.deleteSegments();
        sourceTargetTable.setRowSorter(null);
        attrView.clearSegment();
        srcEvents = new LinkedList<Event>();
        tgtEvents = new LinkedList<Event>();
        setViewportView(null);
        documentSegmentNum = 1;
        setHTML(true);

        parseHTML5Files(new FileInputStream(sourceFile), new FileInputStream(targetFile));
        addFilters();

        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segments.getColumnIndex(SegmentTableModel.COLSEGNUM))
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + documentSegmentNum));

        updateRowHeights();
        setViewportView(sourceTargetTable);
    }

    public void parseHTML5Files(FileInputStream src, FileInputStream tgt) {
        RawDocument srcDoc = new RawDocument(src, "UTF-8", LocaleId.fromString("en"));
        RawDocument tgtDoc = new RawDocument(tgt, "UTF-8", LocaleId.fromString("de"));
        IFilter srcFilter = new HTML5Filter();
        IFilter tgtFilter = new HTML5Filter();
        srcFilter.open(srcDoc);
        tgtFilter.open(tgtDoc);
        int srcEventNum = 0, tgtEventNum = 0;

        while(srcFilter.hasNext() && tgtFilter.hasNext()) {
            Event srcEvent = srcFilter.next();
            Event tgtEvent = tgtFilter.next();
            srcEvents.add(srcEvent);
            tgtEvents.add(tgtEvent);

            ITextUnit srcTu, tgtTu;
            if (srcEvent.isTextUnit() && tgtEvent.isTextUnit()) {
                srcTu = (ITextUnit) srcEvent.getResource();
                tgtTu = (ITextUnit) tgtEvent.getResource();
                TextContainer srcTc = srcTu.getSource();
                TextContainer tgtTc = tgtTu.getSource();

                GenericAnnotations srcITSTags = srcTc.getAnnotation(GenericAnnotations.class);
                GenericAnnotations tgtITSTags = tgtTc.getAnnotation(GenericAnnotations.class);
                List<GenericAnnotation> anns = new LinkedList<GenericAnnotation>();
                // TODO: get annotations for other data categories
                if (srcITSTags != null) {
                    anns.addAll(srcITSTags.getAnnotations(GenericAnnotationType.LQI));
                    anns.addAll(srcITSTags.getAnnotations(GenericAnnotationType.PROV));
                }
                if (tgtITSTags != null) {
                    anns.addAll(tgtITSTags.getAnnotations(GenericAnnotationType.LQI));
                    anns.addAll(tgtITSTags.getAnnotations(GenericAnnotationType.PROV));
                }

                addSegment(srcTc, tgtTc, anns, srcEventNum, tgtEventNum);
            }
            srcEventNum++;
            tgtEventNum++;
        }
        if (srcFilter.hasNext() || tgtFilter.hasNext()) {
            LOG.error("Documents not aligned?");
            while (srcFilter.hasNext()) {
                srcEvents.add(srcFilter.next());
                srcEventNum++;
            }
            while (tgtFilter.hasNext()) {
                tgtEvents.add(tgtFilter.next());
                tgtEventNum++;
            }
        }
    }

    public void parseSegmentsFromXLIFFFile(File sourceFile) throws IOException {
        sourceTargetTable.clearSelection();
        segments.deleteSegments();
        sourceTargetTable.setRowSorter(null);
        attrView.clearSegment();
        tgtEvents = null;
        srcEvents = new LinkedList<Event>();
        setViewportView(null);
        documentSegmentNum = 1;
        setHTML(false);

        parseXLIFFFile(new FileInputStream(sourceFile));
        addFilters();

        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segments.getColumnIndex(SegmentTableModel.COLSEGNUM))
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + documentSegmentNum));

        updateRowHeights();
        setViewportView(sourceTargetTable);
    }

    public void parseXLIFFFile(FileInputStream file) {
        RawDocument fileDoc = new RawDocument(file, "UTF-8", LocaleId.EMPTY, LocaleId.EMPTY);
        this.filter = new XLIFFFilter();
        this.filter.open(fileDoc);
        int fileEventNum = 0;

        while(this.filter.hasNext()) {
            Event event = this.filter.next();
            srcEvents.add(event);

            if (event.isStartSubDocument()) {
                StartSubDocument fileElement = (StartSubDocument)event.getResource();
                if (fileElement.getProperty("sourceLanguage") != null) {
                    String fileSourceLang = fileElement.getProperty("sourceLanguage").getValue();
                    if (srcSLang != null && !srcSLang.equals(fileSourceLang)) {
                        LOG.warn("Mismatch between source languages in file elements");
                    }
                    srcSLang = fileSourceLang;
                    fileDoc.setSourceLocale(LocaleId.fromString(srcSLang));
                }
                if (fileElement.getProperty("targetLanguage") != null) {
                    String fileTargetLang = fileElement.getProperty("targetLanguage").getValue();
                    if (srcTLang != null && !srcTLang.equals(fileTargetLang)) {
                        LOG.warn("Mismatch between target languages in file elements");
                    }
                    srcTLang = fileTargetLang;
                    fileDoc.setTargetLocale(LocaleId.fromString(srcTLang));
                }

            } else if (event.isTextUnit()) {
                ITextUnit tu = (ITextUnit) event.getResource();
                TextContainer srcTu = tu.getSource();
                TextContainer tgtTu = new TextContainer();

                Set<LocaleId> targetLocales = tu.getTargetLocales();
                if (targetLocales.size() > 1) {
                    LOG.warn("More than 1 target locale"+targetLocales);
                } else if (targetLocales.size() == 1) {
                    for (LocaleId tgt : targetLocales) {
			tgtTu = tu.getTarget(tgt);
                    }
                } else {
                    tu.setTarget(LocaleId.fromString(srcTLang), tgtTu);
                }

                GenericAnnotations itsTags = tu.getAnnotation(GenericAnnotations.class);
                List<GenericAnnotation> anns = new LinkedList<GenericAnnotation>();
                // TODO: get annotations for other data categories
                if (itsTags != null) {
                    anns.addAll(itsTags.getAnnotations(GenericAnnotationType.LQI));
                    anns.addAll(itsTags.getAnnotations(GenericAnnotationType.PROV));
                }

                GenericAnnotations srcAnns = srcTu.getAnnotation(GenericAnnotations.class);
                if (srcAnns != null) {
                    anns.addAll(srcAnns.getAnnotations(GenericAnnotationType.LQI));
                    anns.addAll(srcAnns.getAnnotations(GenericAnnotationType.PROV));
                }

                if (tgtTu != null) {
                    GenericAnnotations tgtAnns = tgtTu.getAnnotation(GenericAnnotations.class);
                    if (tgtAnns != null) {
                        anns.addAll(tgtAnns.getAnnotations(GenericAnnotationType.LQI));
                        anns.addAll(tgtAnns.getAnnotations(GenericAnnotationType.PROV));
                    }
                }

                addSegment(srcTu, tgtTu, anns, fileEventNum, fileEventNum);
            }
            fileEventNum++;
        }
    }

    public void addSegment(TextContainer sourceText, TextContainer targetText,
            List<GenericAnnotation> annotations, int srcEventNum, int tgtEventNum) {
        Segment seg = new Segment(documentSegmentNum++, srcEventNum, tgtEventNum,
                sourceText, targetText, this);
        // TODO: parse GenericAnnotations for other data categories.
        for (GenericAnnotation ga : annotations) {
            if (ga.getType().equals(GenericAnnotationType.LQI)) {
                seg.addLQI(new LanguageQualityIssue(ga));
            } else if (ga.getType().equals(GenericAnnotationType.PROV)) {
                seg.addProvenance(new Provenance(ga));
            }
        }
        segments.addSegment(seg);
    }

    public void addFilters() {
        sort = new TableRowSorter(segments);
        sourceTargetTable.setRowSorter(sort);
        sort.setRowFilter(ruleConfig);
    }

    protected void updateRowHeights() {
        setViewportView(null);

        SegmentTextCell segmentCell = new SegmentTextCell();
        segmentCell.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        for (int row = 0; row < sourceTargetTable.getRowCount(); row++) {
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            int rowHeight = font.getHeight();
            for (int col = 1; col < 3; col++) {
                int width = sourceTargetTable.getColumnModel().getColumn(col).getWidth();
                if (col == 1) {
                    String text = segments.getSegment(row).getSource().getCodedText();
                    segmentCell.setText(text);
                } else {
                    String text = segments.getSegment(row).getTarget().getCodedText();
                    segmentCell.setText(text);
                }
                // Need to set width to force text area to calculate a pref height
                segmentCell.setSize(new Dimension(width, sourceTargetTable.getRowHeight(row)));
                rowHeight = Math.max(rowHeight, segmentCell.getPreferredSize().height);
            }
            sourceTargetTable.setRowHeight(row, rowHeight);
        }
        setViewportView(sourceTargetTable);
    }

    public Segment getSelectedSegment() {
        Segment selectedSeg = null;
        if (sourceTargetTable.getSelectedRow() >= 0) {
            selectedSeg = segments.getSegment(sort.convertRowIndexToModel(
                sourceTargetTable.getSelectedRow()));
        }
        return selectedSeg;
    }

    public void selectedSegment() {
        Segment seg = getSelectedSegment();
        if (seg != null) {
            attrView.setSelectedSegment(seg);
            int colIndex = sourceTargetTable.getSelectedColumn();
            if (colIndex >= SegmentTableModel.NONFLAGCOLS) {
                int adjustedFlagIndex = colIndex - SegmentTableModel.NONFLAGCOLS;
                ITSMetadata its = ruleConfig.getTopDataCategory(seg, adjustedFlagIndex);
                if (its != null) {
                    attrView.setSelectedMetadata(its);
                }
            }
        }
    }

    public void notifyAddedLQI(LanguageQualityIssue lqi, Segment seg) {
        attrView.addLQIMetadata(lqi);
    }

    public void notifyAddedNewLQI(LanguageQualityIssue lqi, Segment seg) {
        attrView.setSelectedMetadata(lqi);
        attrView.setSelectedSegment(seg);
        updateEvent(seg);
        int selectedRow = sourceTargetTable.getSelectedRow();
        reloadTable();
        sourceTargetTable.setRowSelectionInterval(selectedRow, selectedRow);
    }

    public void notifyAddedProv(Provenance prov) {
        attrView.addProvMetadata(prov);
    }

    public void notifyDeletedSegments() {
        attrView.deletedSegments();
    }

    public void save(File source) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        saveEvents(this.filter, srcEvents, source.getAbsolutePath(), LocaleId.fromString(srcSLang));
    }

    public void save(File source, File target) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        // TODO: get the actual locale
        saveEvents(new HTML5Filter(), srcEvents, source.getAbsolutePath(), LocaleId.fromString("en"));
        saveEvents(new HTML5Filter(), tgtEvents, target.getAbsolutePath(), LocaleId.fromString("de"));
    }

    public void saveEvents(IFilter filter, List<Event> events, String output, LocaleId locId) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuilder tmp = new StringBuilder();
        ISkeletonWriter skelWriter = filter.createSkeletonWriter();
        EncoderManager encoderManager = filter.getEncoderManager();
        for (Event event : events) {
            switch (event.getEventType()) {
                case START_DOCUMENT:
                    tmp.append(skelWriter.processStartDocument(locId, "UTF-8", null, encoderManager,
                            (StartDocument) event.getResource()));
                    break;
                case END_DOCUMENT:
                    tmp.append(skelWriter.processEndDocument((Ending) event.getResource()));
                    break;
                case START_SUBDOCUMENT:
                    tmp.append(skelWriter.processStartSubDocument((StartSubDocument) event
                            .getResource()));
                    break;
                case END_SUBDOCUMENT:
                    tmp.append(skelWriter.processEndSubDocument((Ending) event.getResource()));
                    break;
                case TEXT_UNIT:
                    ITextUnit tu = event.getTextUnit();
                    tmp.append(skelWriter.processTextUnit(tu));
                    break;
                case DOCUMENT_PART:
                    DocumentPart dp = (DocumentPart) event.getResource();
                    tmp.append(skelWriter.processDocumentPart(dp));
                    break;
                case START_GROUP:
                    StartGroup startGroup = (StartGroup) event.getResource();
                    tmp.append(skelWriter.processStartGroup(startGroup));
                    break;
                case END_GROUP:
                    tmp.append(skelWriter.processEndGroup((Ending) event.getResource()));
                    break;
                case START_SUBFILTER:
                    StartSubfilter startSubfilter = (StartSubfilter) event.getResource();
                    tmp.append(skelWriter.processStartSubfilter(startSubfilter));
                    break;
                case END_SUBFILTER:
                    tmp.append(skelWriter.processEndSubfilter((EndSubfilter) event.getResource()));
                    break;
            }
        }
        skelWriter.close();
        Writer outputFile = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        outputFile.write(tmp.toString());
        outputFile.flush();
        outputFile.close();
    }

    public void updateEvent(Segment seg) {
        if (isHTML) {
            updateHTMLEvent(seg);
        } else {
            updateXLIFFEvent(seg);
        }
    }

    public void updateHTMLEvent(Segment seg) {
        // TODO: Fix the locales, remove old generic annotations
        Event srcEvent = srcEvents.get(seg.getSourceEventNumber());
        ITextUnit srcTu = srcEvent.getTextUnit();
        TextFragment srcTf = srcTu.createTarget(LocaleId.fromString("en"), true, IResource.COPY_ALL).getFirstContent();
        Event tgtEvent = tgtEvents.get(seg.getTargetEventNumber());
        ITextUnit tgtTu = tgtEvent.getTextUnit();
        TextFragment tgtTf = tgtTu.createTarget(LocaleId.fromString("de"), true, IResource.COPY_ALL).getFirstContent();

        GenericAnnotations lqiAnns = new GenericAnnotations();
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.LQI,
                    GenericAnnotationType.LQI_TYPE, lqi.getType(),
                    GenericAnnotationType.LQI_COMMENT, lqi.getComment(),
                    GenericAnnotationType.LQI_SEVERITY, lqi.getSeverity(),
                    GenericAnnotationType.LQI_ENABLED, lqi.isEnabled());
            lqiAnns.add(ga);
            lqiAnns.setData(lqi.getIssuesRef());
        }

        ITSProvenanceAnnotations provAnns = addRWProvenance(seg);
        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, provAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, provAnns);
    }

    public void updateXLIFFEvent(Segment seg) {
        // TODO: Fix the locales, remove old generic annotations
        Event srcEvent = srcEvents.get(seg.getSourceEventNumber());
        ITextUnit textUnit = srcEvent.getTextUnit();
        String rwRef = "RW"+seg.getSegmentNumber();

        ITSLQIAnnotations lqiAnns = new ITSLQIAnnotations();
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.LQI,
                    GenericAnnotationType.LQI_TYPE, lqi.getType(),
                    GenericAnnotationType.LQI_COMMENT, lqi.getComment(),
                    GenericAnnotationType.LQI_SEVERITY, lqi.getSeverity(),
                    GenericAnnotationType.LQI_ENABLED, lqi.isEnabled());
            lqiAnns.add(ga);
        }

        if (lqiAnns.size() > 0) {
            textUnit.setProperty(new Property(Property.ITS_LQI, " its:locQualityIssuesRef=\"#"+rwRef+"\""));
            textUnit.setAnnotation(lqiAnns);
        } else {
            textUnit.setProperty(new Property(Property.ITS_LQI, ""));
            textUnit.setAnnotation(null);
        }
        lqiAnns.setData(rwRef);
        seg.getSource().setProperty(new Property(Property.ITS_LQI, ""));
        seg.getSource().setAnnotation(null);
        textUnit.setSource(seg.getSource());

        Set<LocaleId> targetLocales = textUnit.getTargetLocales();
        if (targetLocales.size() == 1) {
            for (LocaleId tgt : targetLocales) {
                TextContainer tgtTC = textUnit.getTarget(tgt);
                tgtTC.setProperty(new Property(Property.ITS_LQI, ""));
                tgtTC.setAnnotation(null);
                textUnit.setTarget(tgt, tgtTC);
            }
        } else if (targetLocales.isEmpty()) {
            textUnit.setTarget(LocaleId.fromString(srcTLang), seg.getTarget());

        } else {
            LOG.warn("Only 1 target locale in text-unit is currently supported");
        }

        ITSProvenanceAnnotations provAnns = addRWProvenance(seg);
        textUnit.setProperty(new Property(Property.ITS_PROV, " its:provenanceRecordsRef=\"#" + rwRef + "\""));
        provAnns.setData(rwRef);
        textUnit.setAnnotation(provAnns);
    }

    public ITSProvenanceAnnotations addRWProvenance(Segment seg) {
        Properties p = new Properties();
        File rwDir = new File(System.getProperty("user.home"), ".reviewersWorkbench");
        File provFile = new File(rwDir, "provenance.properties");
        if (provFile.exists()) {
            try {
                p.load(new FileInputStream(provFile));
            } catch (IOException ex) {
                LOG.warn(ex);
            }
        }

        ITSProvenanceAnnotations provAnns = new ITSProvenanceAnnotations();
        for (Provenance prov : seg.getProv()) {
            String revPerson = prov.getRevPerson();
            String revOrg = prov.getRevOrg();
            String provRef = prov.getProvRef();
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_PERSON, prov.getPerson(),
                    GenericAnnotationType.PROV_ORG, prov.getOrg(),
                    GenericAnnotationType.PROV_TOOL, prov.getTool(),
                    GenericAnnotationType.PROV_REVPERSON, revPerson,
                    GenericAnnotationType.PROV_REVORG, revOrg,
                    GenericAnnotationType.PROV_REVTOOL, prov.getRevTool(),
                    GenericAnnotationType.PROV_PROVREF, provRef);
            provAnns.add(ga);

            // Check for existing RW annotation.
            if (p.getProperty("revPerson").equals(prov.getRevPerson())
                    && p.getProperty("revOrganization").equals(prov.getRevOrg())
                    && p.getProperty("externalReference").equals(prov.getProvRef())) {
                seg.setAddedRWProvenance(true);
            }
        }

        if (!seg.addedRWProvenance()) {
            GenericAnnotation provGA = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_REVPERSON, p.getProperty("revPerson"),
                    GenericAnnotationType.PROV_REVORG, p.getProperty("revOrganization"),
                    GenericAnnotationType.PROV_PROVREF, p.getProperty("externalReference"));
            provAnns.add(provGA);
            seg.addProvenance(new Provenance(provGA));
            seg.setAddedRWProvenance(true);
        }

        return provAnns;
    }

    /**
     * Rule configuration methods.
     */
    public RuleConfiguration getRuleConfig() {
        return this.ruleConfig;
    }

    @Override
    public void enabledRule(String ruleLabel, boolean enabled) {
        reloadTable();
    }

    @Override
    public void allSegments(boolean enabled) {
        reloadTable();
    }

    @Override
    public void allMetadataSegments(boolean enabled) {
        reloadTable();
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    /**
     * TableCellRenderer for source/target text in the SegmentTableView.
     */
    public class SegmentTextRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
            boolean isSelected, boolean hasFocus, int row, int col) {
            SegmentTextCell renderTextPane = new SegmentTextCell();
            if (segments.getRowCount() > row) {
                Segment seg = segments.getSegment(row);
                TextContainer tc = segments.getColumnIndex(SegmentTableModel.COLSEGSRC) == col ?
                        seg.getSource() : seg.getTarget();
                if (tc != null) {
                    renderTextPane.setTextContainer(tc, false);
                }
                renderTextPane.setBackground(isSelected ? jtable.getSelectionBackground() : jtable.getBackground());
                renderTextPane.setForeground(isSelected ? jtable.getSelectionForeground() : jtable.getForeground());
                renderTextPane.setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());
            }

            return renderTextPane;
        }
    }

    public class DataCategoryFlagRenderer extends JLabel implements TableCellRenderer {

        public DataCategoryFlagRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
            DataCategoryFlag flag = (DataCategoryFlag) obj;
            setBackground(flag.getFill());
            setBorder(hasFocus ?
                    UIManager.getBorder("Table.focusCellHighlightBorder") :
                    flag.getBorder());
            setText(flag.getText());
            setHorizontalAlignment(CENTER);
            return this;
        }
    }

    public class SegmentEditor extends AbstractCellEditor implements TableCellEditor {

        protected SegmentTextCell editorComponent;

        @Override
        public Component getTableCellEditorComponent(JTable jtable, Object value,
            boolean isSelected, int row, int col) {
            editorComponent = new SegmentTextCell(segments.getSegment(row).getTarget(), false);
            editorComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
            editorComponent.getActionMap().put("finish", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
            return editorComponent;
        }

        @Override
        public Object getCellEditorValue() {
            return editorComponent.getTextContainer();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent)anEvent).getClickCount() >= 2;
            }
            return true;
        }
    }
}
