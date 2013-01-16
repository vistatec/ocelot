package com.spartansoftwareinc;

import java.util.LinkedList;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber;
    private String source, target;
    private LinkedList<LanguageQualityIssue> lqiList =
            new LinkedList<LanguageQualityIssue>();

    public Segment(int segNum, String source, String target) {
        this.segmentNumber = segNum;
        this.source = source;
        this.target = target;
    }
    
    public int getSegmentNumber() {
        return segmentNumber;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
    
    public boolean containsLQI() {
        return lqiList.size() > 0;
    }

    public LinkedList<LanguageQualityIssue> getLQI() {
        return lqiList;
    }

    public void addLQI(LanguageQualityIssue lqi) {
        lqiList.add(lqi);
    }

    public LanguageQualityIssue getTopDataCategory(int pos) {
        return pos >= lqiList.size() ? null : lqiList.get(pos);
    }
}
