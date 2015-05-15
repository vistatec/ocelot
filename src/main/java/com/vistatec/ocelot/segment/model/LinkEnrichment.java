package com.vistatec.ocelot.segment.model;

public class LinkEnrichment extends Enrichment  {
    
    public final static String ENRICHMENT_TYPE = "link"; 

    public LinkEnrichment(String nifOffsetString) {
        super(nifOffsetString);
    }

    @Override
    protected String getTagType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getType() {
        
        return ENRICHMENT_TYPE;
    }

}
