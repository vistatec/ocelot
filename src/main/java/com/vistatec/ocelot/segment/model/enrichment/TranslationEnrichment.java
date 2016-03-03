package com.vistatec.ocelot.segment.model.enrichment;

/**
 * The Translation enrichment class.
 */
public class TranslationEnrichment extends Enrichment {

	/** The ITS tag. */
	private final static String ITS_TAG = "its:target";

	/** The tag type. */
	private final static String ITS_TAG_TYPE = "its:any";

	/** The translation. */
	private String translation;

	/** The language. */
	private String language;

	/**
	 * Constructor.
	 * 
	 * @param nifOfsetString
	 *            the NIF offset string.
	 */
	public TranslationEnrichment(String nifOffsetString) {
		super(Enrichment.TRANSLATION_TYPE, nifOffsetString);
	}

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 * @param translation
	 *            the translation.
	 * @param language
	 *            the language.
	 */
	public TranslationEnrichment(String nifOffsetString, String translation,
	        String language) {
		super(Enrichment.TRANSLATION_TYPE, nifOffsetString);
		this.language = language;
		this.translation = translation;
	}

	/**
	 * Sets the translation.
	 * @param translation the translation.
	 */
	public void setTranslation(final String translation) {

		this.translation = translation;
	}

	/**
	 * Sets the language.
	 * @param language the language.
	 */
	public void setLanguage(final String language) {

		this.language = language;
	}

	/**
	 * Gets the translation.
	 * @return the translation.
	 */
	public String getTranslation() {
		return translation;
	}

	/**
	 * Gets the language.
	 * @return the language.
	 */
	public String getLanguage() {
		return language;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagType()
	 */
	@Override
	public String getTagType() {
		return ITS_TAG_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTag()
	 */
	@Override
	public String getTag() {
		return ITS_TAG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagValue()
	 */
	@Override
	public String getTagValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getMarkerTag()
	 */
	@Override
	public String getMarkerTag() {
		return null;
	}

}
