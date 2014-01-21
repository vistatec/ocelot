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
package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.segment.Segment;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuleFilter {

	List<RuleMatcher> matchers;
        private boolean enabled = false;

        public RuleFilter(List<RuleMatcher> matchers) {
            this.matchers = matchers;
        }

        public RuleFilter() {
            this.matchers = new LinkedList<RuleMatcher>();
        }

        public void addRuleMatcher(RuleMatcher matcher) {
            this.matchers.add(matcher);
        }

        public boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(boolean flag) {
            enabled = flag;
        }

        public LinkedList<ITSMetadata> displayMatches(Segment segment) {
            LinkedList<ITSMetadata> itsMatches = new LinkedList<ITSMetadata>();
            for (ITSMetadata its : segment.getAllITSMetadata()) {
                if (matches(its)) {
                    itsMatches.add(its);
                }
            }
            return itsMatches;
        }

	/**
	 * The filter matches a segment if all of its rules
	 * match some piece of ITS metadata in that segment.
	 */
	public boolean matches(Segment segment) {
		// I need to check each piece of metadata.
		// - If all the rules match that piece, success!
		// - If not all the rules match that piece, continue.
		// If I run out of metadata without success, fail.
		for (ITSMetadata its : segment.getAllITSMetadata()) {
			if (matches(its)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * The filter matches a piece of ITS metadata if all of its rules
	 * match that piece of metadata.
	 */
	boolean matches(ITSMetadata its) {
            for (RuleMatcher matcher : matchers) {
                if (!matches(matcher, its)) {
                    return false;
                }
            }

	    return true;
	}

	/**
	 * A matcher matches a piece of ITS metadata if it matches any 
	 * field in that piece of metadata.
	 */
	boolean matches(RuleMatcher matcher, ITSMetadata its) {
	    Map<DataCategoryField, Object> values = its.getFieldValues();
        for (Map.Entry<DataCategoryField, Object> e : values.entrySet()) {
            if (matcher.getField().equals(e.getKey())) {
                if (matcher.matches(e.getValue())) {
                	return true;
                }
            }
        }
        return false;
	}

}
