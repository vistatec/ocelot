/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;
import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Class that uses a {@link RuleConfiguration} to decide whether
 * segment entries should be displayed or not.
 */
public class SegmentSelector {
    private RuleConfiguration ruleConfig;
    
    private boolean showNotTransSegments;
    
    public SegmentSelector(RuleConfiguration ruleConfig, boolean showNotTransSegments) {
        this.ruleConfig = ruleConfig;
        this.showNotTransSegments = showNotTransSegments;
    }
    
//    public void setShowNotTranslatableSegments(boolean show){
//    	this.showNotTransSegments = show;
//    }
    
    public boolean matches(OcelotSegment s) {
        if (ruleConfig.getFilterMode() == FilterMode.ALL &&
            ruleConfig.getStateQualifierMode() == StateQualifierMode.ALL && showNotTransSegments) { 
            return true; 
        }
        
        if(!showNotTransSegments && !s.isTranslatable()){
        	return false;
        }

        if (ruleConfig.getStateQualifierMode() == StateQualifierMode.SELECTED_STATES &&
            (s.getStateQualifier() == null || 
             !ruleConfig.getStateQualifierEnabled(s.getStateQualifier()))) {
            return false;
        }
        switch (ruleConfig.getFilterMode()) {
        case ALL:
            return true;
        case ALL_WITH_METADATA:
            return s.getITSMetadata().size() > 0;
        case SELECTED_SEGMENTS:
            for (Rule r : ruleConfig.getRules()) {
                if (r.getEnabled() && r.matches(s)) {
                    return true;
                }
            }
            break;
        }
        return false;
    }
}
