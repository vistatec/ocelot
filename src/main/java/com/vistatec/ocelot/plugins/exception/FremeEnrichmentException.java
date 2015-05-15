package com.vistatec.ocelot.plugins.exception;

public class FremeEnrichmentException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4405345546068794641L;

    
    
    public FremeEnrichmentException(Throwable cause) {
        super("Error while enriching a NIF content.", cause);
    }
    
    
    public FremeEnrichmentException(String textToEnrich, Throwable cause) {
        super(buildMessage(textToEnrich), cause);
    }
    
    
    private static String buildMessage(final String textToEnrich){
        
        return "Error while enriching following text: " + textToEnrich;
        
    }
    
    

    
}
