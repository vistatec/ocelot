package com.vistatec.ocelot.tm;

import java.io.IOException;
import java.util.List;

import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Service for utilizing TMs.
 */
public interface TmService {
    public List<TmMatch> getFuzzyTermMatches(OcelotSegment segment) throws IOException;

    public List<TmMatch> getConcordanceMatches(OcelotSegment segment) throws IOException;
}
