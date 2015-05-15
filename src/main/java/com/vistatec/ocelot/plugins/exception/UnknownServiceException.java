package com.vistatec.ocelot.plugins.exception;

public class UnknownServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2437668083201198756L;

    public UnknownServiceException(Throwable cause) {
        
        super("Unknown FREME service.", cause );
    }
    
}
