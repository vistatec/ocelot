/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.its.stats.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.its.stats.model.ProvenanceStats.Type;

/**
 * Collect and merge ITS metadata statistics for the document.
 */
public class ITSDocStats {
    private List<ITSStats> stats = new ArrayList<ITSStats>();
    private Map<String, ITSStats> statsMap = new HashMap<String, ITSStats>();

    public List<ITSStats> getStats() {
        return stats;
    }

    public void clear() {
        stats.clear();
        statsMap.clear();
    }

    private void add(ITSStats stat) {
        stats.add(stat);
        statsMap.put(stat.getKey(), stat);
    }

    public void addProvenanceStats(Provenance prov) {
        calcProvenanceStats(Type.person, prov.getPerson());
        calcProvenanceStats(Type.org, prov.getOrg());
        calcProvenanceStats(Type.tool, prov.getTool());
        calcProvenanceStats(Type.revPerson, prov.getRevPerson());
        calcProvenanceStats(Type.revOrg, prov.getRevOrg());
        calcProvenanceStats(Type.revTool, prov.getRevTool());
    }
    

    private void calcProvenanceStats(ProvenanceStats.Type type, String value) {
        if (value != null) {
            updateStats(new ProvenanceStats(type, value));
        }
    }
    
    
    public void addTextAnalysisStats(TextAnalysisMetaData ta){
    	
    	calcTaStats(TextAnalysisStats.Type.annotatorsRef, ta.getTaAnnotatorsRef());
    	calcTaStats(TextAnalysisStats.Type.taClassRef, ta.getTaClassRef());
    	if(ta.getTaConfidence() != null){
    		calcTaStats(TextAnalysisStats.Type.taConfidence, String.valueOf(ta.getTaConfidence()));
    	}
    	calcTaStats(TextAnalysisStats.Type.taIdentRef, ta.getTaIdentRef());
    }
    
    private void calcTaStats(TextAnalysisStats.Type type, String value){
    	
    	if(value != null){
    		updateStats(new TextAnalysisStats(type, value));
    	}
    }
    
    public void addTerminologyStats(TerminologyMetaData term){
    	
    	calcTermStats(TerminologyStats.Type.term, term.getTerm());
    	calcTermStats(TerminologyStats.Type.annotatorsRef, term.getAnnotatorsRef());
    	calcTermStats(TerminologyStats.Type.domain, term.getSense());
    }
    
    private void calcTermStats(TerminologyStats.Type type, String value){
    	
    	if(value != null){
    		updateStats(new TerminologyStats(type, value));
    	}
    }

    public void updateStats(ITSStats stats) {
        ITSStats oldStats = statsMap.get(stats.getKey());
        if (oldStats != null) {
            oldStats.combine(stats);
        }
        else {
            add(stats);
        }
    }
}
