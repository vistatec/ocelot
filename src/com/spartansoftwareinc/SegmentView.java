package com.spartansoftwareinc;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.font.LineBreakMeasurer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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

    public SegmentView(SegmentAttributeView attr) {
        attrView = attr;

        segments = new SegmentTableModel();
        sourceTargetTable = new JTable(segments);
        sourceTargetTable.getTableHeader().setReorderingAllowed(false);

        tableColumnModel = sourceTargetTable.getColumnModel();

        tableSelectionModel = sourceTargetTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new SegmentSelectionHandler());

        DefaultTableCellRenderer segNumAlign = new DefaultTableCellRenderer();
        segNumAlign.setHorizontalAlignment(JLabel.LEFT);
        segNumAlign.setVerticalAlignment(JLabel.TOP);
        sourceTargetTable.setDefaultRenderer(Integer.class, segNumAlign);
        sourceTargetTable.setDefaultRenderer(DataCategoryFlag.class,
                new DataCategoryFlagRenderer());
        sourceTargetTable.setDefaultRenderer(String.class,
                new SegmentTextRenderer());

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
        setViewportView(sourceTargetTable);
    }

    public void parseSegmentsFromFile() throws IOException {
        sourceTargetTable.clearSelection();
        clearSegments();
        attrView.clearTree();
        // TODO: Actually parse the file and retrieve segments/metadata.
        InputStream sampleEnglishDocStream =
                SegmentView.class.getResourceAsStream("sample_english.txt");
        BufferedReader sampleEnglishDoc =
                new BufferedReader(new InputStreamReader(sampleEnglishDocStream, "UTF-8"));

        InputStream sampleFrenchDocStream =
                SegmentView.class.getResourceAsStream("sample_french.txt");
        BufferedReader sampleFrenchDoc =
                new BufferedReader(new InputStreamReader(sampleFrenchDocStream, "UTF-8"));

        int documentSegNum = 1;
        String nextEnglishLine, nextFrenchLine;
        while ((nextEnglishLine = sampleEnglishDoc.readLine()) != null
                && (nextFrenchLine = sampleFrenchDoc.readLine()) != null) {
            Segment seg = new Segment(documentSegNum++, nextEnglishLine, nextFrenchLine);
            for (int i = 0; i < 5; i++) {
                double addChance = Math.random();
                if (addChance < 0.6) {
                    seg.addLQI(generateRandomIssue());
                }
            }
            segments.addSegment(seg);
        }

        // Adjust the segment number column width
        tableColumnModel.getColumn(
                segments.getColumnIndex(SegmentTableModel.COLSEGNUM))
                .setPreferredWidth(this.getFontMetrics(this.getFont())
                .stringWidth(" " + documentSegNum));

        setViewportView(sourceTargetTable);
    }

    private void clearSegments() {
        segments.deleteSegments();
    }

    private LanguageQualityIssue generateRandomIssue() {
        String[] types = {"terminology", "mistranslation", "omission",
            "untranslated", "addition", "duplication", "inconsistency",
            "grammar", "legal", "register", "locale-specific-content",
            "locale-violation", "style", "characters", "misspelling",
            "typographical", "formatting", "inconsistent-entities", "numbers",
            "markup", "pattern-problem", "whitespace", "internationalization",
            "length", "uncategorized", "other"};
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setType(types[(int) Math.floor(Math.random() * 26)]);
        lqi.setComment("testing");
        lqi.setSeverity((int) Math.round(Math.random() * 100));
        return lqi;
    }

    class SegmentSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex() &&
                lsm.getMinSelectionIndex() >= 0) {
                attrView.setSelectedSegment(
                        segments.getSegment(lsm.getMinSelectionIndex()));
            } else {
                // TODO: Log non-single selection error
            }
        }
    }

    class SegmentTableModel extends AbstractTableModel {
        private LinkedList<Segment> segments = new LinkedList<Segment>();
        protected HashMap<String, Integer> colNameToIndex;
        protected HashMap<Integer, String> colIndexToName;
        private static final int NUMFLAGS = 5;
        private static final int NONFLAGCOLS = 3;
        private static final String COLSEGNUM = "#";
        private static final String COLSEGSRC = "source";
        private static final String COLSEGTGT = "target";

        public SegmentTableModel() {
            colNameToIndex = new HashMap<String, Integer>();
            colNameToIndex.put(COLSEGNUM, 0);
            colNameToIndex.put(COLSEGSRC, 1);
            colNameToIndex.put(COLSEGTGT, 2);
            colIndexToName = new HashMap<Integer, String>();
            for (String key : colNameToIndex.keySet()) {
                colIndexToName.put(colNameToIndex.get(key), key);
            }
        }

        @Override
        public String getColumnName(int col) {
            return col < NONFLAGCOLS ? colIndexToName.get(col) : "";
        }

        public int getColumnIndex(String col) {
            return colNameToIndex.get(col);
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == getColumnIndex(COLSEGNUM)) {
                return Integer.class;
            }
            if (columnIndex == getColumnIndex(COLSEGSRC) ||
                columnIndex == getColumnIndex(COLSEGTGT)) {
                return String.class;
            }
            return DataCategoryFlag.class;
        }

        @Override
        public int getRowCount() {
            return segments.size();
        }

        @Override
        public int getColumnCount() {
            return NONFLAGCOLS + NUMFLAGS;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == getColumnIndex(COLSEGNUM)) {
                return getSegment(row).getSegmentNumber();
            }
            if (col == getColumnIndex(COLSEGSRC)) {
                return getSegment(row).getSource();
            }
            if (col == getColumnIndex(COLSEGTGT)) {
                return getSegment(row).getTarget();
            }
            Object ret = segments.get(row).getTopDataCategory(col - NONFLAGCOLS);
            return ret != null ? ret : new NullDataCategoryFlag();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void addSegment(Segment seg) {
            segments.add(seg);
        }

        public Segment getSegment(int row) {
            return segments.get(row);
        }

        private void deleteSegments() {
            segments.clear();
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

            // Compute the largest row height between source and target cells.
            TableColumnModel colModel = jtable.getColumnModel();
            int myRowHeight = checkOptimalRowHeight(colModel.getColumn(col), text);
            int otherRowHeight;
            if (col == segments.getColumnIndex(SegmentTableModel.COLSEGSRC)) {
                String tgtText = (String)jtable.getModel().getValueAt(row,
                        segments.getColumnIndex(SegmentTableModel.COLSEGTGT));
                otherRowHeight = checkOptimalRowHeight(colModel.getColumn(
                        segments.getColumnIndex(SegmentTableModel.COLSEGTGT)),
                        tgtText);
            } else {
                String srcText = (String)jtable.getModel().getValueAt(row,
                        segments.getColumnIndex(SegmentTableModel.COLSEGSRC));
                otherRowHeight = checkOptimalRowHeight(colModel.getColumn(
                        segments.getColumnIndex(SegmentTableModel.COLSEGTGT)),
                        srcText);
            }

            jtable.setRowHeight(row, myRowHeight > otherRowHeight
                    ? myRowHeight : otherRowHeight);
            return this;
        }

        // All height and width integers are in pixels
        public int checkOptimalRowHeight(TableColumn col, String text) {
            FontMetrics font = this.getFontMetrics(this.getFont());
            int colWidth = col.getWidth();
            int lines = 0;

            AttributedString attrStr = new AttributedString(text);
            AttributedCharacterIterator charIter = attrStr.getIterator();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIter, font.getFontRenderContext());
            lineMeasurer.setPosition(charIter.getBeginIndex());
            while(lineMeasurer.getPosition() < charIter.getEndIndex()) {
                lineMeasurer.nextLayout(colWidth);
                lines++;
            }

            int charHeight = font.getHeight();

            return (lines+2)*charHeight;
        }
    }

    public class DataCategoryFlagRenderer extends JLabel implements TableCellRenderer {

        public DataCategoryFlagRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean bln, boolean bln1, int row, int col) {
            DataCategoryFlag flag = (DataCategoryFlag) obj;
            setBackground(flag.getFlagBackgroundColor());
            setBorder(flag.getFlagBorder());
            setText(flag.getFlagText());
            return this;
        }
    }

    public class NullDataCategoryFlag implements DataCategoryFlag {

        @Override
        public Color getFlagBackgroundColor() {
            return null;
        }

        @Override
        public Border getFlagBorder() {
            return null;
        }

        @Override
        public String getFlagText() {
            return "";
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
