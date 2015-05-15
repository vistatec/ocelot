package com.vistatec.ocelot.segment.model;

public class TerminologyEnrichment extends Enrichment {
    
    public final static String ENRICHMENT_TYPE = "term"; 

    public TerminologyEnrichment(String nifOffsetString) {
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
