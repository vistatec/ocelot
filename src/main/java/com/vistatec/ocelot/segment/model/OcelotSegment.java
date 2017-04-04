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

import java.util.List;

import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.OtherITSMetadata;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.model.okapi.Notes;

/**
 * Expected data format of a segment within Ocelot; every data unit representing
 * a segment in Ocelot must at the minimum provide this functionality.
 */
public interface OcelotSegment {

    /**
     * Segment number is used in the ID column of the segment view display.
     * It should uniquely identify a segment in Ocelot in the currently open
     * document.
     * @return
     */
    public int getSegmentNumber();

    public SegmentVariant getSource();
    public SegmentVariant getTarget();
    public SegmentVariant getOriginalTarget();
    
    public String getTuId();
    public String getSegmentId();

    /**
     * Should only set the original target if one does not exist already,
     * which is determined by {@link #hasOriginalTarget}.
     * @param originalTarget
     */
    public void setOriginalTarget(SegmentVariant originalTarget);

    /**
     * Return whether an original target has been set or not.
     * @return
     */
    public boolean hasOriginalTarget();

    /**
     * Updates the target, which should also update the visual target difference
     * between target and original target as well as the edit distance measure.
     * @param updatedTarget
     * @return Whether the target update was successful
     */
    public boolean updateTarget(SegmentVariant updatedTarget);
    /**
     * Set the target back to the original target
     * @return Whether the reset target was successful
     */
    public boolean resetTarget();

    /**
     * Returns the visual difference between the target and original target.
     * See {@link com.vistatec.ocelot.segment.view.SegmentTextCell#setTextPane(java.util.List)}
     * and {@link javax.swing.text.StyledDocument} for target diff return format.
     * @return
     */
    public List<String> getTargetDiff();

    /**
     * Return the edit distance measure between the target and original target
     * text.
     * @return
     */
    public int getEditDistance();

    public Notes getNotes();
    
    public void setNotes(Notes notes);
    
    public List<LanguageQualityIssue> getLQI();
    public void addLQI(LanguageQualityIssue lqi);
    public void addAllLQI(List<LanguageQualityIssue> lqis);
    public void removeLQI(LanguageQualityIssue removeLQI);
    
    public List<Provenance> getProvenance();
    public void addProvenance(Provenance prov);
    public void addAllProvenance(List<Provenance> provs);

    public List<TextAnalysisMetaData> getTextAnalysis();
    public void addTextAnalysis(TextAnalysisMetaData ta);
    public void addAllTextAnalysis(List<TextAnalysisMetaData> tas);
    public void removeTextAnalysis(TextAnalysisMetaData ta);
    
    public List<TerminologyMetaData> getTerms();
    public void addTerm(TerminologyMetaData term);
    public void addAllTerms(List<TerminologyMetaData> terms);
    public void removeTerm(TerminologyMetaData term);
    
    /**
     * Return whether the segment has already gotten the Ocelot provenance
     * record for the current profile user. See
     * {@link com.vistatec.ocelot.config.ProvenanceConfig} for current profile
     * user.
     * @return
     */
    public boolean hasOcelotProvenance();
    /**
     * Indicate whether the Ocelot user profile provenance has been set for
     * this segment. Must be used in conjunction with {@link #addProvenance},
     * as this method does not actually add the provenance to the segment.
     * @param flag
     */
    public void setOcelotProvenance(boolean flag);

    public List<OtherITSMetadata> getOtherITSMetadata();
    public void addAllOtherITSMetadata(List<OtherITSMetadata> otherITS);
    public List<ITSMetadata> getITSMetadata();

    public boolean isEditable();
    
    public boolean isTranslatable();

    // TODO: Examine alternatives as this is XLIFF 1.2 specific
    public StateQualifier getStateQualifier();
}
