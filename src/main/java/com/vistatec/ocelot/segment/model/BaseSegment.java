/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.OtherITSMetadata;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.segment.editdistance.EditDistance;
import com.vistatec.ocelot.segment.model.okapi.Notes;

/**
 * Provides generic functionality for manipulating Ocelot segments that is
 * sufficient for most cases. Does not implement how to determine if a segment
 * is editable.
 */
public abstract class BaseSegment implements OcelotSegment {
    protected final int segmentNumber;
    protected final SegmentVariant source;
    protected SegmentVariant target, originalTarget;
    protected boolean setOriginalTarget = false;
    
    protected boolean translatable;

    protected boolean dirtyEditDistance = true;
    protected int editDistance;
    protected Notes notes;

    protected boolean dirtyTargetDiff = true;
    protected List<String> targetDiff = new ArrayList<>();

    protected final List<LanguageQualityIssue> lqiList = new ArrayList<>();
    protected final List<Provenance> provList = new ArrayList<>();
    protected final List<TextAnalysisMetaData> taList = new ArrayList<TextAnalysisMetaData>();
    protected final List<TerminologyMetaData> termList = new ArrayList<TerminologyMetaData>();
    private boolean addedOcelotProvenance = false;
    protected final List<OtherITSMetadata> otherITSList = new ArrayList<>();

    public BaseSegment(int segmentNumber, SegmentVariant source,
            SegmentVariant target, SegmentVariant originalTarget, boolean translatable) {
        this.segmentNumber = segmentNumber;
        this.source = source;
        this.target = target;
        this.translatable = translatable;
        this.notes = new Notes();
        if (originalTarget != null) {
            setOriginalTarget(originalTarget);

        } else {
            this.originalTarget = source.createEmptyTarget();
        }
    }

    @Override
    public int getSegmentNumber() {
        return this.segmentNumber;
    }

    @Override
    public SegmentVariant getSource() {
        return this.source;
    }

    @Override
    public SegmentVariant getTarget() {
        return this.target;
    }

    @Override
    public SegmentVariant getOriginalTarget() {
        return this.originalTarget;
    }

    @Override
    public final void setOriginalTarget(SegmentVariant originalTarget) {
        if (!this.setOriginalTarget) {
            this.originalTarget = originalTarget;
        }
        this.setOriginalTarget = true;
    }

    @Override
    public boolean hasOriginalTarget() {
        return this.setOriginalTarget;
    }
    
    public void setNotes(Notes notes){
    	this.notes = notes;
    }
    
    public Notes getNotes(){
    	return notes;
    }

    /**
     * Update the segment target if there are differences, and make sure to
     * set the original target and refresh the edit distance and target diff.
     * @param updatedTarget
     * @return Whether the target needed to be updated or not.
     */
    @Override
    public boolean updateTarget(SegmentVariant updatedTarget) {
        if (!updatedTarget.getDisplayText().equals(target.getDisplayText())) {
            if (!hasOriginalTarget()) {
                setOriginalTarget(target);
            }
            target = updatedTarget;
            dirtyEditDistance = true;
            dirtyTargetDiff = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean resetTarget() {
        if (hasOriginalTarget()) {
            updateTarget(getOriginalTarget());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getTargetDiff() {
        if (hasOriginalTarget() && dirtyTargetDiff) {
            this.targetDiff = EditDistance.styleTextDifferences(getTarget(), getOriginalTarget());
            dirtyTargetDiff = false;
        }
        return this.targetDiff;
    }

    @Override
    public int getEditDistance() {
        if (dirtyEditDistance) {
            editDistance = hasOriginalTarget() ?
                EditDistance.calcEditDistance(getTarget(), getOriginalTarget()) : 0;
            dirtyEditDistance = false;
        }
        return editDistance;
    }

    @Override
    public List<LanguageQualityIssue> getLQI() {
        return lqiList;
    }

    @Override
    public void addLQI(LanguageQualityIssue lqi) {
        lqiList.add(lqi);
    }

    @Override
    public void addAllLQI(List<LanguageQualityIssue> lqis) {
        this.lqiList.addAll(lqis);
    }

    @Override
    public void removeLQI(LanguageQualityIssue removeLQI) {
        lqiList.remove(removeLQI);
    }


    @Override
    public List<Provenance> getProvenance() {
        return provList;
    }

    @Override
    public void addProvenance(Provenance prov) {
        provList.add(prov);
    }

    @Override
    public void addAllProvenance(List<Provenance> provs) {
        this.provList.addAll(provs);
    }

    @Override
    public boolean hasOcelotProvenance() {
        return addedOcelotProvenance;
    }

    @Override
    public void setOcelotProvenance(boolean flag) {
        addedOcelotProvenance = flag;
    }
    

    @Override
    public List<TextAnalysisMetaData> getTextAnalysis() {
	    return taList;
    }

	@Override
    public void addTextAnalysis(TextAnalysisMetaData ta) {
	    taList.add(ta);
	    
    }

	@Override
    public void addAllTextAnalysis(List<TextAnalysisMetaData> tas) {
		if(tas != null){
			taList.addAll(tas);
		}
    }
	
	@Override
	public void removeTextAnalysis(TextAnalysisMetaData ta) {
	
		if(taList != null && ta != null){
			taList.remove(ta);
		}
	}
	
	@Override
	public java.util.List<TerminologyMetaData> getTerms() {
		return termList;
	}
	
	@Override
	public void addTerm(TerminologyMetaData term) {
		termList.add(term);
	}
	
	@Override
	public void addAllTerms(List<TerminologyMetaData> terms) {
		
		if(terms != null){
			termList.addAll(terms);
		}
	}
	
	@Override
	public void removeTerm(TerminologyMetaData term) {
		
		if(termList != null && term != null){
			termList.remove(term);
		}
	}

	@Override
    public List<OtherITSMetadata> getOtherITSMetadata() {
        return this.otherITSList;
    }

    @Override
    public void addAllOtherITSMetadata(List<OtherITSMetadata> otherITS) {
        this.otherITSList.addAll(otherITS);
    }

    @Override
    public List<ITSMetadata> getITSMetadata() {
        List<ITSMetadata> its = new ArrayList<>();
        its.addAll(lqiList);
        its.addAll(provList);
        its.addAll(otherITSList);
        its.addAll(taList);
        its.addAll(termList);
        return its;
    }
    
    @Override
	public boolean isTranslatable() {
		return translatable;
	}

}
