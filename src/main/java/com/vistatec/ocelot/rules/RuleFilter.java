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
