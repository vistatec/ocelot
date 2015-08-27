package com.vistatec.ocelot.segment.model;

public class TranslationEnrichment extends Enrichment {

	private final static String ITS_TAG = "its:target";

	private final static String ITS_TAG_TYPE = "its:any";

	public final static String ENRICHMENT_TYPE = "translation";

	private String translation;

	private String language;

	public TranslationEnrichment(String nifOffsetString) {
		super(nifOffsetString);
	}
	
	public TranslationEnrichment(String nifOffsetString, String translation, String language) {
		super(nifOffsetString);
		this.language = language;
		this.translation = translation;
	}

	public void setTranslation(final String translation) {

		this.translation = translation;
	}

	public void setLanguage(final String language) {

		this.language = language;
	}
	
	public String getTranslation(){
		return translation;
	}
	
	public String getLanguage(){
		return language;
	}

	@Override
	public String getTagType() {
		return ITS_TAG_TYPE;
	}

	@Override
	public String getTag() {
		return ITS_TAG;
	}

	@Override
	public String getType() {
		return ENRICHMENT_TYPE;
	}

	@Override
    public String getTagValue() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getMarkerTag() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
