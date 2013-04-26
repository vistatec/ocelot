package com.spartansoftwareinc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
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
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.its.html5.HTML5Filter;
import net.sf.okapi.filters.xliff.XLIFFFilter;
import org.apache.log4j.Logger;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane {
    private static Logger LOG = Logger.getLogger("com.spartansoftwareinc.SegmentView");
    protected JTable sourceTargetTable;
    private SegmentTableModel segments;
    private LinkedList<Event> srcEvents, tgtEvents;
    private ListSelectionModel tableSelectionModel;
    private SegmentAttributeView attrView;
    private TableColumnModel tableColumnModel;
    protected TableRowSorter sort;
    private File sourceFile, targetFile;
    protected RuleConfiguration ruleConfig;
    private int documentSegmentNum;
    protected int selectedRow = -1, selectedCol = -1;
    private IFilter filter;

    public SegmentView(SegmentAttributeView attr) throws IOException, InstantiationException, InstantiationException, IllegalAccessException {
        attrView = attr;
        attrView.setSegmentView(this);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
        ruleConfig = new RuleConfiguration();
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

    public void parseSegmentsFromHTMLFile(File sourceFile, File targetFile) throws IOException {
        sourceTargetTable.clearSelection();
        segments.deleteSegments();
        sourceTargetTable.setRowSorter(null);
        attrView.clearSegment();
        srcEvents = new LinkedList<Event>();
        tgtEvents = new LinkedList<Event>();
        setViewportView(null);
        documentSegmentNum = 1;

        parseHTML5Files(new FileInputStream(sourceFile), new FileInputStream(targetFile));
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        updateRowHeights();
        attrView.aggregateTableView.setDocument();
        addFilters();

        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segments.getColumnIndex(SegmentTableModel.COLSEGNUM))
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + documentSegmentNum));

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
                Iterator<TextPart> srcTextParts = srcTu.getSource().iterator();
                Iterator<TextPart> tgtTextParts = tgtTu.getSource().iterator();

                String srcText = "", tgtText = "";
                while(srcTextParts.hasNext() && tgtTextParts.hasNext()) {
                    srcText += srcTextParts.next().text.getText();
                    tgtText += tgtTextParts.next().text.getText();
                }

                GenericAnnotations srcITSTags = srcTu.getSource().getAnnotation(GenericAnnotations.class);
                GenericAnnotations tgtITSTags = tgtTu.getSource().getAnnotation(GenericAnnotations.class);
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

                addSegment(srcText, tgtText, anns, srcEventNum, tgtEventNum);
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
        targetFile = null;
        tgtEvents = null;
        srcEvents = new LinkedList<Event>();
        setViewportView(null);
        documentSegmentNum = 1;

        parseXLIFFFile(new FileInputStream(sourceFile));
        this.sourceFile = sourceFile;
        updateRowHeights();
        attrView.aggregateTableView.setDocument();
        addFilters();

        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segments.getColumnIndex(SegmentTableModel.COLSEGNUM))
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + documentSegmentNum));

        setViewportView(sourceTargetTable);
    }

    public void parseXLIFFFile(FileInputStream file) {
        RawDocument fileDoc = new RawDocument(file, "UTF-8", LocaleId.fromString("en"), LocaleId.fromString("fr"));
        this.filter = new XLIFFFilter();
        this.filter.open(fileDoc);
        int fileEventNum = 0;

        while(this.filter.hasNext()) {
            Event event = this.filter.next();
            srcEvents.add(event);

            ITextUnit tu;
            if (event.isTextUnit()) {
                tu = (ITextUnit) event.getResource();
                TextContainer srcTu = tu.getSource();
                TextContainer tgtTu = null;

                String srcText = "", tgtText = "";
		Iterator<TextPart> srcTextParts = tu.getSource().iterator();
                while(srcTextParts.hasNext()) {
                    srcText += srcTextParts.next().text;
                }

                Set<LocaleId> targetLocales = tu.getTargetLocales();
                if (targetLocales.size() > 1) {
                    LOG.warn("More than 1 target locale"+targetLocales);
                } else if (targetLocales.size() == 1) {
                    for (LocaleId tgt : targetLocales) {
			tgtTu = tu.getTarget(tgt);
                        Iterator<TextPart> tgtTextParts = tgtTu.iterator();
                        while (tgtTextParts.hasNext()) {
                            tgtText += tgtTextParts.next().text;
                        }
                    }
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

                addSegment(srcText, tgtText, anns, fileEventNum, fileEventNum);
            }
            fileEventNum++;
        }
    }

    public void addSegment(String sourceText, String targetText, List<GenericAnnotation> annotations, int srcEventNum, int tgtEventNum) {
        Segment seg = new Segment(documentSegmentNum++, srcEventNum, tgtEventNum, sourceText, targetText);
        // TODO: parse GenericAnnotations for other data categories.
        for (GenericAnnotation ga : annotations) {
            if (ga.getType().equals(GenericAnnotationType.LQI)) {
                seg.addLQI(new LanguageQualityIssue(ga));
            } else if (ga.getType().equals(GenericAnnotationType.PROV)) {
                seg.addProvenance(new ITSProvenance(ga));
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

        SegmentTextRenderer str = new SegmentTextRenderer();
        str.setLineWrap(true);
        str.setWrapStyleWord(true);
        str.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        for (int row = 0; row < sourceTargetTable.getRowCount(); row++) {
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            int rowHeight = font.getHeight();
            for (int col = 1; col < 3; col++) {
                int width = sourceTargetTable.getColumnModel().getColumn(col).getWidth();
                if (col == 1) {
                    str.setText(segments.getSegment(row).getSource());
                } else {
                    str.setText(segments.getSegment(row).getTarget());
                }
                // Need to set width to force text area to calculate a pref height
                str.setSize(new Dimension(width, sourceTargetTable.getRowHeight(row)));
                rowHeight = Math.max(rowHeight, str.getPreferredSize().height);
            }
            sourceTargetTable.setRowHeight(row, rowHeight);
        }
        setViewportView(sourceTargetTable);
    }

    public void selectedSegment() {
        int colIndex = sourceTargetTable.getSelectedColumn();
        int rowIndex = sourceTargetTable.getSelectedRow();
        if (rowIndex >= 0) {
            int modelRowIndex = sort.convertRowIndexToModel(rowIndex);
            Segment seg = segments.getSegment(modelRowIndex);
            attrView.setSelectedSegment(seg);

            if (colIndex >= SegmentTableModel.NONFLAGCOLS) {
                int adjustedFlagIndex = colIndex - SegmentTableModel.NONFLAGCOLS;
                ITSMetadata its = ruleConfig.getTopDataCategory(seg, adjustedFlagIndex);
                if (its != null) {
                    attrView.setSelectedMetadata(its);
                }
            }
        }
    }

    public void save() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        // TODO: get the actual locale and filename for the files.
        if (targetFile == null) {
            saveEvents(this.filter, srcEvents, sourceFile.getName() + ".output", LocaleId.fromString("en"));
        } else {
            saveEvents(new HTML5Filter(), srcEvents, sourceFile.getName() + ".output", LocaleId.fromString("en"));
            saveEvents(new HTML5Filter(), tgtEvents, targetFile.getName() + ".output", LocaleId.fromString("de"));
        }
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

    public void updateEvent(Segment seg) throws FileNotFoundException, IOException {
        if (targetFile == null) {
            updateXLIFFEvent(seg);
        } else {
            updateHTMLEvent(seg);
        }
    }

    public void updateHTMLEvent(Segment seg) throws FileNotFoundException, IOException {
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

        GenericAnnotations provAnns = new GenericAnnotations();
        for (ITSProvenance prov : seg.getProv()) {
            GenericAnnotation ga = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_PERSON, prov.getPerson(),
                    GenericAnnotationType.PROV_ORG, prov.getOrg(),
                    GenericAnnotationType.PROV_TOOL, prov.getTool(),
                    GenericAnnotationType.PROV_REVPERSON, prov.getRevPerson(),
                    GenericAnnotationType.PROV_REVORG, prov.getRevOrg(),
                    GenericAnnotationType.PROV_REVTOOL, prov.getRevTool(),
                    GenericAnnotationType.PROV_PROVREF, prov.getProvRef());
            provAnns.add(ga);
            provAnns.setData(prov.getRecsRef());
        }

        if (!seg.addedRWProvenance()) {
            Properties p = new Properties();
            File rwDir = new File(System.getProperty("user.home"), ".reviewersWorkbench");
            File provFile = new File(rwDir, "provenance.properties");
            if (provFile.exists()) {
                p.load(new FileInputStream(provFile));
            }
            GenericAnnotation provGA = new GenericAnnotation(GenericAnnotationType.PROV,
                    GenericAnnotationType.PROV_REVPERSON, p.getProperty("revPerson"),
                    GenericAnnotationType.PROV_REVORG, p.getProperty("revOrganization"),
                    GenericAnnotationType.PROV_PROVREF, p.getProperty("externalReference"));
            // TODO: Adding a single provenance record is not supported yet.
            provAnns.add(provGA);
            seg.setAddedRWProvenance(true);
        }

        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        srcTf.annotate(0, srcTf.length(), GenericAnnotationType.GENERIC, provAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, lqiAnns);
        tgtTf.annotate(0, tgtTf.length(), GenericAnnotationType.GENERIC, provAnns);
    }

    public void updateXLIFFEvent(Segment seg) throws FileNotFoundException, IOException {
        // TODO: Fix the locales, remove old generic annotations
        Event srcEvent = srcEvents.get(seg.getSourceEventNumber());
        ITextUnit srcTu = srcEvent.getTextUnit();
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
            srcTu.setProperty(new Property(Property.ITS_LQI, " its:locQualityIssuesRef=\"#"+rwRef+"\""));
            srcTu.setAnnotation(lqiAnns);
        } else {
            srcTu.setProperty(new Property(Property.ITS_LQI, ""));
            srcTu.setAnnotation(null);
        }
        lqiAnns.setData(rwRef);
        srcTu.getSource().setProperty(new Property(Property.ITS_LQI, ""));
        srcTu.getSource().setAnnotation(null);
        Set<LocaleId> targetLocales = srcTu.getTargetLocales();
        for (LocaleId tgt : targetLocales) {
            srcTu.getTarget(tgt).setProperty(new Property(Property.ITS_LQI, ""));
            srcTu.getTarget(tgt).setAnnotation(null);
        }

        Properties p = new Properties();
        File rwDir = new File(System.getProperty("user.home"), ".reviewersWorkbench");
        File provFile = new File(rwDir, "provenance.properties");
        if (provFile.exists()) {
            p.load(new FileInputStream(provFile));
        }

        ITSProvenanceAnnotations provAnns = new ITSProvenanceAnnotations();
        for (ITSProvenance prov : seg.getProv()) {
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
            seg.setAddedRWProvenance(true);
        }

        srcTu.setProperty(new Property(Property.ITS_PROV, " its:provenanceRecordsRef=\"#" + rwRef + "\""));
        provAnns.setData(rwRef);
        srcTu.setAnnotation(provAnns);
    }

    public class SegmentTextRenderer extends JTextArea implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean isSelected, boolean hasFocus, int row, int col) {
            String text = (String) o;
            setLineWrap(true);
            setWrapStyleWord(true);
            setText(text);
            setBackground(isSelected ? jtable.getSelectionBackground() : jtable.getBackground());
            setForeground(isSelected ? jtable.getSelectionForeground() : jtable.getForeground());
            setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : jtable.getBorder());

            return this;
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
}
