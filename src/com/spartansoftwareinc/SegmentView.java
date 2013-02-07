package com.spartansoftwareinc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.filters.its.html5.HTML5Filter;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane {

    protected JTable sourceTargetTable;
    private SegmentTableModel segments;
    private ListSelectionModel tableSelectionModel;
    private SegmentAttributeView attrView;
    private TableColumnModel tableColumnModel;
    protected LinkedList<Integer[]> rowHeights = new LinkedList<Integer[]>();
    protected TableRowSorter sort;
    protected FilterRules filterRules;
    private int documentSegmentNum;
    protected int selectedRow = -1, selectedCol = -1;

    public SegmentView(SegmentAttributeView attr) throws IOException {
        attrView = attr;
        attrView.setSegmentView(this);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createLineBorder(Color.BLUE, 2));
        initializeTable();
        filterRules = new FilterRules();
    }

    public SegmentTableModel getSegments() {
        return segments;
    }

    public void initializeTable() {
        segments = new SegmentTableModel();
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
        setViewportView(sourceTargetTable);
    }

    public void parseSegmentsFromFile(File sourceFile, File targetFile) throws IOException {
        sourceTargetTable.clearSelection();
        segments.deleteSegments();
        sourceTargetTable.setRowSorter(null);
        attrView.treeView.clearTree();
        setViewportView(null);
        documentSegmentNum = 1;

        parseHTML5Files(new FileInputStream(sourceFile), new FileInputStream(targetFile));
        attrView.tableView.setDocument();
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

        while(srcFilter.hasNext() && tgtFilter.hasNext()) {
            Event srcEvent = srcFilter.next();
            Event tgtEvent = tgtFilter.next();

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
                }
                if (tgtITSTags != null) {
                    anns.addAll(tgtITSTags.getAnnotations(GenericAnnotationType.LQI));
                }

                addSegment(srcText, tgtText, anns);
            }
        }
        if (srcFilter.hasNext() || tgtFilter.hasNext()) {
            System.err.println("Documents not aligned?");
        }
    }
    
    public void addSegment(String sourceText, String targetText, List<GenericAnnotation> annotations) {
        Segment seg = new Segment(documentSegmentNum++, sourceText, targetText);
        // TODO: parse GenericAnnotations for other data categories.
        for (GenericAnnotation ga : annotations) {
            seg.addLQI(new LanguageQualityIssue(ga));
        }
        segments.addSegment(seg);
        initializeRowHeight(seg);
    }

    public void addFilters() {
        sort = new TableRowSorter(segments);
        sourceTargetTable.setRowSorter(sort);
        sort.setRowFilter(filterRules);
    }

    public void initializeRowHeight(Segment seg) {
        Integer[] rowHeight = new Integer[SegmentTableModel.NONFLAGCOLS + SegmentTableModel.NUMFLAGS];

        int srcIdx = segments.getColumnIndex(SegmentTableModel.COLSEGSRC);
        int tgtIdx = segments.getColumnIndex(SegmentTableModel.COLSEGTGT);

        JTextArea textMeasure = new JTextArea();
        textMeasure.setLineWrap(true);
        textMeasure.setWrapStyleWord(true);

        int srcColWidth = tableColumnModel.getColumn(srcIdx).getWidth();
        textMeasure.setText(seg.getSource());
        textMeasure.setSize(new Dimension(srcColWidth, 1));
        rowHeight[srcIdx] = textMeasure.getPreferredSize().height;

        int tgtColWidth = tableColumnModel.getColumn(tgtIdx).getWidth();
        textMeasure.setText(seg.getTarget());
        textMeasure.setSize(new Dimension(tgtColWidth, 1));
        rowHeight[tgtIdx] = textMeasure.getPreferredSize().height;

        rowHeights.add(rowHeight);
    }

    protected void updateRowHeights() {
        for (int row = 0; row < sourceTargetTable.getRowCount(); row++) {
            FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable.getFont());
            int rowHeight = font.getHeight();
            for (int col = 0; col < sourceTargetTable.getColumnCount(); col++) {
                if (rowHeights.get(row)[col] != null) {
                    rowHeight = Math.max(rowHeight, rowHeights.get(row)[col]);
                }
            }
            sourceTargetTable.setRowHeight(row, rowHeight);
        }
    }

    public void selectedSegment() {
        int colIndex = sourceTargetTable.getSelectedColumn();
        int rowIndex = sourceTargetTable.getSelectedRow();
        if (colIndex == selectedCol && rowIndex == selectedRow) {
            attrView.tableView.setDocument();
            sourceTargetTable.clearSelection();
        } else if (rowIndex >= 0) {
            int modelRowIndex = sort.convertRowIndexToModel(rowIndex);
            Segment seg = segments.getSegment(modelRowIndex);
            attrView.setSelectedSegment(seg);

            if (colIndex >= SegmentTableModel.NONFLAGCOLS) {
                int adjustedFlagIndex = colIndex - SegmentTableModel.NONFLAGCOLS;
                LanguageQualityIssue lqi = seg.getTopDataCategory(adjustedFlagIndex);
                if (lqi != null) {
                    attrView.setSelectedMetadata(lqi);
                }
            }
        }
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

            // Need to set width to force text area to calculate a pref height
            setSize(new Dimension(jtable.getColumnModel().getColumn(col).getWidth(), jtable.getRowHeight(row)));
            rowHeights.get(row)[col] = getPreferredSize().height;
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
            setBackground(flag.getFlagBackgroundColor());
            setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : flag.getFlagBorder());
            setText(flag.getFlagText());
            setHorizontalAlignment(CENTER);
            return this;
        }
    }
}
