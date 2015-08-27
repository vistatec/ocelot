package com.vistatec.ocelot.plugins;

import java.awt.Window;
import java.util.List;

import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.plugins.exception.UnknownServiceException;
import com.vistatec.ocelot.segment.model.Enrichment;

public interface FremePlugin extends Plugin {
    
    public int EENTITY_SERVICE = 0;
    
    public int ELINK_SERVICE = 1;
//    
    public int ETERMINOLOGY = 2;
    
    public int ETRANSLATION = 3;
    
    void configureServiceChain(Window ocelotMainFrame);
    
    void turnOnService(final int serviceType) throws UnknownServiceException;
    
    void turnOffService(final int serviceType) throws UnknownServiceException;
    
    List<Enrichment> enrichSourceContent(final String plainText) throws FremeEnrichmentException;
    
    List<Enrichment> enrichTargetContent(final String plainText) throws FremeEnrichmentException;
//    
//    List<Enrichment> enrichContent(final InputStream inputContent) throws FremeEnrichmentException;
    
//    public void enrichContent(final OcelotSegment segment)
//	        throws FremeEnrichmentException;
    
    public void setSourceAndTargetLanguages(String sourceLanguage, String targetLanguage); 

}
