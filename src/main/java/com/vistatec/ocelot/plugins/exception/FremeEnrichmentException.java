package com.vistatec.ocelot.plugins.exception;

/**
 * Exception raised when an error occurs while enriching text with FREME
 * services.
 */
public class FremeEnrichmentException extends Exception {

	/** The serial version UID. */
	private static final long serialVersionUID = 4405345546068794641L;

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public FremeEnrichmentException(Throwable cause) {
		super("Error while enriching a NIF content.", cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param textToEnrich
	 *            the text to enrich
	 * @param cause
	 *            the cause
	 */
	public FremeEnrichmentException(String textToEnrich, Throwable cause) {
		super(buildMessage(textToEnrich), cause);
	}

	/**
	 * Builds the error message.
	 * 
	 * @param textToEnrich
	 *            the text to enrich
	 * @return the error message.
	 */
	private static String buildMessage(final String textToEnrich) {

		return "Error while enriching following text: " + textToEnrich;

	}

}
