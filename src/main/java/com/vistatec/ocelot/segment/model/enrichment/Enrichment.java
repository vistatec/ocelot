package com.vistatec.ocelot.segment.model.enrichment;

/**
 * This class represent a generic enrichemnt retrieved by FREME services. All
 * specific enrichments must extends this class.
 */
public abstract class Enrichment {

	/** The Entity enrichment type. */
	public static final String ENTITY_TYPE = "entity";

	/** The Terminology enrichment type. */
	public static final String TERMINOLOGY_TYPE = "terminology";

	/** The Link enrichment type. */
	public static final String LINK_TYPE = "link";

	/** The Translation enrichment type. */
	public static final String TRANSLATION_TYPE = "translation";

	/** The suffix of the NIF offset string. */
	private static final String NIF_OFFSET_STRING = "char=";

	/** The enrichment type. */
	private String type;

	/** The offset start index as it is in the NIF file. */
	protected int offsetStartIdx;

	/** The offset end index as it is in the NIF file. */
	protected int offsetEndIdx;

	/**
	 * The offset start index related to the text obtained by discarding tags.
	 * It has the same value as <code>offsetStartIdx</code> if the text does not
	 * contain any tags.
	 */
	protected int offsetNoTagsStartIdx;
	/**
	 * The offset start index related to the text obtained by discarding tags.
	 * It has the same value as <code>offsetStartIdx</code> if the text does not
	 * contain any tags.
	 */
	protected int offsetNoTagsEndIdx;
	
	/** the marker id. */
	protected String id;

	/** A boolean stating if this enrichment is disabled. */
	protected boolean disabled;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            the type.
	 */
	public Enrichment(String type) {
		this.type = type;
	}

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            the type
	 * @param nifOffsetString
	 *            the NIF offset string.
	 */
	public Enrichment(String type, final String nifOffsetString) {

		this.type = type;
		retrieveOffset(nifOffsetString);
	}

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            the type
	 * @param offsetStartIdx
	 *            the offset start index.
	 * @param offsetEndIdx
	 *            the offset end index.
	 */
	public Enrichment(String type, final int offsetStartIdx,
	        final int offsetEndIdx) {

		this.type = type;
		if (offsetEndIdx < offsetStartIdx) {
			throw new IllegalArgumentException(
			        "The offsetStartIdx parameter value has to be less then offsetEndIdx value. Actual values: offsetStartIdx = "
			                + offsetStartIdx
			                + " - offsetEndIdx = "
			                + offsetEndIdx);
		}
		this.offsetEndIdx = offsetEndIdx;
		this.offsetStartIdx = offsetStartIdx;
	}

	/**
	 * Retrieves the offset indices from the NIF offset string.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 */
	private void retrieveOffset(final String nifOffsetString) {

		if (nifOffsetString == null || nifOffsetString.isEmpty()
		        || !nifOffsetString.contains(NIF_OFFSET_STRING)) {
			throw new IllegalArgumentException("Invalid NIF string: "
			        + nifOffsetString + ". A valid NIF string contains \""
			        + NIF_OFFSET_STRING + "<startIdx>,<endIdx>\"");
		}
		int cutIndex = nifOffsetString.lastIndexOf(NIF_OFFSET_STRING);
		String offsetString = nifOffsetString.substring(cutIndex);
		cutIndex = offsetString.indexOf(",");
		try {
			offsetStartIdx = Integer.valueOf(offsetString.substring(
			        NIF_OFFSET_STRING.length(), cutIndex));
			offsetEndIdx = Integer
			        .valueOf(offsetString.substring(cutIndex + 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
			        "Invalid NIF string: "
			                + nifOffsetString
			                + ". A valid NIF string contains \""
			                + NIF_OFFSET_STRING
			                + "<startIdx>,<endIdx>\" where <startIdx> and <endIdx> are integer numbers.");
		}

	}

	/**
	 * Gets the offset start index.
	 * 
	 * @return the offset start index.
	 */
	public int getOffsetStartIdx() {
		return offsetStartIdx;
	}

	/**
	 * Sets the offset start index.
	 * 
	 * @param offsetStartIdx
	 *            the offset start index.
	 */
	public void setOffsetStartIdx(int offsetStartIdx) {
		this.offsetStartIdx = offsetStartIdx;
	}

	/**
	 * Gets the offset end index.
	 * 
	 * @return the offset end index.
	 */
	public int getOffsetEndIdx() {
		return offsetEndIdx;
	}

	/**
	 * Sets the offset end index.
	 * 
	 * @param offsetEndIdx
	 *            the offset end index.
	 */
	public void setOffsetEndIdx(int offsetEndIdx) {
		this.offsetEndIdx = offsetEndIdx;
	}
	
	/**
	 * Gets the start index of the offset related to the string deprived of
	 * tags.
	 * 
	 * @return the no tags offset start index.
	 */
	public int getOffsetNoTagsStartIdx() {
		return offsetNoTagsStartIdx;
	}

	/**
	 * Sets the start index of the offset related to the string deprived of
	 * tags.
	 * 
	 * @param offsetNoTagsStartIdx
	 *            the no tags offset start index.
	 */
	public void setOffsetNoTagsStartIdx(int offsetNoTagsStartIdx) {
		this.offsetNoTagsStartIdx = offsetNoTagsStartIdx;
	}

	/**
	 * Gets the end index of the offset related to the string deprived of tags.
	 * 
	 * @return the no tags offset end index.
	 */
	public int getOffsetNoTagsEndIdx() {
		return offsetNoTagsEndIdx;
	}

	/**
	 * Sets the end index of the offset related to the string deprived of tags.
	 * 
	 * @param offsetNoTagsEndIdx
	 *            the no tags offset end index.
	 */
	public void setOffsetNoTagsEndIdx(int offsetNoTagsEndIdx) {
		this.offsetNoTagsEndIdx = offsetNoTagsEndIdx;
	}

	


	/**
	 * Gets the enriched text
	 * 
	 * @param text
	 *            the plain text
	 * @return the enriched text
	 */
	public String getEnrichedText(final String text) {

		StringBuffer enrichedText = new StringBuffer();
		if (text != null && offsetStartIdx < text.length()
		        && offsetEndIdx < text.length()) {

			enrichedText.append(text.substring(0, offsetStartIdx));
			enrichedText.append("<mrk id=\"");
			enrichedText.append(id);
			enrichedText.append("\" type=\"");
			enrichedText.append(getTagType());
			enrichedText.append("\" ");
			enrichedText.append(getTag());
			enrichedText.append(">");
			enrichedText.append(text.substring(offsetStartIdx, offsetEndIdx));
			enrichedText.append("</mrk>");
			enrichedText.append(text.substring(offsetEndIdx));
		}

		return enrichedText.toString();
	}

	/**
	 * Gets the tag type.
	 * 
	 * @return the tag type.
	 */
	public abstract String getTagType();

	/**
	 * Gets the tag
	 * 
	 * @return the tag
	 */
	public abstract String getTag();

	/**
	 * Gets the type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the tag value.
	 * 
	 * @return the tag value.
	 */
	public abstract String getTagValue();

	/**
	 * Gets the marker tag
	 * 
	 * @return the marker tag
	 */
	public abstract String getMarkerTag();

	/**
	 * Disables/enables this enrichment.
	 * 
	 * @param disabled
	 *            a boolean stating if the enrichment has to be disabled.
	 */
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Checks if the enrichment is disabled.
	 * 
	 * @return <code>true</code> if it's disabled; <code>false</code> otherwise.
	 */
	public boolean isDisabled() {
		return disabled;
	}

}
