package com.vistatec.ocelot.profile;


/**
 * Exception raised when an error occurs while managing profiles.
 */
public class ProfileException extends Exception {

	/**
	 * 
	 */
    private static final long serialVersionUID = -4026647559526874454L;

	public ProfileException(String message, Throwable cause) {
	    super(message, cause);
    }

	public ProfileException(Throwable cause) {
	    super(cause);
    }
	
	public ProfileException(String message) {
	    super(message);
    }

    
}
