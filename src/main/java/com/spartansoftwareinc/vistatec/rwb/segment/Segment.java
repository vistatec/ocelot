package com.spartansoftwareinc.vistatec.rwb.segment;

import com.spartansoftwareinc.vistatec.rwb.its.ITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.OtherITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber, srcEventNum, tgtEventNum;
    private TextContainer source, target;
    private String phase_name, state_qualifier;
    private boolean addedProvenance = false, setOriginalTarget = false;
    private String lqiID, provID;
    private LinkedList<LanguageQualityIssue> lqiList = new LinkedList<LanguageQualityIssue>();
    private LinkedList<Provenance> provList = new LinkedList<Provenance>();
    private LinkedList<OtherITSMetadata> otherITSList = new LinkedList<OtherITSMetadata>();
    private SegmentController segmentListener;
    private String fileOriginal, transUnitId;
    private TextContainer originalTarget;

    public Segment(int segNum, int srcEventNum, int tgtEventNum,
            TextContainer source, TextContainer target, TextContainer originalTarget,
            SegmentController listener) {
        this.segmentNumber = segNum;
        this.srcEventNum = srcEventNum;
        this.tgtEventNum = tgtEventNum;
        this.source = source;
        this.target = target;
        if (originalTarget != null) {
            this.originalTarget = originalTarget;
            setOriginalTarget = true;
        } else {
            this.originalTarget = new TextContainer();
        }
        this.segmentListener = listener;
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

    public TextContainer getSource() {
        return this.source;
    }

    public TextContainer getTarget() {
        return this.target;
    }

    public TextContainer getOriginalTarget() {
        return this.originalTarget;
    }

    public void setOriginalTarget(TextContainer oriTgt) {
        if (!this.setOriginalTarget) {
            this.originalTarget = oriTgt;
        }
        this.setOriginalTarget = true;
    }

    public boolean hasOriginalTarget() {
        return this.setOriginalTarget;
    }

    public void resetTarget() {
        getTarget().setContent(getOriginalTarget().getUnSegmentedContentCopy());
        segmentListener.fireTableDataChanged();
    }

    /**
     * XLIFF specific fields.
     */
    public String getFileOriginal() {
        return this.fileOriginal;
    }

    public void setFileOriginal(String fileOri) {
        this.fileOriginal = fileOri;
    }

    public String getTransUnitId() {
        return this.transUnitId;
    }

    public void setTransUnitId(String transUnitId) {
        this.transUnitId = transUnitId;
    }

    public boolean isEditablePhase() {
        return !"Rebuttal".equalsIgnoreCase(getPhaseName()) &&
                !"Translator approval".equalsIgnoreCase(getPhaseName());
    }

    public String getPhaseName() {
        return this.phase_name;
    }

    public void setPhaseName(String phaseName) {
        this.phase_name = phaseName;
    }

    public String getStateQualifier() {
        return this.state_qualifier;
    }

    public void setStateQualifier(String state) {
        this.state_qualifier = state;
    }

    public String getProvID() {
        return this.provID;
    }

    public void setProvID(String provId) {
        this.provID = provId;
    }

    public LinkedList<Provenance> getProv() {
        return provList;
    }

    public void addProvenance(Provenance prov) {
        provList.add(prov);
        segmentListener.notifyAddedProv(prov);
    }

    public boolean addedRWProvenance() {
        return addedProvenance;
    }

    public void setAddedRWProvenance(boolean flag) {
        addedProvenance = flag;
    }

    public String getLQIID() {
        return this.lqiID;
    }

    public void setLQIID(String id) {
        this.lqiID = id;
    }

    public boolean containsLQI() {
        return lqiList.size() > 0;
    }

    public LinkedList<LanguageQualityIssue> getLQI() {
        return lqiList;
    }

    public void addLQI(LanguageQualityIssue lqi) {
        lqiList.add(lqi);
        if (segmentListener != null) {
        	segmentListener.notifyAddedLQI(lqi, this);
        }
    }

    public void addNewLQI(LanguageQualityIssue lqi) {
        addLQI(lqi);
        if (segmentListener != null) {
            segmentListener.notifyModifiedLQI(lqi, this);
        }
    }

    public void editedLQI(LanguageQualityIssue lqi) {
        if (segmentListener != null) {
            segmentListener.notifyModifiedLQI(lqi, this);
        }
    }

    public void removeLQI(LanguageQualityIssue removeLQI) {
        lqiList.remove(removeLQI);
        if (segmentListener != null) {
            segmentListener.notifyModifiedLQI(removeLQI, this);
        }
    }

    public void addOtherITSMetadata(OtherITSMetadata mtConfidence) {
        this.otherITSList.add(mtConfidence);
    }

    public List<OtherITSMetadata> getOtherITSMetadata() {
        return this.otherITSList;
    }

    public List<ITSMetadata> getAllITSMetadata() {
        List<ITSMetadata> its = new ArrayList<ITSMetadata>();
        its.addAll(lqiList);
        its.addAll(provList);
        its.addAll(otherITSList);
        return its;
    }
}
