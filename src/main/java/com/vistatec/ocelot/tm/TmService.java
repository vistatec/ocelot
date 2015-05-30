package com.vistatec.ocelot.tm;

import java.io.IOException;
import java.util.List;

import com.vistatec.ocelot.segment.model.SegmentAtom;

/**
 * Service for utilizing TMs.
 */
public interface TmService {
    public List<TmMatch> getFuzzyTermMatches(List<SegmentAtom> segment) throws IOException;

    public List<TmMatch> getConcordanceMatches(List<SegmentAtom> segment) throws IOException;
}
