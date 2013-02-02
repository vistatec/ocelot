package com.spartansoftwareinc;

import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

/**
 * Table model representing the aligned source/target segments and the flags
 * shown in the SegmentView.
 */
class SegmentTableModel extends AbstractTableModel {

    private LinkedList<Segment> segments = new LinkedList<Segment>();
    protected HashMap<String, Integer> colNameToIndex;
    protected HashMap<Integer, String> colIndexToName;
    protected HashMap<String, LQIStatistics> lqiStats;
    public static final int NUMFLAGS = 5;
    public static final int NONFLAGCOLS = 3;
    public static final String COLSEGNUM = "#";
    public static final String COLSEGSRC = "source";
    public static final String COLSEGTGT = "target";

    public SegmentTableModel() {
        colNameToIndex = new HashMap<String, Integer>();
        colNameToIndex.put(COLSEGNUM, 0);
        colNameToIndex.put(COLSEGSRC, 1);
        colNameToIndex.put(COLSEGTGT, 2);
        colIndexToName = new HashMap<Integer, String>();
        for (String key : colNameToIndex.keySet()) {
            colIndexToName.put(colNameToIndex.get(key), key);
        }
        lqiStats = new HashMap<String,LQIStatistics>();
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
        for (LanguageQualityIssue lqi : seg.getLQI()) {
            LQIStatistics stats = lqiStats.get(lqi.getType());
            if (stats == null) {
                stats = new LQIStatistics();
                stats.setRange(lqi.getSeverity());
                lqiStats.put(lqi.getType(), stats);
            } else {
                stats.setRange(lqi.getSeverity());
            }
        }
    }

    public Segment getSegment(int row) {
        return segments.get(row);
    }

    protected void deleteSegments() {
        segments.clear();
    }

    public class LQIStatistics implements ITSStats{
        private String dataCategory, type, value;

        @Override
        public String getDataCategory() {
            return dataCategory;
        }

        public void setCategory(String category) {
            this.dataCategory = category;
        }

        @Override
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        private Integer minRange, maxRange, count = 0;

        @Override
        public String getValue() {
            if (minRange != null && maxRange != null) {
                return minRange+"-"+maxRange;
            } else if (value != null) {
                return value;
            }
            return null;
        }

        @Override
        public Integer getCount() {
            return count;
        }

        public void setRange(int range) {
            if (minRange == null || minRange > range) {
                minRange = range;
            }
            if (maxRange == null || maxRange < range) {
                maxRange = range;
            }
            count++;
        }

        public void setValue(String val) {
            this.value = val;
            count++;
        }
    }
}
