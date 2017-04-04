package com.vistatec.ocelot.lgk;

/**
 * Exception raised when an error occurs while using LingoTek services.
 */
public class LingoTekServiceException extends Exception {

	private static final long serialVersionUID = -1706861302998369714L;

	/** The error severity. */
	public static final int SEVERITY_ERROR = 1;

	/** The warning severity. */
	public static final int SEVERITY_WARNING = 2;

	/** The severity level for the error that generated the exception. */
	private int severity;

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the cause
	 * @param severity
	 *            the severity level
	 */
	public LingoTekServiceException(String message, Throwable cause,
			int severity) {
		super(message, cause);
		this.severity = severity;
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            the exception message
	 * @param severity
	 *            the severity level
	 */
	public LingoTekServiceException(String message, int severity) {
		super(message);
		this.severity = severity;
	}

	/**
	 * Gets the severity level for this exception.
	 * 
	 * @return the severity level.
	 */
	public int getSeverity() {
		return severity;
	}

}
