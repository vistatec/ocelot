package com.spartansoftwareinc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber, srcEventNum, tgtEventNum;
    private String source, target;
    private boolean addedProvenance = false;
    private LinkedList<LanguageQualityIssue> lqiList =
            new LinkedList<LanguageQualityIssue>();
    private LinkedList<ITSProvenance> provList =
            new LinkedList<ITSProvenance>();

    public Segment(int segNum, int srcEventNum, int tgtEventNum, String source, String target) {
        this.segmentNumber = segNum;
        this.srcEventNum = srcEventNum;
        this.tgtEventNum = tgtEventNum;
        this.source = source;
        this.target = target;
    }
    
    public int getSegmentNumber() {
        return segmentNumber;
    }

    public int getSourceEventNumber() {
        return srcEventNum;
    }

    public int getTargetEventNumber() {
        return tgtEventNum;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public LinkedList<ITSProvenance> getProv() {
        return provList;
    }

    public void addProvenance(ITSProvenance prov) {
        provList.add(prov);
    }

    public boolean addedRWProvenance() {
        return addedProvenance;
    }

    public void setAddedRWProvenance(boolean flag) {
        addedProvenance = flag;
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

    public List<ITSMetadata> getAllITSMetadata() {
    	List<ITSMetadata> its = new ArrayList<ITSMetadata>();
    	its.addAll(lqiList);
        its.addAll(provList);
    	return its;
    }
}
