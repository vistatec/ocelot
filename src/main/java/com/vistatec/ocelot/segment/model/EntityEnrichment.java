package com.vistatec.ocelot.segment.model;

public class EntityEnrichment extends Enrichment {

    private final static String ITS_TAG = "its:taIdentRef";

    private final static String ITS_TAG_TYPE = "its:any";
    
    public final static String ENRICHMENT_TYPE = "entity"; 
    
    private String entityURL;
    
    
    public EntityEnrichment(String nifOffsetString, String entityURL) {
        super(nifOffsetString);
        this.entityURL = entityURL;
//        icon = 
    }
    
    public String getEntityURL(){
        return entityURL;
    }

    @Override
    protected String getTagType() {
        return ITS_TAG_TYPE;
    }

    @Override
    protected String getTag() {
        return ITS_TAG + "=\"" + entityURL + "\"";
    }

    @Override
    public String toString() {
     
        return entityURL;
    }

    @Override
    public String getType() {
        return ENRICHMENT_TYPE;
    }
    
    
}
