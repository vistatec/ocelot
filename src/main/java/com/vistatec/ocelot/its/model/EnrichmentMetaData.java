package com.vistatec.ocelot.its.model;

/**
 * Meta data representing the enrichments from FREME.
 */
public abstract class EnrichmentMetaData extends ITSMetadata {

	/** Segment constant. */
	public static final String SEGMENT = "Segment";

	/** Source constant. */
	public static final String SOURCE = "Source";

	/** The target constant. */
	public static final String TARGET = "Target";

	/** The part of the segment containing this meta data. */
	private String segPart;

	/**
	 * Gets the part of the segment containing the meta data.
	 * 
	 * @return the part of the segment containing the meta data.
	 */
	public String getSegPart() {
		return segPart;
	}

	/**
	 * Sets the part of the segment containing the meta data.
	 * 
	 * @param segPart
	 *            the part of the segment containing the meta data.
	 */
	public void setSegPart(String segPart) {
		this.segPart = segPart;
	}

}
