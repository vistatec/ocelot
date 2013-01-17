package com.spartansoftwareinc;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane {

    private JTable sourceTargetTable;
    private SegmentTableModel segments;
    private ListSelectionModel tableSelectionModel;
    private SegmentAttributeView attrView;
    private static final int NUMFLAGS = 5;
    private static final int NONFLAGCOLS = 3;

    public SegmentView(SegmentAttributeView attr) {
        attrView = attr;

        segments = new SegmentTableModel();
        sourceTargetTable = new JTable(segments);
        tableSelectionModel = sourceTargetTable.getSelectionModel();
        tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSelectionModel.addListSelectionListener(new SegmentSelectionHandler());
        sourceTargetTable.setDefaultRenderer(DataCategoryFlag.class,
                new DataCategoryFlagRenderer());

        TableColumnModel tableColumns = sourceTargetTable.getColumnModel();
        int minWidth = 15, prefWidth = 15, maxWidth = 20;
        for (int i = NONFLAGCOLS; i < NONFLAGCOLS + NUMFLAGS; i++) {
            tableColumns.getColumn(i).setMinWidth(minWidth);
            tableColumns.getColumn(i).setPreferredWidth(prefWidth);
            tableColumns.getColumn(i).setMaxWidth(maxWidth);
        }
        tableColumns.getColumn(0).setMinWidth(minWidth);
        tableColumns.getColumn(0).setPreferredWidth(prefWidth);
        tableColumns.getColumn(0).setMaxWidth(maxWidth);
        setViewportView(sourceTargetTable);
    }

    public void parseSegmentsFromFile() {
        // TODO: Actually parse the file and retrieve segments/metadata.
        int documentSegNum = 1;
        Segment num1 = new Segment(documentSegNum++, "heat sink", "dissipateur de chaleur");
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setType("terminology");
        lqi.setComment("testing");
        num1.addLQI(lqi);
        segments.addSegment(num1);

        Segment num2 = new Segment(documentSegNum++, "heat sink", "dissipation thermique");
        LanguageQualityIssue lqi2 = new LanguageQualityIssue();
        lqi2.setType("mistranslation");
        lqi2.setComment("testing");
        num2.addLQI(lqi2);
        segments.addSegment(num2);

        setViewportView(sourceTargetTable);
    }

    class SegmentSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex()) {
                attrView.setSelectedSegment(
                        segments.getSegment(lsm.getMinSelectionIndex()));
            } else {
                // TODO: Log non-single selection error
            }
        }
    }

    class SegmentTableModel extends AbstractTableModel {

        private String[] columns = {"#", "Source", "Target"};
        private LinkedList<Segment> segments = new LinkedList<Segment>();

        @Override
        public String getColumnName(int col) {
            return col < 3 ? columns[col] : "";
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) { return Integer.class; }
            if (columnIndex == 1 || columnIndex == 2) { return String.class; }
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
            if (col == 0) {
                return getSegment(row).getSegmentNumber();
            }
            if (col == 1) {
                return getSegment(row).getSource();
            }
            if (col == 2) {
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
