package com.vistatec.ocelot.plugins;

import java.io.InputStream;
import java.util.List;

import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.plugins.exception.UnknownServiceException;
import com.vistatec.ocelot.segment.model.Enrichment;

public interface FremePlugin extends Plugin {
    
    public int EENTITY_SERVICE = 0;
    
    public int ELINK_SERVICE = 1;
//    
//    int ETERMINOLOGY = 2;
//    
//    int ETRANSLATION = 3;
    
    void configureServiceChain(int[] services);
    
    void turnOnService(final int serviceType) throws UnknownServiceException;
    
    void turnOffService(final int serviceType) throws UnknownServiceException;
    
    List<Enrichment> enrichContent(final String plainText) throws FremeEnrichmentException;
    
    List<Enrichment> enrichContent(final InputStream inputContent) throws FremeEnrichmentException;
    
}
