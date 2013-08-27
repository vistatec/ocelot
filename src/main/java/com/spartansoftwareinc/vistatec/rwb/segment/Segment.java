package com.spartansoftwareinc.vistatec.rwb.segment;

import com.spartansoftwareinc.vistatec.rwb.its.ITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber, srcEventNum, tgtEventNum;
    private TextContainer source, target;
    private boolean addedProvenance = false;
    private String lqiID, provID;
    private LinkedList<LanguageQualityIssue> lqiList =
            new LinkedList<LanguageQualityIssue>();
    private LinkedList<Provenance> provList =
            new LinkedList<Provenance>();
    private SegmentController segmentListener;
    private String fileOriginal, transUnitId;
    private TextContainer originalTarget;

    public Segment(int segNum, int srcEventNum, int tgtEventNum,
            TextContainer source, TextContainer target, SegmentController listener) {
        this.segmentNumber = segNum;
        this.srcEventNum = srcEventNum;
        this.tgtEventNum = tgtEventNum;
        this.source = source;
        this.target = target;
        if (target != null) {
            AltTranslationsAnnotation altTrans = target.getAnnotation(AltTranslationsAnnotation.class);
            if (altTrans != null) {
                Iterator<AltTranslation> iterAltTrans = altTrans.iterator();
                while (iterAltTrans.hasNext()) {
                    AltTranslation altTran = iterAltTrans.next();
                    // Check if alt-trans is RWB generated.
                    if (altTran.getOrigin() != null && altTran.getOrigin().equals("Reviewer's Workbench")) {
                        originalTarget = altTran.getTarget();
                    }
                }
            }
            if (originalTarget == null) {
                AltTranslation rwbAltTrans = new AltTranslation(LocaleId.fromString(listener.getFileSourceLang()),
                        LocaleId.fromString(listener.getFileTargetLang()), null,
                        source.getUnSegmentedContentCopy(), target.getUnSegmentedContentCopy(),
                        MatchType.UKNOWN, 100, "Reviewer's Workbench");
                altTrans = altTrans == null ? new AltTranslationsAnnotation() : altTrans;
                altTrans.add(rwbAltTrans);
                target.setAnnotation(altTrans);
                originalTarget = rwbAltTrans.getTarget();
            }
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

    public List<ITSMetadata> getAllITSMetadata() {
    	List<ITSMetadata> its = new ArrayList<ITSMetadata>();
    	its.addAll(lqiList);
        its.addAll(provList);
    	return its;
    }
}
