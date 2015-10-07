package com.vistatec.ocelot.plugins.exception;

/**
 * Exception raised when an unknown FREME service is requested.
 */
public class UnknownServiceException extends Exception {

	/** The serial version UID. */
	private static final long serialVersionUID = -2437668083201198756L;

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public UnknownServiceException(Throwable cause) {

		super("Unknown FREME service.", cause);
	}

}
