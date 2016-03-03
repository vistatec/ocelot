package com.vistatec.ocelot.xliff.freme.helper;

/**
 * Factory class for XLIFF Helper classes. Depending on the XLIFF version, this
 * class provides the proper helper object.
 */
public class FremeXliffHelperFactory {

	/** XLIFF 1.2 version constant. */
	private static final String XLIFF_1_2_VERSION = "1.2";

	/** XLIFF 2.0 version constant. */
	private static final String XLIFF_2_0_VERSION = "2.0";

	/**
	 * Creates the proper helper depending on the version.
	 * 
	 * @param version
	 *            the version
	 * @return the helper object
	 * @throws UnsupportedVersionException
	 *             exception raised if the requested version is not supported.
	 */
	public static FremeXliffHelper createHelper(final String version)
	        throws UnsupportedVersionException {

		FremeXliffHelper helper = null;
		if (version != null) {
			switch (version) {
			case XLIFF_1_2_VERSION:
				helper = new FremeXliff1_2Helper();
				break;
			case XLIFF_2_0_VERSION:
				helper = new FremeXliff2_0Helper();
				break;
			default:
				throw new UnsupportedVersionException(version);
			}
		}
		return helper;
	}

	/**
	 * Exception raised when a request is received for an unsupported XLIFF
	 * version.
	 */
	public static class UnsupportedVersionException extends Exception {

		/** The serial version UID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 * 
		 * @param unsupportedVersion
		 *            the unsupported version.
		 */
		public UnsupportedVersionException(String unsupportedVersion) {

			super(buildMessage(unsupportedVersion));
		}

		/**
		 * Builds the error message.
		 * 
		 * @param unsupportedVersion
		 *            the unsupported version
		 * @return the error message.
		 */
		private static String buildMessage(String unsupportedVersion) {

			return "Unsupported XLIFF version: " + unsupportedVersion + ".";
		}

	}
}
