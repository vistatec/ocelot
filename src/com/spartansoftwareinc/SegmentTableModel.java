package com.spartansoftwareinc;

import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

/**
 * Table model representing the aligned source/target segments and the flags
 * shown in the SegmentView.
 */
class SegmentTableModel extends AbstractTableModel {

    protected SegmentView segmentView;
    private LinkedList<Segment> segments = new LinkedList<Segment>();
    protected HashMap<String, Integer> colNameToIndex;
    protected HashMap<Integer, String> colIndexToName;
    protected HashMap<String, LQIStatistics> lqiStats;
    protected HashMap<String, ProvStatistics> provStats;
    public static final int NUMFLAGS = 5;
    public static final int NONFLAGCOLS = 3;
    public static final String COLSEGNUM = "#";
    public static final String COLSEGSRC = "source";
    public static final String COLSEGTGT = "target";

    public SegmentTableModel(SegmentView sv) {
        segmentView = sv;
        colNameToIndex = new HashMap<String, Integer>();
        colNameToIndex.put(COLSEGNUM, 0);
        colNameToIndex.put(COLSEGSRC, 1);
        colNameToIndex.put(COLSEGTGT, 2);
        colIndexToName = new HashMap<Integer, String>();
        for (String key : colNameToIndex.keySet()) {
            colIndexToName.put(colNameToIndex.get(key), key);
        }
        lqiStats = new HashMap<String,LQIStatistics>();
        provStats = new HashMap<String,ProvStatistics>();
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
        if (columnIndex == getColumnIndex(COLSEGSRC)
                || columnIndex == getColumnIndex(COLSEGTGT)) {
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
            return getSegment(row).getSource().getCodedText();
        }
        if (col == getColumnIndex(COLSEGTGT)) {
            return getSegment(row).getTarget().getCodedText();
        }
        Object ret = segmentView.ruleConfig.getTopDataCategory(
                segments.get(row), col-NONFLAGCOLS);
        return ret != null ? ret : new NullDataCategoryFlag();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == colNameToIndex.get(COLSEGTGT);
    }

    public void addSegment(Segment seg) {
        segments.add(seg);
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            LQIStatistics stats = lqiStats.get(lqi.getType());
            if (stats == null) {
                stats = new LQIStatistics();
                stats.setType(lqi.getType());
                stats.setRange(lqi.getSeverity());
                lqiStats.put(lqi.getType(), stats);
            } else {
                stats.setRange(lqi.getSeverity());
            }
        }
        for (ITSProvenance prov : seg.getProv()) {
            calcProvenanceStats("person", prov.getPerson());
            calcProvenanceStats("org", prov.getOrg());
            calcProvenanceStats("tool", prov.getTool());
            calcProvenanceStats("revPerson", prov.getRevPerson());
            calcProvenanceStats("revOrg", prov.getRevOrg());
            calcProvenanceStats("revTool", prov.getRevTool());
        }
    }

    private void calcProvenanceStats(String type, String value) {
        if (value != null) {
            ProvStatistics stats = provStats.get(type + ":" + value);
            if (stats == null) {
                stats = new ProvStatistics();
                stats.setType(type);
                stats.setValue(value);
                provStats.put(type + ":" + value, stats);
            } else {
                stats.setCount(stats.getCount() + 1);
            }
        }
    }

    public Segment getSegment(int row) {
        return segments.get(row);
    }

    protected void deleteSegments() {
        segments.clear();
        lqiStats = new HashMap<String,LQIStatistics>();
        provStats = new HashMap<String,ProvStatistics>();
    }
}
