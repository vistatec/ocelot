/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.OtherITSMetadata;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.editdistance.EditDistance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber, srcEventNum, tgtEventNum;
    private SegmentVariant source, target;
    private String phase_name;
    private StateQualifier state_qualifier;
    private boolean addedProvenance = false, setOriginalTarget = false;
    private String lqiID, provID;
    private List<LanguageQualityIssue> lqiList = new LinkedList<LanguageQualityIssue>();
    private List<Provenance> provList = new LinkedList<Provenance>();
    private List<OtherITSMetadata> otherITSList = new LinkedList<OtherITSMetadata>();
    private SegmentController segmentListener;
    private String fileOriginal, transUnitId;
    private SegmentVariant originalTarget;
    private ArrayList<String> targetDiff = new ArrayList<String>();

    public Segment() { }

    public Segment(int segNum, int srcEventNum, int tgtEventNum,
            SegmentVariant source, SegmentVariant target, SegmentVariant originalTarget) {
        this.segmentNumber = segNum;
        this.srcEventNum = srcEventNum;
        this.tgtEventNum = tgtEventNum;
        this.source = source;
        this.target = target;
        if (originalTarget != null) {
            this.originalTarget = originalTarget;
            setOriginalTarget = true;

            this.targetDiff = EditDistance.styleTextDifferences(target, originalTarget);
        } else {
            this.originalTarget = target.createEmpty();
        }
    }

    public void setSegmentListener(SegmentController segController) {
        this.segmentListener = segController;
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

    public SegmentVariant getSource() {
        return this.source;
    }

    public SegmentVariant getTarget() {
        return this.target;
    }

    public SegmentVariant getOriginalTarget() {
        return this.originalTarget;
    }

    public void setOriginalTarget(SegmentVariant oriTgt) {
        if (!this.setOriginalTarget) {
            // XXX Unclear when this branch is ever taken since the ctor always sets a value 
            this.originalTarget = oriTgt;
        }
        this.setOriginalTarget = true;
    }

    public boolean hasOriginalTarget() {
        return this.setOriginalTarget;
    }

    public void resetTarget() {
        getTarget().setContent(getOriginalTarget());
        if (segmentListener != null) {
            segmentListener.fireTableDataChanged();
        }
    }

    public ArrayList<String> getTargetDiff() {
        return this.targetDiff;
    }

    public void setTargetDiff(ArrayList<String> targetDiff) {
        this.targetDiff = targetDiff;
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

    public StateQualifier getStateQualifier() {
        return this.state_qualifier;
    }

    public void setStateQualifier(StateQualifier state) {
        this.state_qualifier = state;
    }

    public String getProvID() {
        return this.provID;
    }

    public void setProvID(String provId) {
        this.provID = provId;
    }

    public List<Provenance> getProv() {
        return provList;
    }

    public void setProv(List<Provenance> provList) {
        this.provList = provList;
    }

    public void addProvenance(Provenance prov) {
        provList.add(prov);
        if (segmentListener != null) {
            segmentListener.notifyAddedProv(prov);
        }
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

    public List<LanguageQualityIssue> getLQI() {
        return lqiList;
    }

    public void setLQI(List<LanguageQualityIssue> lqi) {
        this.lqiList = lqi;
    }

    public void addLQI(LanguageQualityIssue lqi) {
        lqiList.add(lqi);
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

    public List<OtherITSMetadata> getOtherITSMetadata() {
        return this.otherITSList;
    }

    public void setOtherITSMetadata(List<OtherITSMetadata> otherList) {
        this.otherITSList = otherList;
    }

    public List<ITSMetadata> getAllITSMetadata() {
        List<ITSMetadata> its = new ArrayList<ITSMetadata>();
        its.addAll(lqiList);
        its.addAll(provList);
        its.addAll(otherITSList);
        return its;
    }
}
