package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.segment.view.SegmentTextCell;

public class EnrichedAtom extends TextAtom  {

	private List<Enrichment> enrichments;
	
	private int startIndex;

    private int endIndex;
	
	public EnrichedAtom(final String text, final List<Enrichment> enrichments) {
		
		super(text);
		this.enrichments = enrichments;
	}
	
	public EnrichedAtom(final String text) {
		super(text);
	}
	
	public void addEnrichment(final Enrichment enrichment){
		if (enrichments == null) {
            enrichments = new ArrayList<Enrichment>();
        }
        enrichments.add(enrichment);
        if (startIndex == -1 || startIndex > enrichment.getOffsetStartIdx()) {
            startIndex = enrichment.getOffsetStartIdx();
        }

        if (endIndex == -1 || endIndex < enrichment.getOffsetEndIdx()) {
            endIndex = enrichment.getOffsetEndIdx();
        }
	}
	
	@Override
	public String getTextStyle() {
		return SegmentTextCell.enrichedStyle;
	}

	public List<Enrichment> getEnrichments(){
		return enrichments;
	}

	 public boolean containsOffset(final int offestStartIdx) {

	        return offestStartIdx >= startIndex && offestStartIdx <= endIndex;
	    }
}
