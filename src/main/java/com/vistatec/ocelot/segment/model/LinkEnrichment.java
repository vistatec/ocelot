package com.vistatec.ocelot.segment.model;

public class LinkEnrichment extends Enrichment  {
    
    public final static String ENRICHMENT_TYPE = "link"; 

    private String url;
    
    private String value;
    
    public LinkEnrichment(String nifOffsetString, String url, String value) {
        super(nifOffsetString);
        this.url = url;
        this.value = value;
    }
    
    public LinkEnrichment(String nifOffsetString, String url) {
    	this(nifOffsetString, url, "");
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

    
    public String getUrl(){
    	return url;
    }
    
    public String getValue(){
    	return value;
    }
    
    @Override
    public String toString() {
    
    	return url + " " + value;
    }
}
