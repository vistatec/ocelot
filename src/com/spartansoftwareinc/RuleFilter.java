package com.spartansoftwareinc;

import java.util.List;
import java.util.Map;

public class RuleFilter implements Filter {

	List<RuleMatcher> matchers;
	
	public RuleFilter(List<RuleMatcher> matchers) {
		this.matchers = matchers;
	}

	/**
	 * The filter matches a segment if all of its rules
	 * match some piece of ITS metadata in that segment.
	 */
	@Override
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
